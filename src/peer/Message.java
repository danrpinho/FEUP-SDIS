package peer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import utils.Utils;

public final class Message {
	
	public final static char CR = (char) 0x0D;
	public final static char LF = (char) 0x0A;
	public final static String endHeader = "" + CR + LF + CR + LF;
	
	private Message() {}
	
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
	public static byte[] createHeader(MessageType type, String version, String peerID, String fileID, int chunkNo, int replicationDeg) {
		// header has at least type, version, sender and file id
		String res = type.name() + " " + version + " " + peerID + " " + fileID;
		if (chunkNo != -1) {
			String secondPart = new String();
			if (replicationDeg == -1)
				secondPart = " " + chunkNo;
			else
				secondPart = " " + chunkNo + " " + replicationDeg;
			res = res + secondPart + Message.endHeader;
		}
		return res.getBytes();
	}
	
	
	public static byte[] createPutchunkHeader(String version, String peerID, String fileID, int chunkNo,
			int replicationDeg) {
		byte[] res = createHeader(MessageType.PUTCHUNK, version, peerID, fileID, chunkNo, replicationDeg);
		return res;
	}

	public static byte[] createStoredHeader(String version, String peerID, String fileID, int chunkNo) {
		byte[] res = createHeader(MessageType.STORED, version, peerID, fileID, chunkNo, -1);
		return res;
	}
	
	public static byte[] createGetchunkHeader(String version, String peerID, String fileID, int chunkNo) {
		byte[] res = createHeader(MessageType.GETCHUNK, version, peerID, fileID, chunkNo, -1);
		return res;
	}
	
	public static byte[] createChunkHeader(String version, String peerID, String fileID, int chunkNo) {
		byte[] res = createHeader(MessageType.CHUNK, version, peerID, fileID, chunkNo, -1);
		return res;
	}
	
	public static byte[] createDeleteHeader(String version, String peerID, String fileID, int chunkNo) {
		byte[] res = createHeader(MessageType.DELETE, version, peerID, fileID, -1, -1);
		return res;
	}
	
	public static byte[] createRemovedHeader(String version, String peerID, String fileID, int chunkNo) {
		byte[] res = createHeader(MessageType.REMOVED, version, peerID, fileID, chunkNo, -1);
		return res;
	}
	
	/**
	 * @brief Acquires a file's metadata, in order to create the fileID parameter
	 *        for the header.
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	public static String getFileData(File file) throws IOException, NoSuchAlgorithmException {
		String name = file.getName();
		String size = Objects.toString(file.length());
		String last = Files.getLastModifiedTime(file.toPath(), LinkOption.NOFOLLOW_LINKS).toString();
		String owner = Files.getOwner(file.toPath(), LinkOption.NOFOLLOW_LINKS).toString();
		String temp = name + "::" + size + "::" + last + "::" + owner;

		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] initialData = digest.digest(temp.getBytes(StandardCharsets.UTF_8));
		String hashedData = Utils.encodeByteArray(initialData);
		return hashedData;
	}
	
	public static String[] splitMessage(String message) {
		
		int firstIndexOfCR = message.indexOf(CR);
		String header = message.substring(0, firstIndexOfCR);
		String headerWithoutRepeatedSpaces = header.replaceAll(" +"," ");
		String [] headerArgs = headerWithoutRepeatedSpaces.split(" ");
		String version = headerArgs[0];
		String body = "";
		if(version.equals("CHUNK") || version.equals("PUTCHUNK")) {
			System.out.println("b1");
			body = " "+message.substring(firstIndexOfCR + 4);
		}
		
		
		String messageWithoutCR = headerWithoutRepeatedSpaces + body;
		System.out.print(body.length());
		System.out.print("body: ");
		System.out.println(body);
		System.out.print("messageWithoutCR: ");
		System.out.println(messageWithoutCR);
		
		String [] messageArgs = messageWithoutCR.split(" ");
		return messageArgs;
	}

}
