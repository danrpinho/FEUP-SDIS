/*This class
 * is not doing anything here
 * if i have time i will implement it
 * */
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
	private byte[] content = null;
	private int currentChunk;
	private final int chunkSize = 640;
	private int replicationDeg;
	private MulticastSocket mdbSocket = null;
	
	public BackupChunk(String fileID, byte[] content, int currentChunk, int replicationDeg) {
		this.fileID = fileID;
		this.content = content;
		this.currentChunk = currentChunk;
		this.replicationDeg = replicationDeg;
		try {
			mdbSocket = new MulticastSocket();
		} catch (IOException e) {
			System.err.println("Exception in BackupChunk Constructor");
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		int resendCounter = 0;
		while (resendCounter < 5) {
			try {
			
			byte[] currentHeader = Message.createPutchunkHeader(Peer.getVersion(), ((Integer) Peer.getPeerID()).toString(), fileID, currentChunk, this.replicationDeg); // creating header

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(currentHeader.length + this.chunkSize);
			outputStream.write(currentHeader);
			outputStream.write(content);
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
