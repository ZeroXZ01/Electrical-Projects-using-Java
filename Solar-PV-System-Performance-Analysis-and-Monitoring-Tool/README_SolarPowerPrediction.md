# Solar Power Prediction using Simple Linear Regression in Java

This project contains a Java class (`SolarPowerPrediction.java`) that analyzes the linear relationships between individual weather features and solar power output from a dataset using simple linear regression. It utilizes the Apache Commons Math library for statistical analysis.

## Class: `SolarPowerPrediction.java`

This class focuses on using simple linear regression to understand how solar irradiance, temperature, humidity, and wind speed individually correlate with the generated solar power.

**Key Features:**

* **Data Loading:** Reads solar data (including weather features and power output) from a CSV file (`solar_power_data.csv`).
* **Simple Linear Regression:** Performs a separate linear regression analysis for each of the following weather features against the solar power output:
    * Solar Irradiance vs. Power Output
    * Temperature vs. Power Output
    * Humidity vs. Power Output
    * Wind Speed vs. Power Output
* **Relationship Analysis:** For each regression, the class calculates and displays:
    * **Slope:** The change in power output for a unit change in the weather feature.
    * **Intercept:** The predicted power output when the weather feature is zero.
    * **Coefficient of Determination (R-squared):** A measure of how well the linear model fits the data for that specific feature.
* **Basic Prediction Example:** Includes a rudimentary example of using the derived linear model to predict power output based on the last observed value of each weather feature.

**Equations Used:**

**1. Simple Linear Regression Model:**

The relationship between the solar power output ($y$) and a single weather feature ($x$) is modeled by a linear equation:

$$y = \beta_0 + \beta_1 x + \epsilon$$

where:
* $y$ is the predicted solar power output.
* $x$ is the independent weather feature (solar irradiance, temperature, humidity, or wind speed).
* $\beta_0$ is the intercept, representing the estimated power output when the weather feature has a value of zero.
* $\beta_1$ is the slope, indicating the change in power output for each unit increase in the weather feature.
* $\epsilon$ is the error term, representing the difference between the actual and predicted power output.

The `SimpleRegression` class from the Apache Commons Math library uses the method of least squares to estimate the values of $\beta_0$ and $\beta_1$ that best fit the provided data.

**2. Coefficient of Determination (R-squared):**

The R-squared value ($R^2$) quantifies the proportion of the variance in the power output that can be explained by the linear relationship with the chosen weather feature. It ranges from 0 to 1:

$$R^2 = 1 - \frac{\sum(y_i - \hat{y}_i)^2}{\sum(y_i - \bar{y})^2}$$

where:
* $y_i$ is the actual power output for a given data point.
* $\hat{y}_i$ is the predicted power output from the linear regression model for that data point.
* $\bar{y}$ is the mean of all actual power output values.

A higher R-squared value suggests that the linear model provides a better fit to the data.

**How to Run:**

1.  **Prerequisites:** You need to have Java Development Kit (JDK) installed and the Apache Commons Math library included in your project (if using Maven, ensure the dependency is in your `pom.xml`).
    ```xml
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-math3</artifactId>
        <version>3.6.1</version> </dependency>
    ```
2.  **Data File:** Ensure you have a CSV file named `solar_power_data.csv` in the same directory as your Java code (or provide the correct path in the code). The CSV should contain columns for `timestamp`, `solar_irradiance`, `temperature`, `humidity`, `wind_speed`, and `power_output`.
3.  **Compilation:** Compile the `SolarPowerPrediction.java` file using a Java compiler: `javac SolarPowerPrediction.java`
4.  **Execution:** Run the compiled class: `java SolarPowerPrediction`

**Data File (`solar_power_data.csv`)**

The class is designed to read data from a CSV file named `solar_power_data.csv`. The expected format is:

```csv
timestamp,solar_irradiance,temperature,humidity,wind_speed,power_output
1672531200,100,25.5,70,5.2,25
1672531800,150,26.1,68,5.8,38
... (more data rows)