import java.awt.BasicStroke;
import java.awt.Button;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLine3DRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

  public class NivisChartFactory {

	public static ChartPanel makeLineChart(XYSeries series) {
 
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);

//makes a new graph
		JFreeChart dataGraph = ChartFactory.createXYLineChart("Data", "Time", "Current", dataset);
		dataGraph.setBackgroundPaint(Color.BLACK);

		//gets the plot of the grahp
		XYPlot plot = dataGraph.getXYPlot();

		
		//sets the range of the graph 
		NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
	
		rangeAxis.setTickUnit(new NumberTickUnit(.1));
		rangeAxis.setRange(4,6);

		((NumberAxis)plot.getDomainAxis()).setTickUnit(new NumberTickUnit(5));
		
		ChartPanel chartPanel = new ChartPanel(dataGraph);

		//creates a renderer to make the line
		XYLineAndShapeRenderer renderer = new XYLine3DRenderer();
		renderer.setSeriesPaint(0, Color.RED);
		renderer.setSeriesStroke(0, new BasicStroke(3.9f));
		
		plot.setRenderer(renderer);
	
	
		JLabel eui = new JLabel(series.getDescription());
		chartPanel.add(eui);
		
		Button button = new Button("Go Back");
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				 chartPanel.setVisible(false);
				
			}
		});
		chartPanel.add(button);

return chartPanel;
		
	}
 
}
