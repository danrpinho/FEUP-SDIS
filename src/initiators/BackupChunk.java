package initiators;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

import peer.Message;
import peer.Peer;

public class BackupChunk implements Runnable {
	
	private String fileID = null;
	private FileInputStream stream = null;
	private int currentChunk;
	private final int chunkSize = 640;
	private int replicationDeg;
	private MulticastSocket mdbSocket = null;
	
	public BackupChunk(String fileID, FileInputStream stream, int currentChunk, int replicationDeg) throws IOException {
		this.fileID = fileID;
		this.stream = stream;
		this.currentChunk = currentChunk;
		this.replicationDeg = replicationDeg;
		mdbSocket = new MulticastSocket();
	}
	
	@Override
	public void run() {
		int resendCounter = 0;
		while (resendCounter < 5) {
			try {
			byte[] currentData = new byte[this.chunkSize]; // reading from file
			stream.read(currentData, this.currentChunk * this.chunkSize, this.chunkSize);
			byte[] currentHeader = Message.createPutchunkHeader(Peer.getVersion(), ((Integer) Peer.getPeerID()).toString(), fileID, currentChunk, this.replicationDeg); // creating header

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(currentHeader.length + this.chunkSize);
			outputStream.write(currentHeader);
			outputStream.write(currentData);
			byte[] message = outputStream.toByteArray(); // concatenating the two arrays
			outputStream.close();

			DatagramPacket packet = new DatagramPacket(message, message.length, Peer.getMDBAddress(), Peer.getMDBPort());
			System.out.println("Backup Packet sent before");
			mdbSocket.send(packet);
			System.out.println("Backup Packet sent after");
			long timeout = (long) (1000 * Math.pow(2, resendCounter));
			Thread.sleep(timeout);
			/*Utils.printHashMap(Peer.getFileStores());
			System.out.print("current chunk: ");
			System.out.println(currentChunk);
			System.out.println(Peer.getFileStores().containsKey(fileID));
			System.out.println(Peer.getFileStores().get(fileID).peers.containsKey(currentChunk));*/
			if (Peer.getFileStores().containsKey(fileID)
					&& Peer.getFileStores().get(fileID).peers.containsKey(currentChunk)) {
				if (Peer.getFileStores().get(fileID).peers.get(currentChunk)
						.size() >= this.replicationDeg)
					break;
			}
			resendCounter++;
			}catch(Exception e) {
				System.err.println("Exception in BackupChunk");
				e.printStackTrace();
				return;
			}
		}
		
	}
	
}
