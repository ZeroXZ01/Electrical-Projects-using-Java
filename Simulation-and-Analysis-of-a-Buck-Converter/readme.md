# Buck Converter Simulation in Java

This project simulates the behavior of a buck DC-DC converter, a fundamental power electronic circuit that steps down a DC voltage. The simulation uses a discrete-time step approach based on the Euler method to model the inductor current and capacitor voltage waveforms over time. The results are visualized using JFreeChart.

## Key Features

* **Buck Converter Modeling:** Simulates the core components of a buck converter: an inductor (L), a capacitor (C), a resistor (R representing the load), and an ideal switch controlled by a Pulse Width Modulation (PWM) signal.
* **Pulse Width Modulation (PWM):** Implements a simple PWM scheme based on a fixed switching frequency and duty cycle to control the switch.
* **Discrete-Time Simulation:** Uses the Euler method, a first-order numerical method, to approximate the continuous-time behavior of the circuit at discrete time steps.
* **Waveform Visualization:** Generates plots of the inductor current and output voltage (capacitor voltage) over the simulation time using the JFreeChart library.
* **Average Output Voltage Calculation:** Calculates an approximate average output voltage from the simulation results.
* **Comparison with Theoretical Value:** Compares the simulated average output voltage with the theoretical output voltage for an ideal buck converter in continuous conduction mode (CCM).

## Equations Used in the Simulation

The simulation is based on the fundamental voltage-current relationships for the inductor and capacitor, and the switching behavior controlled by the PWM signal.

**1. Inductor Voltage-Current Relationship:**
The voltage across an inductor ($V_L$) is related to the rate of change of current through it ($\frac{di_L}{dt}$) by:
$$V_L = L \frac{di_L}{dt}$$

**2. Capacitor Voltage-Current Relationship:**
The current through a capacitor ($i_C$) is related to the rate of change of voltage across it ($\frac{dv_C}{dt}$) by:
$$i_C = C \frac{dv_C}{dt}$$

**3. Circuit Behavior During Switch ON (Time $0 < t < D \cdot T_{sw}$):**
When the switch (typically a MOSFET) is ON, the diode is OFF. The voltage across the inductor is:
$$V_L = V_{in} - v_C$$
The current through the capacitor is:
$$i_C = i_L - \frac{v_C}{R}$$

**4. Circuit Behavior During Switch OFF (Time $D \cdot T_{sw} < t < T_{sw}$):**
When the switch is OFF, the inductor current flows through the diode. The voltage across the inductor is:
$$V_L = -v_C$$
The current through the capacitor is:
$$i_C = i_L - \frac{v_C}{R}$$

**5. Euler's Method for Numerical Integration:**
The simulation uses the Euler method to approximate the values of inductor current ($i_L$) and capacitor voltage ($v_C$) at the next time step ($t + dt$) based on their current values and their derivatives:
$$i_L(t + dt) \approx i_L(t) + \frac{di_L}{dt}(t) \cdot dt$$
$$v_C(t + dt) \approx v_C(t) + \frac{dv_C}{dt}(t) \cdot dt$$
where $\frac{di_L}{dt}$ and $\frac{dv_C}{dt}$ are determined by the circuit equations for the current switch state.

**6. Theoretical Output Voltage (for ideal CCM operation):**
For an ideal buck converter operating in Continuous Conduction Mode (CCM), the average output voltage ($V_{out}$) is given by:
$$V_{out} = D \cdot V_{in}$$
where $D$ is the duty cycle and $V_{in}$ is the input voltage.

## Key Java Libraries Used

* **JFreeChart:** For creating the line charts to visualize the simulated inductor current and output voltage waveforms over time.
* **Java Collections Framework:** (`java.util.ArrayList`, `java.util.List`) for storing the time and the simulated values of inductor current and capacitor voltage.

## How to Run

1.  **Clone the Repository:** Clone this repository to your local machine.
2.  **Install JFreeChart:** Ensure that the JFreeChart library (`jfreechart-1.5.4.jar` and `jcommon-1.0.23.jar` or the latest stable versions) are included in your project's classpath. If you are using a build tool like Maven, the `pom.xml` file is already configured to download these dependencies.
3.  **Compile:** Compile the `BuckConverterSimulation.java` file using a Java compiler (e.g., `javac BuckConverterSimulation.java`).
4.  **Run:** Execute the `main` method of the `BuckConverterSimulation` class (e.g., `java BuckConverterSimulation`). A window will appear displaying the simulated inductor current and output voltage waveforms over time, and the calculated average output voltage will be printed to the console along with the theoretical value.

## Project Structure

* `BuckConverterSimulation.java`: Contains the main class with methods for defining the buck converter parameters, performing the discrete-time simulation using Euler's method, calculating the average output voltage, and creating the plots using JFreeChart.

## Further Development

This simulation can be extended in several ways:

* **More Accurate Component Models:** Implement more detailed models for the switch (e.g., MOSFET with on-resistance, switching times) and the diode (e.g., forward voltage drop).
* **Continuous and Discontinuous Conduction Mode (CCM/DCM) Detection:** Implement logic to automatically detect and simulate the converter's operation in different conduction modes.
* **Efficiency Calculation:** Calculate the power input and output to estimate the converter's efficiency.
* **Control Loop Implementation:** Simulate a closed-loop control system with a feedback mechanism (e.g., PID controller) to regulate the output voltage.
* **More Advanced Numerical Methods:** Use higher-order numerical integration methods (e.g., Runge-Kutta) for potentially more accurate simulation results.
* **Parameter Sweeps:** Implement functionality to easily simulate the converter's behavior for a range of component values or operating conditions.

## Author

[Your Name/Organization]

## License

[Your License (e.g., MIT License, Apache License 2.0)]