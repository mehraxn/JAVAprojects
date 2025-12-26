package com.weather.report.model.entities;

import jakarta.persistence.Entity;                                              //ADDED FOR R1
import jakarta.persistence.Id;                                                  //ADDED FOR R1

/// An _operator_ is an entity that receives notifications when a threshold violation is detected.  
@Entity                                                                         //ADDED FOR R1
public class Operator {

  @Id                                                                           //ADDED FOR R1
  private String email;                                                         //ADDED FOR R1
  private String firstName;                                                     //ADDED FOR R1
  private String lastName;                                                      //ADDED FOR R1
  private String phoneNumber;                                                   //ADDED FOR R1

  public Operator() {}                                                          //ADDED FOR R1

  public Operator(String email, String firstName, String lastName, String phoneNumber) {  //ADDED FOR R1
    this.email = email;                                                         //ADDED FOR R1
    this.firstName = firstName;                                                 //ADDED FOR R1
    this.lastName = lastName;                                                   //ADDED FOR R1
    this.phoneNumber = phoneNumber;                                             //ADDED FOR R1
  }                                                                             //ADDED FOR R1

  public String getFirstName() {                                                //ADDED FOR R1
    return firstName;                                                           //ADDED FOR R1
  }                                                                             //ADDED FOR R1

  public String getLastName() {                                                 //ADDED FOR R1
    return lastName;                                                            //ADDED FOR R1
  }                                                                             //ADDED FOR R1

  public String getEmail() {                                                    //ADDED FOR R1
    return email;                                                               //ADDED FOR R1
  }                                                                             //ADDED FOR R1

  public String getPhoneNumber() {                                              //ADDED FOR R1
    return phoneNumber;                                                         //ADDED FOR R1
  }                                                                             //ADDED FOR R1

}                                                                               //ADDED FOR R1