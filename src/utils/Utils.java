package utils;

import java.io.File;

public final class Utils {
	
	private Utils() {}
	
	public static boolean validFilePath(String filePath, File file) {
		file = new File(filePath);
		
		if(file.exists())
			return true;		
		else
			return false;
			
	}
	
	public static boolean validInt(String s_integer, int integer) {
		try {
			integer = Integer.parseInt(s_integer);
		}catch(NumberFormatException e) {
			return false;
		}

		return true;
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

}
