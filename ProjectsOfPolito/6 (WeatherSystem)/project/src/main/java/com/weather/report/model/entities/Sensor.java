package com.weather.report.model.entities;

import java.util.Objects;

import com.weather.report.model.Timestamped; //ADDED FOR R1

public class Sensor extends Timestamped {

  private String code; //ADDED FOR R1 (Skeleton was missing fields)
  private String name; //ADDED FOR R1 (Required for the test)
  private Threshold threshold; //ADDED FOR R1

  public Threshold getThreshold() {
    return threshold;
  }

  public String getCode() {
    return code; 
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return null;
  }
  
  // Minimal setters for the entity to function if R1 tests rely on Sensor existence implicitly
  public void setCode(String code) { this.code = code; } //ADDED FOR R1
  public void setName(String name) { this.name = name; } //ADDED FOR R1
  public void setThreshold(Threshold threshold) { this.threshold = threshold; } //ADDED FOR R1

  @Override //ADDED FOR R1
  public boolean equals(Object o) { //ADDED FOR R1
    if (this == o) return true; //ADDED FOR R1
    if (o == null || getClass() != o.getClass()) return false; //ADDED FOR R1
    Sensor sensor = (Sensor) o; //ADDED FOR R1
    return Objects.equals(code, sensor.code); //ADDED FOR R1
  } //ADDED FOR R1

  @Override //ADDED FOR R1
  public int hashCode() { //ADDED FOR R1
    return Objects.hash(code); //ADDED FOR R1
  } //ADDED FOR R1
}