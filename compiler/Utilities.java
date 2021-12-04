/**
 * Utilities class to read byte arrays
 */
import java.io.*;

public class Utilities {

	public static void writeFile(byte[] bytearray, String fileName) {
		
		try {
			FileOutputStream out = new FileOutputStream(fileName);
			out.write(bytearray);
			out.close();
		}//endtry
		catch(IOException e) {
			System.out.println(e.getMessage());
			
		}//endcatch
		
	}//end writeFile
	
}//end class