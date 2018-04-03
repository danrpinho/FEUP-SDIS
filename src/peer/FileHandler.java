package peer;


public class FileHandler implements Runnable {

	@Override
	public void run() {
		while(true) {
		Peer.writeChunksInPeer();
		Peer.writeFileStores();
		Peer.writePeersToBeDeleted();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			
			return;
		}
		}
		
	}

}
