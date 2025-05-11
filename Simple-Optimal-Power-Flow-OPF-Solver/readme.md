# DC Optimal Power Flow Simulation in Java (Using Apache Commons Math)

This project simulates a basic Direct Current (DC) Optimal Power Flow (OPF) for a small 3-bus power system. It utilizes the BOBYQA optimizer from the Apache Commons Math library to find the optimal voltage angles that minimize the total generation cost while satisfying the power balance equations and line flow limits (although line flow limits are not strictly enforced in this simplified DC OPF).

## Key Features

* **Simple 3-Bus System:** Models a power system with three buses, including one slack bus and two load buses with potential generation.
* **DC Power Flow Model:** Uses the linearized DC power flow equations, which assume lossless lines and a flat voltage magnitude profile.
* **Generation Cost Minimization:** The objective function minimizes the total cost of real power generation.
* **BOBYQA Optimizer:** Employs the Bound Optimization BY Quadratic Approximation (BOBYQA) algorithm from Apache Commons Math for derivative-free optimization.
* **Output Display:** Prints the optimal voltage angles, total generation cost, generator dispatch, and line flows.

## Equations Used

The DC Optimal Power Flow formulation used in this simulation relies on the following equations:

**1. DC Power Flow Equation:**

The real power flow $P_{ij}$ on a transmission line between bus $i$ and bus $j$ with reactance $x_{ij}$ is approximated by:

$$P_{ij} = \frac{\theta_i - \theta_j}{x_{ij}}$$

where:
* $\theta_i$ is the voltage angle at bus $i$ (in radians).
* $\theta_j$ is the voltage angle at bus $j$ (in radians).
* $x_{ij}$ is the reactance of the line between bus $i$ and bus $j$.

**2. Power Balance Equation at Each Bus:**

At each bus $i$, the sum of power generation $P_{gi}$ and power inflow from connected lines must equal the sum of power demand $P_{di}$ and power outflow to connected lines:

$$P_{gi} - P_{di} = \sum_{j \in \mathcal{N}_i} P_{ij}$$

where $\mathcal{N}_i$ is the set of buses directly connected to bus $i$.

**3. Generator Cost Function:**

The cost of real power generation at each bus $i$ is assumed to be a linear function:

$$C_i(P_{gi}) = a_i + b_i P_{gi}$$

where:
* $C_i(P_{gi})$ is the cost of generation at bus $i$.
* $a_i$ is the constant cost coefficient.
* $b_i$ is the linear cost coefficient.
* $P_{gi}$ is the real power generation at bus $i$.

**4. Objective Function:**

The objective of the OPF is to minimize the total generation cost:

$$\text{Minimize } \sum_{i \in \mathcal{G}} C_i(P_{gi})$$

where $\mathcal{G}$ is the set of all generating buses.

**5. Generator Limits:**

The real power generation at each generator $i$ is constrained by its minimum and maximum capacity:

$$P_{gi}^{min} \leq P_{gi} \leq P_{gi}^{max}$$

**6. Voltage Angle of Slack Bus:**

One bus in the system is designated as the slack bus, and its voltage angle is fixed (usually at 0 radians):

$$\theta_{slack} = 0$$

## How the Code Works

1.  **System Data:** The code defines the bus data (demand, generation limits), line data (from bus, to bus, reactance, flow limit), and generator cost coefficients.
2.  **Optimization Variables:** The voltage angles of the non-slack buses are the optimization variables.
3.  **Objective Function (`calculateTotalCost`):** This method calculates the total generation cost based on the current voltage angles. It first determines the generator output required at each bus to meet the demand and line flows, and then calculates the cost using the linear cost coefficients.
4.  **Power Flow Calculation (`calculateGeneratorOutput`, `calculateLineFlows`):** These methods implement the DC power flow equations to determine the power injections at each bus and the power flow on each transmission line based on the voltage angles.
5.  **Optimizer (`BOBYQAOptimizer`):** The BOBYQA optimizer from Apache Commons Math is used to find the set of voltage angles that minimize the total generation cost. Bounds are set on the voltage angles (between $-\pi$ and $\pi$ radians).
6.  **Output:** The code prints the optimal voltage angles, the minimum total generation cost, the power generated at each bus, and the power flow on each line.

## How to Run

1.  **Prerequisites:** You need to have Java Development Kit (JDK) installed and a build tool like Maven.
2.  **Maven Dependency:** Ensure you have the following dependency in your `pom.xml` file:
    ```xml
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-math3</artifactId>
        <version>3.6.1</version>
    </dependency>
    ```
3.  **Build:** Use Maven to build the project: `mvn clean install`
4.  **Run:** Execute the main class `com.example.OptimalPowerFlowDC` (adjust the `mainClass` in your `pom.xml` if needed). You can typically run this from your IDE or using the Maven exec plugin: `mvn exec:java -Dexec.mainClass="com.example.OptimalPowerFlowDC"`

## Further Development

This is a basic DC OPF simulation. Potential extensions include:

* **Enforcing Line Flow Limits:** Incorporate constraints to ensure power flow on each line does not exceed its capacity. This would require a constrained optimization algorithm.
* **More Complex Cost Functions:** Implement non-linear generation cost functions.
* **AC Power Flow:** Extend the model to use the non-linear AC power flow equations for a more accurate representation of the power system.
* **Larger Systems:** Scale the simulation to handle power systems with a larger number of buses and lines.
* **Graphical Output:** Visualize the power system and the results.
