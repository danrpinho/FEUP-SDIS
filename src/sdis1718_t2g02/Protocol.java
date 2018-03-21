package sdis1718_t2g02;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Objects;

public abstract class Protocol {

	protected String version;
	protected String senderID;
	protected File file;
	final protected int chunkSize = 64000;

	public enum MessageType {
		PUTCHUNK, STORED, GETCHUNK, CHUNK, DELETE, REMOVED
	};
	
	/**
	 * @brief Creates a message header.
	 * @param type
	 * @param fileID
	 * @param chunkNo
	 * @param replicationDeg
	 * @return
	 */
	public byte[] createHeader(MessageType type, String fileID, int chunkNo, int replicationDeg) {
		//header has at least type, version, sender and file id
		String res = type.name() + " " + this.version + " " + this.senderID + " " + fileID;
		if (chunkNo != -1) {
			String secondPart = new String();
			if (replicationDeg == -1)
				secondPart = " " + chunkNo;
			else
				secondPart = " " + chunkNo + " " + replicationDeg;
			res = res + secondPart;
		}
		return res.getBytes();
	}

	/**
	 * @brief Acquires a file's metadata, in order to create the fileID parameter for the header.
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
	 * @brief Encodes a byte array to a String representation of their hexadecimal representations.
	 * @param data
	 * @return
	 */
	private String encodeByteArray(byte[] data) {
		StringBuilder sb = new StringBuilder();
		for (byte b: data) {
			sb.append(String.format("%02X", b));
		}
		return sb.toString();
	}

	/**
	 * @brief Protocol constructor.
	 * @param version
	 * @param senderID
	 */
	public Protocol(String version, String senderID) {
		this.version = version;
		this.senderID = senderID;
	}

}
