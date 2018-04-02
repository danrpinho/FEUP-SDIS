package channels;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadTCP implements Runnable {
	
	protected int port;
	protected InetAddress address;
	protected ServerSocket socketTCP;
	
	public ThreadTCP (InetAddress address, int port) throws IOException{
		//this.address = address;
		this.port = port;
		this.socketTCP = new ServerSocket(this.port);
	}
	
	public void close() throws IOException {
		this.socketTCP.close();
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				Socket data;
				data = socketTCP.accept();
				processData(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void processData(Socket socket) throws IOException, InterruptedException {
		DataInputStream input = new DataInputStream(socket.getInputStream());
		byte[] buffer = new byte[input.available()];
		input.readFully(buffer);
		DatagramPacket packet = new DatagramPacket(new byte[buffer.length], buffer.length);
		packet.setData(buffer);
		System.out.println("Calling ThreadMDR.receive");
		ThreadMDR.receive(packet);
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

	public ServerSocket getSocket() {
		return socketTCP;
	}

	public void setSocket(ServerSocket socket) {
		this.socketTCP = socket;
	}

}
