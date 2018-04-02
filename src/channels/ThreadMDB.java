package channels;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;

import peer.Message;
import peer.Peer;
import utils.Pair;
import utils.Utils;

public class ThreadMDB extends MulticastThread {
	
	private MulticastSocket mcSocket = null;

	public ThreadMDB(InetAddress address, int port) throws IOException {
		super(address, port);
		mcSocket = new MulticastSocket();
	}

	@Override
	public void run() {
		System.out.println("Thread mdb run");
		while(true) {
			try {
				DatagramPacket packet = receivePacket(64512);
				System.out.print("Thread MDB Packet received: ");
				//System.out.println(new String(packet.getData()));
				String firstWord = getFirstWord(new String(packet.getData(), "ISO-8859-1"));
				if (firstWord.equals("PUTCHUNK")) {
					store(packet);
				} else {
					throw new IOException("Invalid packet header!");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public boolean store(DatagramPacket packet) throws IOException, InterruptedException {
		System.out.println("Packet length: " + packet.getLength());
		byte[] data = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
		String[] packetData = new String(data, "ISO-8859-1").split(Message.endHeader, 2);
		byte[] chunk = packetData[1].getBytes("ISO-8859-1");
		String[] header = packetData[0].split(" ");
		int chunkNo = Integer.parseInt(header[4]);
		String fileID = header[3];
		packetData = null;
		if (header[2].equals(Integer.toString(Peer.getPeerID())) || Peer.getReclaimedChunks().contains(new Pair<String, Integer> (fileID, chunkNo))
				|| Peer.getChunkPeerInit(fileID) == Peer.getPeerID() ) // avoids storing chunks
			return false;
		else {
			int currentID = Peer.getPeerID();
			
			int replicationDeg = Integer.parseInt(header[5]);

			// if (Peer.getInstance().getFileStores().contains(header[3]) &&
			// Peer.getInstance().getFileStores().get(header[3]).peers.containsKey(chunkNo)
			// &&
			// Peer.getInstance().getFileStores().get(header[3]).peers.get(chunkNo).contains(currentID))
			// {
			// return true;
			// }
			Peer.createHashMapEntry(fileID, replicationDeg, Integer.parseInt(header[2]));
			Peer.addPeerToHashmap(fileID, chunkNo, currentID);
			Utils.printHashMap(Peer.getFileStores());

			Peer.getPutchunksReceived().add(new Pair<String, Integer>(fileID, chunkNo));
			System.out.println("Added putchunk "+fileID+((Integer) chunkNo).toString());
			String filename = ((Integer) Peer.getPeerID()).toString() + "-" + fileID + "." + header[4] + ".chunk";
			Peer.addToChunksInPeer(fileID, chunkNo);
			FileOutputStream out = new FileOutputStream(filename);
			out.write(chunk);
			out.close();

			byte[] confirmationData = Message.createStoredHeader(header[1], Integer.toString(currentID), fileID,
					chunkNo);
			long timeout = (long) Utils.generateRandomInteger(0, 400);
			Thread.sleep(timeout);
			System.out.println("Stored message sent");
			mcSocket.send(new DatagramPacket(confirmationData, confirmationData.length, Peer.getMCAddress(),
					Peer.getMCPort()));

			// TODO adicionar à hashtable se não estiver presente
			return true;
		}
	}
}
