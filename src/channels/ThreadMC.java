package channels;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

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
				System.out.print("Thread MC Packet received: ");
				System.out.println(new String(packet.getData()));
				String data = new String(packet.getData(), "ISO-8859-1");
				String firstWord = Utils.getFirstWord(data);
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
	
	private void processGetchunk(DatagramPacket packet) throws IOException, InterruptedException {
		String[] arguments = Message.splitMessage(new String(packet.getData()));
		Integer chunkNo = Integer.parseInt(arguments[4]);
		Integer senderID = Integer.parseInt(arguments[2]);
		Integer peerID = Peer.getPeerID();

		if(Peer.peerStoredChunk(arguments[3], chunkNo, peerID)) {
			System.out.println("Getting chunk");
			String filename = peerID + "-" + arguments[3] + "." + chunkNo.toString() + ".chunk";
			FileInputStream fs = new FileInputStream(filename);
			byte[] header = Message.createChunkHeader(arguments[1], peerID.toString(), arguments[3], chunkNo);
			byte[] data = new byte[64000];
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

	protected void processStored(DatagramPacket packet) throws UnsupportedEncodingException {
		String[] arguments = Message.splitMessage(new String(packet.getData()));
		//ConcurrentHashMap<String,ChunkStoreRecord> hashMap = Peer.getFileStores();
		Integer chunkNo = Integer.parseInt(arguments[4]);
		Integer senderID = Integer.parseInt(arguments[2]);
		/*System.out.println(arguments.length);
		for(int i=0; i < arguments.length; i++)
			System.out.println(arguments[i]);*/
		if(senderID != Peer.getPeerID())
			Peer.addPeerToHashmap(arguments[3], chunkNo, senderID);
		
		System.out.println("Received Stored Message:");
		Utils.printHashMap(Peer.getFileStores());
		
//		if (hashMap.contains(arguments[3])) {		//must contain entry if a STORED message was received
//			ChunkStoreRecord record = hashMap.get(arguments[3]);
//			if(record.peers.containsKey(chunkNo)) {
//				ArrayList<Integer> list = record.peers.get(chunkNo);
//				if(!list.contains(senderID)) {
//					list.add(senderID);
//					record.peers.put(chunkNo, list);
//				}
//			} else {
//				ArrayList<Integer> list = new ArrayList<Integer>();
//				list.add(senderID);
//				record.peers.put(chunkNo, list);
//			}
//			hashMap.put(arguments[3], record);
//		} else {
//			throw new NullPointerException();
//		}
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
				System.out.print("Timeout: ");
				System.out.println(timeout);
				Peer.getPutchunksReceived().clear();
				// TODO isto pode parar o thread, se calhar e melhor criarmos uma thread nova para esta funcao
				Thread.sleep(timeout);
			} catch (IOException | InterruptedException e) {
				System.err.println("Exception in processing Removed");
				e.printStackTrace();
			}
			
			Utils.printVectorOfPairs(Peer.getPutchunksReceived());
			if(! Peer.getPutchunksReceived().contains(new Pair<String, Integer>(fileID, chunkNo))) {
				
				(new Thread(new BackupChunk(fileID, content, chunkNo, 1))).start();
				Peer.getPutchunksReceived().remove(new Pair<String, Integer>(fileID, chunkNo));
			}
			
		}
	}

	

}
