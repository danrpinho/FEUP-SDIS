package peer;

import java.io.*;
import java.net.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import rmi.RMIInterface;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class Peer implements RMIInterface{

	protected static Peer instance;
	protected static String version = null;
	private static int peerID;
	protected static String accessPoint = null;
	protected static ThreadMC MCThread;
	protected static ThreadMDR MDRThread;
	protected static ThreadMDB MDBThread;
	
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
		if (args.length != 9) {
			System.out.println(
					"Usage: java Peer <Protocol_Version> <Server_ID> <Service_Access_Point> <MC_IP_Multicast_Address> <MC_Port> <MDB_IP_Multicast_Address> <MDB_Port> <MDR_IP_Multicast_Address> <MRD_Port>");
			return;
		}

		else {
			version = args[0];
			setPeerID(Integer.parseInt(args[1]));
			accessPoint = args[2];
			MCThread = new ThreadMC(args[3], args[4]);
			MDRThread = new ThreadMDR(args[5], args[6]);
			MDBThread = new ThreadMDB(args[7], args[8]);
			
			if(initRMI(accessPoint) == false) {
				return;
			}
			
			launchThreads();
		}
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
		MCThread.run();
		MDRThread.run();
		MDBThread.run();
	}

	private static void closeThreads() throws IOException {
		MCThread.close();
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
}