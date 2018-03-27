package peer;

import java.io.IOException;
import java.net.DatagramPacket;

public class ThreadMDB extends MulticastThread {

	public ThreadMDB(String address, String port) throws IOException {
		super(address, port);
	}

	@Override
	public void run() {
		while(true) {
			try {
				DatagramPacket packet = receivePacket(64512);
				String firstWord = getFirstWord(new String(packet.getData(), "UTF-8"));
				if (firstWord.equals("PUTCHUNK"))
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
