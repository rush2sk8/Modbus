import java.awt.FlowLayout;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class MainInterface extends ApplicationFrame {

	private static final long serialVersionUID = -3118738086863395089L;


	private Modbus modbus;

	public MainInterface(String ipString) {
		super("NIVIS Data Visualizer");

		modbus = new Modbus(ipString, 502);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);


		setLayout(new FlowLayout());
		setSize(500,500);
		RefineryUtilities.centerFrameOnScreen(this);

		JTextField  eui  = new JTextField();
		JTextField ad= new JTextField();

		Object[] message = {
				"Input EUI-64 of the Sensor:",eui,
				"Input the Start Address (0 is default):",ad,

		};

		int ans = JOptionPane.showConfirmDialog(null, message,"Enter all Values",JOptionPane.OK_CANCEL_OPTION);

		if(ans==JOptionPane.OK_OPTION) {
			XYSeries series = new XYSeries(eui.getText().toString());
			
			int x=0;
			try {
			x = Integer.parseInt(ad.getText().toString());
			}catch(NumberFormatException exception) {
				JOptionPane.showMessageDialog(null, "Invalid Address");
				
			}
			spawnGraphThread(series,x);
			setContentPane(NivisChartFactory.makeLineChart(series));
		}
		else
			System.exit(-1);


		pack();
		setVisible(true);
	}

	private void spawnGraphThread(XYSeries series,int r) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				double time = 0;

				while(true) {
					try {
						float current = modbus.getDataFromInputRegister(r);
						System.out.println("Time: "+time+" Current: "+current);

						XYDataItem item = new XYDataItem(time,current);

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
	public static void main(String... args) {
		new MainInterface("192.168.0.101");
	}
}
