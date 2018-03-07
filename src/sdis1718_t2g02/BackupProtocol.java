	package sdis1718_t2g02;

public class BackupProtocol extends Protocol {

	public BackupProtocol() {
		// TODO Auto-generated constructor stub
	}
	
	private byte[] createPutchunkHeader(String version, String senderID, String fileID, int chunkNo, int replicationDeg) {
		byte[] res = super.createHeader(MessageType.PUTCHUNK, version, senderID, fileID, chunkNo, replicationDeg);
		return res;
	}
	
	private byte[] createStoredHeader(String version, String senderID, String fileID, int chunkNo, int replicationDeg) {
		byte[] res = super.createHeader(MessageType.PUTCHUNK, version, senderID, fileID, chunkNo, -1);
		return res;
	}

}
