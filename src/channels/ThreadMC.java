package channels;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import peer.ChunkStoreRecord;
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
				System.out.println("Thread MC: Packet received");
				String data = new String(packet.getData(), "UTF-8");
				String firstWord = Utils.getFirstWord(data);
				switch(firstWord) {
					case "STORED":
						processStored(packet);
						break;
					case "GETCHUNK":
						break;
					case "DELETE":
						break;
					case "REMOVED":
						break;
					default:
						throw new IOException("Invalid packet header!");
				}
								
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void processStored(DatagramPacket packet) throws UnsupportedEncodingException {
		String[] arguments = new String(packet.getData(), "UTF-8").split(" ");
		ConcurrentHashMap<String,ChunkStoreRecord> hashMap = Peer.getInstance().getFileStores();
		Integer chunkNo = Integer.parseInt(arguments[4]);
		Integer senderID = Integer.parseInt(arguments[2]);
		if (hashMap.contains(arguments[3])) {		//must contain entry if a STORED message was received
			ChunkStoreRecord record = hashMap.get(arguments[3]);
			if(record.peers.containsKey(chunkNo)) {
				ArrayList<Integer> list = record.peers.get(chunkNo);
				if(!list.contains(senderID)) {
					list.add(senderID);
					record.peers.put(chunkNo, list);
				}
			} else {
				ArrayList<Integer> list = new ArrayList<Integer>();
				list.add(senderID);
				record.peers.put(chunkNo, list);
			}
			hashMap.put(arguments[3], record);
		} else {
			throw new NullPointerException();
		}
	}

	

}
