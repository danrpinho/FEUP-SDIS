package utils;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import peer.ChunkStoreRecord;
import peer.Peer;

public final class Utils {
	
	private Utils() {}
	
	public static File validFilePath(String filePath) {
		File file = new File(filePath);
		
		if(file.exists())
			return file;		
		else
			return null;
			
	}
	
	public static int validInt(String s_integer) {
			int integer = -1;
		try {
			integer = Integer.parseInt(s_integer);
		}catch(NumberFormatException e) {
			return -1;
		}

		return integer;
	}
	
	/**
	 * @brief Encodes a byte array to a String representation of their hexadecimal
	 *        representations.
	 * @param data
	 * @return
	 */
	public static String encodeByteArray(byte[] data) {
		StringBuilder sb = new StringBuilder();
		for (byte b : data) {
			sb.append(String.format("%02X", b));
		}
		return sb.toString();
	}
	
	public static String getFirstWord(String data) {
		String[] stringArray = data.split(" ");
		return stringArray[0];
	}
	
	public static int generateRandomInteger(int min, int max) {
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}
	
	/*public String replaceWithPattern(String str,String replace){
        
        Pattern ptn = Pattern.compile("\\s+");
        Matcher mtch = ptn.matcher(str);
        return mtch.replaceAll(replace);
    }*/
	
	/*public static boolean peerStoredChunk(String fileID, Integer chunkNo, Integer peerID) {
		if (checkChunkPeers(fileID, chunkNo) <= 0) {
			return false;
		} else {
			ConcurrentHashMap<String, ChunkStoreRecord> hashmap = Peer.getFileStores();
			return hashmap.get(fileID).peers.get(chunkNo).contains(peerID);
		}
	}
	
	public static int checkChunkPeers(String fileID, Integer chunkNo) {
		ConcurrentHashMap<String, ChunkStoreRecord> hashmap = Peer.getFileStores();
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
		ConcurrentHashMap<String, ChunkStoreRecord> hashmap = Peer.getFileStores();
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
	}*/
	
	public static void printHashMap(ConcurrentHashMap<String, ChunkStoreRecord> hash) {
		System.out.println("Print HashMap: ");
		for (String name: hash.keySet()){

           
            System.out.print(name);  
            System.out.println(": ");
            
            ConcurrentHashMap<Integer,ArrayList<Integer>> hash2 = hash.get(name).peers;
            
            
            
            for(int name2: hash2.keySet()) {
            	ArrayList<Integer> arr = hash2.get(name2);
            	System.out.print(name2);System.out.print(" { ");
            	for(int i : arr) {
            		System.out.print(i); System.out.print(" ");
            	}
            	System.out.println("}");
            }
           
         }
	}
	
	public static void printChunksInPeer(ConcurrentHashMap<String, ArrayList<Integer> > hash) {
		System.out.println("Print HashMap: ");
		for (String name: hash.keySet()){                  
           
            	ArrayList<Integer> arr = hash.get(name);
            	System.out.print(name);System.out.print(" { ");
            	for(int i : arr) {
            		System.out.print(i); System.out.print(" ");
            	}
            	System.out.println("}");
            
           
         }
	}


}
