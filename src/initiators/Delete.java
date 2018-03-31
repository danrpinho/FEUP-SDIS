package initiators;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.security.NoSuchAlgorithmException;

import peer.Message;
import peer.Peer;

public class Delete implements Runnable {
	
	private String fileID = null;
	private MulticastSocket mcSocket = null;
	
	public Delete(File file) {
		try {
			this.fileID = Message.getFileData(file);
		
			mcSocket = new MulticastSocket();
		} catch (Exception e) {
			System.err.println("Error in Delete Constructor: "+e.toString());
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void run() {
		byte[] message = Message.createDeleteHeader(Peer.getVersion(), ((Integer) Peer.getPeerID()).toString(), this.fileID);;
		DatagramPacket packet = new DatagramPacket(message, message.length, Peer.getMCAddress(), Peer.getMCPort());
		try {
			System.out.print("Delete Message will be sent: ");
			System.out.println(message.length);
			System.out.println(new String(message));
			System.out.println(new String(message).indexOf((char) 0x0D));
			mcSocket.send(packet);
		} catch (IOException e) {
			System.err.println("Error in Sending Delete Message: "+e.toString());
			e.printStackTrace();
		}
		
	}

}
