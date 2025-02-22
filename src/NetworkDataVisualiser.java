import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class NetworkDataVisualiser {
    ArrayList<XYChart> charts = new ArrayList<XYChart>();
    ArrayList<ArrayList<Double>> xOrdinates = new ArrayList<>();
    ArrayList<ArrayList<Double>> yOrdinates = new ArrayList<>();

    public NetworkDataVisualiser() {

    }

    public void addOrdinates(final String type, final ArrayList<Double> ordinates){
        if(type.equals("x") || type.equals("X")){
            xOrdinates.add(ordinates);
        }else if(type.equals("y") || type.equals("Y")){
            yOrdinates.add(ordinates);
        }else{
            System.out.println("  !! ERROR: Invalid ordinate option passed to addOrdinates(), set not added");
        }
    }

    public int getChartAmount(){
        return charts.size();
    }

    public void createChart(final int ordinateIndex, final String xAxisTitle, final String yAxisTitle, final int height, final int width){
        XYChart newChart = new XYChartBuilder().xAxisTitle(xAxisTitle).yAxisTitle(yAxisTitle).height(height).width(width).build();
        newChart.getStyler().setYAxisMax(100.0);
        newChart.getStyler().setYAxisMin(0.0);
        newChart.addSeries(" ", xOrdinates.get(ordinateIndex), yOrdinates.get(ordinateIndex));
        charts.add(newChart);

    }

    public void displayCharts(){
        if(charts.size() == 0){
            System.out.println("  !! ERROR: No data found");
        }else{
            new SwingWrapper<XYChart>(charts).displayChartMatrix();
        }
    }
}

