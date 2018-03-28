package peer;



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
	public static byte[] createHeader(MessageType type, String fileID, int chunkNo, int replicationDeg) {
		// header has at least type, version, sender and file id
		String res = type.name() + " " + Peer.version + " " + Peer.peerID + " " + fileID;
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
	
	
	public static byte[] createPutchunkHeader(String fileID, int chunkNo,
			int replicationDeg) {
		byte[] res = createHeader(MessageType.PUTCHUNK, fileID, chunkNo, replicationDeg);
		return res;
	}

	public static byte[] createStoredHeader(String fileID, int chunkNo) {
		byte[] res = createHeader(MessageType.STORED, fileID, chunkNo, -1);
		return res;
	}


}
