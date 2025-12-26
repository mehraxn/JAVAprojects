package com.weather.report.model.entities;

import java.util.ArrayList;                                          //ADDED FOR R1
import java.util.Collection;

import com.weather.report.model.Timestamped;                                            //ADDED FOR R1

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;                                   //ADDED FOR R1
import jakarta.persistence.Id;                                //ADDED FOR R1
import jakarta.persistence.ManyToMany;                                       //ADDED FOR R1
import jakarta.persistence.Table;                               //ADDED FOR R1


@Entity                                                              //ADDED FOR R1 //This @Entity annotation specifies that the class is an entity and is mapped to a database table.
@Table(name = "NETWORKS")                                            //ADDED FOR R1 //This @Table annotation specifies the name of the database table to be used for mapping.
public class Network extends Timestamped {

  @Id                                                                //ADDED FOR R1 //This @Id annotation specifies the primary key of an entity.
  private String code;                                               //ADDED FOR R1 //Primary fields for Network entity
  private String name;                                               //ADDED FOR R1 //Basic fields for Network entity
  private String description;                                        //ADDED FOR R1 //Basic fields for Network entity

  @ManyToMany(fetch = FetchType.EAGER)                               //ADDED FOR R1 //This @ManyToMany annotation specifies a many-to-many relationship between Network and Operator entities.
  private Collection<Operator> operators = new ArrayList<>();        //ADDED FOR R1

  public Network() {}                                                //ADDED FOR R1 //Every JPA entity must have a no-argument constructor

  public Network(String code, String name, String description) {     //ADDED FOR R1
    this.code = code;                                                //ADDED FOR R1
    this.name = name;                                                //ADDED FOR R1
    this.description = description;                                  //ADDED FOR R1
  }                                                                  //ADDED FOR R1

  public Collection<Operator> getOperators() {
    return operators;                                                //ADDED FOR R1
  }

  public void setOperators(Collection<Operator> operators) {          //ADDED FOR R1
    this.operators = operators;                                      //ADDED FOR R1
  }                                                                  //ADDED FOR R1

  public String getCode() {
    return code;                                                     //ADDED FOR R1
  }

  public void setCode(String code) {                                 //ADDED FOR R1
    this.code = code;                                                //ADDED FOR R1
  }                                                                  //ADDED FOR R1

  public String getName() {
    return name;                                                     //ADDED FOR R1
  }

  public void setName(String name) {                                 //ADDED FOR R1
    this.name = name;                                                //ADDED FOR R1
  }                                                                  //ADDED FOR R1

  public String getDescription() {
    return description;                                              //ADDED FOR R1
  }

  public void setDescription(String description) {                   //ADDED FOR R1
    this.description = description;                                  //ADDED FOR R1
  }                                                                  //ADDED FOR R1

}