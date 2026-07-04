package com.weather.report.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/// A _parameter_ is a value associated with the gateway it belongs to.
/// 
/// It allows storing state or configuration information.
@Entity
@Table(
    name = "PARAMETERS",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"gateway_code", "code"})
    }
) 
public class Parameter {

  public static final String EXPECTED_MEAN_CODE = "EXPECTED_MEAN";
  public static final String EXPECTED_STD_DEV_CODE = "EXPECTED_STD_DEV";
  public static final String BATTERY_CHARGE_PERCENTAGE_CODE = "BATTERY_CHARGE";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(nullable = false)
  private String code;
  private String name;
  private String description;

  @Column(name = "parameter_value")
  private double value;

  @ManyToOne(fetch = FetchType.LAZY) 
  private Gateway gateway;

  public Parameter(){}
  public Parameter(String code, String name, String description, double value){
    this.code = code;
    this.name = name;
    this.value = value;
    this.description = description;
  }


  public String getCode() {
    return code;
  }

  public void setCode(String code){
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name){
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description){
    this.description = description;
  }

  public double getValue() {
    return value;
  }
  public void setValue(double value){
    this.value = value;
  }
  public Gateway getGateway() { 
    return gateway; 
  }
  public void setGateway(Gateway gateway) { 
    this.gateway = gateway;
  }
}
