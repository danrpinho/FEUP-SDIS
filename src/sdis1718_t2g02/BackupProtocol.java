package sdis1718_t2g02;

import java.io.File;
import java.io.IOException;
import java.net.MulticastSocket;
import java.util.Arrays;
import java.net.DatagramPacket;
import java.io.FileInputStream;

public class BackupProtocol extends Protocol {

	private int replicationDeg;
	// private String path;
	private int chunkCount;

	public BackupProtocol(String version, String senderID, int replicationDeg, String path) {
		super(version, senderID);
		
		if (replicationDeg < 1 || replicationDeg > 9) {
			throw new IllegalArgumentException("Replication degree must be between 1 and 9");
		} else
			this.replicationDeg = replicationDeg;

		this.file = new File(path);
		this.chunkCount = (int) Math.ceil(file.length() / (double) this.chunkSize);
		if (file.length() % this.chunkSize == 0)
			this.chunkCount++;
	}

	private boolean backup(MulticastSocket socket) throws IOException {
		String fileID = super.getFileData(file);
		FileInputStream stream = new FileInputStream(this.file);	
		
		
		for (int currentChunk = 0; currentChunk < this.chunkCount; currentChunk++) {
			byte[] chunk = new byte[this.chunkSize];
			byte[] currentHeader = createPutchunkHeader(this.version, this.senderID, fileID, currentChunk,
					this.replicationDeg);
			DatagramPacket header = new DatagramPacket(currentHeader, currentHeader.length);
			socket.send(header);
			stream.read(chunk);
			
			
		}

		return false;
	}

	private boolean store() {
		// TODO implementar store
		return false;
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
