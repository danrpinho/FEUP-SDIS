package peer;

public class RestoreStatus {
	
	protected String fileID;
	protected Integer chunkNo;
	protected boolean received;

	public RestoreStatus(String fileID, Integer chunkNo) {
		this.fileID = fileID;
		this.chunkNo = chunkNo;
		this.received = false;
	}

	public String getFileID() {
		return fileID;
	}

	public void setFileID(String fileID) {
		this.fileID = fileID;
	}

	public Integer getChunkNo() {
		return chunkNo;
	}

	public void setChunkNo(Integer chunkNo) {
		this.chunkNo = chunkNo;
	}

	public boolean isReceived() {
		return received;
	}

	public void setReceived(boolean received) {
		this.received = received;
	}

}
