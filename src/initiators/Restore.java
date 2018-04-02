package initiators;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.security.NoSuchAlgorithmException;

import peer.Message;
import peer.Peer;
import peer.RestoreStatus;

public class Restore implements Runnable {
	
	protected File file;
	final protected int chunkSize = 64000;
	
	private int chunkCount;
	private int replicationDeg;
	private MulticastSocket mdbSocket = null;
	private MulticastSocket mcSocket = null;


	public Restore(File file) throws IOException {
		this.file = file;
		this.chunkCount = (int) Math.ceil(file.length() / (double) this.chunkSize);
		if (file.length() % this.chunkSize == 0)
			this.chunkCount++;
		this.mcSocket = new MulticastSocket();
	}	
	
	public void run() {
		try {
			String fileID = Message.getFileData(file);
			String version = Peer.getVersion();
			String peerID = ((Integer) Peer.getPeerID()).toString();
			for (int chunkNo = 0; chunkNo < chunkCount; chunkNo++) {
				Peer.setCurrentRestore(new RestoreStatus(fileID, chunkNo));
				byte[] data = Message.createGetchunkHeader(version, peerID, fileID, chunkNo);
				DatagramPacket packet = new DatagramPacket(data, data.length, Peer.getMCAddress(), Peer.getMCPort());
				mcSocket.send(packet);
				Thread.sleep(500);
				if(!Peer.getCurrentRestore().isReceived()) {
					System.out.println("Chunk " + chunkNo + " missing from peers; Aborting.");
					return;
				}
			}
			System.out.println("All " + chunkCount + " chunks received. Assembling file...");
			String filename = assembleFile(fileID);
			System.out.println("File was reassembled successfully, it can be found in " + filename);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private String assembleFile(String fileID) throws IOException {
		File fileOut = new File(fileID + "_restored");
		FileOutputStream output = new FileOutputStream(fileOut);
		for (int i = 0; i < chunkCount; i++) {
			String chunkName = Peer.getPeerID() + "-" + fileID + "." + i + ".chunk";
			File fileIn = new File(chunkName);
			FileInputStream input = new FileInputStream(fileIn);
			int length = (int) fileIn.length();
			byte[] current = new byte[length];
			input.read(current);
			output.write(current);
			input.close();
		}
		output.close();
		return fileOut.getPath().toString();
	}

	public boolean restore(MulticastSocket mdrSocket, File file) throws NoSuchAlgorithmException, IOException {
		String fileData = Message.getFileData(file);
		
		return false;
	}
	
}
