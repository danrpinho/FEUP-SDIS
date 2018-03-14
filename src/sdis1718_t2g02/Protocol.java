package sdis1718_t2g02;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
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

	public String getFileData(File file) throws IOException {
		String name = file.getName();
		String size = Objects.toString(file.length());
		String last = Files.getLastModifiedTime(file.toPath(), LinkOption.NOFOLLOW_LINKS).toString();
		String owner = Files.getOwner(file.toPath(), LinkOption.NOFOLLOW_LINKS).toString();
		String temp = name + "::" + size + "::" + last + "::" + owner;
		
		
		
		//TODO encode e mandar sequencia de bytes para UTF-8
		String res = null;
		return res;
	}

	public Protocol(String version, String senderID) {
		this.version = version;
		this.senderID = senderID;
	}

}
