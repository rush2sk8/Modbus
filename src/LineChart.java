import java.awt.BasicStroke;
import java.awt.Color;
import java.rmi.server.Skeleton;
import java.util.Scanner;

import javax.swing.JOptionPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLine3DRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.statistics.Regression;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class LineChart extends ApplicationFrame{

	public LineChart(String title) {
		super(title);
		setBackground(Color.WHITE);

		XYSeries series = new XYSeries("00-1B-1E-F8-76-02-22-DD");
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);


		//polls the sensors to update the graph
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

		JFreeChart dataGraph = ChartFactory.createXYLineChart("Data", "Time", "Current", dataset);
		dataGraph.setBackgroundPaint(Color.BLACK);

		XYPlot plot = dataGraph.getXYPlot();

		NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();

		rangeAxis.setTickUnit(new NumberTickUnit(.2));

		ChartPanel chartPanel = new ChartPanel(dataGraph);

		XYLineAndShapeRenderer renderer = new XYLine3DRenderer();
		renderer.setSeriesPaint(0, Color.RED);
		renderer.setSeriesStroke(0, new BasicStroke(3.9f));
		plot.setRenderer(renderer);


		this.setContentPane(chartPanel);
pack();
RefineryUtilities.centerFrameOnScreen(this);
setVisible(true);
	}


	public static void main(String[] args) {
	new LineChart("NIVIS Data Visualizer");
 
	}
}
