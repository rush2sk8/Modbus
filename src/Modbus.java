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

	private boolean hasCalledSensorMap;
	private TCPMasterConnection masterConnection;
	private Map<String, Map<String, String>> sensorMap;

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
	public void setSensorMap(Map<String, Map<String, String>> map) {
		sensorMap = map;
		hasCalledSensorMap = true;
	}

 

	/**
	 * Returns the current of a sensor through the MODBUS Server on the VR910
	 * <p> The device state must be a 2
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

			for(int i=11;i<=14;i++)
				currentString+=payload[i];

			return Utils.hexToFloat(currentString);

		} catch (ModbusException e) {
			e.printStackTrace();
		}

		return -1;
	}

 

	/**TEST LOCATION**/
	public static void main(String[] args) throws Exception {
		Modbus modbus = new Modbus("192.168.0.101", DEFAULT_PORT);
 System.out.println(modbus.getDataFromInputRegister(0));
	}
}
