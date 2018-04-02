package initiators;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MulticastSocket;

import peer.Message;
import peer.Peer;

public class Backup implements Runnable {

	
	private File file;
	private long lastChunkSize;
	private int chunkCount;
	private int replicationDeg;
	private MulticastSocket mdbSocket = null;


	public Backup( File file, int replicationDeg) throws IOException {
		
			if (replicationDeg < 1 || replicationDeg > 9) {
				throw new IllegalArgumentException("Replication degree must be between 1 and 9");
			} else
				this.replicationDeg = replicationDeg;

			this.file = file;
			this.chunkCount = (int) Math.ceil(file.length() / (double) Peer.getChunkSize());
			this.lastChunkSize = file.length() % Peer.getChunkSize();
			if (this.lastChunkSize == 0)
				this.chunkCount++;
						
			mdbSocket = new MulticastSocket();
		
	}

	
	
	
	@Override
	public void run(){
		try {
			long currentChunkSize = Peer.getChunkSize();
			String fileID = Message.getFileData(file);
			FileInputStream stream = new FileInputStream(this.file);
			Peer.createHashMapEntry(fileID, replicationDeg, Peer.getPeerID(), file.toPath().getFileName().toString());
			boolean success = true;
			String version = Peer.getVersion();
			String peerID = ((Integer) Peer.getPeerID()).toString();
			byte[] fileContent = new byte[this.chunkCount * Peer.getChunkSize()];
			stream.read(fileContent);
			stream.close();

			// reading from file this.chunkSize bytes at a time
			for (int currentChunk = 0; currentChunk < this.chunkCount; currentChunk++) {
				if (currentChunk == this.chunkCount - 1) {
					currentChunkSize = this.lastChunkSize;
				}
				System.out.println("Chunk size for chunk #" + currentChunk + ": " + currentChunkSize);

				byte[] content = new byte[(int) currentChunkSize];
				System.out.println("Content size: " + content.length);
				System.arraycopy(fileContent, currentChunk * Peer.getChunkSize(), content, 0, (int) currentChunkSize);
				(new Thread(new BackupChunk(fileID, content, currentChunk, replicationDeg))).run();

			}
		} catch (Exception e) {
			return;
		}

	}

}
 



