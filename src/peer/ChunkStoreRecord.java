package peer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkStoreRecord {
	
	public ChunkStoreRecord(int replicationDeg) {
		super();
		this.peers = new ConcurrentHashMap<Integer,ArrayList<Integer>>();
		this.replicationDeg = replicationDeg;
	}
	
	public ChunkStoreRecord() {
		super();
		this.peers = new ConcurrentHashMap<Integer,ArrayList<Integer>>();
		this.replicationDeg = 0;
	}
	
	public ConcurrentHashMap<Integer, ArrayList<Integer>> peers;
	public int replicationDeg;
	
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
}
