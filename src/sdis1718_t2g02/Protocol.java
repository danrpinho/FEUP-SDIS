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

	public byte[] createHeader(MessageType type, String fileID, int chunkNo, int replicationDeg) {
		String firstPart = type.name() + " " + this.version + " " + this.senderID + " ";
		String res = new String();
		if (chunkNo == -1) {
			res = firstPart + fileID;
		} else {
			String secondPart = new String();
			if (replicationDeg == -1)
				secondPart = " " + chunkNo;
			else
				secondPart = " " + chunkNo + " " + replicationDeg;
			res = firstPart + fileID + secondPart;
		}
		return res.getBytes();
	}

	public String getFileData(File file) throws IOException, NoSuchAlgorithmException {
		String name = file.getName();
		String size = Objects.toString(file.length());
		String last = Files.getLastModifiedTime(file.toPath(), LinkOption.NOFOLLOW_LINKS).toString();
		String owner = Files.getOwner(file.toPath(), LinkOption.NOFOLLOW_LINKS).toString();
		String temp = name + "::" + size + "::" + last + "::" + owner;
		
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] initialData = digest.digest(temp.getBytes(StandardCharsets.UTF_8));
		String hashedData = encodeByteArray(initialData);
		return new String(hashedData);		
	}

	private String encodeByteArray(byte[] data) {
		StringBuilder sb = new StringBuilder();
		for (byte b: data) {
			sb.append(String.format("%02X", b));
		}
		return sb.toString();
	}

	public Protocol(String version, String senderID) {
		this.version = version;
		this.senderID = senderID;
	}

}
