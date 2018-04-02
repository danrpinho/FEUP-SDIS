package channels;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPThread implements Runnable {
	
	protected int port;
	protected InetAddress address;
	protected ServerSocket socketTCP;
	
	TCPThread (InetAddress address, int port) throws IOException{
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
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void processData(Socket data) {
		
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
