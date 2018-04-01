package initiators;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import peer.Message;

public class Reclaim implements Runnable{
	
	private String fileID;
	
	public Reclaim(File file) {
		try {
			this.fileID = Message.getFileData(file);
		} catch (NoSuchAlgorithmException | IOException e) {
			System.err.println("Error in Delete Constructor: "+e.toString());
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		
		
	}

}
