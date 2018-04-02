package initiators;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MulticastSocket;

import peer.Message;
import peer.Peer;

public class Backup implements Runnable {

	/*protected String version;
	protected String senderID;*/
	private File file;
	final private int chunkSize = 64000;
	final private int maximumFileSize = 2000000000;
	
	private int chunkCount;
	private int replicationDeg;
	private MulticastSocket mdbSocket = null;

	

	/*public enum ProtocolType {
		BACKUP, RESTORE, DELETE, RECLAIM
	}*/

	

	public Backup(/*ProtocolType type, String version, String senderID,*/ File file, int replicationDeg) throws IOException {
		/*this.version = version;
		this.senderID = senderID;*/
		/*if (type == ProtocolType.BACKUP) {*/
			if (replicationDeg < 1 || replicationDeg > 9) {
				throw new IllegalArgumentException("Replication degree must be between 1 and 9");
			} else
				this.replicationDeg = replicationDeg;

			this.file = file;
			this.chunkCount = (int) Math.ceil(file.length() / (double) this.chunkSize);
			if (file.length() % this.chunkSize == 0)
				this.chunkCount++;
			
			mdbSocket = new MulticastSocket();
		/*}*/
	}

	
	
	
	@Override
	public void run(){
		try {
		String fileID = Message.getFileData(file);
		FileInputStream stream = new FileInputStream(this.file);
		Peer.createHashMapEntry(fileID, replicationDeg, Peer.getPeerID());
		boolean success = true;
		String version = Peer.getVersion();
		String peerID = ((Integer) Peer.getPeerID()).toString();
		byte [] fileContent = new byte[this.chunkCount*this.chunkSize];
		stream.read(fileContent);
		stream.close();

		// reading from file this.chunkSize bytes at a time
		for (int currentChunk = 0; currentChunk < this.chunkCount; currentChunk++) {
			byte [] content = new byte[this.chunkSize];
			System.arraycopy(fileContent, currentChunk*this.chunkSize, content, 0, this.chunkSize);
			(new Thread(new BackupChunk(fileID, content, currentChunk, replicationDeg))).start();
			
		}}catch(Exception e) {
			return;
		}
		
	}
	
	
	
	

 


}
	

 



