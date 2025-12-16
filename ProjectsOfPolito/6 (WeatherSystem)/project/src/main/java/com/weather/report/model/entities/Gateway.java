package com.weather.report.model.entities;

import java.util.Collection;
import java.util.Objects; //ADDED FOR R1

import com.weather.report.model.Timestamped;

public class Gateway extends Timestamped {

  private String code; //ADDED FOR R1 (Skeleton was missing fields, adding for consistency)

  public Collection<Parameter> getParameters() {
    return null;
  }

  public String getCode() {
    return code; 
  }

  public String getName() {
    return null;
  }

  public String getDescription() {
    return null;
  }

  @Override //ADDED FOR R1
  public boolean equals(Object o) { //ADDED FOR R1
    if (this == o) return true; //ADDED FOR R1
    if (o == null || getClass() != o.getClass()) return false; //ADDED FOR R1
    Gateway gateway = (Gateway) o; //ADDED FOR R1
    return Objects.equals(code, gateway.code); //ADDED FOR R1
  } //ADDED FOR R1

  @Override //ADDED FOR R1
  public int hashCode() { //ADDED FOR R1
    return Objects.hash(code); //ADDED FOR R1
  } //ADDED FOR R1
}