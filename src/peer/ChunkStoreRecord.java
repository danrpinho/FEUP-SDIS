package peer;

import java.util.ArrayList;
import java.util.HashMap;

public class ChunkStoreRecord {
	
	public ChunkStoreRecord(int replicationDeg) {
		super();
		this.peers = new HashMap<Integer,ArrayList<Integer>>();
		this.replicationDeg = replicationDeg;
	}
	
	public ChunkStoreRecord() {
		super();
		this.peers = new HashMap<Integer,ArrayList<Integer>>();
		this.replicationDeg = 0;
	}
	
	public HashMap<Integer, ArrayList<Integer>> peers;
	public int replicationDeg;
	
	public HashMap<Integer, ArrayList<Integer>> getPeers() {
		return peers;
	}

	public void setPeers(HashMap<Integer, ArrayList<Integer>> peers) {
		this.peers = peers;
	}

	public int getReplicationDeg() {
		return replicationDeg;
	}

	public void setReplicationDeg(int replicationDeg) {
		this.replicationDeg = replicationDeg;
	}	
}
