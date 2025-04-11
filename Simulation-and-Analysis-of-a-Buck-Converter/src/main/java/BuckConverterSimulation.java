import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class BuckConverterSimulation extends ApplicationFrame {

    private double vin = 12.0;   // Input Voltage (V)
    private double l = 100e-6;   // Inductance (H)
    private double c = 100e-6;   // Capacitance (F)
    private double r = 10.0;     // Load Resistance (Ohms)
    private double fSw = 100e3;  // Switching Frequency (Hz)
    private double tSw = 1 / fSw; // Switching Period (s)
    private double d = 0.4;     // Duty Cycle

    private double tSim = 0.01; // Simulation Time (s)
    private double dt = 1e-6;   // Time Step (s)
    private int numSteps = (int) (tSim / dt);

    private List<Double> time = new ArrayList<>();
    private List<Double> iL = new ArrayList<>();   // Inductor current
    private List<Double> vC = new ArrayList<>();   // Capacitor voltage (output voltage)

    public BuckConverterSimulation(String title) {
        super(title);
        simulateBuckConverter();
        createPlots();
        this.pack();
        RefineryUtilities.centerFrameOnScreen(this);
        this.setVisible(true);
    }

    private void simulateBuckConverter() {
        time.add(0.0);
        iL.add(0.0);
        vC.add(0.0);

        for (int n = 0; n < numSteps - 1; n++) {
            double currentTime = time.get(n);
            double currentIL = iL.get(n);
            double currentVC = vC.get(n);
            double diL_dt;
            double dvC_dt;

            // Determine switch state based on duty cycle
            if ((currentTime % tSw) < (d * tSw)) {
                // Switch ON
                diL_dt = (vin - currentVC) / l;
            } else {
                // Switch OFF (Diode ON)
                diL_dt = -currentVC / l;
            }

            dvC_dt = currentIL / c - currentVC / (r * c);

            // Update current and voltage using Euler's method
            iL.add(currentIL + diL_dt * dt);
            vC.add(currentVC + dvC_dt * dt);
            time.add(currentTime + dt);
        }

        // Calculate average output voltage (approximation)
        double sumVC = 0;
        for (int i = numSteps / 2; i < numSteps; i++) {
            sumVC += vC.get(i);
        }
        double vOutAvg = sumVC / (numSteps - numSteps / 2);
        System.out.printf("Average Output Voltage (Simulation): %.2f V%n", vOutAvg);
        System.out.printf("Theoretical Output Voltage (Vout = D * Vin): %.2f V%n", d * vin);
    }

    private void createPlots() {
        // Inductor Current
        XYSeries seriesIL = new XYSeries("Inductor Current (mA)");
        for (int i = 0; i < time.size(); i++) {
            seriesIL.add(time.get(i) * 1e3, iL.get(i) * 1e3); // Time in ms, current in mA
        }
        XYSeriesCollection datasetIL = new XYSeriesCollection(seriesIL);
        JFreeChart chartIL = ChartFactory.createXYLineChart(
                "Buck Converter Simulation - Inductor Current",
                "Time (ms)",
                "Inductor Current (mA)",
                datasetIL,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        ChartPanel chartPanelIL = new ChartPanel(chartIL);
        chartPanelIL.setPreferredSize(new java.awt.Dimension(500, 300));
        this.add(chartPanelIL);

        // Output Voltage
        XYSeries seriesVC = new XYSeries("Output Voltage (V)");
        for (int i = 0; i < time.size(); i++) {
            seriesVC.add(time.get(i) * 1e3, vC.get(i)); // Time in ms, voltage in V
        }
        XYSeriesCollection datasetVC = new XYSeriesCollection(seriesVC);
        JFreeChart chartVC = ChartFactory.createXYLineChart(
                "Buck Converter Simulation - Output Voltage",
                "Time (ms)",
                "Output Voltage (V)",
                datasetVC,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        ChartPanel chartPanelVC = new ChartPanel(chartVC);
        chartPanelVC.setPreferredSize(new java.awt.Dimension(500, 300));
        this.add(chartPanelVC);

        this.setLayout(new java.awt.FlowLayout());
    }

    public static void main(String[] args) {
        BuckConverterSimulation simulation = new BuckConverterSimulation("Buck Converter Simulation");
    }
}