import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.regression.SimpleRegression; // Added import

public class SolarPowerPrediction {

    private static final String CSV_FILE_PATH = "solar_power_data.csv"; // Replace with your file path

    private List<Double> timestamps = new ArrayList<>();
    private List<Double> solarIrradiance = new ArrayList<>();
    private List<Double> temperature = new ArrayList<>();
    private List<Double> humidity = new ArrayList<>();
    private List<Double> windSpeed = new ArrayList<>();
    private List<Double> powerOutput = new ArrayList<>();

    private SimpleRegression irradianceRegression; // Regression for Irradiance vs. Power
    private SimpleRegression temperatureRegression; // Regression for Temperature vs. Power
    private SimpleRegression humidityRegression;   // Regression for Humidity vs. Power
    private SimpleRegression windSpeedRegression;  // Regression for Wind Speed vs. Power

    public SolarPowerPrediction() {
        irradianceRegression = new SimpleRegression();
        temperatureRegression = new SimpleRegression();
        humidityRegression = new SimpleRegression();
        windSpeedRegression = new SimpleRegression();
        loadDataFromCSV(CSV_FILE_PATH);
        analyzeDataRelationships();
        // You would likely have your more advanced prediction logic here
        // based on multiple factors or a more sophisticated model
    }

    private void loadDataFromCSV(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip header row
                }
                String[] values = line.split(",");
                if (values.length == 6) {
                    try {
                        timestamps.add(Double.parseDouble(values[0]));
                        solarIrradiance.add(Double.parseDouble(values[1]));
                        temperature.add(Double.parseDouble(values[2]));
                        humidity.add(Double.parseDouble(values[3]));
                        windSpeed.add(Double.parseDouble(values[4]));
                        powerOutput.add(Double.parseDouble(values[5]));
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing numerical value in line: " + line);
                        // Handle the error appropriately, e.g., skip the line
                    }
                } else {
                    System.err.println("Skipping line with incorrect number of values: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void analyzeDataRelationships() {
        System.out.println("--- Analyzing Relationships with Simple Linear Regression ---");

        analyzeFeatureVsPower("Solar Irradiance", solarIrradiance, irradianceRegression);
        analyzeFeatureVsPower("Temperature", temperature, temperatureRegression);
        analyzeFeatureVsPower("Humidity", humidity, humidityRegression);
        analyzeFeatureVsPower("Wind Speed", windSpeed, windSpeedRegression);

        System.out.println("-------------------------------------------------------");
    }

    private void analyzeFeatureVsPower(String featureName, List<Double> featureData, SimpleRegression regressionModel) {
        if (featureData.size() != powerOutput.size() || featureData.isEmpty()) {
            System.err.println("Error: Inconsistent data size for " + featureName + " and Power Output.");
            return;
        }

        regressionModel.clear(); // Clear previous data
        for (int i = 0; i < featureData.size(); i++) {
            regressionModel.addData(featureData.get(i), powerOutput.get(i));
        }

        double slope = regressionModel.getSlope();
        double intercept = regressionModel.getIntercept();
        double rSquared = regressionModel.getRSquare();

        System.out.println("\nRelationship: " + featureName + " vs. Power Output");
        System.out.println("  Slope (change in power per unit of " + featureName + "): " + slope);
        System.out.println("  Intercept (power when " + featureName + " is zero): " + intercept);
        System.out.println("  R-squared (goodness of linear fit): " + rSquared);

        // Basic prediction example (using the last data point's feature value)
        if (!featureData.isEmpty()) {
            double lastFeatureValue = featureData.get(featureData.size() - 1);
            double predictedPower = intercept + slope * lastFeatureValue;
            System.out.println("  Basic Prediction (using last " + featureName + " value): " + predictedPower);
        }
    }

    public static void main(String[] args) {
        new SolarPowerPrediction();
    }
}
