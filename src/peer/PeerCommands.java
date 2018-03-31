package peer;

public class PeerCommands {
	public static final int PEER_NoArgs = 9;
	public static final String ChunksInPeerPathName = "chunksInPeer.data";
	
	public static void printUsage() {
		System.out.println(
				"Usage: java Peer <Protocol_Version> <Server_ID> <Service_Access_Point> <MC_IP_Multicast_Address> <MC_Port> <MDB_IP_Multicast_Address> <MDB_Port> <MDR_IP_Multicast_Address> <MRD_Port>");
	}
}
