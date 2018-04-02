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
				String protocol = getFirstWord(new String(packet.getData(), "ISO-8859-1"));
				String version = getSecondWord(new String(packet.getData(), "ISO-8859-1"));
				if (protocol.equals("PUTCHUNK")) {
					if (version.equals("1"))
						store(packet);
					else if (version.equals("2"))
						storeEnhanced(packet);
				} else {
					throw new IOException("Invalid packet header!");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	

	private boolean storeEnhanced(DatagramPacket packet) throws IOException, InterruptedException {
		byte[] data = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
		String[] packetData = new String(data, "ISO-8859-1").split(Message.endHeader, 2);
		byte[] chunk = packetData[1].getBytes("ISO-8859-1");
		String[] header = packetData[0].split(" ");
		
		int chunkNo = Integer.parseInt(header[4]);
		int replicationDeg = Integer.parseInt(header[5]);
		int currentID = Peer.getPeerID();
		String fileID = header[3];
		packetData = null;
		
		if (header[2].equals(Integer.toString(Peer.getPeerID())) || Peer.getReclaimedChunks().contains(new Pair<String, Integer> (fileID, chunkNo))
				|| Peer.getChunkPeerInit(fileID) != -1) // avoids storing chunks
			return false;
		else {
			long timeout = (long) Utils.generateRandomInteger(0, 400);
			Thread.sleep(timeout);
			if (Peer.checkChunkPeers(fileID, chunkNo) >= replicationDeg)
				return false;

			Peer.createHashMapEntry(fileID, replicationDeg, Integer.parseInt(header[2]));
			Peer.addPeerToHashmap(fileID, chunkNo, currentID);
			Utils.printHashMap(Peer.getFileStores());

			Peer.getPutchunksReceived().add(new Pair<String, Integer>(header[3], chunkNo));
			String filename = ((Integer) Peer.getPeerID()).toString() + "-" + fileID + "." + header[4] + ".chunk";
			Peer.addToChunksInPeer(header[3], chunkNo);
			FileOutputStream out = new FileOutputStream(filename);
			out.write(chunk);
			out.close();

			byte[] confirmationData = Message.createStoredHeader(header[1], Integer.toString(currentID), fileID,
					chunkNo);
			
			mcSocket.send(new DatagramPacket(confirmationData, confirmationData.length, Peer.getMCAddress(),
					Peer.getMCPort()));

			return true;
		}
	}

	public boolean store(DatagramPacket packet) throws IOException, InterruptedException {
		byte[] data = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
		String[] packetData = new String(data, "ISO-8859-1").split(Message.endHeader, 2);
		byte[] chunk = packetData[1].getBytes("ISO-8859-1");
		String[] header = packetData[0].split(" ");
		int chunkNo = Integer.parseInt(header[4]);
		String fileID = header[3];
		packetData = null;
		if (header[2].equals(Integer.toString(Peer.getPeerID())) || Peer.getReclaimedChunks().contains(new Pair<String, Integer> (fileID, chunkNo))
				|| Peer.getChunkPeerInit(fileID) != -1) // avoids storing chunks
			return false;
		else {
			int currentID = Peer.getPeerID();
			int replicationDeg = Integer.parseInt(header[5]);

			Peer.createHashMapEntry(fileID, replicationDeg, Integer.parseInt(header[2]));
			Peer.addPeerToHashmap(fileID, chunkNo, currentID);
			Utils.printHashMap(Peer.getFileStores());

			Peer.getPutchunksReceived().add(new Pair<String, Integer>(header[3], chunkNo));
			String filename = ((Integer) Peer.getPeerID()).toString() + "-" + fileID + "." + header[4] + ".chunk";
			Peer.addToChunksInPeer(header[3], chunkNo);
			FileOutputStream out = new FileOutputStream(filename);
			out.write(chunk);
			out.close();

			byte[] confirmationData = Message.createStoredHeader(header[1], Integer.toString(currentID), fileID,
					chunkNo);
			long timeout = (long) Utils.generateRandomInteger(0, 400);
			Thread.sleep(timeout);
			mcSocket.send(new DatagramPacket(confirmationData, confirmationData.length, Peer.getMCAddress(),
					Peer.getMCPort()));

			return true;
		}
	}
}
