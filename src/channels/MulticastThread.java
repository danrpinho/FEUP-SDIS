package channels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public abstract class MulticastThread implements Runnable {
	
	protected int port;
	protected InetAddress address;
	protected MulticastSocket socket;
	
	MulticastThread(String address, int port) throws IOException{
		this.address = InetAddress.getByName(address);
		this.port = port;
		this.socket = new MulticastSocket(this.port);
		this.socket.joinGroup(this.address);
	}
	
	public void close() throws IOException {
		this.socket.leaveGroup(this.address);
		this.socket.close();
	}
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
	}

	public MulticastSocket getSocket() {
		return socket;
	}

	public void setSocket(MulticastSocket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
	}
	
	protected String getFirstWord(String data) {
		String[] stringArray = data.split(" ");
		return stringArray[0];
	}
	
	protected DatagramPacket receivePacket(int bufferSize) throws IOException {
		byte[] rbuf = new byte[bufferSize];
		DatagramPacket packet = new DatagramPacket(rbuf, rbuf.length);
		socket.receive(packet);
		return packet;
	}

}
