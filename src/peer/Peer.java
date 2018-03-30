package peer;

import java.io.*;
import java.net.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import channels.ThreadMC;
import channels.ThreadMDB;
import channels.ThreadMDR;
import initiators.Backup;
import rmi.RMIInterface;
import utils.Utils;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class Peer implements RMIInterface{

	protected static Peer instance;
	private static String version = null;
	private static int peerID;
	private static String accessPoint = null;
	private static ThreadMC MCThread;
	private static ThreadMDR MDRThread;
	private static ThreadMDB MDBThread;
	private static int mcPort;
	private static int mdrPort;
	private static int mdbPort;
	private static InetAddress mcAddress;
	private static InetAddress mdrAddress;
	private static InetAddress mdbAddress;
	
	
	private static ConcurrentHashMap<String, ChunkStoreRecord> fileStores = new ConcurrentHashMap<String, ChunkStoreRecord>();

	public static Peer getInstance() {
		if (instance == null) {
			instance = new Peer();
		}

		return instance;
	}
	
	public Peer() {};
	
	public static void main(String[] args) throws IOException {
		getInstance();		
		
		if(!validArgs(args))
			return;
		
		if(initRMI(accessPoint) == false) 
			return;
		
		
		
		
		MCThread = new ThreadMC(mcAddress, mcPort);
		MDRThread = new ThreadMDR(mdrAddress, mdrPort);
		MDBThread = new ThreadMDB(mdbAddress, mdbPort);
		
		launchThreads();
		
		
	}
	
	public static boolean validArgs(String[] args) throws UnknownHostException {
		boolean retValue = true;
		if(args.length != PeerCommands.PEER_NoArgs) 
			retValue = false;
		
		
		else if( (peerID=Utils.validInt(args[1])) <= 0) {
			System.out.println("<Peer_ID> must be an integer greater than 0");
			retValue = false;
		}
		else if((mcPort=Utils.validInt(args[4])) <= 0) {
			System.out.println("<MC_Port> must be an integer");
			retValue = false;
		}
		else if((mdrPort=Utils.validInt(args[6])) <= 0) {
			System.out.println("<MDR_Port> must be an integer");
			retValue = false;
		}
		else if((mdbPort=Utils.validInt(args[8])) <= 0) {
			System.out.println("<MDB_Port> must be an integer");
			retValue = false;
		}				
		else {
			version = args[0];
			accessPoint = args[2];
			mcAddress = InetAddress.getByName(args[3]);
			mdrAddress = InetAddress.getByName(args[5]);
			mdbAddress = InetAddress.getByName(args[7]);
		}
		
		
		if(retValue == false)
			PeerCommands.printUsage();
		
		
		return retValue;
		
	}
	
	public static boolean initRMI(String accessPoint) {
		try {
			Peer obj = new Peer();
			RMIInterface stub = (RMIInterface) UnicastRemoteObject.exportObject(obj, 0);
		
			Registry registry = LocateRegistry.getRegistry();
			registry.bind(accessPoint, stub);
			
			System.err.println("Peer Ready");
		} catch(Exception e) {
			System.err.println("Peer exception: "+e.toString());
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public void backup(File file, int repDegree) {
		System.out.println("Backup function called");
		try {
			new Backup(file, repDegree).run();
		}
		catch(Exception e) {
			System.err.println("Backup exception: "+e.toString());
			e.printStackTrace();
		}
		
	}
	
	public void restore(File filename) {
		
	}
	
	public void delete(File filename) {
		
	}
	
	public void reclaim(int space) {
		
	}
	
	public void state() {
		
	}
	
	private static void launchThreads() {
		
		System.out.println("Launch threads");
		(new Thread(MCThread)).start();
		(new Thread(MDRThread)).start();
		(new Thread(MDBThread)).start();
	}

	private static void closeThreads() throws IOException {

		MCThread.close();
		MDRThread.close();
		MDBThread.close();
	}

	public static ConcurrentHashMap<String, ChunkStoreRecord> getFileStores() {
		return fileStores;
	}

	public static void setFileStores(ConcurrentHashMap<String, ChunkStoreRecord> hashmap) {
		fileStores = hashmap;
	}

	public void createHashMapEntry(String fileID, int replicationDeg) {
		if (!fileStores.containsKey(fileID)) {
			ChunkStoreRecord record = new ChunkStoreRecord(replicationDeg);
			fileStores.put(fileID, record);
		}
	}

	/**
	 * @return the peerID
	 */
	public static int getPeerID() {
		return peerID;
	}

	/**
	 * @param peerID the peerID to set
	 */
	public static void setPeerID(int peerID) {
		Peer.peerID = peerID;
	}

	public static String getVersion() {
		return version;
	}

	public static void setVersion(String version) {
		Peer.version = version;
	}
	
	
	/**
	 * @return the mCThread
	 */
	/*public static ThreadMC getMCThread() {
		return MCThread;
	}*/

	/**
	 * @param mCThread the mCThread to set
	 */
	/*public static void setMCThread(ThreadMC mCThread) {
		MCThread = mCThread;
	}*/
	
	public static int getMCPort() {
		return mcPort;
	}
	
	public static int getMDBPort() {
		return mdbPort;
	}
	
	public static int getMDRPort() {
		return mdrPort;
	}
	
	public static InetAddress getMCAddress() {
		return mcAddress;
	}
	
	public static InetAddress getMDBAddress() {
		return mdbAddress;
	}
	
	public static InetAddress getMDRAddress() {
		return mdrAddress;
	}
	
	
	
	public static boolean peerStoredChunk(String fileID, Integer chunkNo, Integer peerID) {
		if (checkChunkPeers(fileID, chunkNo) <= 0) {
			return false;
		} else {
			ConcurrentHashMap<String, ChunkStoreRecord> hashmap = getFileStores();
			return hashmap.get(fileID).peers.get(chunkNo).contains(peerID);
		}
	}
	
	public static int checkChunkPeers(String fileID, Integer chunkNo) {
		ConcurrentHashMap<String, ChunkStoreRecord> hashmap = getFileStores();
		if (hashmap.contains(fileID)) {
			if (hashmap.get(fileID).peers.containsKey(chunkNo)) {
				return hashmap.get(fileID).peers.get(chunkNo).size();
			} else {
				return -2;	//file exists in hashmap, but not the chunk
			}
		} else {	//file does not exist in hashmap
			return -1;
		}
	}
	
	public static boolean addPeerToHashmap(String fileID, Integer chunkNo, Integer peerID) {
		int chunkStatus = checkChunkPeers(fileID, chunkNo);
		ConcurrentHashMap<String, ChunkStoreRecord> hashmap = getFileStores();
		ChunkStoreRecord record = new ChunkStoreRecord();
		ArrayList<Integer> peers = new ArrayList<Integer>();
		
		switch(chunkStatus) {
		case -1:	//new fileID
			break;
		case -2:	//new chunkNo
			record = hashmap.get(fileID);
			break;
		default:	//chunkNo exists
			record = hashmap.get(fileID);
			peers = record.peers.get(chunkNo);
			if(peers.contains(peerID))
				return false;
		}
		
		peers.add(peerID);	
		record.peers.put(chunkNo, peers);
		hashmap.put(fileID, record);
		Peer.setFileStores(hashmap);
		
		return true;
	}
}