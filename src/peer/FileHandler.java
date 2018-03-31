package peer;

public class FileHandler implements Runnable {

	@Override
	public void run() {
		while(true) {
		Peer.writeChunksInPeer();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return;
		}
		}
		
	}

}
