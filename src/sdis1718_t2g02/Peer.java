package sdis1718_t2g02;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class Peer {
	
		protected static String version = null;
		protected static int peerID;
		protected static String accessPoint = null;
		protected static InetAddress MC_address = null;
		protected static InetAddress MDB_address = null;
		protected static InetAddress MDR_address = null;
		protected static int MC_port;
		protected static int MDB_port;
		protected static int MDR_port;
		protected static MulticastSocket MC_socket = null;
		protected static MulticastSocket MDB_socket = null;
		protected static MulticastSocket MDR_socket = null;
		
		public static void main(String[] args) throws IOException {
			if(args.length != 9) {
				System.out.println("Usage: java Peer <Protocol_Version> <Server_ID> <Service_Access_Point> <MC_IP_Multicast_Address> <MC_Port> <MDB_IP_Multicast_Address> <MDB_Port> <MDR_IP_Multicast_Address> <MRD_Port>");
				return;
			}
			
			else {
				version = args[0];
				peerID = Integer.parseInt(args[1]);
				accessPoint = args[2];
				MC_address = InetAddress.getByName(args[3]);
				MC_port = Integer.parseInt(args[4]);
				MDB_address = InetAddress.getByName(args[5]);
				MDB_port = Integer.parseInt(args[6]);
				MDR_address = InetAddress.getByName(args[7]);
				MDR_port = Integer.parseInt(args[8]);
				
				MC_socket = new MulticastSocket(MC_port);
				MC_socket.joinGroup(MC_address);
				
				MDB_socket = new MulticastSocket(MDB_port);
				MDB_socket.joinGroup(MDB_address);
				
				MDR_socket = new MulticastSocket(MDR_port);
				MDR_socket.joinGroup(MDR_address);
				
			}
		}
		
		public static void close() throws IOException {
			MC_socket.leaveGroup(MC_address);
			MC_socket.close();
			
			MDB_socket.leaveGroup(MC_address);
			MDB_socket.close();
			
			MDR_socket.leaveGroup(MC_address);
			MDR_socket.close();
		}	
		
		
}