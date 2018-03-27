package sdis1718_t2g02;

import java.util.ArrayList;
import java.util.HashMap;

public class ChunkStoreRecord {
	
	public ChunkStoreRecord(int replicationDeg) {
		super();
		this.peers = new HashMap<Integer,ArrayList<Integer>>();
		this.replicationDeg = replicationDeg;
	}
	public HashMap<Integer, ArrayList<Integer>> peers;
	public int replicationDeg;
	
	
}
