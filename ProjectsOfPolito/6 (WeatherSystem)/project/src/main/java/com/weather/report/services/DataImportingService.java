package com.weather.report.services;

import java.io.BufferedReader;                                          //ADDED FOR R1 //Necessary for file reading and method storeMeasurements
import java.io.FileReader;                                              //ADDED FOR R1 //Necessary for file reading and method storeMeasurements
import java.io.IOException;                                             //ADDED FOR R1
import java.time.LocalDateTime;                                         //ADDED FOR R1 //Necessary for date parsing and method storeMeasurements
import java.util.Collection;                                            //ADDED FOR R1

import com.weather.report.model.ThresholdType;                          //ADDED FOR R1
import com.weather.report.model.entities.Measurement;                   //ADDED FOR R1
import com.weather.report.model.entities.Network;                       //ADDED FOR R1
import com.weather.report.model.entities.Operator;                      //ADDED FOR R1
import com.weather.report.model.entities.Sensor;                        //ADDED FOR R1
import com.weather.report.model.entities.Threshold;                     //ADDED FOR R1
import com.weather.report.repositories.CRUDRepository;
import com.weather.report.repositories.MeasurementRepository;           //ADDED FOR R1

/**
 * Service responsible for importing measurements from CSV files and validating
 * them
 * against sensor thresholds, triggering notifications when needed (see README).
 */
public class DataImportingService {

  private static final double EPSILON = 0.000000001;                    //ADDED FOR R1

  private DataImportingService(){
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
    
    MeasurementRepository measurementRepository = new MeasurementRepository();                     //ADDED FOR R1

    try {                                                                                           //ADDED FOR R1
      FileReader fileReader = new FileReader(filePath);                                             //ADDED FOR R1
      BufferedReader bufferedReader = new BufferedReader(fileReader);                               //ADDED FOR R1
      
      String headerLine = bufferedReader.readLine();                                                //ADDED FOR R1 //Skip header
      
      String currentLine = bufferedReader.readLine();                                               //ADDED FOR R1
      while (currentLine != null) {                                                                 //ADDED FOR R1
        String[] csvParts = currentLine.split(",");                                                 //ADDED FOR R1
        
        boolean lineHasEnoughData = csvParts.length >= 5;                                           //ADDED FOR R1
        if (lineHasEnoughData) {                                                                    //ADDED FOR R1
          
          String dateString = csvParts[0].trim();                                                   //ADDED FOR R1
          String networkCode = csvParts[1].trim();                                                  //ADDED FOR R1
          String gatewayCode = csvParts[2].trim();                                                  //ADDED FOR R1
          String sensorCode = csvParts[3].trim();                                                   //ADDED FOR R1
          String valueString = csvParts[4].trim();                                                  //ADDED FOR R1
          
          String[] dateTimeParts = dateString.split(" ");                                           //ADDED FOR R1
          String datePart = dateTimeParts[0];                                                       //ADDED FOR R1
          String timePart = dateTimeParts[1];                                                       //ADDED FOR R1
          
          String[] datePieces = datePart.split("-");                                                //ADDED FOR R1
          int year = Integer.valueOf(datePieces[0]);                                                //ADDED FOR R1
          int month = Integer.valueOf(datePieces[1]);                                               //ADDED FOR R1
          int day = Integer.valueOf(datePieces[2]);                                                 //ADDED FOR R1
          
          String[] timePieces = timePart.split(":");                                                //ADDED FOR R1
          int hour = Integer.valueOf(timePieces[0]);                                                //ADDED FOR R1
          int minute = Integer.valueOf(timePieces[1]);                                              //ADDED FOR R1
          int second = Integer.valueOf(timePieces[2]);                                              //ADDED FOR R1
          
          LocalDateTime measurementDate = LocalDateTime.of(year, month, day, hour, minute, second); //ADDED FOR R1
          double measurementValue = Double.valueOf(valueString);                                    //ADDED FOR R1

          Measurement measurement = new Measurement(networkCode, gatewayCode, sensorCode, measurementValue, measurementDate);  //ADDED FOR R1
          
          measurementRepository.create(measurement);                                                //ADDED FOR R1
          checkMeasurement(measurement);                                                            //ADDED FOR R1
        }                                                                                           //ADDED FOR R1
        
        currentLine = bufferedReader.readLine();                                                    //ADDED FOR R1
      }                                                                                             //ADDED FOR R1
      
      bufferedReader.close();                                                                       //ADDED FOR R1
    } catch (IOException exception) {                                                               //ADDED FOR R1
      exception.printStackTrace();                                                                  //ADDED FOR R1
    }                                                                                               //ADDED FOR R1
  }

  /**
   * Validates the saved measurement against the threshold of the corresponding
   * sensor
   * and notifies operators when the value is out of bounds. To be implemented in
   * R1.
   *
   * @param measurement newly stored measurement
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
    
    boolean sensorExists = currentSensor != null;                                                  //ADDED FOR R1
    if (sensorExists) {                                                                            //ADDED FOR R1
      
      Threshold sensorThreshold = currentSensor.getThreshold();                                    //ADDED FOR R1
      boolean sensorHasThreshold = sensorThreshold != null;                                        //ADDED FOR R1
      
      if (sensorHasThreshold) {                                                                    //ADDED FOR R1
        
        double measurementValue = measurement.getValue();                                          //ADDED FOR R1
        double thresholdValue = sensorThreshold.getValue();                                        //ADDED FOR R1
        ThresholdType thresholdType = sensorThreshold.getType();                                   //ADDED FOR R1
        
        boolean violationDetected = false;                                                         //ADDED FOR R1
        
        boolean isLessThanType = thresholdType == ThresholdType.LESS_THAN;                         //ADDED FOR R1
        if (isLessThanType) {                                                                      //ADDED FOR R1
          boolean valueLessThanThreshold = measurementValue < thresholdValue;                      //ADDED FOR R1
          violationDetected = valueLessThanThreshold;                                              //ADDED FOR R1
        }                                                                                          //ADDED FOR R1
        
        boolean isGreaterThanType = thresholdType == ThresholdType.GREATER_THAN;                   //ADDED FOR R1
        if (isGreaterThanType) {                                                                   //ADDED FOR R1
          boolean valueGreaterThanThreshold = measurementValue > thresholdValue;                   //ADDED FOR R1
          violationDetected = valueGreaterThanThreshold;                                           //ADDED FOR R1
        }                                                                                          //ADDED FOR R1
        
        boolean isLessOrEqualType = thresholdType == ThresholdType.LESS_OR_EQUAL;                  //ADDED FOR R1
        if (isLessOrEqualType) {                                                                   //ADDED FOR R1
          boolean valueLessOrEqualThreshold = measurementValue <= thresholdValue;                  //ADDED FOR R1
          violationDetected = valueLessOrEqualThreshold;                                           //ADDED FOR R1
        }                                                                                          //ADDED FOR R1
        
        boolean isGreaterOrEqualType = thresholdType == ThresholdType.GREATER_OR_EQUAL;            //ADDED FOR R1
        if (isGreaterOrEqualType) {                                                                //ADDED FOR R1
          boolean valueGreaterOrEqualThreshold = measurementValue >= thresholdValue;               //ADDED FOR R1
          violationDetected = valueGreaterOrEqualThreshold;                                        //ADDED FOR R1
        }                                                                                          //ADDED FOR R1
        
        boolean isEqualType = thresholdType == ThresholdType.EQUAL;                                //ADDED FOR R1
        if (isEqualType) {                                                                         //ADDED FOR R1
          double difference = Math.abs(measurementValue - thresholdValue);                         //ADDED FOR R1
          boolean valuesAreEqual = difference < EPSILON;                                           //ADDED FOR R1
          violationDetected = valuesAreEqual;                                                      //ADDED FOR R1
        }                                                                                          //ADDED FOR R1
        
        boolean isNotEqualType = thresholdType == ThresholdType.NOT_EQUAL;                         //ADDED FOR R1
        if (isNotEqualType) {                                                                      //ADDED FOR R1
          double difference = Math.abs(measurementValue - thresholdValue);                         //ADDED FOR R1
          boolean valuesAreNotEqual = difference >= EPSILON;                                       //ADDED FOR R1
          violationDetected = valuesAreNotEqual;                                                   //ADDED FOR R1
        }                                                                                          //ADDED FOR R1

        if (violationDetected) {                                                                   //ADDED FOR R1
          
          CRUDRepository<Network, String> networkRepository = new CRUDRepository<>(Network.class); //ADDED FOR R1
          String networkCode = measurement.getNetworkCode();                                       //ADDED FOR R1
          Network network = networkRepository.read(networkCode);                                   //ADDED FOR R1
          
          boolean networkExists = network != null;                                                 //ADDED FOR R1
          if (networkExists) {                                                                     //ADDED FOR R1
            
            Collection<Operator> operators = network.getOperators();                               //ADDED FOR R1
            boolean networkHasOperators = operators != null;                                       //ADDED FOR R1
            
            if (networkHasOperators) {                                                             //ADDED FOR R1
              boolean operatorsListNotEmpty = !operators.isEmpty();                                //ADDED FOR R1
              
              if (operatorsListNotEmpty) {                                                         //ADDED FOR R1
                String sensorName = currentSensor.getName();                                       //ADDED FOR R1
                AlertingService.notifyThresholdViolation(operators, sensorName);                   //ADDED FOR R1
              }                                                                                    //ADDED FOR R1
            }                                                                                      //ADDED FOR R1
          }                                                                                        //ADDED FOR R1
        }                                                                                          //ADDED FOR R1
      }                                                                                            //ADDED FOR R1
    }                                                                                              //ADDED FOR R1
  }

}