	package sdis1718_t2g02;

public class BackupProtocol extends Protocol {
	
	private int replicationDeg;
	
	public BackupProtocol(String version, String senderID, int replicationDeg) {
		super(version, senderID);
		this.replicationDeg = replicationDeg;
	}
	
	private byte[] createPutchunkHeader(String version, String senderID, String fileID, int chunkNo, int replicationDeg) {
		byte[] res = super.createHeader(MessageType.PUTCHUNK, fileID, chunkNo, replicationDeg);
		return res;
	}
	
	private byte[] createStoredHeader(String version, String senderID, String fileID, int chunkNo, int replicationDeg) {
		byte[] res = super.createHeader(MessageType.STORED, fileID, chunkNo, -1);
		return res;
	}

}
