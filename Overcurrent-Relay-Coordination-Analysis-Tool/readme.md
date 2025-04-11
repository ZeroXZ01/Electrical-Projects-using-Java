# Overcurrent Relay Coordination Analysis Tool in Java

This project provides a Java-based tool for analyzing the time-current characteristics of overcurrent relays and assessing their coordination in a power system. It defines relay parameters, calculates tripping times for various fault currents based on standard inverse time characteristics, visualizes these characteristics using JFreeChart with logarithmic axes, and performs a basic coordination analysis between selected relays.

## Key Features

* **Relay Data Representation:** Defines relay parameters:
    * **Relay ID:** A unique identifier for the relay.
    * **Pickup Current (pu):** The current level above which the relay starts to time out, expressed as a multiple of the relay's current transformer (CT) primary rated current.
    * **Time Multiplier Setting (TMS):** A setting that scales the time-current characteristic curve, effectively adjusting the tripping time for a given fault current.
    * **Characteristic Type:** Selects the inverse time characteristic curve of the relay (Inverse, Very Inverse, Extremely Inverse).
* **Fault Current Input:** Allows defining fault current magnitudes at different locations in the power system, expressed in per-unit (pu) of the system's base current at the fault location.
* **Tripping Time Calculation:** Implements the standard IEC inverse time characteristic equations to calculate the tripping time of the overcurrent relays.
* **Time-Current Characteristic Plot:** Generates a plot visualizing the tripping time of the relays against the fault current (as a multiple of the pickup current) using JFreeChart with logarithmic axes for both current and time.
* **Basic Coordination Analysis:** Compares the tripping times of a primary and a backup relay for a specific fault location to assess the coordination time interval, which should be within a specified range for proper selective tripping.

## Equations Used for Tripping Time Calculation

The tripping time ($T$) of an overcurrent relay with an inverse time characteristic is calculated using the following general formula based on the IEC standard:

$$T = TMS \times \frac{k}{((\frac{I_{fault}}{I_{pickup}})^n - 1)}$$

Where:

* $T$ = Tripping time in seconds.
* $TMS$ = Time Multiplier Setting (a user-defined setting).
* $I_{fault}$ = The fault current magnitude.
* $I_{pickup}$ = The relay's pickup current setting.
* $k$ and $n$ = Constants that define the specific inverse time characteristic curve:

    * **Inverse:** $k = 0.14$, $n = 0.02$
    * **Very Inverse:** $k = 13.5$, $n = 1.0$
    * **Extremely Inverse:** $k = 80.0$, $n = 2.0$

The tool implements this formula within the `calculateTrippingTime()` method, selecting the appropriate $k$ and $n$ values based on the relay's `characteristicType`.

## Key Java Libraries Used

* **JFreeChart:** For creating the logarithmic axis time-current characteristic plots, enabling clear visualization of the relay operating curves.
* **Java Collections Framework:** For managing relay data (`ArrayList`) and mapping fault locations to current values (`HashMap`).

## How to Run

1.  **Clone the Repository:** Clone this repository to your local machine.
2.  **Install JFreeChart:** Ensure that the JFreeChart library (`jfreechart-1.5.4.jar` and `jcommon-1.0.23.jar` or the latest stable versions) are included in your project's classpath. If you are using a build tool like Maven, the `pom.xml` file is already configured to download these dependencies.
3.  **Compile:** Compile the `OvercurrentRelayAnalysis.java` file using a Java compiler (e.g., `javac OvercurrentRelayAnalysis.java`).
4.  **Run:** Execute the `main` method of the `OvercurrentRelayAnalysis` class (e.g., `java OvercurrentRelayAnalysis`). A window will appear displaying the time-current characteristic curves of the defined relays, and the basic coordination analysis will be printed to the console.

## Project Structure

* `OvercurrentRelayAnalysis.java`: Contains the main class with methods for loading relay and fault data, calculating tripping times based on the IEC inverse time characteristic equations, generating the logarithmic time-current characteristic plot using JFreeChart, and performing a basic coordination analysis.

## Further Development

This tool can be extended in several ways:

* **Data Input from Files:** Implement functionality to read relay parameters and fault current data from external files (e.g., CSV, XML).
* **More Comprehensive Coordination Logic:** Implement more advanced coordination rules, including consideration of different fault types, multiple fault locations, and coordination time margins.
* **Graphical User Interface (GUI):** Develop a user-friendly GUI for easier data input, visualization, and analysis of coordination results.
* **Standard Relay Library:** Expand the tool to include a library of predefined standard relay characteristic curves and parameters.
* **Fault Current Calculation:** Integrate with fault analysis tools or implement basic fault current calculation methods.
* **Reporting:** Generate detailed reports on the relay characteristics and coordination analysis.

## Author

[Your Name/Organization]

## License

[Your License (e.g., MIT License, Apache License 2.0)]