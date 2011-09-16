package Graphing;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A simple demonstration application showing how to create a line chart using data from an
 * {@link XYDataset}.
 *
 */
public class LineGraph extends ApplicationFrame {

    /**
     * Creates a new demo.
     *
     * @param title  the frame title.
     */
    public LineGraph(final String title) {

        super(title);

        final XYDataset dataSet = new DataSheet().createDataset();
        final JFreeChart chart = createChart(dataSet);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        chartAsPNG(chart, 500, 270, null);
        setContentPane(chartPanel);

    }
    
    /**
     * Creates a sample dataset.
     * 
     * @return a sample dataset.
     */
 
    
    /**
     * Creates a chart.
     * 
     * @param dataset  the data for the chart.
     * 
     * @return a chart.
     */
    private JFreeChart createChart(final XYDataset dataset) {
        
        // create the chart...
        final JFreeChart chart = ChartFactory.createXYLineChart(
            "Wins Over Time",      // chart title
            "Wins",                      // x axis label
            "Months",                      // y axis label
            dataset,                  // data
            PlotOrientation.VERTICAL,
            true,                     // include legend
            true,                     // tooltips
            false                     // urls
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chart.setBackgroundPaint(Color.white);

//        final StandardLegend legend = (StandardLegend) chart.getLegend();
  //      legend.setDisplaySeriesShapes(true);
        
        // get a reference to the plot for further customisation...
        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.lightGray);
    //    plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        
        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesShapesVisible(1, false);
        plot.setRenderer(renderer);

        // change the auto tick unit selection to integer units only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        // OPTIONAL CUSTOMISATION COMPLETED.
                
        return chart;
        
    }
    
    public void chartAsPNG(JFreeChart aChart, int aWidth, int aHeight, String aFileName)
    {
    	 try {
			ChartUtilities.writeChartAsPNG( new FileOutputStream("C:\\Users\\dmatthews\\Desktop\\Neumont\\Q8\\Pro380\\test\\WebContent"),
			         aChart,
			         aWidth, aHeight);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    // ****************************************************************************
    // * JFREECHART DEVELOPER GUIDE                                               *
    // * The JFreeChart Developer Guide, written by David Gilbert, is available   *
    // * to purchase from Object Refinery Limited:                                *
    // *                                                                          *
    // * http://www.object-refinery.com/jfreechart/guide.html                     *
    // *                                                                          *
    // * Sales are used to provide funding for the JFreeChart project - please    * 
    // * support us so that we can continue developing free software.             *
    // ****************************************************************************
    
//    /**
//     * Starting point for the demonstration application.
//     *
//     * @param args  ignored.
//     */
//    public static void main(final String[] args) {
//
//        final LineGraph demo = new LineGraph("Line Chart Demo 6");
//        demo.pack();
//        Refin  eryUtilities.centerFrameOnScreen(demo);
//        demo.setVisible(true);
//
//    }

}