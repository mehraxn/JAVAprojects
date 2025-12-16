package com.weather.report.model.entities;

import java.util.Objects; //ADDED FOR R1

import jakarta.persistence.Entity; //ADDED FOR R1
import jakarta.persistence.Id; //ADDED FOR R1
import jakarta.persistence.Table; //ADDED FOR R1

/// An _operator_ is an entity that receives notifications when a threshold violation is detected.  
@Entity //ADDED FOR R1
@Table(name = "OPERATORS") //ADDED FOR R1
public class Operator {

  @Id //ADDED FOR R1
  private String email; //ADDED FOR R1
  private String firstName; //ADDED FOR R1
  private String lastName; //ADDED FOR R1
  private String phoneNumber; //ADDED FOR R1

  public Operator() {} //ADDED FOR R1

  public Operator(String email, String firstName, String lastName, String phoneNumber) { //ADDED FOR R1
    this.email = email; //ADDED FOR R1
    this.firstName = firstName; //ADDED FOR R1
    this.lastName = lastName; //ADDED FOR R1
    this.phoneNumber = phoneNumber; //ADDED FOR R1
  } //ADDED FOR R1

  public String getFirstName() {
    return firstName; //ADDED FOR R1
  }

  public void setFirstName(String firstName) { //ADDED FOR R1
    this.firstName = firstName; //ADDED FOR R1
  } //ADDED FOR R1

  public String getLastName() {
    return lastName; //ADDED FOR R1
  }

  public void setLastName(String lastName) { //ADDED FOR R1
    this.lastName = lastName; //ADDED FOR R1
  } //ADDED FOR R1

  public String getEmail() {
    return email; //ADDED FOR R1
  }

  public void setEmail(String email) { //ADDED FOR R1
    this.email = email; //ADDED FOR R1
  } //ADDED FOR R1

  public String getPhoneNumber() {
    return phoneNumber; //ADDED FOR R1
  }

  public void setPhoneNumber(String phoneNumber) { //ADDED FOR R1
    this.phoneNumber = phoneNumber; //ADDED FOR R1
  } //ADDED FOR R1

  @Override //ADDED FOR R1
  public boolean equals(Object o) { //ADDED FOR R1
    if (this == o) return true; //ADDED FOR R1
    if (o == null || getClass() != o.getClass()) return false; //ADDED FOR R1
    Operator operator = (Operator) o; //ADDED FOR R1
    return Objects.equals(email, operator.email); //ADDED FOR R1
  } //ADDED FOR R1

  @Override //ADDED FOR R1
  public int hashCode() { //ADDED FOR R1
    return Objects.hash(email); //ADDED FOR R1
  } //ADDED FOR R1

}