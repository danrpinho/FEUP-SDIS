package sdis1718_t2g02;

public class Protocol {

	public enum MessageType {
		PUTCHUNK, STORED, GETCHUNK, CHUNK, DELETE, REMOVED
	};

	public byte[] createHeader(MessageType type, String version, String senderID, String fileID, int chunkNo,
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

}
