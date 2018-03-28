package channels;

import java.io.IOException;

public class ThreadMDR extends MulticastThread {

	public ThreadMDR(String address, int port) throws IOException {
		super(address, port);
	}
	
	@Override
	public void run() {
		
	}

}
