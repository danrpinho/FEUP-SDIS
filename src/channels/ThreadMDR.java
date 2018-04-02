package channels;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;

import peer.Message;
import peer.Peer;
import utils.Utils;


public class ThreadMDR extends MulticastThread {

	public ThreadMDR(InetAddress address, int port) throws IOException {
		super(address, port);
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				DatagramPacket packet = receivePacket(64512);
				//System.out.println(new String(packet.getData()));
				String protocol = Utils.getFirstWord(new String(packet.getData(), "ISO-8859-1"));
				System.out.println("Thread MDB Packet received: " + protocol);
				if (protocol.equals("CHUNK")) {
					receive(packet);
				} else {
					throw new IOException("Invalid packet header!");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}	
	
	public boolean receive(DatagramPacket packet) throws UnsupportedEncodingException, IOException, InterruptedException {
		Peer.incrementMdrPacketsReceived();
		if(Peer.getCurrentRestore() == null)
			return false;
		
		System.out.println("receiving packet");
		byte[] data = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
		String[] packetData = new String(data, "ISO-8859-1").split(Message.endHeader, 2);
		System.out.println("data length = " + data.length);
		byte[] chunk = packetData[1].getBytes("ISO-8859-1");
		String[] header = packetData[0].split(" ");
		packetData = null;
		
		if (header[2].equals(Integer.toString(Peer.getPeerID()))) // avoids storing own chunks
			return false;
		
		int currentID = Peer.getPeerID();
		int chunkNo = Integer.parseInt(header[4]);
		
		//checks target chunk
		if (!(Peer.getCurrentRestore().getFileID().equals(header[3]) && chunkNo == Peer.getCurrentRestore().getChunkNo()))
			return false;
		
		Peer.getCurrentRestore().setReceived(true);

		//saves chunk
		String filename = ((Integer) currentID).toString() + "-" + header[3] + "." + header[4] + ".chunk";
		File fileOut = new File(filename);
		Peer.addToChunksInPeer(header[3], chunkNo);
		FileOutputStream out = new FileOutputStream(fileOut);
		out.write(chunk);
		out.close();

		return true;

	}

}
