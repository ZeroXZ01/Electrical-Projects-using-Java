import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class SolarPVAnalysis extends ApplicationFrame {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private List<LocalDateTime> timestamps = new ArrayList<>();
    private List<Double> irradiance = new ArrayList<>();
    private List<Double> panelTemperature = new ArrayList<>();
    private List<Double> voltage = new ArrayList<>();
    private List<Double> current = new ArrayList<>();
    private List<Double> loadPower = new ArrayList<>();
    private List<Double> dcPower = new ArrayList<>();
    private List<Double> efficiency = new ArrayList<>();

    private double panelAreaM2 = 1.6; // Example area

    public SolarPVAnalysis(String title, String csvFilePath) {
        super(title);
        loadDataFromCSV(csvFilePath);
        calculateDCPower();
        calculateEfficiency();
        createPlots();
        this.pack();
        RefineryUtilities.centerFrameOnScreen(this);
        this.setVisible(true);
    }

    private void loadDataFromCSV(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean headerSkipped = false;
            while ((line = br.readLine()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }
                String[] values = line.split(",");
                if (values.length == 6) {
                    timestamps.add(LocalDateTime.parse(values[0].trim(), TIMESTAMP_FORMATTER));
                    irradiance.add(Double.parseDouble(values[1].trim()));
                    panelTemperature.add(Double.parseDouble(values[2].trim()));
                    voltage.add(Double.parseDouble(values[3].trim()));
                    current.add(Double.parseDouble(values[4].trim()));
                    loadPower.add(Double.parseDouble(values[5].trim()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle file not found or other IO errors
        }
    }

    private void calculateDCPower() {
        for (int i = 0; i < voltage.size(); i++) {
            dcPower.add(voltage.get(i) * current.get(i));
        }
    }

    private void calculateEfficiency() {
        for (int i = 0; i < dcPower.size(); i++) {
            double powerPerArea = dcPower.get(i) / panelAreaM2;
            if (irradiance.get(i) > 0) {
                efficiency.add((powerPerArea / irradiance.get(i)) * 100);
            } else {
                efficiency.add(0.0);
            }
        }
    }

    private void createPlots() {
        // Irradiance vs. DC Power
        XYSeries series1 = new XYSeries("DC Power");
        for (int i = 0; i < irradiance.size(); i++) {
            series1.add(irradiance.get(i), dcPower.get(i));
        }
        XYSeriesCollection dataset1 = new XYSeriesCollection(series1);
        JFreeChart chart1 = ChartFactory.createScatterPlot(
                "Solar PV System Performance", "Irradiance (W/m^2)", "DC Power (W)",
                dataset1, PlotOrientation.VERTICAL, true, true, false);
        ChartPanel chartPanel1 = new ChartPanel(chart1);
        chartPanel1.setPreferredSize(new java.awt.Dimension(500, 300));
        this.add(chartPanel1);

        // DC Power vs. Load Power over Time
        XYSeries series2a = new XYSeries("DC Power Generated");
        XYSeries series2b = new XYSeries("Load Power Consumption");
        for (int i = 0; i < timestamps.size(); i++) {
            long timeMillis = timestamps.get(i).toInstant(ZoneOffset.UTC).toEpochMilli();
            series2a.add(timeMillis, dcPower.get(i));
            series2b.add(timeMillis, loadPower.get(i));
        }
        XYSeriesCollection dataset2 = new XYSeriesCollection();
        dataset2.addSeries(series2a);
        dataset2.addSeries(series2b);
        JFreeChart chart2 = ChartFactory.createTimeSeriesChart(
                "Power Generation vs. Consumption", "Time", "Power (W)",
                dataset2, true, true, false);
        ChartPanel chartPanel2 = new ChartPanel(chart2);
        chartPanel2.setPreferredSize(new java.awt.Dimension(500, 300));
        this.add(chartPanel2);

        // Efficiency over Time
        XYSeries series3 = new XYSeries("System Efficiency (%)");
        for (int i = 0; i < timestamps.size(); i++) {
            long timeMillis = timestamps.get(i).toInstant(ZoneOffset.UTC).toEpochMilli();
            series3.add(timeMillis, efficiency.get(i));
        }
        XYSeriesCollection dataset3 = new XYSeriesCollection(series3);
        JFreeChart chart3 = ChartFactory.createTimeSeriesChart(
                "Simplified System Efficiency Over Time", "Time", "Efficiency (%)",
                dataset3, true, true, false);
        ChartPanel chartPanel3 = new ChartPanel(chart3);
        chartPanel3.setPreferredSize(new java.awt.Dimension(500, 300));
        this.add(chartPanel3);

        this.setLayout(new java.awt.FlowLayout());
    }

    public static void main(String[] args) {
        // Create a dummy CSV file for testing
        String csvFilePath = "solar_data.csv";
        try (java.io.PrintWriter writer = new java.io.PrintWriter(csvFilePath)) {
            writer.println("timestamp,irradiance (W/m^2),panel_temperature (°C),voltage (V),current (A),load_power (W)");
            for (int i = 0; i < 60; i++) {
                LocalDateTime now = LocalDateTime.now().minusMinutes(60 - i);
                double irr = 1000 * Math.sin(Math.PI * i / 60.0) + Math.random() * 50;
                double temp = 25 + 0.2 * irr + Math.random() * 2;
                double volt = 12 + 0.01 * irr + Math.random() * 0.1;
                double curr = 0.1 * irr + Math.random() * 0.05;
                double load = 50 + 0.05 * irr + Math.random() * 5;
                writer.println(now.format(TIMESTAMP_FORMATTER) + "," + String.format("%.2f", irr) + "," + String.format("%.2f", temp) + "," + String.format("%.2f", volt) + "," + String.format("%.2f", curr) + "," + String.format("%.2f", load));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        SolarPVAnalysis analysis = new SolarPVAnalysis("Solar PV System Analysis", csvFilePath);
    }
}
