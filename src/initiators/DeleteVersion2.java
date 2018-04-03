package initiators;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import peer.Message;
import peer.Peer;
import utils.Utils;

public class DeleteVersion2 implements Runnable {
	
	
	private MulticastSocket mcSocket = null;
	
	public DeleteVersion2() {
		try {
			
		
			mcSocket = new MulticastSocket();
		} catch (Exception e) {
			System.err.println("Error in Delete Constructor: "+e.toString());
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void run() {
		System.out.println("Delete version 2 is running");
		long timeout = Utils.generateRandomInteger(0, 5000);
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {
			System.err.println("Error in Sleeping Thread: "+e.toString());
			e.printStackTrace();
		}
		Iterator<Entry<String, ArrayList<Integer>>> peersToBeDeletedIt = Peer.getPeersToBeDeleted().entrySet().iterator();
		while(peersToBeDeletedIt.hasNext()) {
			Map.Entry<String, ArrayList<Integer>> pair = (Entry<String, ArrayList<Integer>>) peersToBeDeletedIt.next();
			if(pair.getValue().size() > 0) {
				byte[] message = Message.createDeleteHeader(Peer.getVersion(), ((Integer) Peer.getPeerID()).toString(), pair.getKey());
				DatagramPacket packet = new DatagramPacket(message, message.length, Peer.getMCAddress(), Peer.getMCPort());
				try {
					System.out.print("Delete Message will be sent: ");
					System.out.println(message.length);
					System.out.println(new String(message));
					mcSocket.send(packet);
				} catch (IOException e) {
					System.err.println("Error in Sending Delete Message: "+e.toString());
					e.printStackTrace();
				}
			}
		}
		
	}

}