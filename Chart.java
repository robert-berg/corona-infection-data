import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;
import java.awt.Color;
import java.util.Collections;

class Chart {
    public static void main(String[] args) {

        // Change the next line to choose the countries you're interested in
        String[] outputCountries = { "DE", "ES", "AR", "CN" };

        for (String outputCountry : outputCountries) {

            PopulationData populationData = new PopulationData("UN.csv");
            InfectionList infectionsList = new InfectionList("WHO.csv", outputCountry);
            CountryCodes countryCodes = new CountryCodes("CountryCodes.csv");

            InfectionAnalysis infectionAnalysis = new InfectionAnalysis(infectionsList, populationData, countryCodes);

            plotAnalysis(infectionAnalysis, outputCountry + ".png");
        }

    }

    public static void plotAnalysis(InfectionAnalysis ia, String filename) {
        double[] xdata = new double[700];
        double[] ydata = new double[700];

        for (int i = 1; i < xdata.length; i++) {
            xdata[i] = i;
            ydata[i] = ydata[i - 1] + (50 - 0.5);
        }

        XYChart chart = new XYChart(800, 300);
        chart.setTitle("Infection data for " + ia.getCountryISO2());
        chart.setXAxisTitle("Day");
        chart.setYAxisGroupTitle(0, "Daily Infections");
        chart.setYAxisGroupTitle(1, "7-day incidence");

        XYSeries series1 = chart.addSeries("Daily Infections", ia.getDailyInfections());
        series1.setYAxisGroup(0);
        series1.setLineColor(Color.decode("#2553ff"));
        XYSeries series2 = chart.addSeries("7-day incidence", ia.get7di());
        series2.setLineColor(Color.decode("#ffb51b"));
        XYSeries series3 = chart.addSeries("7-day incidence: 50", Collections.nCopies(ia.getNumDatapoints(), 50));
        series3.setLineColor(Color.decode("#ff0f0f"));
        series2.setYAxisGroup(1);
        series3.setYAxisGroup(1);

        series1.setMarker(SeriesMarkers.NONE);
        series2.setMarker(SeriesMarkers.NONE);
        series3.setMarker(SeriesMarkers.NONE);

        try {
            BitmapEncoder.saveBitmap(chart, "./output/" + filename, BitmapFormat.PNG);
        } catch (Exception e) {
            System.out.println("Fehler beim Speichern der Bilddatei.");
        }
    }
}