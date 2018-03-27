package sdis1718_t2g02;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadMC extends MulticastThread {

	public ThreadMC(String address, String port) throws IOException {
		super(address, port);
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				byte[] rbuf = new byte[512];
				DatagramPacket packet = new DatagramPacket(rbuf, rbuf.length);
				socket.receive(packet);
				
				String data = new String(packet.getData(), "UTF-8");
				String firstWord = getFirstWord(data);
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
						throw new IOException();
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
