import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class OvercurrentRelayAnalysis extends ApplicationFrame {

    private static class RelayData {
        String relayId;
        double pickupCurrentPu;
        double tms;
        String characteristicType;

        public RelayData(String relayId, double pickupCurrentPu, double tms, String characteristicType) {
            this.relayId = relayId;
            this.pickupCurrentPu = pickupCurrentPu;
            this.tms = tms;
            this.characteristicType = characteristicType;
        }
    }

    private List<RelayData> relayList = new ArrayList<>();
    private Map<String, Double> faultCurrents = new HashMap<>();
    private Map<String, Map<String, Double>> trippingTimes = new HashMap<>();

    public OvercurrentRelayAnalysis(String title) {
        super(title);
        loadRelayData();
        loadFaultCurrents();
        calculateTrippingTimes();
        createTCPlot();
        analyzeCoordination("R1", "R2", "Fault_A"); // Example coordination analysis
        this.pack();
        RefineryUtilities.centerFrameOnScreen(this);
        this.setVisible(true);
    }

    private void loadRelayData() {
        relayList.add(new RelayData("R1", 1.5, 0.1, "Inverse"));
        relayList.add(new RelayData("R2", 1.2, 0.2, "Very Inverse"));
        relayList.add(new RelayData("R3", 1.0, 0.3, "Inverse"));
    }

    private void loadFaultCurrents() {
        faultCurrents.put("Fault_A", 5.0);
        faultCurrents.put("Fault_B", 3.5);
        faultCurrents.put("Fault_C", 2.0);
    }

    private double calculateTrippingTime(double faultCurrentPu, double pickupCurrentPu, double tms, String characteristicType) {
        double ratio = faultCurrentPu / pickupCurrentPu;
        if (ratio <= 1) {
            return Double.POSITIVE_INFINITY;
        }

        double k = 0;
        double n = 0;

        switch (characteristicType) {
            case "Inverse":
                k = 0.14;
                n = 0.02;
                break;
            case "Very Inverse":
                k = 13.5;
                n = 1.0;
                break;
            case "Extremely Inverse":
                k = 80.0;
                n = 2.0;
                break;
            default:
                return Double.NaN; // Unknown characteristic
        }

        return tms * (k / (Math.pow(ratio, n) - 1));
    }

    private void calculateTrippingTimes() {
        for (RelayData relay : relayList) {
            Map<String, Double> times = new HashMap<>();
            for (Map.Entry<String, Double> faultEntry : faultCurrents.entrySet()) {
                double tripTime = calculateTrippingTime(faultEntry.getValue(), relay.pickupCurrentPu, relay.tms, relay.characteristicType);
                times.put(faultEntry.getKey(), tripTime);
            }
            trippingTimes.put(relay.relayId, times);
        }

        System.out.println("Tripping Times (seconds):");
        trippingTimes.forEach((relayId, times) -> {
            System.out.println(relayId + ": " + times);
        });
    }

    private XYSeries generateTCcurve(double pickupCurrentPu, double tms, String characteristicType, String relayId) {
        XYSeries series = new XYSeries(relayId + " (" + characteristicType + ")");
        for (double currentPu = 1.1 * pickupCurrentPu; currentPu <= 10 * pickupCurrentPu; currentPu += 0.05 * pickupCurrentPu) {
            double time = calculateTrippingTime(currentPu, pickupCurrentPu, tms, characteristicType);
            if (!Double.isInfinite(time) && !Double.isNaN(time) && time < 100) { // Limit for plotting
                series.add(currentPu / pickupCurrentPu, time); // Plotting against multiple of pickup
            }
        }
        return series;
    }

    private void createTCPlot() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (RelayData relay : relayList) {
            dataset.addSeries(generateTCcurve(relay.pickupCurrentPu, relay.tms, relay.characteristicType, relay.relayId));
        }

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Overcurrent Relay Time-Current Characteristics",
                "Fault Current (pu of Pickup)",
                "Tripping Time (seconds)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Set the domain axis (x-axis) to be logarithmic
        XYPlot plot = (XYPlot) chart.getPlot();
        LogAxis xAxis = new LogAxis("Fault Current (pu of Pickup)");
        plot.setDomainAxis(xAxis);

        // Optionally, set the range axis (y-axis) to be logarithmic as well
        LogAxis yAxis = new LogAxis("Tripping Time (seconds)");
        plot.setRangeAxis(yAxis);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 300));
        this.add(chartPanel);
    }

    private void analyzeCoordination(String relayPrimaryId, String relayBackupId, String faultLocation) {
        System.out.println("\nCoordination Analysis (comparing " + relayPrimaryId + " and " + relayBackupId + " for " + faultLocation + "):");

        if (trippingTimes.containsKey(relayPrimaryId) && trippingTimes.containsKey(relayBackupId) &&
                trippingTimes.get(relayPrimaryId).containsKey(faultLocation) &&
                trippingTimes.get(relayBackupId).containsKey(faultLocation)) {

            double primaryTripTime = trippingTimes.get(relayPrimaryId).get(faultLocation);
            double backupTripTime = trippingTimes.get(relayBackupId).get(faultLocation);
            double coordinationTimeInterval = backupTripTime - primaryTripTime;

            System.out.printf("Tripping time of %s for %s: %.3f s%n", relayPrimaryId, faultLocation, primaryTripTime);
            System.out.printf("Tripping time of %s for %s: %.3f s%n", relayBackupId, faultLocation, backupTripTime);
            System.out.printf("Coordination Time Interval: %.3f s%n", coordinationTimeInterval);

            if (coordinationTimeInterval > 0.2 && coordinationTimeInterval < 0.5) {
                System.out.println("Coordination between " + relayPrimaryId + " and " + relayBackupId + " for " + faultLocation + " is likely acceptable.");
            } else {
                System.out.println("Coordination between " + relayPrimaryId + " and " + relayBackupId + " for " + faultLocation + " might need adjustment.");
            }

        } else {
            System.out.println("Could not perform coordination analysis due to missing data.");
        }
    }

    public static void main(String[] args) {
        OvercurrentRelayAnalysis analysis = new OvercurrentRelayAnalysis("Overcurrent Relay Analysis");
    }
}