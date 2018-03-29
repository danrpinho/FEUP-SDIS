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
import rmi.RMIInterface;
import utils.Utils;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class Peer implements RMIInterface{

	protected static Peer instance;
	protected static String version = null;
	protected static int peerID;
	protected static String accessPoint = null;
	private static ThreadMC MCThread;
	protected static ThreadMDR MDRThread;
	protected static ThreadMDB MDBThread;
	private static int MCPort;
	private static int MDRPort;
	private static int MDBPort;
	
	protected static ConcurrentHashMap<String, ChunkStoreRecord> fileStores = new ConcurrentHashMap<String, ChunkStoreRecord>();

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
		
		if(initRMI(accessPoint) == false) {
			return;
		}
		System.out.println(MCPort);
		setMCThread(new ThreadMC(args[3], MCPort));
		MDRThread = new ThreadMDR(args[5], MDRPort);
		MDBThread = new ThreadMDB(args[7], MDBPort);
		
		launchThreads();
		
		
	}
	
	public static boolean validArgs(String[] args) {
		boolean retValue = true;
		if(args.length != PeerCommands.PEER_NoArgs) 
			retValue = false;
		
		
		else if( (peerID=Utils.validInt(args[1])) <= 0) {
			System.out.println("<Peer_ID> must be an integer greater than 0");
			retValue = false;
		}
		else if((MCPort=Utils.validInt(args[4])) <= 0) {
			System.out.println("<MC_Port> must be an integer");
			retValue = false;
		}
		else if((MDRPort=Utils.validInt(args[6])) <= 0) {
			System.out.println("<MDR_Port> must be an integer");
			retValue = false;
		}
		else if((MDBPort=Utils.validInt(args[8])) <= 0) {
			System.out.println("<MDB_Port> must be an integer");
			retValue = false;
		}				
		else {
			setPeerID(peerID);
			accessPoint = args[2];
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
		
	}
	
	public void restore(String filename) {
		
	}
	
	public void delete(String filename) {
		
	}
	
	public void reclaim(int space) {
		
	}
	
	public void state() {
		
	}
	
	private static void launchThreads() {
		getMCThread().run();
		MDRThread.run();
		MDBThread.run();
	}

	private static void closeThreads() throws IOException {
		getMCThread().close();
		MDRThread.close();
		MDBThread.close();
	}

	public ConcurrentHashMap<String, ChunkStoreRecord> getFileStores() {
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
	public static ThreadMC getMCThread() {
		return MCThread;
	}

	/**
	 * @param mCThread the mCThread to set
	 */
	public static void setMCThread(ThreadMC mCThread) {
		MCThread = mCThread;
	}
}