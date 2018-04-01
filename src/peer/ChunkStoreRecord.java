package peer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkStoreRecord implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ConcurrentHashMap<Integer, ArrayList<Integer>> peers;
	private int replicationDeg;
	private int peerInit;
	
	public ChunkStoreRecord(int replicationDeg) {
		super();
		this.peers = new ConcurrentHashMap<Integer,ArrayList<Integer>>();
		this.replicationDeg = replicationDeg;
		this.peerInit = 0;
	}
	
	public ChunkStoreRecord() {
		super();
		this.peers = new ConcurrentHashMap<Integer,ArrayList<Integer>>();
		this.replicationDeg = 0;
		this.peerInit = 0;
	}
	
	public ChunkStoreRecord(int replicationDeg, int peerInit) {
		super();
		this.peers = new ConcurrentHashMap<Integer,ArrayList<Integer>>();
		this.replicationDeg = replicationDeg;
		this.peerInit = peerInit;
	}
	
	
	
	
	public ConcurrentHashMap<Integer, ArrayList<Integer>> getPeers() {
		return peers;
	}

	public void setPeers(ConcurrentHashMap<Integer, ArrayList<Integer>> peers) {
		this.peers = peers;
	}

	public int getReplicationDeg() {
		return replicationDeg;
	}

	public void setReplicationDeg(int replicationDeg) {
		this.replicationDeg = replicationDeg;
	}	
	
	public int getPeerInit(){
		return peerInit;
	}
	
	 private void readObject( ObjectInputStream aInputStream) throws ClassNotFoundException, IOException {
		     //always perform the default de-serialization first
		     aInputStream.defaultReadObject();

		     
		  }

		    /**
		    * This is the default implementation of writeObject.
		    * Customise if necessary.
		    */
		    private void writeObject(ObjectOutputStream aOutputStream) throws IOException {
		      //perform the default serialization for all non-transient, non-static fields
		      aOutputStream.defaultWriteObject();
		    }
}
