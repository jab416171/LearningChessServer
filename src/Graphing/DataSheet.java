package Graphing;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class DataSheet {

	
	   public XYDataset createDataset() {
	        
	        final XYSeries series1 = new XYSeries("First");
	        series1.add(15.0, 1.0);
	        series1.add(45.0, 2.0);
	        series1.add(100.0, 3.0);
	        series1.add(210.0, 4.0);
	        series1.add(300.0, 5.0);
	        series1.add(500.0, 6.0);
	        series1.add(746.0, 7.0);
	        series1.add(1050.0, 8.0);
	        series1.add(1350.0, 9.0);
	        series1.add(1560.0, 10.0);
	        series1.add(1790.0, 11.0);
	        series1.add(2150.0, 12.0);
	        
	        final XYSeriesCollection dataset = new XYSeriesCollection();
	        dataset.addSeries(series1);
	        
	                
	        return dataset;
	        
	    }
}
