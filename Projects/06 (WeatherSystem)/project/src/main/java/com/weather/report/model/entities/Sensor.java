package com.weather.report.model.entities;

import com.weather.report.model.Timestamped;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/// A _sensor_ measures a physical quantity and periodically sends the
/// corresponding measurements.
/// 
/// A sensor may have a _threshold_ defined by the user to detect
/// anomalous behaviours.
@Entity
@Table(name = "SENSORS")
public class Sensor extends Timestamped {

  @Id
  private String code;
  private String name;
  private String description;
  @Embedded
  private Threshold threshold;

  
  @ManyToOne
  @JoinColumn(name = "gateway_code")
  private Gateway gateway;

  public Sensor() {
    // default constructor
  }

  public Threshold getThreshold() {
    return threshold;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setThreshold(Threshold threshold) {
    this.threshold = threshold;
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public Gateway getGateway() {
    return gateway;
  }

  
  public void setGateway(Gateway gateway) {
    this.gateway = gateway;
  }
}