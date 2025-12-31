package com.weather.report.reports;

import java.time.Duration;//ADDED FOR R2
import java.time.LocalDateTime;//ADDED FOR R2
import java.util.Collection;//ADDED FOR R2
import java.util.Collections;//ADDED FOR R2
import java.util.List;//ADDED FOR R2
import java.util.Map;//ADDED FOR R2
import java.util.Optional;//ADDED FOR R2
import java.util.SortedMap;//ADDED FOR R2
import java.util.stream.Collector;//ADDED FOR R2
import java.util.stream.Collectors;//ADDED FOR R2

import com.weather.report.exceptions.ElementNotFoundException;//ADDED FOR R2
import com.weather.report.model.entities.Gateway;//ADDED FOR R2
import com.weather.report.model.entities.Measurement;//ADDED FOR R2
import com.weather.report.model.entities.Parameter;//ADDED FOR R2
import com.weather.report.repositories.CRUDRepository;//ADDED FOR R2

public class GatewayReportImplementation implements GatewayReport {//ADDED FOR R2
  String code;//ADDED FOR R2
  String start;//ADDED FOR R2
  String end;//ADDED FOR R2
  private List<Measurement> measurements;//ADDED FOR R2
  private Map<String, Long> countBySensor; //amount of sensors per sensor code//ADDED FOR R2

  public GatewayReportImplementation(String code, String start, String end){//ADDED FOR R2
    this.code = code;//ADDED FOR R2
    this.start = start;//ADDED FOR R2
    this.end = end;//ADDED FOR R2

    CRUDRepository <Measurement, Long> measurementRepo = new CRUDRepository<>(Measurement.class);//ADDED FOR R2

    List<Measurement> allMeasurements = measurementRepo.read();//ADDED FOR R2
    //turning start and end time strings to LocalDateTime format
    LocalDateTime startDateTime = Optional.ofNullable(start) //ADDED FOR R2
                                  .map(this::toLocalDateTime)//ADDED FOR R2
                                  .orElse(null);//ADDED FOR R2

    LocalDateTime endDateTime = Optional.ofNullable(end)//ADDED FOR R2
                                .map(this::toLocalDateTime)//ADDED FOR R2
                                .orElse(null);//ADDED FOR R2
    
    this.measurements = allMeasurements.stream()//ADDED FOR R2
                                       .filter(m -> m.getGatewayCode().equals(code))//among the measurements finds gateway that has the code
                                       .filter(m -> startDateTime == null || !m.getTimestamp().isBefore(startDateTime)) //checks if it is after start time
                                       .filter(m -> endDateTime == null || !m.getTimestamp().isAfter(endDateTime)) //checks if its before end time
                                       .toList();//returns a list

    this.countBySensor = measurements.stream()//ADDED FOR R2
                                     .collect(Collectors.groupingBy(Measurement::getSensorCode, //group measurements by sensor code
                                      Collectors.counting()));//count each sensor
    


  }

  @Override//ADDED FOR R2
  public String getCode() {//ADDED FOR R2
    return code;//ADDED FOR R2
  }

  @Override//ADDED FOR R2
  public String getStartDate() {//ADDED FOR R2
    return start;//ADDED FOR R2
  }

  @Override//ADDED FOR R2
  public String getEndDate() {//ADDED FOR R2
    return end;//ADDED FOR R2
  }

  @Override//ADDED FOR R2
  public long getNumberOfMeasurements() {//ADDED FOR R2
    return measurements.size();//ADDED FOR R2
  }

  @Override//ADDED FOR R2
  public Collection<String> getMostActiveSensors() {//ADDED FOR R2
    if (countBySensor.isEmpty()) {//if there is no sensor//ADDED FOR R2
    return Collections.emptyList();//returns empty list//ADDED FOR R2
  }
    long max = Collections.max(countBySensor.values());//finds max amount of values//ADDED FOR R2
    Collection<String> mostActive = countBySensor.entrySet().stream()//ADDED FOR R2
        .filter(e -> e.getValue() == max) //filters the sensor with max amount of value//ADDED FOR R2
        .map(Map.Entry::getKey)//extract only the key//ADDED FOR R2
        .toList();//turn into a list//ADDED FOR R2
    return mostActive;//ADDED FOR R2

  }

  @Override//ADDED FOR R2
  public Collection<String> getLeastActiveSensors() {//ADDED FOR R2
    if (countBySensor.isEmpty()) {//ADDED FOR R2
    return Collections.emptyList();//ADDED FOR R2
    }
    long min = Collections.min(countBySensor.values());//ADDED FOR R2
    Collection<String> leastActive =//ADDED FOR R2
    countBySensor.entrySet().stream()//ADDED FOR R2
        .filter(e -> e.getValue() == min)//ADDED FOR R2
        .map(Map.Entry::getKey)//ADDED FOR R2
        .toList();//ADDED FOR R2
    return leastActive;//ADDED FOR R2
  }

   @Override//ADDED FOR R2
    public Map<String, Double> getSensorsLoadRatio() {//ADDED FOR R2
    if (getNumberOfMeasurements() == 0) {//check if measurements are empty//ADDED FOR R2
    return Collections.emptyMap();//ADDED FOR R2
    }

    Map<String, Double> sensorsLoadRatio = countBySensor.entrySet().stream().collect(Collectors.toMap( //transform stream to map//ADDED FOR R2
                                          Map.Entry::getKey,//gets key per entryv
                                          e -> e.getValue() /(double) getNumberOfMeasurements())); //number of measurements/total num measurements//ADDED FOR R2

    return sensorsLoadRatio;//ADDED FOR R2
  }

  @Override//ADDED FOR R2
  public Collection<String> getOutlierSensors() {//ADDED FOR R2
    if (measurements.isEmpty()) {//ADDED FOR R2
      return Collections.emptyList();//ADDED FOR R2
    }
    double expectedMean = getGatewayParameterValue(Parameter.EXPECTED_MEAN_CODE);//ADDED FOR R2
    double expectedStd = getGatewayParameterValue(Parameter.EXPECTED_STD_DEV_CODE);//ADDED FOR R2

    Map<String, Double> meanBySensor =measurements.stream()//ADDED FOR R2
          .collect(Collectors.groupingBy(//ADDED FOR R2
              Measurement::getSensorCode,//groups by sensor code//ADDED FOR R2
              Collectors.averagingDouble(Measurement::getValue) //finds average//ADDED FOR R2
          ));

    return meanBySensor.entrySet().stream()//ADDED FOR R2
      .filter(e ->
          Math.abs(e.getValue() - expectedMean) >= 2 * expectedStd //filters outliers//ADDED FOR R2
      )
      .map(Map.Entry::getKey) //maps out each entry to its key//ADDED FOR R2
      .toList();//ADDED FOR R2
}

  @Override
  public double getBatteryChargePercentage() {//ADDED FOR R2
    return getGatewayParameterValue(Parameter.BATTERY_CHARGE_PERCENTAGE_CODE);//ADDED FOR R2
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
  public SortedMap<Range<Duration>, Long> getHistogram() {//ADDED FOR R2
    SortedMap<Range<Duration>, Long> histogram = new java.util.TreeMap<>();//ADDED FOR R2

  if (measurements.size() < 2) { //if there isnt enough measurements//ADDED FOR R2
    return histogram; //return empty histogram//ADDED FOR R2
  }

  List<Measurement> sorted = measurements.stream()//ADDED FOR R2
                              .sorted((m1, m2) ->//takes 2 measurmenets at a time
                              m1.getTimestamp().compareTo(m2.getTimestamp()))//sort them by timestamp
                              .toList();

  List<Duration> deltas = new java.util.ArrayList<>();//ADDED FOR R2

  for (int i = 1; i < sorted.size(); i++) {//ADDED FOR R2
    deltas.add(Duration.between( // how much time passes between each measurement and then store to delta//ADDED FOR R2
        sorted.get(i - 1).getTimestamp(),//ADDED FOR R2
        sorted.get(i).getTimestamp()//ADDED FOR R2
    ));
  }

  Duration min = Collections.min(deltas); //find smallest duration//ADDED FOR R2
  Duration max = Collections.max(deltas); //and biggest//ADDED FOR R2

  if (min.equals(max)) {//if all deltas equal then create one range //ADDED FOR R2
    histogram.put(new GatewayRange<>(min, max), (long) deltas.size());//ADDED FOR R2
    return histogram;//ADDED FOR R2
  }

  Duration step = max.minus(min).dividedBy(20);//divide the range to 20 equal parts//ADDED FOR R2

  for (int i = 0; i < 20; i++) {//ADDED FOR R2
  final boolean isLast = (i == 19);//ADDED FOR R2

  Duration start = min.plus(step.multipliedBy(i));//ADDED FOR R2
  Duration end = isLast//ADDED FOR R2
      ? max
      : min.plus(step.multipliedBy(i + 1));

  Range<Duration> range = new GatewayRange<>(start, end, isLast);//ADDED FOR R2

  long count = deltas.stream()//ADDED FOR R2
      .filter(d ->
          isLast
              ? !d.minus(start).isNegative() && !d.minus(end).isPositive()//ADDED FOR R2
              : !d.minus(start).isNegative() && d.minus(end).isNegative()//ADDED FOR R2
      )
      .count();

  histogram.put(range, count);//ADDED FOR R2
}


  return histogram;//ADDED FOR R2
  }

  //HELPER FUNCTIONS
  LocalDateTime toLocalDateTime(String dateString){ //turns the string to LocalDateTime format //ADDED FOR R2
    String[] parts = dateString.split(" "); //ADDED FOR R2                                    
    String date = parts[0];                //ADDED FOR R2                                 
    String time = parts[1];                //ADDED FOR R2                                 
    String[] dateParts = date.split("-");   //ADDED FOR R2                               
    int year = Integer.parseInt(dateParts[0]);     //ADDED FOR R2                            
    int month = Integer.parseInt(dateParts[1]);    //ADDED FOR R2                             
    int day = Integer.parseInt(dateParts[2]);      //ADDED FOR R2                             
    String[] timeParts = time.split(":");   //ADDED FOR R2                                
    int hour = Integer.parseInt(timeParts[0]);    //ADDED FOR R2                              
    int min = Integer.parseInt(timeParts[1]);    //ADDED FOR R2                            
    int sec = Integer.parseInt(timeParts[2]);    //ADDED FOR R2                            
    return LocalDateTime.of(year, month, day, hour, min, sec);//ADDED FOR R2

  }

  private double getGatewayParameterValue(String parameterCode) {//ADDED FOR R2
  CRUDRepository<Gateway, String> repo = new CRUDRepository<>(Gateway.class);//ADDED FOR R2

  Gateway gateway = repo.read(code);//ADDED FOR R2

  return gateway.getParameters().stream()//ADDED FOR R2
      .filter(p -> p.getCode().equals(parameterCode))//ADDED FOR R2
      .map(Parameter::getValue)//ADDED FOR R2
      .findFirst()//ADDED FOR R2
      .orElse(0.0); // safe default//ADDED FOR R2
}
 
}
