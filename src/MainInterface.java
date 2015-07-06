import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class MainInterface extends ApplicationFrame {

	private static final long serialVersionUID = -3118738086863395089L;

	//holds data of all added sensors
	private Map<String, ArrayList<XYDataItem>> data;

	private Map<String, ChartPanel> sensorPanels;

	private FileWriter out;

	private Modbus modbus;

	public MainInterface(String ipString) {
		super("NIVIS Data Visualizer");

		data = new HashMap<String, ArrayList<XYDataItem>>();
		sensorPanels = new HashMap<String,ChartPanel>();
		modbus = new Modbus("192.168.0.101", 502);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		if(!new File("sensorList.txt").exists()) {
			try {
				out = new FileWriter(new File("sensorList.txt"));
				out.write("************************************DO NOT DELETE OR YOU WILL HAVE TO READD THE SENSORS************************************\r\n");
			} catch (IOException e1) {e1.printStackTrace();}

		}else 
			readSensorFile(IOUtils.readFile("sensorList.txt"));
		

		setLayout(new FlowLayout());
		setSize(500,500);
		RefineryUtilities.centerFrameOnScreen(this);

				XYSeries series = new XYSeries("00-1B-1E-F8-76-02-22-DD");
				spawnGraphThread(series);
				sensorPanels.put((String) series.getKey(), NivisChartFactory.makeLineChart(series));
this.setContentPane(sensorPanels.get(series.getKey()));

//		JButton addSensor = new JButton("Add a Sensor");
//		addSensor.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//
//				JTextField  eui  = new JTextField();
//				JTextField ad= new JTextField();
//
//
//				Object[] message = {
//						"Input EUI-64 of the Sensor:",eui,
//						"Input the Start Address:",ad,
//
//				};
//
//				int ans = JOptionPane.showConfirmDialog(null, message,"Enter all Values",JOptionPane.OK_CANCEL_OPTION);
//
//				if(ans==JOptionPane.OK_OPTION) {
//					addSensorToProgram(eui.getText().toString(),Integer.parseInt(ad.getText().toString()));
//				}
//
//			}
//		});
//		this.add(addSensor);	

		pack();
		setVisible(true);
	}

	private void readSensorFile(ArrayList<String> readFile) {
		// TODO Auto-generated method stub
		
	}

	private void spawnGraphThread(XYSeries series) {

		final String eui_64 = (String) series.getKey();
		System.out.println(eui_64);

		ArrayList<XYDataItem> holdingdata = new ArrayList<XYDataItem>();
		data.put(eui_64, holdingdata);

		new Thread(new Runnable() {

			@Override
			public void run() {
				double time = 0;

				while(true) {
					try {
						float current = modbus.getDataFromInputRegister(0);
						System.out.println("Time: "+time+" Current: "+current);

						XYDataItem item = new XYDataItem(time,current);

						holdingdata.add(item);	
						series.add(item);
						Thread.sleep(5000);
						time += 5;

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

	}

	private void addSensorToProgram(String string, int i) {

	}

	public void dispose() {
		try {
			out.flush();
			out.close();
		}catch(Exception exception) {}
	}



	public static void main(String... args) {
		new MainInterface("192.168.0.101");
	}
}
