package com.weather.report.operations;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.weather.report.WeatherReport;
import com.weather.report.exceptions.ElementNotFoundException;
import com.weather.report.exceptions.IdAlreadyInUseException;
import com.weather.report.exceptions.InvalidInputDataException;
import com.weather.report.exceptions.UnauthorizedException;
import com.weather.report.model.ThresholdType;
import com.weather.report.model.UserType;
import com.weather.report.model.entities.Measurement;
import com.weather.report.model.entities.Sensor;
import com.weather.report.model.entities.Threshold;
import com.weather.report.model.entities.User;
import com.weather.report.reports.SensorReport;
import com.weather.report.reports.SensorReportImpl;
import com.weather.report.repositories.CRUDRepository;
import com.weather.report.repositories.MeasurementRepository;
import com.weather.report.services.AlertingService;

public class SensorOperationsImplement implements SensorOperations {

  private final CRUDRepository<Sensor, String> sensorRepository = new CRUDRepository<>(Sensor.class);
  private final CRUDRepository<User, String> userRepository = new CRUDRepository<>(User.class);

  // Helper Methods 

  private void validateMaintainer(String username) throws UnauthorizedException {
    if (username == null || username.isBlank()) {
      throw new UnauthorizedException("User is missing or unauthorized");
    }
    User u = userRepository.read(username);
    if (u == null || u.getType() != UserType.MAINTAINER) {
      throw new UnauthorizedException("User is missing or unauthorized");
    }
  }

  private LocalDateTime parseDateOrNull(String date) throws InvalidInputDataException {
    if (date == null) {
      return null;
    }
    try {
      return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(WeatherReport.DATE_FORMAT));
    } catch (DateTimeParseException e) {
      throw new InvalidInputDataException("Bad date format");
    }
  }

  // Sensor CRUD

  @Override
  public Sensor createSensor(String code, String name, String description, String username)
      throws IdAlreadyInUseException, InvalidInputDataException, UnauthorizedException {

    validateMaintainer(username);

    if (code == null || !code.matches("S_\\d{6}")) {
      throw new InvalidInputDataException("Bad code format");
    }

    Sensor sensor = new Sensor();
    sensor.setCode(code);
    sensor.setName(name);
    sensor.setDescription(description);
    sensor.setCreatedBy(username);
    sensor.setCreatedAt(LocalDateTime.now());

    try {
      sensorRepository.create(sensor);
    } catch (RuntimeException e) {
      // Map persistence duplicate errors to specific exception
      throw new IdAlreadyInUseException("Code already exists");
    }

    return sensor;
  }

  @Override
  public Sensor updateSensor(String code, String name, String description, String username)
      throws InvalidInputDataException, ElementNotFoundException, UnauthorizedException {

    validateMaintainer(username);

    if (code == null || code.isBlank()) {
      throw new InvalidInputDataException("Sensor code is mandatory");
    }

    Sensor existing = sensorRepository.read(code);
    if (existing == null) {
      throw new ElementNotFoundException("Sensor not found: " + code);
    }

    existing.setName(name);
    existing.setDescription(description);
    existing.setModifiedBy(username);
    existing.setModifiedAt(LocalDateTime.now());

    return sensorRepository.update(existing);
  }

  @Override
  public Sensor deleteSensor(String code, String username)
      throws InvalidInputDataException, ElementNotFoundException, UnauthorizedException {

    validateMaintainer(username);

    if (code == null || code.isBlank()) {
      throw new InvalidInputDataException("Sensor code is mandatory");
    }

    Sensor existing = sensorRepository.read(code);
    if (existing == null) {
      throw new ElementNotFoundException("Sensor not found: " + code);
    }

    Sensor deleted = sensorRepository.delete(code);

    // Trigger deletion notification
    AlertingService.notifyDeletion(username, code, Sensor.class);

    return deleted;
  }

  @Override
  public Collection<Sensor> getSensors(String... sensorCodes) {
    Collection<Sensor> allSensors = sensorRepository.read();

    if (sensorCodes == null || sensorCodes.length == 0) {
      return allSensors;
    }

    Set<String> wanted = new HashSet<>(Arrays.asList(sensorCodes));

    return allSensors.stream()
        .filter(s -> s.getCode() != null && wanted.contains(s.getCode()))
        .toList();
  }

  // Thresholds

  @Override
  public Threshold createThreshold(String sensorCode, ThresholdType type, double value, String username)
      throws InvalidInputDataException, ElementNotFoundException, IdAlreadyInUseException, UnauthorizedException {

    validateMaintainer(username);

    if (sensorCode == null || sensorCode.isBlank())
      throw new InvalidInputDataException("Sensor code is mandatory");
    if (type == null)
      throw new InvalidInputDataException("Threshold type is mandatory");

    Sensor sensor = sensorRepository.read(sensorCode);
    if (sensor == null) {
      throw new ElementNotFoundException("Sensor not found: " + sensorCode);
    }

    if (sensor.getThreshold() != null) {
      throw new IdAlreadyInUseException("Threshold already exists for sensor " + sensorCode);
    }

    Threshold threshold = new Threshold(type, value);
    sensor.setThreshold(threshold);
    sensorRepository.update(sensor);

    return threshold;
  }

  @Override
  public Threshold updateThreshold(String sensorCode, ThresholdType type, double value, String username)
      throws InvalidInputDataException, ElementNotFoundException, UnauthorizedException {

    validateMaintainer(username);

    if (sensorCode == null || sensorCode.isBlank())
      throw new InvalidInputDataException("Sensor code is mandatory");
    if (type == null)
      throw new InvalidInputDataException("Threshold type is mandatory");

    Sensor sensor = sensorRepository.read(sensorCode);
    if (sensor == null) {
      throw new ElementNotFoundException("Sensor not found: " + sensorCode);
    }

    Threshold threshold = sensor.getThreshold();
    if (threshold == null) {
      throw new ElementNotFoundException("Threshold not found for sensor " + sensorCode);
    }

    threshold.setType(type);
    threshold.setValue(value);
    sensorRepository.update(sensor);

    return threshold;
  }

  // Reporting

  @Override
  public SensorReport getSensorReport(String code, String startDate, String endDate)
      throws InvalidInputDataException, ElementNotFoundException {

    // Validate Input
    if (code == null || code.isBlank()) {
      throw new InvalidInputDataException("Code mandatory");
    }

    // Ensure sensor exists
    if (sensorRepository.read(code) == null) {
      throw new ElementNotFoundException("Sensor not found: " + code);
    }

    // Parse Dates
    LocalDateTime start = parseDateOrNull(startDate);
    LocalDateTime end = parseDateOrNull(endDate);

    // Fetch Measurements using the optimized repository
    MeasurementRepository mRepo = new MeasurementRepository();
    List<Measurement> measurements = mRepo.findBySensorAndDateRange(code, start, end);

    // Delegate Logic to Report Implementation
    // All math, outliers, and histogram logic is now encapsulated in the Report

    return new SensorReportImpl(code, startDate, endDate, measurements);
  }

}