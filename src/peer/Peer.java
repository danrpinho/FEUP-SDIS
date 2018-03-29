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
	protected static String version = null;
	protected static int peerID;
	protected static String accessPoint = null;
	private static ThreadMC MCThread;
	private static ThreadMDR MDRThread;
	private static ThreadMDB MDBThread;
	private static int mcPort;
	private static int mdrPort;
	private static int mdbPort;
	
	
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
		
		if(initRMI(accessPoint) == false) 
			return;
		
		
		
		
		MCThread = new ThreadMC(args[3], mcPort);
		MDRThread = new ThreadMDR(args[5], mdrPort);
		MDBThread = new ThreadMDB(args[7], mdbPort);
		
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
		System.out.println("Backup function called");
		try {
			new Backup(file, repDegree).run();
		}
		catch(Exception e) {
			System.err.println("Backup exception: "+e.toString());
			e.printStackTrace();
		}
		
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
	
	
	/*public static boolean createSockets() {
		try {
			MCSocket = new MulticastSocket(MCPort);
			MDBSocket = new MulticastSocket(MDBPort);
			MDRSocket = new MulticastSocket(MDRPort);
			
		}catch(Exception e) {
			System.out.println("Could not initialize sockets");
			return false;
		}
		return true;
	}*/
	
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
	
	public static int getMDBPort() {
		return mdbPort;
	}
}