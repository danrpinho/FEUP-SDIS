package initiators;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.security.NoSuchAlgorithmException;

import peer.Message;
import peer.Peer;

public class Restore implements Runnable {
	
	protected File file;
	final protected int chunkSize = 64000;
	
	private int chunkCount;
	private int replicationDeg;
	private MulticastSocket mdbSocket = null;
	private MulticastSocket mcSocket = null;


	public Restore() {
		this.chunkCount = (int) Math.ceil(file.length() / (double) this.chunkSize);
		if (file.length() % this.chunkSize == 0)
			this.chunkCount++;
	}
	public void run() {
		try {
			String fileID = Message.getFileData(file);
			String version = Peer.getInstance().getVersion();
			String peerID = ((Integer) Peer.getInstance().getPeerID()).toString();
			for (int chunkNo = 0; chunkNo < chunkCount; chunkNo++) {
				byte[] data = Message.createGetchunkHeader(version, peerID, fileID, chunkNo);
				DatagramPacket packet = new DatagramPacket(data, data.length);
				mcSocket.send(packet);
				
				//TODO verificar se o chunk foi recebido
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public boolean restore(MulticastSocket mdrSocket, File file) throws NoSuchAlgorithmException, IOException {
		String fileData = Message.getFileData(file);
		
		return false;
	}
	
}
