import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.nonstiff.EulerIntegrator;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class DCMotorPIDControl extends ApplicationFrame {

    // DC Motor Parameters
    private double ra = 2.0;      // Armature resistance (Ohms)
    private double la = 0.05;     // Armature inductance (H)
    private double kb = 0.01;     // Back EMF constant (V/rad/s)
    private double kt = kb;       // Torque constant (Nm/A)
    private double j = 0.005;     // Moment of inertia (kg.m^2)
    private double b = 0.001;     // Viscous friction coefficient (Nm/(rad/s))
    private double tl = 0.1;      // Load torque (Nm)

    // Simulation Parameters
    private double tSim = 5.0;   // Simulation time (s)
    private double dt = 0.001;    // Time step (s)
    private List<Double> time = new ArrayList<>();
    private List<Double> omega = new ArrayList<>();       // Angular speed
    private List<Double> ia = new ArrayList<>();          // Armature current
    private List<Double> va = new ArrayList<>();          // Armature voltage (control signal)
    private List<Double> omegaRefList = new ArrayList<>(); // Reference speed

    // PID Controller Parameters (to be tuned)
    private double kp = 1.0;
    private double ki = 0.5;
    private double kd = 0.1;
    private double integralError = 0.0;
    private double previousError = 0.0;

    public DCMotorPIDControl(String title) {
        super(title);
        simulateDCMotorControl();
        createPlots();
        this.pack();
        RefineryUtilities.centerFrameOnScreen(this);
        this.setVisible(true);
    }

    private double speedRef(double t) {
        if (t < 1) {
            return 50.0;  // rad/s
        } else if (t < 3) {
            return 100.0; // rad/s
        } else {
            return 75.0;  // rad/s
        }
    }

    public class MotorEquations implements FirstOrderDifferentialEquations {
        private double appliedVoltage;

        public void setAppliedVoltage(double voltage) {
            this.appliedVoltage = voltage;
        }

        @Override
        public int getDimension() {
            return 2; // We are solving for two variables: ia (index 0) and omega (index 1)
        }

        @Override
        public void computeDerivatives(double t, double[] y, double[] yDot) throws MaxCountExceededException, DimensionMismatchException {
            double currentIa = y[0];
            double currentOmega = y[1];
            double dIa_dt = (appliedVoltage - currentIa * ra - kb * currentOmega) / la;
            double dOmega_dt = (kt * currentIa - b * currentOmega - tl) / j;
            yDot[0] = dIa_dt;
            yDot[1] = dOmega_dt;
        }
    }

    private void simulateDCMotorControl() {
        MotorEquations motorEquations = new MotorEquations();
        EulerIntegrator integrator = new EulerIntegrator(dt);
        double[] initialState = {0.0, 0.0}; // Initial values for [ia, omega]
        double currentTime = 0.0;
        double[] finalState = new double[2];

        time.add(currentTime);
        ia.add(initialState[0]);
        omega.add(initialState[1]);
        va.add(0.0); // Initial control voltage
        omegaRefList.add(speedRef(currentTime));

        while (currentTime < tSim) {
            double omegaRef = speedRef(currentTime);
            omegaRefList.add(omegaRef);
            double currentOmega = omega.get(omega.size() - 1);
            double currentIa = ia.get(ia.size() - 1);

            double error = omegaRef - currentOmega;

            // PID Controller
            double proportional = kp * error;
            integralError += ki * error * dt;
            double derivative = kd * (error - previousError) / dt;
            double controlSignal = proportional + integralError + derivative;

            // Limit control signal (armature voltage)
            double vaApplied = Math.max(Math.min(controlSignal, 12.0), -12.0);
            va.add(vaApplied);

            motorEquations.setAppliedVoltage(vaApplied);

            // Integrate over one time step
            integrator.integrate(motorEquations, currentTime, initialState, currentTime + dt, finalState);

            currentTime += dt;
            time.add(currentTime);
            ia.add(finalState[0]);
            omega.add(finalState[1]);

            initialState = finalState.clone(); // Update initial state for the next step
            previousError = error;
        }
    }

    private void createPlots() {
        // Speed Tracking
        XYSeries referenceSpeedSeries = new XYSeries("Reference Speed (rad/s)");
        XYSeries actualSpeedSeries = new XYSeries("Actual Speed (rad/s)");
        for (int i = 0; i < time.size(); i++) {
            referenceSpeedSeries.add(time.get(i), omegaRefList.get(i));
            actualSpeedSeries.add(time.get(i), omega.get(i));
        }
        XYSeriesCollection speedDataset = new XYSeriesCollection();
        speedDataset.addSeries(referenceSpeedSeries);
        speedDataset.addSeries(actualSpeedSeries);
        JFreeChart speedChart = ChartFactory.createXYLineChart(
                "DC Motor Speed Control with PID (Apache Commons Math ODE Solver)",
                "Time (s)",
                "Angular Speed (rad/s)",
                speedDataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        ChartPanel speedChartPanel = new ChartPanel(speedChart);
        speedChartPanel.setPreferredSize(new java.awt.Dimension(500, 300));
        this.add(speedChartPanel);

        // Armature Voltage (Control Signal)
        XYSeries voltageSeries = new XYSeries("Armature Voltage (V)");
        for (int i = 0; i < time.size(); i++) {
            voltageSeries.add(time.get(i), va.get(i));
        }
        XYSeriesCollection voltageDataset = new XYSeriesCollection(voltageSeries);
        JFreeChart voltageChart = ChartFactory.createXYLineChart(
                "Control Signal (Armature Voltage)",
                "Time (s)",
                "Armature Voltage (V)",
                voltageDataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        ChartPanel voltageChartPanel = new ChartPanel(voltageChart);
        voltageChartPanel.setPreferredSize(new java.awt.Dimension(500, 300));
        this.add(voltageChartPanel);

        this.setLayout(new java.awt.FlowLayout());
    }

    public static void main(String[] args) {
        DCMotorPIDControl simulation = new DCMotorPIDControl("DC Motor PID Control Simulation");
    }
}