package channels;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class TCPThread implements Runnable {
	
	protected int port;
	protected InetAddress address;
	protected ServerSocket socket;
	
	TCPThread (InetAddress address, int port) throws IOException{
		this.address = address;
		this.port = port;
		this.socket = new ServerSocket(this.port);
	}
	
	public void close() throws IOException {
		this.socket.close();
	}
	
	@Override
	public void run() {
		

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
		return socket;
	}

	public void setSocket(ServerSocket socket) {
		this.socket = socket;
	}

}
