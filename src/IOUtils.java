import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

 

public class IOUtils {

	/**
	 * 
	 * @return - an arraylist of each line of the file
	 */
	public static ArrayList<String> readFile(String fileName){
		ArrayList<String> toReturn = new ArrayList<String>();

		try(Scanner in = new Scanner(new File(fileName))){

			while(in.hasNextLine())
				toReturn.add(in.nextLine());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return toReturn;
	}

}
