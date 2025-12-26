package com.weather.report.model.entities;

import com.weather.report.model.Timestamped;                                    //ADDED FOR R1

import jakarta.persistence.Id;                                              //ADDED FOR R1

public class Sensor extends Timestamped {

  @Id                                                                           //ADDED FOR R1
  private String code;                                                          //ADDED FOR R1
  private Threshold threshold;                                                  //ADDED FOR R1

  public Sensor() {}                                                            //ADDED FOR R1

  public Threshold getThreshold() {
    return threshold;                                                           //ADDED FOR R1
  }

  public String getCode() {
    return code;                                                                //ADDED FOR R1
  }

  public String getName() {
    return null;
  }

  public String getDescription() {
    return null;
  }

}                                                                               //ADDED FOR R1