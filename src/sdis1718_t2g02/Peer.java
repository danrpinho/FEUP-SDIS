package sdis1718_t2g02;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class Peer {
		
		protected static Peer instance;
		protected static String version = null;
		protected static int peerID;
		protected static String accessPoint = null;
		protected static ThreadMC MCThread;
		protected static ThreadMDR MDRThread;
		protected static ThreadMDB MDBThread;
//		protected static InetAddress MC_address = null;
//		protected static InetAddress MDB_address = null;
//		protected static InetAddress MDR_address = null;
//		protected static int MC_port;
//		protected static int MDB_port;
//		protected static int MDR_port;
//		protected static MulticastSocket MC_socket = null;
//		protected static MulticastSocket MDB_socket = null;
//		protected static MulticastSocket MDR_socket = null;
		protected static ConcurrentHashMap<String, StoreRecord> fileStores = new ConcurrentHashMap<String, StoreRecord>();
		
		public static Peer getInstance() {
			if(instance == null) {
				instance = new Peer();
			}
			
			return instance;
		}
		
		public static void main(String[] args) throws IOException {
			getInstance();
			if(args.length != 9) {
				System.out.println("Usage: java Peer <Protocol_Version> <Server_ID> <Service_Access_Point> <MC_IP_Multicast_Address> <MC_Port> <MDB_IP_Multicast_Address> <MDB_Port> <MDR_IP_Multicast_Address> <MRD_Port>");
				return;
			}
			
			else {
				version = args[0];
				peerID = Integer.parseInt(args[1]);
				accessPoint = args[2];
				MCThread = new ThreadMC(args[3],args[4]);
				MDRThread = new ThreadMDR(args[5],args[6]);
				MDBThread = new ThreadMDB(args[7], args[8]);
				launchThreads();
			}
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
		
		public ConcurrentHashMap<String, StoreRecord> getFileStores(){
			return fileStores;
		}
		
		public  static void setFileStores(ConcurrentHashMap<String, StoreRecord> hashmap) {
			fileStores = hashmap;
		}
}