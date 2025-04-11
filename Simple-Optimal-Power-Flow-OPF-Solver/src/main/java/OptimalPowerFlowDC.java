import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleBounds;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.BOBYQAOptimizer;

public class OptimalPowerFlowDC {

    private static final int NUM_BUSES = 3;
    private static final int NUM_LINES = 3;

    // Bus data: [Pd (demand), Pg_min, Pg_max]
    private static final double[][] busData = {
            {100.0, 0.0, 300.0}, // Bus 1 (Slack)
            {50.0, 0.0, 200.0},  // Bus 2
            {75.0, 0.0, 250.0}   // Bus 3
    };

    // Line data: [from_bus, to_bus, x (reactance), P_max_flow]
    private static final double[][] lineData = {
            {0.0, 1.0, 0.1, 150.0},
            {0.0, 2.0, 0.2, 100.0},
            {1.0, 2.0, 0.15, 120.0}
    };

    // Generation cost coefficients: [a (constant), b (linear)] - for Pg
    private static final double[][] genCostCoeff = {
            {0.0, 2.0},   // Bus 1
            {0.0, 3.0},   // Bus 2
            {0.0, 2.5}    // Bus 3
    };

    public static void main(String[] args) {
        int nVar = NUM_BUSES - 1; // Number of voltage angles to optimize (excluding slack)
        double[] initialGuess = new double[nVar]; // Initial guess for voltage angles (excluding slack)
        double[] lowerBounds = new double[nVar];
        double[] upperBounds = new double[nVar];

        // Set initial guess and bounds for voltage angles (excluding slack bus 0)
        for (int i = 0; i < nVar; i++) {
            initialGuess[i] = 0.0; // Initial guess of 0 radians
            lowerBounds[i] = -Math.PI; // Lower bound: -pi radians
            upperBounds[i] = Math.PI;  // Upper bound: pi radians
        }

        BOBYQAOptimizer optimizer = new BOBYQAOptimizer(
                2 * nVar + 1, // Number of interpolation points
                10000,        // Max number of function evaluations
                1e-6           // Convergence tolerance
        );

        ObjectiveFunction objectiveFunction = new ObjectiveFunction(OptimalPowerFlowDC::calculateTotalCost);
        InitialGuess initialPoint = new InitialGuess(initialGuess);
        GoalType goal = GoalType.MINIMIZE;
        MaxEval maxEval = new MaxEval(10000);
        SimpleBounds bounds = new SimpleBounds(lowerBounds, upperBounds);

        try {
            PointValuePair optimal = optimizer.optimize(objectiveFunction, initialPoint, goal, maxEval, bounds);
            double[] optimalAngles = optimal.getPoint();
            double minCost = optimal.getValue();

            System.out.println("Optimal DC Optimal Power Flow Solution:");
            System.out.println("---------------------------------------");
            System.out.println("Total Generation Cost: $" + String.format("%.2f", minCost));
            System.out.println("\nOptimal Voltage Angles (radians):");
            System.out.printf("Bus %d (Slack): %.4f%n", 1, 0.0); // Slack bus angle is 0
            for (int i = 0; i < nVar; i++) {
                System.out.printf("Bus %d: %.4f%n", i + 2, optimalAngles[i]);
            }

            System.out.println("\nGenerator Dispatch (MW):");
            double[] optimalPg = calculateGeneratorOutput(optimalAngles);
            for (int i = 0; i < NUM_BUSES; i++) {
                System.out.printf("Bus %d: %.2f MW%n", i + 1, optimalPg[i]);
            }

            System.out.println("\nLine Flows (MW):");
            calculateLineFlows(optimalAngles);

        } catch (TooManyEvaluationsException e) {
            System.err.println("Optimization failed: Too many evaluations.");
        } catch (ConvergenceException e) {
            System.err.println("Optimization failed: Convergence error.");
        }
    }

    private static double calculateTotalCost(double[] angles) {
        double totalCost = 0.0;
        double[] pg = calculateGeneratorOutput(angles);
        for (int i = 0; i < NUM_BUSES; i++) {
            totalCost += genCostCoeff[i][0] + genCostCoeff[i][1] * pg[i];
        }
        return totalCost;
    }

    private static double[] calculateGeneratorOutput(double[] angles) {
        double[] pg = new double[NUM_BUSES];
        double[] theta = new double[NUM_BUSES];
        theta[0] = 0.0; // Slack bus angle
        for (int i = 0; i < angles.length; i++) {
            theta[i + 1] = angles[i];
        }

        double[] pInjection = new double[NUM_BUSES];
        for (int i = 0; i < NUM_BUSES; i++) {
            for (int j = 0; j < NUM_LINES; j++) {
                int from = (int) lineData[j][0];
                int to = (int) lineData[j][1];
                double x = lineData[j][2];
                if (from == i) {
                    pInjection[i] += (theta[i] - theta[to]) / x;
                }
                if (to == i) {
                    pInjection[i] += (theta[i] - theta[from]) / x;
                }
            }
            pg[i] = busData[i][0] + pInjection[i]; // Pg = Pd + Net outflow
            // Enforce generator limits (simplified - slack handles imbalance)
            pg[i] = Math.max(pg[i], busData[i][1]);
            pg[i] = Math.min(pg[i], busData[i][2]);
        }
        return pg;
    }

    private static void calculateLineFlows(double[] angles) {
        double[] theta = new double[NUM_BUSES];
        theta[0] = 0.0; // Slack bus angle
        for (int i = 0; i < angles.length; i++) {
            theta[i + 1] = angles[i];
        }

        for (int i = 0; i < NUM_LINES; i++) {
            int from = (int) lineData[i][0];
            int to = (int) lineData[i][1];
            double x = lineData[i][2];
            double flow = (theta[from] - theta[to]) / x;
            System.out.printf("Line %d-%d: %.2f MW (Limit: %.2f MW)%n", from + 1, to + 1, flow, lineData[i][3]);
        }
    }
}