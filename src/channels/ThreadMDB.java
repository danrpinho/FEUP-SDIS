package channels;

import java.io.FileNotFoundException;
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
		while (true) {
			try {
				DatagramPacket packet = receivePacket(64512);
				// System.out.println(new String(packet.getData()));
				String protocol = getFirstWord(new String(packet.getData(), "ISO-8859-1"));
				String version = getSecondWord(new String(packet.getData(), "ISO-8859-1"));
				System.out.println("Thread MDB Packet received: " + protocol + ", v" + version);
				if (protocol.equals("PUTCHUNK")) {
					if (version.equals("1")) {
						System.out.println("Starting backup protocol");
						store(packet);
					}
					else if (version.equals("2")) {
						System.out.println("Starting enhanced backup protocol");
						storeEnhanced(packet);
					}
				} else {
					throw new IOException("Invalid packet header!");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
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
				|| Peer.getChunkPeerInit(fileID) == Peer.getPeerID() ) // avoids storing chunks
			return false;
		else {
			int currentID = Peer.getPeerID();
			int replicationDeg = Integer.parseInt(header[5]);

			Peer.createHashMapEntry(fileID, replicationDeg, Integer.parseInt(header[2]), "");
			Peer.addPeerToHashmap(fileID, chunkNo, currentID);
			Utils.printHashMap(Peer.getFileStores());

			Peer.getPutchunksReceived().add(new Pair<String, Integer>(fileID, chunkNo));
			saveFile(Peer.getPeerID(), fileID, (Integer) chunkNo, chunk);
			waitForSomeTime(400);
			sendStoredChunk(header[1], currentID, fileID, chunkNo);
			return true;
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
		
		System.out.println(fileID + "-" + chunkNo + "-" + replicationDeg + "-" + currentID + "-" + header[2]);

		if (header[2].equals(Integer.toString(Peer.getPeerID())) || Peer.getReclaimedChunks().contains(new Pair<String, Integer> (fileID, chunkNo))
				|| Peer.getChunkPeerInit(fileID) == Peer.getPeerID() ) { // avoids storing chunks
			System.out.println("Ignoring owned chunk");
			return false;
		} else {
			
			System.out.println("Starting to process packet for " + fileID + ", " + chunkNo);
			if (Peer.peerStoredChunk(fileID, chunkNo, currentID)) {
				System.out.println("Peer has stored chunk");
				sendStoredChunk(header[1], currentID, fileID, chunkNo);
				return true;
			}
			
			System.out.println("Peer has NOT stored chunk");
			waitForSomeTime(400);

			if (Peer.checkChunkPeers(fileID, chunkNo) >= replicationDeg)
				return false;

			Peer.createHashMapEntry(fileID, replicationDeg, Integer.parseInt(header[2]), "");
			Peer.addPeerToHashmap(fileID, chunkNo, currentID);
			Utils.printHashMap(Peer.getFileStores());
			Peer.getPutchunksReceived().add(new Pair<String, Integer>(header[3], chunkNo));
			saveFile(Peer.getPeerID(), fileID, (Integer) chunkNo, chunk);
			sendStoredChunk(header[1], currentID, fileID, chunkNo);
			return true;
		}
	}

	private void sendStoredChunk(String version, Integer currentID, String fileID, Integer chunkNo) throws IOException {
		System.out.println("Sending STORED...");
		byte[] confirmationData = Message.createStoredHeader(version, Integer.toString(currentID), fileID, chunkNo);
		mcSocket.send(new DatagramPacket(confirmationData, confirmationData.length, Peer.getMCAddress(), Peer.getMCPort()));
	}

	private void waitForSomeTime(int max) throws InterruptedException {
		long timeout = (long) Utils.generateRandomInteger(0, max);
		Thread.sleep(timeout);
	}

	private void saveFile(int peerID, String fileID, Integer chunkNo, byte[] chunk) throws IOException {
		String filename = ((Integer) Peer.getPeerID()).toString() + "-" + fileID + "." + chunkNo + ".chunk";
		Peer.addToChunksInPeer(fileID, chunkNo);
		FileOutputStream out = new FileOutputStream(filename);
		out.write(chunk);
		out.close();
	}
}
