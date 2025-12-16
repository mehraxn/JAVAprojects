package com.weather.report.model.entities;

import java.util.ArrayList; //ADDED FOR R1
import java.util.Collection;
import java.util.Objects; //ADDED FOR R1

import com.weather.report.model.Timestamped;

import jakarta.persistence.Entity; //ADDED FOR R1
import jakarta.persistence.FetchType; //ADDED FOR R1
import jakarta.persistence.Id; //ADDED FOR R1
import jakarta.persistence.ManyToMany; //ADDED FOR R1
import jakarta.persistence.Table; //ADDED FOR R1

/// A _monitoring network_ that represents a logical set of system elements.
/// 
/// It may have a list of _operators_ responsible for receiving notifications.
@Entity //ADDED FOR R1
@Table(name = "NETWORKS") //ADDED FOR R1
public class Network extends Timestamped {

  @Id //ADDED FOR R1
  private String code; //ADDED FOR R1
  private String name; //ADDED FOR R1
  private String description; //ADDED FOR R1

  @ManyToMany(fetch = FetchType.EAGER) //ADDED FOR R1
  private Collection<Operator> operators = new ArrayList<>(); //ADDED FOR R1

  public Network() {} //ADDED FOR R1

  public Network(String code, String name, String description) { //ADDED FOR R1
    this.code = code; //ADDED FOR R1
    this.name = name; //ADDED FOR R1
    this.description = description; //ADDED FOR R1
  } //ADDED FOR R1

  public Collection<Operator> getOperators() {
    return operators; //ADDED FOR R1
  }

  public void setOperators(Collection<Operator> operators) { //ADDED FOR R1
    this.operators = operators; //ADDED FOR R1
  } //ADDED FOR R1

  public String getCode() {
    return code; //ADDED FOR R1
  }

  public void setCode(String code) { //ADDED FOR R1
    this.code = code; //ADDED FOR R1
  } //ADDED FOR R1

  public String getName() {
    return name; //ADDED FOR R1
  }

  public void setName(String name) { //ADDED FOR R1
    this.name = name; //ADDED FOR R1
  } //ADDED FOR R1

  public String getDescription() {
    return description; //ADDED FOR R1
  }

  public void setDescription(String description) { //ADDED FOR R1
    this.description = description; //ADDED FOR R1
  } //ADDED FOR R1

  @Override //ADDED FOR R1
  public boolean equals(Object o) { //ADDED FOR R1
    if (this == o) return true; //ADDED FOR R1
    if (o == null || getClass() != o.getClass()) return false; //ADDED FOR R1
    Network network = (Network) o; //ADDED FOR R1
    return Objects.equals(code, network.code); //ADDED FOR R1
  } //ADDED FOR R1

  @Override //ADDED FOR R1
  public int hashCode() { //ADDED FOR R1
    return Objects.hash(code); //ADDED FOR R1
  } //ADDED FOR R1

}