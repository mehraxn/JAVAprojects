package com.weather.report.model.entities;

import java.util.ArrayList;
import java.util.Collection;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/// A _parameter_ is a value associated with the gateway it belongs to.
/// 
/// It allows storing state or configuration information.
@Entity//ADDED FOR R2
@Table(//ADDED FOR R2
    name = "PARAMETERS",//ADDED FOR R2
    uniqueConstraints = {//ADDED FOR R2
        @UniqueConstraint(columnNames = {"gateway_code", "code"})//ADDED FOR R2
    }
) 
public class Parameter {

  public static final String EXPECTED_MEAN_CODE = "EXPECTED_MEAN";
  public static final String EXPECTED_STD_DEV_CODE = "EXPECTED_STD_DEV";
  public static final String BATTERY_CHARGE_PERCENTAGE_CODE = "BATTERY_CHARGE";

  @Id//ADDED FOR R2
  @GeneratedValue(strategy = GenerationType.IDENTITY)//ADDED FOR R2
  private Long id;//ADDED FOR R2
  
  @Column(nullable = false)//ADDED FOR R2
  private String code;//ADDED FOR R2
  private String name;//ADDED FOR R2
  private String description;//ADDED FOR R2

  @Column(name = "parameter_value")//ADDED FOR R2
  private double value;//ADDED FOR R2

  @ManyToOne(fetch = FetchType.LAZY) //ADDED FOR R2
  private Gateway gateway;//ADDED FOR R2

  public Parameter(){}//ADDED FOR R2
  public Parameter(String code, String name, String description, double value){//ADDED FOR R2
    this.code = code;//ADDED FOR R2
    this.name = name;//ADDED FOR R2
    this.value = value;//ADDED FOR R2
    this.description = description;//ADDED FOR R2
  }


  public String getCode() {
    return code;//ADDED FOR R2
  }

  public void setCode(String code){//ADDED FOR R2
    this.code = code;//ADDED FOR R2
  }

  public String getName() {
    return name;//ADDED FOR R2
  }

  public void setName(String name){//ADDED FOR R2
    this.name = name;//ADDED FOR R2
  }

  public String getDescription() {
    return description;//ADDED FOR R2
  }

  public void setDescription(String description){//ADDED FOR R2
    this.description = description;//ADDED FOR R2
  }

  public double getValue() {
    return value;//ADDED FOR R2
  }
  public void setValue(double value){//ADDED FOR R2
    this.value = value;//ADDED FOR R2
  }
  public Gateway getGateway() { 
    return gateway; //ADDED FOR R2
  }
  public void setGateway(Gateway gateway) { //ADDED FOR R2
    this.gateway = gateway;//ADDED FOR R2
  }
}
