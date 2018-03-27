package client;

import java.io.File;

import utils.utils;

public class Client {
	protected static File file;

	public static void main(String[] args) {
		showMainMenu();
		if(!validArgs(args))
			return;
	}

	private static void showMainMenu() {
		System.out.println("SDIS1718-T2G02 TESTING CLIENT");
		System.out.println("  - ");
	}
	
	private static boolean validArgs(String[] args) {
		if(args.length < 2) {
			ClientCommands.printUsage();
		}
		
		boolean retValue = true;
		
		if(args[1].equals(ClientCommands.BACKUP)) {
			
			int repDegree=-1;
			
			if(args.length != ClientCommands.BACKUP_NoArgs) 
				retValue = false;		
			else if(!utils.validFilePath(args[2], file))
				retValue = false;						
			else if(!utils.validInt(args[3], repDegree))
				retValue = false;			
			else if(repDegree < 1 || repDegree > 9) {
				System.out.println("The replication degree parameter must be a value between 1 and 9");
				retValue = false;
			}
			
			if(retValue == false)
				System.out.println(ClientCommands.BACKUP_Usage);
			
			return retValue;
		}
		
		else if(args[1].equals(ClientCommands.RESTORE)) {
			
			if(args.length != ClientCommands.RESTORE_NoArgs)
				retValue = false;
			
			
			if(retValue == false)
				System.out.println(ClientCommands.RESTORE_Usage);
			
			return retValue;
		}
		
		else if(args[1].equals(ClientCommands.DELETE)) {
			
			if(args.length != ClientCommands.DELETE_NoArgs)
				retValue = false;
			
			if(retValue == false)
				System.out.println(ClientCommands.DELETE_Usage);
			
			return retValue;			
		}
		
		else if(args[1].equals(ClientCommands.RECLAIM)) {
			if(args.length != ClientCommands.RECLAIM_NoArgs)
				retValue = false;
			
			if(retValue == false)
				System.out.println(ClientCommands.RECLAIM_Usage);
			
			return retValue;
		}
		
		else
			return false;
		
		
	}
	
	
	

}
