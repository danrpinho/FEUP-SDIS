package channels;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

import initiators.BackupChunk;
import peer.Message;
import peer.Peer;
//import javafx.util.Pair;
import utils.Pair;
import utils.Utils;

public class ThreadMC extends MulticastThread {

	public ThreadMC(InetAddress address, int port) throws IOException {
		super(address, port);
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				DatagramPacket packet = receivePacket(512);		
				String data = new String(packet.getData(), "ISO-8859-1");
				String firstWord = Utils.getFirstWord(data);
				String version  = Utils.getSecondWord(data);
				System.out.println("Thread MC Packet received: " + firstWord);
				switch(firstWord) {
					case "STORED":
						processStored(packet);
						break;
					case "GETCHUNK":
						processGetchunk(packet);
						break;
					case "DELETE":
						processDelete(packet);
						break;
					case "REMOVED":
						processRemoved(packet);
						break;
					default:
						throw new IOException("Invalid packet header!");
				}
								
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void processStored(DatagramPacket packet) throws UnsupportedEncodingException {
		String[] arguments = Message.splitMessage(new String(packet.getData()));
		Integer chunkNo = Integer.parseInt(arguments[4]);
		Integer senderID = Integer.parseInt(arguments[2]);
		if(senderID != Peer.getPeerID())
			Peer.addPeerToHashmap(arguments[3], chunkNo, senderID);
		
	}	

	private void processGetchunk(DatagramPacket packet) throws IOException, InterruptedException {
		String[] arguments = Message.splitMessage(new String(packet.getData()));
		Integer chunkNo = Integer.parseInt(arguments[4]);
		Integer peerID = Peer.getPeerID();

		if(Peer.peerStoredChunk(arguments[3], chunkNo, peerID)) {
			System.out.println("Getting chunk");
			String filename = peerID + "-" + arguments[3] + "." + chunkNo.toString() + ".chunk";
			File fileIn = new File(filename);
			FileInputStream fs = new FileInputStream(fileIn);
			byte[] header = Message.createChunkHeader(arguments[1], peerID.toString(), arguments[3], chunkNo);
			byte[] data = new byte[(int) fileIn.length()];
			fs.read(data);
			fs.close();
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(header.length + data.length);
			outputStream.write(header);
			outputStream.write(data);
			byte[] message = outputStream.toByteArray(); // concatenating the two arrays
			outputStream.close();
			
			DatagramPacket chunk = new DatagramPacket(message, message.length, Peer.getMDRAddress(), Peer.getMDRPort());	
			MulticastSocket socket = new MulticastSocket();
			Peer.setMdrPacketsReceived(0);
			long timeout = Utils.generateRandomInteger(0, 400);
			Thread.sleep(timeout);
			if (Peer.getMdrPacketsReceived() == 0) {
				socket.send(chunk);
			}
			socket.close();
		}		
	}
	
	private void processDelete(DatagramPacket packet) {
		String[] arguments = Message.splitMessage((new String(packet.getData())));
		String fileID = arguments[3];
		Integer senderID = Integer.parseInt(arguments[2]);
		
		if(senderID != Peer.getPeerID())
			Peer.deleteFile(fileID);
	}
	
	private void processRemoved(DatagramPacket packet) {
		String[] arguments = Message.splitMessage((new String(packet.getData())));
		String fileID = arguments[3];
		Integer senderID = Integer.parseInt(arguments[2]);
		Integer chunkNo = Integer.parseInt(arguments[4]);
		
		
		Peer.removeFileStoresPeer(fileID, chunkNo, senderID);
			
		if(Peer.getChunksInPeer().containsKey(fileID) && Peer.getChunksInPeer().get(fileID).contains(chunkNo) && 
				Peer.getChunksInPeer().containsKey(fileID) && Peer.getChunksInPeer().get(fileID).contains(chunkNo) && 
				Peer.getFileStores().get(fileID).peers.get(chunkNo).size() < Peer.getFileStores().get(fileID).getReplicationDeg()) {
			
			File file= new File(((Integer) Peer.getPeerID()).toString() + "-"+ fileID+"."+chunkNo.toString()+".chunk");
			
			FileInputStream stream = null;
			byte [] content = new byte[Peer.getChunkSize()];
			try {
				stream = new FileInputStream(file);			
				stream.read(content);
				long timeout = Utils.generateRandomInteger(0, 400);
				
				Peer.getPutchunksReceived().clear();
				// TODO isto pode parar o thread, se calhar e melhor criarmos uma thread nova para esta funcao
				Thread.sleep(timeout);
			} catch (IOException | InterruptedException e) {
				System.err.println("Exception in processing Removed");
				e.printStackTrace();
			}
			
			
			if(! Peer.getPutchunksReceived().contains(new Pair<String, Integer>(fileID, chunkNo))) {
				
				(new Thread(new BackupChunk(fileID, content, chunkNo, 1))).start();
				Peer.getPutchunksReceived().remove(new Pair<String, Integer>(fileID, chunkNo));
			}
			
		}
	}

	

}
