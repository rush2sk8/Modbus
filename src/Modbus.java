import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersResponse;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;

/**
 * Allows people to read data from VR910 Modbus Server
 * 
 * Must configure it according to this.
 * <p><a href="https://github.com/rcandell/tesim_nivis_setup">https://github.com/rcandell/tesim_nivis_setup</a>
 * @author Rushad Antia
 *
 */
public class Modbus {

	public static final int DEFAULT_PORT = 502;

	private TCPMasterConnection masterConnection;
	private Map<String, Map<String, Integer>> sensorMap;

	/**
	 * Creates an instance of MODBUS class
	 * @param gatewayIP - the IP of the modbus server
	 * @param port - port number use <tt>Modbus.DEFAULT_PORT<tt>
	 */
	public Modbus(String gatewayIP, int port) {

		try {		
			masterConnection = new TCPMasterConnection(InetAddress.getByName(gatewayIP));
			masterConnection.setPort(port);
			masterConnection.connect();

		} catch (UnknownHostException e) {
			System.out.println("Invalid IP");
		} catch (Exception e) {
			System.out.println("Cannot Connect");
		}
	}

	/**
	 * Allows you to set the sensor map.
	 * <p>FORMAT: Map< EUI-64, Map< device_variable_name,start_address >>
	 * <p> device_variable_name = { Current, Temp, Humidity, DewPoint, etc... } Can be found in the MCS in the Readings tab under Name
	 * @param map - the map of the sensors used
	 */
	public void setSensorMap(Map<String, Map<String, Integer>> map) {
		sensorMap = map;
	}

	/**
	 * Sets the sensor map based on the 2 files that can be downloaded from the MCS. 
	 * @param modbus_gw - Found under the MODBUS tab in the MCS. "modbus_gw.ini"
	 * @param monitorHost - Found under the Devices tab in the MCS. "Monitor_Host_Publishers.conf"
	 */
	public void setSensorMapFromFilename(String modbus_gw,String monitorHost) {

		sensorMap = new HashMap<String,Map<String, Integer>>();

		try (Scanner modbus = new Scanner(new File(modbus_gw)); Scanner mhp = new Scanner(new File(monitorHost)) ){

			Map<String, ArrayList<String>> monitoring_Host_Publishers = new HashMap<String,ArrayList<String>>();
			String currentEUI_64 = "";

			while(mhp.hasNextLine()) {
				String data = mhp.nextLine();

				if(data.contains("#")||data.isEmpty())
					continue;

				else if (data.contains("[")) {
					currentEUI_64 = data;
					ArrayList<String> keys = new ArrayList<String>();
					monitoring_Host_Publishers.put(currentEUI_64, keys);
				}

				else if(data.contains("VARIABLE"))
					monitoring_Host_Publishers.get(currentEUI_64).add(data);				
			}
			System.out.println(monitoring_Host_Publishers);


			//skips the header of the file
			modbus.nextLine();
//TODO make this work
			while(modbus.hasNextLine()) {
				String data = modbus.nextLine();

				if(!data.contains("REGISTER")||data.contains("HOLDING")||data.contains("gw"))continue;

				String[] line = data.substring(11).split(",");

				String eui_64 = line[2];
				String burstMessage = line[5];
				String startAddress = line[0];


				Map<String, Integer> map = new HashMap<String,Integer>();

				ArrayList<String> list = monitoring_Host_Publishers.get("["+eui_64+"]");
				for(String var:list) {
					String[] varLine = var.split(", ");
					for(String s:varLine)
						System.out.println(s);
				}
			}


		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Returns the current of a sensor through the MODBUS Server on the VR910
	 * 
	 * <p>{START ADDRESS, WORD COUNT, EUI64,REGISTER TYPE,BURST MESSAGE,DEVICE VARIABLE CODE,DEVICE STATE}<p>
	 * 
	 * @param startAddress - the address of the input register
	 * @return the current of a sensor in mA (-1 if error)
	 */
	public float getDataFromInputRegister(int startAddress) {

		ModbusTCPTransaction transaction = new ModbusTCPTransaction(masterConnection);

		try {
			transaction.setRequest(new ReadInputRegistersRequest(startAddress, 3));
			transaction.setReconnecting(true);
			transaction.execute();

			ReadInputRegistersResponse response = (ReadInputRegistersResponse)transaction.getResponse();
			String[] payload = response.getHexMessage().split(" ");

			String currentString = "";

			for(int i=9;i<=12;i++)
				currentString+=payload[i];

			return Float.intBitsToFloat(new Long(Long.parseLong(currentString,16)).intValue());

		} catch (ModbusException e) {
			e.printStackTrace();
		}

		return -1;
	}

	/**
	 * MUST CALL <tt>setSensorMap</tt> before using!!
	 * <p>Allows you to retrieve the data from MODBUS server via a EUI-64 and name of the variable you put in the sensorMap
	 * @param eui_64 - The MAC Address of the sensor delimited by -'s every 2 characters. Can be found in the MCS in the Devices tab
	 * @param key - The name of the variable you want to retrieve. e.g. Current, Temp, Humidity, or whatever you put in the sensor map
	 * @return - The data 
	 */
	public float getDataFromKey(String eui_64,String key) {
		if(sensorMap.containsKey(eui_64)) 
			return getDataFromInputRegister(sensorMap.get(eui_64).get(key));

		else 
			return -1;
	}


	/**TEST LOCATION**/
	public static void main(String[] args) throws Exception {
		Modbus modbus = new Modbus("192.168.0.101", DEFAULT_PORT);
		modbus.setSensorMapFromFilename("modbus_gw.ini","Monitor_Host_Publishers.conf");


	}
}
