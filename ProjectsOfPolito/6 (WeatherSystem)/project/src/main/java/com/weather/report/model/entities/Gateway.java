package com.weather.report.model.entities;

import java.util.Collection;

import com.weather.report.model.Timestamped; //ADDED FOR R1

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
}