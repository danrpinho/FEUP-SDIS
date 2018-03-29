package initiators;

import java.io.File;
import java.io.IOException;
import java.net.MulticastSocket;
import java.security.NoSuchAlgorithmException;

import peer.Message;

public class Restore implements Runnable {

//	public Restore() {
//	}
	public void run() {
		
	}

	public boolean restore(MulticastSocket mdrSocket, File file) throws NoSuchAlgorithmException, IOException {
		String fileData = Message.getFileData(file);
		
		return false;
	}
	
}
