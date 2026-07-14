package com.weather.report.reports;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.stream.Collectors;

import com.weather.report.model.entities.Gateway;
import com.weather.report.model.entities.Measurement;
import com.weather.report.model.entities.Parameter;
import com.weather.report.repositories.CRUDRepository;
import com.weather.report.repositories.MeasurementRepository;
import com.weather.report.util.DateParsingUtils;

public class GatewayReportImplementation implements GatewayReport {
  String code;
  String start;
  String end;
  private List<Measurement> measurements;
  private Map<String, Long> countBySensor; //amount of sensors per sensor code

  public GatewayReportImplementation(String code, String start, String end){
    this.code = code;
    this.start = start;
    this.end = end;

    MeasurementRepository measurementRepo = new MeasurementRepository();

    //turning start and end time strings to LocalDateTime format
    LocalDateTime startDateTime = Optional.ofNullable(start)
                                  .map(this::toLocalDateTime)
                                  .orElse(null);

    LocalDateTime endDateTime = Optional.ofNullable(end)
                                .map(this::toLocalDateTime)
                                .orElse(null);

    // Database-side filtering (inclusive range, null bound = unbounded), replacing
    // the previous "read all measurements then filter in Java" pattern. Behaviour
    // is identical: gateway code match + timestamp >= start + timestamp <= end.
    this.measurements = measurementRepo.findByGatewayCodeAndDateRange(code, startDateTime, endDateTime);

    this.countBySensor = measurements.stream()
                                     .collect(Collectors.groupingBy(Measurement::getSensorCode, //group measurements by sensor code
                                      Collectors.counting()));//count each sensor
    


  }

  @Override
  public String getCode() {
    return code;
  }

  @Override
  public String getStartDate() {
    return start;
  }

  @Override
  public String getEndDate() {
    return end;
  }

  @Override
  public long getNumberOfMeasurements() {
    return measurements.size();
  }

  @Override
  public Collection<String> getMostActiveSensors() {
    if (countBySensor.isEmpty()) {//if there is no sensor
    return Collections.emptyList();//returns empty list
  }
    long max = Collections.max(countBySensor.values());//finds max amount of values
    Collection<String> mostActive = countBySensor.entrySet().stream()
        .filter(e -> e.getValue() == max) //filters the sensor with max amount of value
        .map(Map.Entry::getKey)//extract only the key
        .toList();//turn into a list
    return mostActive;

  }

  @Override
  public Collection<String> getLeastActiveSensors() {
    if (countBySensor.isEmpty()) {
    return Collections.emptyList();
    }
    long min = Collections.min(countBySensor.values());
    Collection<String> leastActive =
    countBySensor.entrySet().stream()
        .filter(e -> e.getValue() == min)
        .map(Map.Entry::getKey)
        .toList();
    return leastActive;
  }

   @Override
    public Map<String, Double> getSensorsLoadRatio() {
    if (getNumberOfMeasurements() == 0) {//check if measurements are empty
    return Collections.emptyMap();
    }

    // Percentage (0-100) of the gateway's measurements produced by each sensor.
    // Consistent with NetworkReport.getGatewaysLoadRatio and README section R2
    // (both load ratios are expressed as a percentage).
    Map<String, Double> sensorsLoadRatio = countBySensor.entrySet().stream().collect(Collectors.toMap(
                                          Map.Entry::getKey,
                                          e -> (e.getValue() / (double) getNumberOfMeasurements()) * 100.0));

    return Collections.unmodifiableMap(sensorsLoadRatio);
  }

  @Override
  public Collection<String> getOutlierSensors() {
    if (measurements.isEmpty()) {
      return Collections.emptyList();
    }
    double expectedMean = getGatewayParameterValue(Parameter.EXPECTED_MEAN_CODE);
    double expectedStd = getGatewayParameterValue(Parameter.EXPECTED_STD_DEV_CODE);

    Map<String, Double> meanBySensor =measurements.stream()
          .collect(Collectors.groupingBy(
              Measurement::getSensorCode,//groups by sensor code
              Collectors.averagingDouble(Measurement::getValue) //finds average
          ));

    return meanBySensor.entrySet().stream()
      .filter(e ->
          Math.abs(e.getValue() - expectedMean) >= 2 * expectedStd //filters outliers
      )
      .map(Map.Entry::getKey) //maps out each entry to its key
      .toList();
}

  @Override
  public double getBatteryChargePercentage() {
    return getGatewayParameterValue(Parameter.BATTERY_CHARGE_PERCENTAGE_CODE);
  }

  /*map <Range<Duration>, count>.
Represents the histogram of inter-arrival times between consecutive
measurements of the gateway within the requested interval.
If at least two measurements are available, all inter-arrival durations are
computed and the resulting range is partitioned into 20 contiguous buckets
represented by Range<Duration>.
Each key identifies a duration interval following the global histogram
convention: all buckets except the last one are left-closed and right-open
[start, end), while the last bucket is [start, end] so that the maximum
inter-arrival time is included.
The associated value is the number of inter-arrival times whose duration
falls into that interval.
All buckets together fully cover the [minDuration, maxDuration] interval and
the map is a SortedMap ordered by ascending bucket start duration, so
iterating over the entries yields the buckets in increasing order of
inter-arrival time. */

  @Override
  public SortedMap<Range<Duration>, Long> getHistogram() {
    SortedMap<Range<Duration>, Long> histogram = new java.util.TreeMap<>();

  if (measurements.size() < 2) { //if there isnt enough measurements
    return Collections.unmodifiableSortedMap(histogram); //return empty histogram
  }

  List<Measurement> sorted = measurements.stream()
                              .sorted((m1, m2) ->//takes 2 measurmenets at a time
                              m1.getTimestamp().compareTo(m2.getTimestamp()))//sort them by timestamp
                              .toList();

  List<Duration> deltas = new java.util.ArrayList<>();

  for (int i = 1; i < sorted.size(); i++) {
    deltas.add(Duration.between( // how much time passes between each measurement and then store to delta
        sorted.get(i - 1).getTimestamp(),
        sorted.get(i).getTimestamp()
    ));
  }

  Duration min = Collections.min(deltas); //find smallest duration
  Duration max = Collections.max(deltas); //and biggest

  if (min.equals(max)) {//if all deltas equal then create one range 
    histogram.put(new GatewayRange<>(min, max), (long) deltas.size());
    return Collections.unmodifiableSortedMap(histogram);
  }

  Duration step = max.minus(min).dividedBy(20);//divide the range to 20 equal parts

  for (int i = 0; i < 20; i++) {
  final boolean isLast = (i == 19);

  Duration start = min.plus(step.multipliedBy(i));
  Duration end = isLast
      ? max
      : min.plus(step.multipliedBy(i + 1));

  Range<Duration> range = new GatewayRange<>(start, end, isLast);

  long count = deltas.stream()
      .filter(d ->
          isLast
              ? !d.minus(start).isNegative() && !d.minus(end).isPositive()
              : !d.minus(start).isNegative() && d.minus(end).isNegative()
      )
      .count();

  histogram.put(range, count);
}


  return Collections.unmodifiableSortedMap(histogram);
  }

  //HELPER FUNCTIONS
  LocalDateTime toLocalDateTime(String dateString){ // centralised date parsing (DATE_FORMAT)
    return DateParsingUtils.parseDateTime(dateString);
  }

  private double getGatewayParameterValue(String parameterCode) {
  CRUDRepository<Gateway, String> repo = new CRUDRepository<>(Gateway.class);

  Gateway gateway = repo.read(code);
  if (gateway == null) {
    return 0.0; // gateway removed/absent -> safe default, avoids NPE
  }

  return gateway.getParameters().stream()
      .filter(p -> p.getCode().equals(parameterCode))
      .map(Parameter::getValue)
      .findFirst()
      .orElse(0.0); // safe default
}
 
}
