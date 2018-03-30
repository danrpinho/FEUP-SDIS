package utils;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	

}
