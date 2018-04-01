package initiators;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;

import javafx.util.Pair;
import peer.Message;
import peer.Peer;

public class Reclaim implements Runnable{
	
	private String fileID;
	private Vector<Pair <String, Integer>> filesDeleted;
	private MulticastSocket mcSocket;
	
	public Reclaim(File file, Vector<Pair <String, Integer>> filesDeleted) {
		this.filesDeleted = filesDeleted;
		try {
			this.fileID = Message.getFileData(file);
			mcSocket = new MulticastSocket();
		} catch (NoSuchAlgorithmException | IOException e) {
			System.err.println("Error in Reclaims Constructor: "+e.toString());
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		for(int i=0; i < filesDeleted.size(); i++) {
			byte[] message = Message.createRemovedHeader(Peer.getVersion(), ((Integer) Peer.getPeerID()).toString(), filesDeleted.elementAt(i).getKey(), filesDeleted.elementAt(i).getValue());
			DatagramPacket packet = new DatagramPacket(message, message.length, Peer.getMCAddress(), Peer.getMCPort());
			try {
				mcSocket.send(packet);
			} catch (IOException e) {
				System.err.println("Error in Delete Constructor: "+e.toString());
				e.printStackTrace();
			}
		}
		
	}
	
	

}
