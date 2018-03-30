package channels;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Random;

import initiators.Backup;
import peer.Message;
import peer.Peer;
import utils.Utils;

import java.util.Random;

public class ThreadMDB extends MulticastThread {
	
	private MulticastSocket mcSocket = null;

	public ThreadMDB(InetAddress address, int port) throws IOException {
		super(address, port);
		mcSocket = new MulticastSocket();
		System.out.println("Thread MDB constructor");
	}

	@Override
	public void run() {
		System.out.println("Thread mdb run");
		while(true) {
			try {
				DatagramPacket packet = receivePacket(64512);
				System.out.print("MDB Thread Packet received: ");
				System.out.println(new String(packet.getData()));
				String firstWord = getFirstWord(new String(packet.getData(), "UTF-8"));
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
		String[] packetData = new String(packet.getData(), "UTF-8").split(Message.endHeader);
		byte[] chunk = packetData[1].getBytes();
		String[] header = packetData[0].split(" ");
		packetData = null;
		if (header[2].equals(Integer.toString(Peer.getInstance().getPeerID()))) // avoids storing chunks
			return false;
		
		int currentID = Peer.getInstance().getPeerID();
		int chunkNo = Integer.parseInt(header[4]);
		int replicationDeg = Integer.parseInt(header[5]);
		
		if (Peer.getInstance().getFileStores().contains(header[3]) &&
			Peer.getInstance().getFileStores().get(header[3]).peers.containsKey(chunkNo) &&
			Peer.getInstance().getFileStores().get(header[3]).peers.get(chunkNo).contains(currentID)) {
			return true;
		}
				
		String filename = header[2] + "-" + header[3] + "." + header[4] + ".chunk";
		FileOutputStream out = new FileOutputStream(filename);
		out.write(chunk);
		out.close();
		
		byte[] confirmationData = Message.createStoredHeader(header[1], Integer.toString(currentID), header[3], chunkNo);
		long timeout  = (long) Utils.generateRandomInteger(0, 400);
		Thread.sleep(timeout);
		System.out.println("Stored message sent");
		mcSocket.send(new DatagramPacket(confirmationData, confirmationData.length,Peer.getMCAddress(), Peer.getMCPort()));
		
		// TODO adicionar à hashtable se não estiver presente
		return true;
	}
}
