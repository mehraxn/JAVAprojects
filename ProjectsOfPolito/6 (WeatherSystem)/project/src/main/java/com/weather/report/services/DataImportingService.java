package com.weather.report.services;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.Collection;

import com.weather.report.model.ThresholdType;
import com.weather.report.model.entities.Measurement;
import com.weather.report.model.entities.Network;
import com.weather.report.model.entities.Operator;
import com.weather.report.model.entities.Sensor;
import com.weather.report.model.entities.Threshold;
import com.weather.report.repositories.CRUDRepository;

/**
 * Service responsible for importing measurements from CSV files and validating
 * them
 * against sensor thresholds, triggering notifications when needed (see README).
 */
public class DataImportingService {

  private static final double EPSILON = 0.000000001;

  private DataImportingService() {
    // utility class
  }

  /**
   * Reads measurements from CSV files, persists them through repositories and
   * invokes {@link #checkMeasurement(Measurement)} after each insertion.
   * The time window format and CSV location are defined in the README.
   *
   * @param filePath path to the CSV file to import
   */
  public static void storeMeasurements(String filePath) {
    CRUDRepository<Measurement, Long> measurementRepository = new CRUDRepository<>(Measurement.class);
    
    try {
      BufferedReader reader = new BufferedReader(new FileReader(filePath));
      String line = reader.readLine();
      
      if (line != null) {
        line = reader.readLine();
      }
      
      while (line != null) {
        
        if (line.trim().equals("")) {
          line = reader.readLine();
          continue;
        }
        
        String[] parts = line.split(",");
        
        if (parts.length >= 5) {
          
          String dateString = parts[0].trim();
          String networkCode = parts[1].trim();
          String gatewayCode = parts[2].trim();
          String sensorCode = parts[3].trim();
          double value = Double.parseDouble(parts[4].trim());
          
          String[] dateTimeParts = dateString.split(" ");
          String[] dateParts = dateTimeParts[0].split("-");
          String[] timeParts = dateTimeParts[1].split(":");
          
          int year = Integer.parseInt(dateParts[0]);
          int month = Integer.parseInt(dateParts[1]);
          int day = Integer.parseInt(dateParts[2]);
          int hour = Integer.parseInt(timeParts[0]);
          int minute = Integer.parseInt(timeParts[1]);
          int second = Integer.parseInt(timeParts[2]);
          
          LocalDateTime timestamp = LocalDateTime.of(year, month, day, hour, minute, second);
          
          Measurement measurement = new Measurement(networkCode, gatewayCode, sensorCode, value, timestamp);
          
          measurementRepository.create(measurement);
          
          checkMeasurement(measurement);
        }
        
        line = reader.readLine();
      }
      
      reader.close();
      
    } catch (Exception e) {
      throw new RuntimeException("Error reading CSV file: " + filePath);
    }
  }

  /**
   * Validates the saved measurement against the threshold of the corresponding
   * sensor and notifies operators when the value is out of bounds.
   */
  private static void checkMeasurement(Measurement measurement) {
    
    CRUDRepository<Sensor, String> sensorRepository = new CRUDRepository<>(Sensor.class);
    Sensor currentSensor = sensorRepository.read().stream()
        .filter(s -> measurement.getSensorCode().equals(s.getCode()))
        .findFirst()
        .orElse(null);

    
    boolean sensorExists = currentSensor != null;
    if (sensorExists) {
      Threshold sensorThreshold = currentSensor.getThreshold();
      boolean sensorHasThreshold = sensorThreshold != null;
      
      if (sensorHasThreshold) {
        double measurementValue = measurement.getValue();
        double thresholdValue = sensorThreshold.getValue();
        ThresholdType thresholdType = sensorThreshold.getType();
        
        boolean violationDetected = false;
        
        if (thresholdType == ThresholdType.LESS_THAN) {
          violationDetected = measurementValue < thresholdValue;
        } else if (thresholdType == ThresholdType.GREATER_THAN) {
          violationDetected = measurementValue > thresholdValue;
        } else if (thresholdType == ThresholdType.LESS_OR_EQUAL) {
          violationDetected = measurementValue <= thresholdValue;
        } else if (thresholdType == ThresholdType.GREATER_OR_EQUAL) {
          violationDetected = measurementValue >= thresholdValue;
        } else if (thresholdType == ThresholdType.EQUAL) {
          violationDetected = Math.abs(measurementValue - thresholdValue) < EPSILON;
        } else if (thresholdType == ThresholdType.NOT_EQUAL) {
          violationDetected = Math.abs(measurementValue - thresholdValue) >= EPSILON;
        }

        if (violationDetected) {
          CRUDRepository<Network, String> networkRepository = new CRUDRepository<>(Network.class);
          Network network = networkRepository.read(measurement.getNetworkCode());
          
          if (network != null) {
            Collection<Operator> operators = network.getOperators();
            if (operators != null && !operators.isEmpty()) {
                AlertingService.notifyThresholdViolation(operators, currentSensor.getCode());
            }
          }
        }
      }
    }
  }

}