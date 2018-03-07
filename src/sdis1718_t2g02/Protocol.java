package sdis1718_t2g02;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.Objects;

public abstract class Protocol {
	
	private String version;
	private String senderID;
	
	public enum MessageType {
		PUTCHUNK, STORED, GETCHUNK, CHUNK, DELETE, REMOVED
	};

	public byte[] createHeader(MessageType type, /*String version, String senderID,*/ String fileID, int chunkNo,
			int replicationDeg) {		
		switch (type) {
		case PUTCHUNK: 
			break;
		case STORED:
			break;
		case GETCHUNK:
			break;
		case CHUNK:
			break;
		case DELETE:
			break;
		case REMOVED:
		default:
		}
		return null;
	}
	
	public String getFileData(File file) throws IOException {
		String name = file.getName();
		String size = Objects.toString(file.length());
		String last = Files.getLastModifiedTime(file.toPath(), LinkOption.NOFOLLOW_LINKS).toString();
		String owner = Files.getOwner(file.toPath(), LinkOption.NOFOLLOW_LINKS).toString();
		String res =  name + "::" + size + "::" + last + "::" + owner;
		return res;
	}
	
	
	
	public Protocol(String version, String senderID) {
		this.version = version;
		this.senderID = senderID;
	}

}
