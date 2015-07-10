import java.net.InetAddress;
import java.net.UnknownHostException;

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
	 * Returns the current of a sensor through the MODBUS Server on the VR910
	 * <p> The device state must be a 2 otherwise it will not work 
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

		} catch (ModbusException|IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		return -1;
	}



	/**TEST LOCATION**/
	public static void main(String[] args) throws Exception {
		Modbus modbus = new Modbus("192.168.1.101", 502);
	/*	System.out.println("00-1B-1E-F8-76-02-22-DD Current: "+ modbus .getDataFromInputRegister(0));
		System.out.println("00-1B-1E-F8-76-02-22-DD Temp: "+ modbus.getDataFromInputRegister(3));
		System.out.println("00-1B-1E-F8-76-02-22-DD Humidity: "+ modbus.getDataFromInputRegister(6));
		System.out.println("00-1B-1E-F8-76-02-22-DD DewPoint: "+ modbus.getDataFromInputRegister(9));
		
		System.out.println("00-1B-1E-F8-76-02-22-DF Current: "+ modbus.getDataFromInputRegister(12));
		System.out.println("00-1B-1E-F8-76-02-22-DF Temp: "+ modbus.getDataFromInputRegister(15));
		System.out.println("00-1B-1E-F8-76-02-22-DF Humidity: "+ modbus.getDataFromInputRegister(18));
		System.out.println("00-1B-1E-F8-76-02-22-DF DewPoint: "+ modbus.getDataFromInputRegister(21));*/
		System.out.println(modbus.getDataFromInputRegister(24));
	}
}
