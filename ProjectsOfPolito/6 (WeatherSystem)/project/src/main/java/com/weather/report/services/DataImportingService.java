package com.weather.report.services;

import java.io.BufferedReader; //ADDED FOR R1
import java.io.FileReader; //ADDED FOR R1
import java.io.IOException; //ADDED FOR R1
import java.time.LocalDateTime; //ADDED FOR R1
import java.time.format.DateTimeFormatter; //ADDED FOR R1

import com.weather.report.WeatherReport; //ADDED FOR R1
import com.weather.report.model.ThresholdType; //ADDED FOR R1
import com.weather.report.model.entities.Measurement;
import com.weather.report.model.entities.Network; //ADDED FOR R1
import com.weather.report.model.entities.Sensor;
import com.weather.report.model.entities.Threshold; //ADDED FOR R1
import com.weather.report.repositories.CRUDRepository;
import com.weather.report.repositories.MeasurementRepository; //ADDED FOR R1

/**
 * Service responsible for importing measurements from CSV files and validating
 * them
 * against sensor thresholds, triggering notifications when needed (see README).
 */
public class DataImportingService {

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
    MeasurementRepository repo = new MeasurementRepository(); //ADDED FOR R1
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(WeatherReport.DATE_FORMAT); //ADDED FOR R1

    try { //ADDED FOR R1
      FileReader reader = new FileReader(filePath); //ADDED FOR R1
      BufferedReader br = new BufferedReader(reader); //ADDED FOR R1
      String line = br.readLine(); //Skip header //ADDED FOR R1

      while ((line = br.readLine()) != null) { //ADDED FOR R1
        String[] parts = line.split(","); //ADDED FOR R1
        if (parts.length >= 5) { //ADDED FOR R1
          LocalDateTime date = LocalDateTime.parse(parts[0].trim(), formatter); //ADDED FOR R1
          String netCode = parts[1].trim(); //ADDED FOR R1
          String gwCode = parts[2].trim(); //ADDED FOR R1
          String sensorCode = parts[3].trim(); //ADDED FOR R1
          double value = Double.parseDouble(parts[4].trim()); //ADDED FOR R1

          Measurement m = new Measurement(netCode, gwCode, sensorCode, value, date); //ADDED FOR R1
          repo.create(m); //ADDED FOR R1
          checkMeasurement(m); //ADDED FOR R1
        } //ADDED FOR R1
      } //ADDED FOR R1
      br.close(); //ADDED FOR R1
    } catch (IOException e) { //ADDED FOR R1
      e.printStackTrace(); //ADDED FOR R1
    } //ADDED FOR R1
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
    if (currentSensor != null && currentSensor.getThreshold() != null) { //ADDED FOR R1
      Threshold t = currentSensor.getThreshold(); //ADDED FOR R1
      boolean violation = false; //ADDED FOR R1
      double val = measurement.getValue(); //ADDED FOR R1
      double thrVal = t.getValue(); //ADDED FOR R1
      ThresholdType type = t.getType(); //ADDED FOR R1

      if (type == ThresholdType.LESS_THAN) { //ADDED FOR R1
        violation = val < thrVal; //ADDED FOR R1
      } else if (type == ThresholdType.GREATER_THAN) { //ADDED FOR R1
        violation = val > thrVal; //ADDED FOR R1
      } else if (type == ThresholdType.LESS_OR_EQUAL) { //ADDED FOR R1
        violation = val <= thrVal; //ADDED FOR R1
      } else if (type == ThresholdType.GREATER_OR_EQUAL) { //ADDED FOR R1
        violation = val >= thrVal; //ADDED FOR R1
      } else if (type == ThresholdType.EQUAL) { //ADDED FOR R1
        violation = val == thrVal; //ADDED FOR R1
      } else if (type == ThresholdType.NOT_EQUAL) { //ADDED FOR R1
        violation = val != thrVal; //ADDED FOR R1
      } //ADDED FOR R1

      if (violation) { //ADDED FOR R1
        CRUDRepository<Network, String> netRepo = new CRUDRepository<>(Network.class); //ADDED FOR R1
        Network net = netRepo.read(measurement.getNetworkCode()); //ADDED FOR R1
        if (net != null && net.getOperators() != null && !net.getOperators().isEmpty()) { //ADDED FOR R1
          AlertingService.notifyThresholdViolation(net.getOperators(), currentSensor.getName()); //ADDED FOR R1
        } //ADDED FOR R1
      } //ADDED FOR R1
    } //ADDED FOR R1
  }

}