# Collection of Power Engineering and Programming Projects in Java

This repository contains Java implementations of five different projects that combine principles of power engineering with programming concepts. Each project aims to solve a specific problem or simulate a system relevant to the field.

## Project List

1.  **Solar PV System Performance Analysis and Monitoring Tool (`SolarPVAnalysis.java`)**
    * **Description:** This project analyzes the performance of a solar photovoltaic (PV) system using data typically collected from sensors. It loads data from a CSV file, calculates DC power and a simplified efficiency, and visualizes the irradiance vs. DC power and power/efficiency trends over time using JFreeChart.
    * **Key Java Libraries:**
        * `java.io`: For file input/output operations.
        * `java.time`: For handling timestamps.
        * `java.util`: For data structures like `ArrayList`.
        * `org.jfree.chart`: For creating plots and charts.
    * **How to Run:**
        1.  Ensure you have JFreeChart installed and included in your project's classpath.
        2.  Compile `SolarPVAnalysis.java`.
        3.  Run the `main` method. A dummy `solar_data.csv` will be created if it doesn't exist, and a window displaying the analysis plots will appear.

2.  **Overcurrent Relay Coordination Analysis Tool (`OvercurrentRelayAnalysis.java`)**
    * **Description:** This tool analyzes the time-current characteristics of overcurrent relays and helps in assessing their coordination. It defines relay parameters, calculates tripping times for different fault currents, plots the time-current curves using JFreeChart, and performs a basic coordination analysis between selected relays.
    * **Key Java Libraries:**
        * `java.util`: For data structures like `ArrayList` and `HashMap`.
        * `org.jfree.chart`: For creating logarithmic axis charts for relay characteristics.
    * **How to Run:**
        1.  Ensure you have JFreeChart installed.
        2.  Compile `OvercurrentRelayAnalysis.java`.
        3.  Run the `main` method. A window displaying the time-current characteristic curves will appear, and the coordination analysis will be printed to the console.

3.  **Simulation and Analysis of a Buck Converter (`BuckConverterSimulation.java`)**
    * **Description:** This project simulates the behavior of a buck DC-DC converter using a discrete-time step approach (Euler's method). It models the inductor current and output voltage waveforms over time and calculates the average output voltage. The results are visualized using JFreeChart.
    * **Key Java Libraries:**
        * `java.util`: For `ArrayList` to store simulation data.
        * `org.jfree.chart`: For plotting the simulated waveforms.
    * **How to Run:**
        1.  Ensure you have JFreeChart installed.
        2.  Compile `BuckConverterSimulation.java`.
        3.  Run the `main` method. A window displaying the inductor current and output voltage waveforms will appear, and the average output voltage will be printed to the console.

4.  **Simulation of a PID Controller for DC Motor Speed Control (`DCMotorPIDControl.java`)**
    * **Description:** This project simulates the closed-loop speed control of a DC motor using a Proportional-Integral-Derivative (PID) controller. It models the motor dynamics and the PID control algorithm, tracking a defined reference speed profile. The actual speed and the control signal (armature voltage) are visualized using JFreeChart.
    * **Key Java Libraries:**
        * `java.util`: For `ArrayList` to store simulation data.
        * `org.jfree.chart`: For plotting the speed and voltage over time.
    * **How to Run:**
        1.  Ensure you have JFreeChart installed.
        2.  Compile `DCMotorPIDControl.java`.
        3.  Run the `main` method. A window displaying the speed tracking performance and the armature voltage will appear.

5.  **Simple Optimal Power Flow (OPF) Solver (`OptimalPowerFlowDC.java`)**
    * **Description:** This project implements a simplified DC Optimal Power Flow (OPF) solver for a small 3-bus power system. It uses the BOBYQA optimizer from Apache Commons Math to minimize the total generation cost while satisfying power balance (approximately) and line flow limits.
    * **Key Java Libraries:**
        * `java.util`: For `ArrayList` and other utility classes.
        * `org.apache.commons.math3.optim`: For the BOBYQA optimization algorithm.
    * **How to Run:**
        1.  Ensure you have Apache Commons Math installed and included in your project's classpath.
        2.  Compile `OptimalPowerFlowDC.java`.
        3.  Run the `main` method. The program will print the optimal generation dispatch, voltage angles, total cost, and line flows to the console.

## Dependencies

These projects rely on the following external Java libraries:

* **JFreeChart:** For creating various types of charts and plots. You can download it from the official JFreeChart website or include it as a dependency in your build tool (e.g., Maven, Gradle).
* **Apache Commons Math:** For numerical computation and optimization algorithms (specifically used in the OPF solver). You can download it from the Apache Commons Math website or include it as a dependency in your build tool.

## How to Use

1.  **Clone the Repository:** Clone this repository to your local machine.
2.  **Install Dependencies:** Ensure that the required libraries (JFreeChart and Apache Commons Math) are installed and accessible by your Java development environment.
3.  **Compile:** Compile the `.java` files for each project using a Java compiler (e.g., `javac`).
4.  **Run:** Execute the `main` method of each project's main class to run the corresponding simulation or analysis.

## Further Development

These projects serve as basic examples and can be extended in various ways, including:

* Implementing more sophisticated models and algorithms.
* Adding graphical user interfaces (GUIs) for easier interaction.
* Reading data from external files or databases.
* Implementing more advanced analysis and control techniques.
* Expanding the scope to larger and more complex systems.
