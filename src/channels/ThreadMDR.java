package channels;

import java.io.IOException;
import java.net.InetAddress;

public class ThreadMDR extends MulticastThread {

	public ThreadMDR(InetAddress address, int port) throws IOException {
		super(address, port);
	}
	
	@Override
	public void run() {
		
	}

}
