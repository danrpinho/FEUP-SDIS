package utils;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

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
	
	

}
