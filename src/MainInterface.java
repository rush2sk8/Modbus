import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.ApplicationFrame;

public class MainInterface extends ApplicationFrame {

	private static final long serialVersionUID = -3118738086863395089L;


	public MainInterface(String ipString) {
		super("NIVIS Data Visualizer");
	
		XYSeries series = new XYSeries("00-1B-1E-F8-76-02-22-DD");
		
		this.setContentPane(NivisChartFactory.makeLineChart(series));
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				double time = 0;
				Modbus modbus = new Modbus("192.168.0.101", Modbus.DEFAULT_PORT);

				while(true) {
					try {
						float current = modbus.getDataFromInputRegister(0);
						System.out.println("Time: "+time+" Current: "+current);

						series.add(new XYDataItem(time,current));
						Thread.sleep(5000);
						time += 5;

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		pack();
		setVisible(true);
	}
 

 
	
	public static void main(String[] args) {
		new MainInterface("192.168.0.101");
	}
}
