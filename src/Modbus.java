import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersResponse;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;

/**
 * Allows people to read data from VR910 Modbus Server
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
			return;
		} catch (Exception e) {
			System.out.println("Cannot Connect");
		}

	}


	/**
	 * Returns the current of a sensor through the MODBUS Server on the VR910
	 * @param startAddress - the address of the input register
	 * @return the current of a sensor in mA (-1 if error)
	 */
	public float getCurrentFromInputRegister(int startAddress) {

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


	public static void main(String[] args) throws Exception {
		Modbus modbus = new Modbus("192.168.0.101", DEFAULT_PORT);//.getCurrentFromInputRegister(0));

		while(true) {
			Thread.sleep(1000);
			System.out.println(modbus.getCurrentFromInputRegister(0));
		}

	}
}
