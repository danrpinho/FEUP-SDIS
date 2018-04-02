package channels;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;

import peer.Message;
import peer.Peer;


public class ThreadMDR extends MulticastThread {

	public ThreadMDR(InetAddress address, int port) throws IOException {
		super(address, port);
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				DatagramPacket packet = receivePacket(64512);
				System.out.print("Thread MDB Packet received: ");
				System.out.println(new String(packet.getData()));
				String firstWord = getFirstWord(new String(packet.getData(), "ISO-8859-1"));
				if (firstWord.equals("CHUNK")) {
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
		Peer.getInstance().incrementMdrPacketsReceived();
		if(Peer.getInstance().getCurrentRestore() == null)
			return false;
		
		String[] packetData = new String(packet.getData(), "ISO-8859-1").split(Message.endHeader, 2);
		byte[] chunk = packetData[1].getBytes();
		String[] header = packetData[0].split(" ");
		packetData = null;
		
		if (header[2].equals(Integer.toString(Peer.getInstance().getPeerID()))) // avoids storing chunks
			return false;
		
		int currentID = Peer.getInstance().getPeerID();
		int chunkNo = Integer.parseInt(header[4]);
		
		//checks target chunk
		if (!(Peer.getInstance().getCurrentRestore().getFileID().equals(header[3]) && 
				chunkNo == Peer.getInstance().getCurrentRestore().getChunkNo()))
			return false;
		
		Peer.getInstance().getCurrentRestore().setReceived(true);
		
//		// checks if peer had already stored target chunk
//		if (Peer.peerStoredChunk(header[3], chunkNo, currentID)) {
//			return false;
//		}

		//saves chunk
		String filename = ((Integer) currentID).toString() + "-" + header[3] + "." + header[4] + ".chunk";
		Peer.addToChunksInPeer(header[3], chunkNo);
		FileOutputStream out = new FileOutputStream(filename);
		out.write(chunk);
		out.close();

		return true;

	}

}
