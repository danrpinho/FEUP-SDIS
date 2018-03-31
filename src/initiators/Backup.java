package initiators;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import peer.ChunkStoreRecord;
import peer.Message;
import peer.Peer;
import utils.Utils;

import java.lang.Thread;

public class Backup implements Runnable {

	/*protected String version;
	protected String senderID;*/
	protected File file;
	final protected int chunkSize = 64000;
	
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

	/*public Protocol(ProtocolType type, String version, String senderID) {
		this.version = version;
		this.senderID = senderID;
		if (type == ProtocolType.BACKUP) {
			throw new IllegalArgumentException("Protocol requires replication degree and file path!");
		}
	}*/

	/*public void run(MulticastSocket mdbSocket) throws IOException, NoSuchAlgorithmException, InterruptedException {
		String fileID = getFileData(file);
		FileInputStream stream = new FileInputStream(this.file);
		Peer.getInstance().createHashMapEntry(fileID, replicationDeg);
		boolean success = true;
		String version = Peer.getInstance().getVersion();
		String peerID = ((Integer) Peer.getInstance().getPeerID()).toString();
		// reading from file this.chunkSize bytes at a time
		for (int currentChunk = 0; currentChunk < this.chunkCount; currentChunk++) {
			int resendCounter = 0;
			while (resendCounter < 5) {
				byte[] currentData = new byte[this.chunkSize]; // reading from file
				stream.read(currentData);
				byte[] currentHeader = Message.createPutchunkHeader(version, peerID, fileID, currentChunk, this.replicationDeg); // creating header
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream(currentHeader.length + this.chunkSize);
				outputStream.write(currentHeader);
				outputStream.write(currentData);
				byte[] message = outputStream.toByteArray(); // concatenating the two arrays
				outputStream.close();
				DatagramPacket packet = new DatagramPacket(message, message.length);
				mdbSocket.send(packet);
				long timeout = (long) (1000 * Math.pow(2, resendCounter));
				Thread.sleep(timeout);
				if (Peer.getInstance().getFileStores().contains(fileID)
						&& Peer.getInstance().getFileStores().get(fileID).peers.containsKey(currentChunk)) {
					if (Peer.getInstance().getFileStores().get(fileID).peers.get(currentChunk)
							.size() >= this.replicationDeg)
						break;
				}
				resendCounter++;
			}
			if (resendCounter == 5)
				success = false;
		}
		return success;
	}*/


	@Override
	public void run(){
		try {
		String fileID = Message.getFileData(file);
		FileInputStream stream = new FileInputStream(this.file);
		Peer.getInstance().createHashMapEntry(fileID, replicationDeg);
		boolean success = true;
		String version = Peer.getVersion();
		String peerID = ((Integer) Peer.getPeerID()).toString();

		// reading from file this.chunkSize bytes at a time
		for (int currentChunk = 0; currentChunk < this.chunkCount; currentChunk++) {
			int resendCounter = 0;
			while (resendCounter < 5) {
				byte[] currentData = new byte[this.chunkSize]; // reading from file
				stream.read(currentData);
				byte[] currentHeader = Message.createPutchunkHeader(version, peerID, fileID, currentChunk, this.replicationDeg); // creating header

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
					System.out.println(Peer.getFileStores().get(fileID).peers.get(currentChunk).size());
					if (Peer.getFileStores().get(fileID).peers.get(currentChunk)
							.size() >= this.replicationDeg)
						break;
				}
				resendCounter++;
			}
			if (resendCounter == 5)
				success = false;
			
			
		}}catch(Exception e) {
			return;
		}
		
	}
	
	

 


}
	

 



