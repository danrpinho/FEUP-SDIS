package client;

public class ClientCommands {
	public static final String BACKUP = "BACKUP";
	public static final String RESTORE = "RESTORE";
	public static final String DELETE = "DELETE";
	public static final String RECLAIM = "RECLAIM";
	public static final String STATE = "STATE";
	
	public static final int BACKUP_NoArgs = 4;
	public static final int RESTORE_NoArgs = 3;
	public static final int DELETE_NoArgs = 3;
	public static final int RECLAIM_NoArgs = 3;
	public static final int STATE_NoArgs = 2;
	
	public static final String BACKUP_Usage = "java Client <peer_ap> BACKUP <File_Path> <Replication_Degree>"; 
	public static final String RESTORE_Usage = "java Client <peer_ap> RESTORE <File_Path>"; 
	public static final String DELETE_Usage = "java Client <peer_ap> DELETE <File_Path>"; 
	public static final String RECLAIM_Usage = "java Client <peer_ap> RECLAIM <space>"; 
	public static final String STATE_Usage = "java Client <peer_ap> STATE";

	public static void printUsage() {
		System.out.println(BACKUP_Usage);
		System.out.println(RESTORE_Usage);
		System.out.println(DELETE_Usage);
		System.out.println(RECLAIM_Usage);
		System.out.println(STATE_Usage);
	}
}
