package peer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Objects;

import peer.Protocol.MessageType;

import java.lang.Thread;

public abstract class Protocol {

	protected String version;
	protected String senderID;
	protected File file;
	final protected int chunkSize = 64000;
	public final static char CR = (char) 0x0D;
	public final static char LF = (char) 0x0A;
	public final static String endHeader = "" + CR + LF + CR + LF;
	private int chunkCount;
	private int replicationDeg;

	public enum MessageType {
		PUTCHUNK, STORED, GETCHUNK, CHUNK, DELETE, REMOVED
	};

	public enum ProtocolType {
		BACKUP, RESTORE, DELETE, RECLAIM
	}

	/**
	 * @brief Creates a message header.
	 * @param type
	 * @param fileID
	 * @param chunkNo
	 * @param replicationDeg
	 * @return
	 */
	public byte[] createHeader(MessageType type, String fileID, int chunkNo, int replicationDeg) {
		// header has at least type, version, sender and file id
		String res = type.name() + " " + this.version + " " + this.senderID + " " + fileID;
		if (chunkNo != -1) {
			String secondPart = new String();
			if (replicationDeg == -1)
				secondPart = " " + chunkNo;
			else
				secondPart = " " + chunkNo + " " + replicationDeg;
			res = res + secondPart + this.endHeader;
		}
		return res.getBytes();
	}

	/**
	 * @brief Acquires a file's metadata, in order to create the fileID parameter
	 *        for the header.
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	public String getFileData(File file) throws IOException, NoSuchAlgorithmException {
		String name = file.getName();
		String size = Objects.toString(file.length());
		String last = Files.getLastModifiedTime(file.toPath(), LinkOption.NOFOLLOW_LINKS).toString();
		String owner = Files.getOwner(file.toPath(), LinkOption.NOFOLLOW_LINKS).toString();
		String temp = name + "::" + size + "::" + last + "::" + owner;

		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] initialData = digest.digest(temp.getBytes(StandardCharsets.UTF_8));
		String hashedData = encodeByteArray(initialData);
		return hashedData;
	}

	/**
	 * @brief Encodes a byte array to a String representation of their hexadecimal
	 *        representations.
	 * @param data
	 * @return
	 */
	private String encodeByteArray(byte[] data) {
		StringBuilder sb = new StringBuilder();
		for (byte b : data) {
			sb.append(String.format("%02X", b));
		}
		return sb.toString();
	}

	public Protocol(ProtocolType type, String version, String senderID, String path, int replicationDeg) {
		this.version = version;
		this.senderID = senderID;
		if (type == ProtocolType.BACKUP) {
			if (replicationDeg < 1 || replicationDeg > 9) {
				throw new IllegalArgumentException("Replication degree must be between 1 and 9");
			} else
				this.replicationDeg = replicationDeg;

			this.file = new File(path);
			this.chunkCount = (int) Math.ceil(file.length() / (double) this.chunkSize);
			if (file.length() % this.chunkSize == 0)
				this.chunkCount++;
		}
	}

	public Protocol(ProtocolType type, String version, String senderID) {
		this.version = version;
		this.senderID = senderID;
		if (type == ProtocolType.BACKUP) {
			throw new IllegalArgumentException("Protocol requires replication degree and file path!");
		}

	}

	public boolean backup(MulticastSocket mdbSocket) throws IOException, NoSuchAlgorithmException, InterruptedException {
		String fileID = getFileData(file);
		FileInputStream stream = new FileInputStream(this.file);
		Peer.getInstance().createHashMapEntry(fileID, replicationDeg);
		boolean success = true;

		// reading from file this.chunkSize bytes at a time
		for (int currentChunk = 0; currentChunk < this.chunkCount; currentChunk++) {
			int resendCounter = 0;
			while (resendCounter < 5) {
				byte[] currentData = new byte[this.chunkSize]; // reading from file
				stream.read(currentData);
				byte[] currentHeader = createPutchunkHeader(this.version, this.senderID, fileID, currentChunk,
						this.replicationDeg); // creating header

				ByteArrayOutputStream outputStream = new ByteArrayOutputStream(currentHeader.length + this.chunkSize);
				outputStream.write(currentHeader);
				outputStream.write(currentData);
				byte[] message = outputStream.toByteArray(); // concatenating the two arrays
				outputStream.close();

				DatagramPacket packet = new DatagramPacket(message, message.length);
				mdbSocket.send(packet);
				long timeout = (long) (1000 * Math.pow(2, resendCounter));
				Thread.sleep(timeout);
				if (Peer.getInstance().getFileStores().contains(fileID)
						&& Peer.getInstance().getFileStores().get(fileID).peers.containsKey(currentChunk)) {
					if (Peer.getInstance().getFileStores().get(fileID).peers.get(currentChunk)
							.size() >= this.replicationDeg)
						break;
				}
				resendCounter++;
			}
			if (resendCounter == 5)
				success = false;
		}
		return success;
	}

	

	private byte[] createPutchunkHeader(String version, String senderID, String fileID, int chunkNo,
			int replicationDeg) {
		byte[] res = createHeader(MessageType.PUTCHUNK, fileID, chunkNo, replicationDeg);
		return res;
	}

	public byte[] createStoredHeader(String version, String senderID, String fileID, int chunkNo) {
		byte[] res = createHeader(MessageType.STORED, fileID, chunkNo, -1);
		return res;
	}
}
