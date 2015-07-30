import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class MODBUSRegisterGenerator {

	public static void generateMODBUSRegisterFile(){

		File file = new File("Monitor_Host_Publishers.conf");

		if(!file.exists()) { 
			JOptionPane.showMessageDialog(null, "Please put me in the same directory as the Monitor_Host_Publishers.conf file");
			System.exit(-1);
		}else if (JOptionPane.showConfirmDialog(null, "Click Yes to generate the register file.\nMake sure I am in the same directory as the Monitor_Host_Publishers.conf file")!=JOptionPane.OK_OPTION) {
			System.exit(-1);
		}

		try(Scanner in = new Scanner(file);
				FileWriter out = new FileWriter(new File("modbus_gw.ini"));){

			out.write("[INPUT_REGISTERS]\r\n\r\n");

			int startAddress = 0;

			String eui ="";

			while(in.hasNextLine()) {
				String line = in.nextLine();

				if(line.contains("#"))continue;
				else if(line.contains("["))eui = line.substring(1,line.length()-1);

				else if(line.contains("VARIABLE")) {

					String[] data = line.split(",");

					out.write("REGISTER = "+startAddress+",3,"+eui+",device_variable,"+data[1]+","+data[2]+",2\r\n");
					out.flush();
					startAddress+=3;
				}
			}

			out.write("\r\n\r\n[HOLDING_REGISTERS]");
		}catch (Exception e) {}
	}

	public static void main(String[] args) {
		generateMODBUSRegisterFile();

	}

}
