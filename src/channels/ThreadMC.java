package channels;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import peer.ChunkStoreRecord;
import peer.Message;
import peer.Peer;
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
		Integer peerID = Peer.getInstance().getPeerID();
		
		if(Peer.peerStoredChunk(arguments[3], chunkNo, peerID)) {
			String filename = peerID + "-" + arguments[3] + "." + chunkNo.toString() + ".chunk";
			FileInputStream fs = new FileInputStream(filename);
			byte[] header = Message.createChunkHeader(arguments[1], peerID.toString(), arguments[3], chunkNo);
			byte[] data = new byte[64000];
			fs.read(data);
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(header.length + data.length);
			outputStream.write(header);
			outputStream.write(data);
			byte[] message = outputStream.toByteArray(); // concatenating the two arrays
			outputStream.close();
			
			DatagramPacket chunk = new DatagramPacket(message, message.length, Peer.getMDRAddress(), Peer.getMDRPort());	
			MulticastSocket socket = new MulticastSocket();
			Peer.getInstance().setMdrPacketsReceived(0);
			long timeout = Utils.generateRandomInteger(0, 400);	
			// TODO isto pode parar o thread, se calhar e melhor criarmos uma thread nova para esta funcao
			Thread.sleep(timeout);
			if (Peer.getInstance().getMdrPacketsReceived() == 0)
				socket.send(chunk);
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

	

}
