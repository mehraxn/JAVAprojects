package com.weather.report.reports;

import java.time.LocalDateTime;
import java.util.Collection;

import com.weather.report.exceptions.ElementNotFoundException;
import com.weather.report.exceptions.IdAlreadyInUseException;
import com.weather.report.exceptions.InvalidInputDataException;
import com.weather.report.exceptions.UnauthorizedException;
import com.weather.report.model.ThresholdType;
import com.weather.report.model.UserType;
import com.weather.report.model.entities.Sensor;
import com.weather.report.model.entities.Threshold;
import com.weather.report.model.entities.User;
import com.weather.report.operations.SensorOperations;
import com.weather.report.repositories.CRUDRepository;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.weather.report.WeatherReport;
import com.weather.report.model.entities.Measurement;
import com.weather.report.repositories.MeasurementRepository;
import com.weather.report.services.AlertingService;
import com.weather.report.reports.Report.Range;

public class SensorOperationsimplement implements SensorOperations {

  private final CRUDRepository<Sensor, String> sensorRepository = new CRUDRepository<>(Sensor.class);

  private final CRUDRepository<User, String> userRepository = new CRUDRepository<>(User.class);

  private void validateMaintainer(String username) throws UnauthorizedException {
    if (username == null || username.isBlank()) {
      throw new UnauthorizedException("User is missing or unauthorized");
    }
    User u = userRepository.read(username); // Optimized: Direct DB lookup
    if (u == null || u.getType() != UserType.MAINTAINER) {
      throw new UnauthorizedException("User is missing or unauthorized");
    }
  }

  @Override
  public Sensor createSensor(String code, String name, String description, String username)
      throws IdAlreadyInUseException, InvalidInputDataException, UnauthorizedException {

    // 1) User must be MAINTAINER
    validateMaintainer(username);

    // 2) Validate code format
    if (code == null || !code.matches("S_\\d{6}")) {
      throw new InvalidInputDataException("Bad code format");
    }

    // 3) Build sensor
    Sensor sensor = new Sensor();
    sensor.setCode(code);
    sensor.setName(name);
    sensor.setDescription(description);

    LocalDateTime now = LocalDateTime.now();
    sensor.setCreatedBy(username);
    sensor.setCreatedAt(now);

    // Persist, mapping "duplicate key" failures to IdAlreadyInUseException
    try {
      sensorRepository.create(sensor);
    } catch (RuntimeException e) {
      // Any persistence-level duplicate PK / uniqueness violation should be
      // surfaced as IdAlreadyInUseException for the tests.
      throw new IdAlreadyInUseException("Code already exists");
    }

    return sensor;

  }

  @Override
  public Sensor updateSensor(String code, String name, String description, String username)
      throws InvalidInputDataException, ElementNotFoundException, UnauthorizedException {
    // TODO Auto-generated method stub
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
    // TODO Auto-generated method stub
    validateMaintainer(username);

    if (code == null || code.isBlank()) {
      throw new InvalidInputDataException("Sensor code is mandatory");
    }

    Sensor existing = sensorRepository.read(code);
    if (existing == null) {
      throw new ElementNotFoundException("Sensor not found: " + code);
    }

    // Delete and return deleted entity
    Sensor deleted = sensorRepository.delete(code);

    // Trigger deletion notification according to R3
    AlertingService.notifyDeletion(username, code, Sensor.class);

    return deleted;
  }

  @Override
  public Collection<Sensor> getSensors(String... sensorCodes) {
    // TODO Auto-generated method stub
    // Read all sensors from the repository
    Collection<Sensor> allSensors = sensorRepository.read();

    // Spec: when invoked with no arguments, returns all sensors
    if (sensorCodes == null || sensorCodes.length == 0) {
      return allSensors;
    }

    // Spec: unknown codes are ignored
    java.util.Set<String> wanted = new java.util.HashSet<>(java.util.Arrays.asList(sensorCodes));

    return allSensors.stream()
        .filter(s -> s.getCode() != null && wanted.contains(s.getCode()))
        .toList();
  }

  @Override
  public Threshold createThreshold(String sensorCode, ThresholdType type, double value, String username)
      throws InvalidInputDataException, ElementNotFoundException, IdAlreadyInUseException, UnauthorizedException {
    // TODO Auto-generated method stub
    // User must be a MAINTAINER
    validateMaintainer(username);

    // sensorCode mandatory
    if (sensorCode == null || sensorCode.isBlank()) {
      throw new InvalidInputDataException("Sensor code is mandatory");
    }

    // Threshold type mandatory
    if (type == null) {
      throw new InvalidInputDataException("Threshold type is mandatory");
    }

    // Sensor must exist
    Sensor sensor = sensorRepository.read(sensorCode);
    if (sensor == null) {
      throw new ElementNotFoundException("Sensor not found: " + sensorCode);
    }

    // Sensor must not already have a threshold
    if (sensor.getThreshold() != null) {
      throw new IdAlreadyInUseException("Threshold already exists for sensor " + sensorCode);
    }

    // Create and attach threshold
    Threshold threshold = new Threshold(type, value);
    sensor.setThreshold(threshold);

    // Persist the change to the sensor
    sensorRepository.update(sensor);

    return threshold;
  }

  @Override
  public Threshold updateThreshold(String sensorCode, ThresholdType type, double value, String username)
      throws InvalidInputDataException, ElementNotFoundException, UnauthorizedException {
    // TODO Auto-generated method stub
    // User must be a MAINTAINER
    validateMaintainer(username);

    // sensorCode mandatory
    if (sensorCode == null || sensorCode.isBlank()) {
      throw new InvalidInputDataException("Sensor code is mandatory");
    }

    // Threshold type mandatory
    if (type == null) {
      throw new InvalidInputDataException("Threshold type is mandatory");
    }

    // Sensor must exist
    Sensor sensor = sensorRepository.read(sensorCode);
    if (sensor == null) {
      throw new ElementNotFoundException("Sensor not found: " + sensorCode);
    }

    // Sensor must already have a threshold
    Threshold threshold = sensor.getThreshold();
    if (threshold == null) {
      throw new ElementNotFoundException("Threshold not found for sensor " + sensorCode);
    }

    // Update threshold fields
    threshold.setType(type);
    threshold.setValue(value);

    // Persist changes on the sensor
    sensorRepository.update(sensor);

    return threshold;
  }

  @Override
  public SensorReport getSensorReport(String code, String startDate, String endDate)
      throws InvalidInputDataException, ElementNotFoundException {
    // TODO Auto-generated method stub
    // Validate sensor code
    if (code == null || code.isBlank())
      throw new InvalidInputDataException("Code mandatory");

    // Ensure sensor exists
    if (sensorRepository.read(code) == null) {
      throw new ElementNotFoundException("Sensor not found: " + code);
    }

    LocalDateTime start = parseDateOrNull(startDate);
    LocalDateTime end = parseDateOrNull(endDate);

    // USE OPTIMIZED REPO METHOD HERE
    MeasurementRepository mRepo = new MeasurementRepository();
    List<Measurement> measurements = mRepo.findBySensorAndDateRange(code, start, end);

    // --- MATH LOGIC (Unchanged from your logic, just applied to list) ---

    int n = measurements.size();
    double mean = 0.0, variance = 0.0, stdDev = 0.0, min = 0.0, max = 0.0;
    List<Measurement> outliers = new ArrayList<>();
    List<Measurement> nonOutliers;

    if (n >= 2) {
      double sum = measurements.stream().mapToDouble(Measurement::getValue).sum();
      mean = sum / n;

      double finalMean = mean;
      double sqSum = measurements.stream()
          .mapToDouble(m -> Math.pow(m.getValue() - finalMean, 2))
          .sum();
      variance = sqSum / (n - 1);
      stdDev = Math.sqrt(variance);

      if (stdDev > 0.0) {
        double limit = 2.0 * stdDev;
        for (Measurement m : measurements) {
          if (Math.abs(m.getValue() - mean) >= limit) {
            outliers.add(m);
          }
        }
      }
    }

    if (n < 2 || stdDev == 0.0) {
      nonOutliers = new ArrayList<>(measurements);
      outliers.clear();
      mean = 0.0;
      variance = 0.0;
      stdDev = 0.0;
    } else {
      List<Measurement> finalOutliers = outliers;
      nonOutliers = measurements.stream()
          .filter(m -> !finalOutliers.contains(m))
          .collect(Collectors.toList());
    }

    if (!nonOutliers.isEmpty()) {
      min = nonOutliers.stream().mapToDouble(Measurement::getValue).min().orElse(0.0);
      max = nonOutliers.stream().mapToDouble(Measurement::getValue).max().orElse(0.0);
    }

    SortedMap<Range<Double>, Long> histogram = buildHistogram(nonOutliers, min, max);

    return new SimpleSensorReport(code, startDate, endDate, n, mean, variance, stdDev, min, max, outliers, histogram);
  }

  //// END OF MAIN CODE

  private LocalDateTime parseDateOrNull(String date) throws InvalidInputDataException {
    if (date == null) {
      return null;
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(WeatherReport.DATE_FORMAT);
    try {
      return LocalDateTime.parse(date, formatter);
    } catch (DateTimeParseException e) {
      throw new InvalidInputDataException("Bad date format");
    }
  }

  private SortedMap<Range<Double>, Long> buildHistogram(List<Measurement> nonOutliers,
      double min,
      double max) {
    SortedMap<Range<Double>, Long> histogram = new TreeMap<>(Comparator.comparing(Range::getStart));

    if (nonOutliers == null || nonOutliers.isEmpty()) {
      return histogram;
    }

    // All values identical: one bucket [min, max] containing all values
    if (min == max) {
      DoubleRange bucket = new DoubleRange(min, max, true);
      histogram.put(bucket, (long) nonOutliers.size());
      return histogram;
    }

    final int BUCKET_COUNT = 20;
    double span = max - min;
    double width = span / BUCKET_COUNT;

    List<DoubleRange> buckets = new ArrayList<>();

    // Create 20 equal-width buckets
    for (int i = 0; i < BUCKET_COUNT; i++) {
      double start = min + i * width;
      double end = (i == BUCKET_COUNT - 1) ? max : min + (i + 1) * width;
      boolean last = (i == BUCKET_COUNT - 1);
      DoubleRange range = new DoubleRange(start, end, last);
      buckets.add(range);
      histogram.put(range, 0L);
    }

    // Assign each non-outlier value to exactly one bucket
    for (Measurement m : nonOutliers) {
      double v = m.getValue();
      for (DoubleRange bucket : buckets) {
        if (bucket.contains(v)) {
          histogram.put(bucket, histogram.get(bucket) + 1);
          break;
        }
      }
    }

    return histogram;
  }

  private static class DoubleRange implements Range<Double> {
    private final double start;
    private final double end;
    private final boolean last;

    DoubleRange(double start, double end, boolean last) {
      this.start = start;
      this.end = end;
      this.last = last;
    }

    @Override
    public Double getStart() {
      return start;
    }

    @Override
    public Double getEnd() {
      return end;
    }

    @Override
    public boolean contains(Double value) {
      if (value == null) {
        return false;
      }
      if (last) {
        // Last bucket: [start, end]
        return value >= start && value <= end;
      } else {
        // Other buckets: [start, end)
        return value >= start && value < end;
      }
    }
  }

  /// FINAL SCETION OF CODE

  private static class SimpleSensorReport implements SensorReport {

    private final String code;
    private final String startDate;
    private final String endDate;
    private final long numberOfMeasurements;
    private final double mean;
    private final double variance;
    private final double stdDev;
    private final double minValue;
    private final double maxValue;
    private final List<Measurement> outliers;
    private final SortedMap<Range<Double>, Long> histogram;

    SimpleSensorReport(String code,
        String startDate,
        String endDate,
        long numberOfMeasurements,
        double mean,
        double variance,
        double stdDev,
        double minValue,
        double maxValue,
        List<Measurement> outliers,
        SortedMap<Range<Double>, Long> histogram) {
      this.code = code;
      this.startDate = startDate;
      this.endDate = endDate;
      this.numberOfMeasurements = numberOfMeasurements;
      this.mean = mean;
      this.variance = variance;
      this.stdDev = stdDev;
      this.minValue = minValue;
      this.maxValue = maxValue;
      this.outliers = outliers == null ? List.of() : List.copyOf(outliers);
      this.histogram = (histogram == null) ? new TreeMap<>(Comparator.comparing(Range::getStart)) : histogram;
    }

    // --- Report common fields ---

    @Override
    public String getCode() {
      return code;
    }

    @Override
    public String getStartDate() {
      return startDate;
    }

    @Override
    public String getEndDate() {
      return endDate;
    }

    @Override
    public long getNumberOfMeasurements() {
      return numberOfMeasurements;
    }

    @Override
    public SortedMap<Range<Double>, Long> getHistogram() {
      return histogram;
    }

    // --- SensorReport specific fields ---

    @Override
    public double getMean() {
      return mean;
    }

    @Override
    public double getVariance() {
      return variance;
    }

    @Override
    public double getStdDev() {
      return stdDev;
    }

    @Override
    public double getMinimumMeasuredValue() {
      return minValue;
    }

    @Override
    public double getMaximumMeasuredValue() {
      return maxValue;
    }

    @Override
    public List<Measurement> getOutliers() {
      return outliers;
    }
  }

}
