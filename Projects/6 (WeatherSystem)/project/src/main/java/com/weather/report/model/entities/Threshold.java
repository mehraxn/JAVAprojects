package com.weather.report.model.entities;

import com.weather.report.model.ThresholdType;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/// A _threshold_ defines an acceptable limit for the values measured by a
/// sensor.
/// 
/// It **always** consists of a numeric value and a
/// [ThresholdType][com.weather.report.model.ThresholdType] that the system must
/// apply to decide whether a measurement is anomalous.
@Embeddable
public class Threshold {

  @Column(name = "threshold_value")
  private double value;
  @Enumerated(EnumType.STRING)
  @Column(name = "threshold_type")
  private ThresholdType type;

  public Threshold() {
    // default constructor
  }

  public Threshold(ThresholdType type, double value) {
    this.type = type;
    this.value = value;
  }

  public double getValue() {
    return value;
  }

  public ThresholdType getType() {
    return type;
  }

  public void setValue(double value) {
    this.value = value;
  }

  public void setType(ThresholdType type) {
    this.type = type;
  }

}
