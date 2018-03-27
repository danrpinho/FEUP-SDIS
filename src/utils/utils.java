package utils;

import java.io.File;

public class utils {
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
}
