package com.weather.report.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
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

  private static final double EPSILON = 0.000000001;                                            //ADDED FOR R1

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
    CRUDRepository<Measurement, Long> measurementRepo = new CRUDRepository<>(Measurement.class);  //ADDED FOR R2
    String decodedPath = URLDecoder.decode(filePath, StandardCharsets.UTF_8);                                 //ADDED FOR R2
    if (decodedPath.startsWith("/")                                                                   //ADDED FOR R2
        && decodedPath.length() > 2                                                                           //ADDED FOR R2
        && Character.isLetter(decodedPath.charAt(1))                                                    //ADDED FOR R2
        && decodedPath.charAt(2) == ':') {                                                              //ADDED FOR R2
      decodedPath = decodedPath.substring(1);                                                       //ADDED FOR R2
    }

    Path csvFile;                                                                               //ADDED FOR R2
    try {                                                                                       //ADDED FOR R2
      csvFile = Paths.get(decodedPath);                                                         //ADDED FOR R2
    } catch (InvalidPathException e) {                                                          //ADDED FOR R2
      throw new RuntimeException("Failed to import measurements from CSV: " + filePath, e);     //ADDED FOR R2
    }                                                                                           //ADDED FOR R2

    try (BufferedReader br = Files.newBufferedReader(csvFile)) {                                //ADDED FOR R2

      String row;                                                                               //ADDED FOR R2
      
      // Simple header skipping logic (Required for R1 test data compatibility)
      br.mark(4096);                                                                            //ADDED FOR R2
      String firstLine = br.readLine();                                                         //ADDED FOR R2
      if (firstLine != null && !firstLine.isEmpty() && !Character.isDigit(firstLine.charAt(0))) {//ADDED FOR R2
          // It is likely a header, do nothing (we just consumed it)                            //ADDED FOR R2
      } else {                                                                                  //ADDED FOR R2
          // It looks like data, go back to start                                               //ADDED FOR R2
          br.reset();                                                                           //ADDED FOR R2
      }                                                                                         //ADDED FOR R2

      while ((row = br.readLine()) != null) {                                                   //ADDED FOR R2

        row = row.trim();                                                                       //ADDED FOR R2
        if (row.isEmpty()) {                                                                    //ADDED FOR R2
          continue;                                                                             //ADDED FOR R2
        }                                                                                       //ADDED FOR R2

        String[] fields = row.split(",");                                                       //ADDED FOR R2

        if (fields.length < 5) {                                                                //ADDED FOR R2
          continue;                                                                             //ADDED FOR R2
        }

        String dateString = fields[0].trim();                                                   //ADDED FOR R2
        String network = fields[1].trim();                                                      //ADDED FOR R2
        String gateway = fields[2].trim();                                                      //ADDED FOR R2
        String sensor = fields[3].trim();                                                       //ADDED FOR R2
        String valueStr = fields[4].trim();                                                     //ADDED FOR R2

        // Manual Parsing (R1 Logic) to handle test data formats that R2 parser rejects
        String[] dateTimeParts = dateString.split(" ");                                         //ADDED FOR R2
        String datePart = dateTimeParts[0];                                                     //ADDED FOR R2
        String timePart = dateTimeParts[1];                                                     //ADDED FOR R2
        
        String[] datePieces = datePart.split("-");                                              //ADDED FOR R2
        int year = Integer.valueOf(datePieces[0]);                                              //ADDED FOR R2
        int month = Integer.valueOf(datePieces[1]);                                             //ADDED FOR R2
        int day = Integer.valueOf(datePieces[2]);                                               //ADDED FOR R2
        
        String[] timePieces = timePart.split(":");                                              //ADDED FOR R2
        int hour = Integer.valueOf(timePieces[0]);                                              //ADDED FOR R2
        int minute = Integer.valueOf(timePieces[1]);                                            //ADDED FOR R2
        int second = Integer.valueOf(timePieces[2]);                                            //ADDED FOR R2
        
        LocalDateTime timestamp = LocalDateTime.of(year, month, day, hour, minute, second);     //ADDED FOR R2
        double value = Double.valueOf(valueStr);                                                //ADDED FOR R2

        Measurement m = new Measurement(network, gateway, sensor, value, timestamp);            //ADDED FOR R2
        measurementRepo.create(m);                                                              //ADDED FOR R2

        checkMeasurement(m);                                                                    //ADDED FOR R2
      } 

    } catch (IOException e) {                                                                   //ADDED FOR R2
      throw new RuntimeException("Failed to import measurements from CSV: " + filePath, e);     //ADDED FOR R2
    } 
  }

  /**
   * Validates the saved measurement against the threshold of the corresponding
   * sensor and notifies operators when the value is out of bounds.
   */
  private static void checkMeasurement(Measurement measurement) {
    /***********************************************************************/
    /* Do not change these lines, use currentSensor to check for possible */
    /* threshold violation, tests mocks this db interaction */
    /***********************************************************************/
    CRUDRepository<Sensor, String> sensorRepository = new CRUDRepository<>(Sensor.class);
    Sensor currentSensor = sensorRepository.read().stream()
        .filter(s -> measurement.getSensorCode().equals(s.getCode()))
        .findFirst()
        .orElse(null);
    /***********************************************************************/
    
    boolean sensorExists = currentSensor != null;                                               //ADDED FOR R1
    if (sensorExists) {                                                                         //ADDED FOR R1
      Threshold sensorThreshold = currentSensor.getThreshold();                                 //ADDED FOR R1
      boolean sensorHasThreshold = sensorThreshold != null;                                     //ADDED FOR R1
      
      if (sensorHasThreshold) {                                                                 //ADDED FOR R1
        double measurementValue = measurement.getValue();                                       //ADDED FOR R1
        double thresholdValue = sensorThreshold.getValue();                                     //ADDED FOR R1
        ThresholdType thresholdType = sensorThreshold.getType();                                //ADDED FOR R1
        
        boolean violationDetected = false;                                                      //ADDED FOR R1
        
        if (thresholdType == ThresholdType.LESS_THAN) {                                         //ADDED FOR R1
          violationDetected = measurementValue < thresholdValue;                                //ADDED FOR R1
        } else if (thresholdType == ThresholdType.GREATER_THAN) {                               //ADDED FOR R1
          violationDetected = measurementValue > thresholdValue;                                //ADDED FOR R1
        } else if (thresholdType == ThresholdType.LESS_OR_EQUAL) {                              //ADDED FOR R1
          violationDetected = measurementValue <= thresholdValue;                               //ADDED FOR R1
        } else if (thresholdType == ThresholdType.GREATER_OR_EQUAL) {                           //ADDED FOR R1
          violationDetected = measurementValue >= thresholdValue;                               //ADDED FOR R1
        } else if (thresholdType == ThresholdType.EQUAL) {                                      //ADDED FOR R1
          violationDetected = Math.abs(measurementValue - thresholdValue) < EPSILON;            //ADDED FOR R1
        } else if (thresholdType == ThresholdType.NOT_EQUAL) {                                  //ADDED FOR R1
          violationDetected = Math.abs(measurementValue - thresholdValue) >= EPSILON;           //ADDED FOR R1
        }

        if (violationDetected) {                                                                //ADDED FOR R1
          CRUDRepository<Network, String> networkRepository = new CRUDRepository<>(Network.class);//ADDED FOR R1
          Network network = networkRepository.read(measurement.getNetworkCode());               //ADDED FOR R1
          
          if (network != null) {                                                                //ADDED FOR R1
            Collection<Operator> operators = network.getOperators();                            //ADDED FOR R1
            if (operators != null && !operators.isEmpty()) {                                    //ADDED FOR R1
                // Notify using Sensor Code (required for R1 Tests)
                AlertingService.notifyThresholdViolation(operators, currentSensor.getCode());   //ADDED FOR R1
            }
          }
        }
      }
    }
  }

}