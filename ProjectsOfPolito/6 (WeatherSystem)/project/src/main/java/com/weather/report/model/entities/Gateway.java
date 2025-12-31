package com.weather.report.model.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import com.weather.report.model.Timestamped; 

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/// A _gateway_ groups multiple devices that monitor the same physical quantity.  
/// 
/// It can be configured through parameters that provide information about its state or values needed for interpreting the measurements.
@Entity //ADDED FOR R2
@Table(name = "GATEWAYS")   //ADDED FOR R2
public class Gateway extends Timestamped {
  
  @Id //ADDED FOR R2
  String code; //ADDED FOR R2
  String name; //ADDED FOR R2
  String description; //ADDED FOR R2
  LocalDateTime now; //ADDED FOR R2

  @OneToMany( //ADDED FOR R2
    mappedBy = "gateway", //ADDED FOR R2
    cascade = CascadeType.ALL, //ADDED FOR R2
    orphanRemoval = true, //ADDED FOR R2
    fetch = FetchType.EAGER //ADDED FOR R2
  )
  private Collection<Parameter> parameters = new ArrayList<>(); //ADDED FOR R2

  @ManyToOne //ADDED FOR R4
  @JoinColumn(name = "network_code") //ADDED FOR R4
  private Network network; //ADDED FOR R4

  public Gateway(){} //ADDED FOR R2

  public Gateway(String code, String name, String description){ //ADDED FOR R2
    this.code = code; //ADDED FOR R2
    this.name = name;//ADDED FOR R2
    this.description = description;//ADDED FOR R2
  }

  public Collection<Parameter> getParameters() {
    return parameters;//ADDED FOR R2
  }

  public String getCode() {
    return code;//ADDED FOR R2
  }

  public void setCode(String code){//ADDED FOR R2
    this.code=code;//ADDED FOR R2
  }

  public String getName() {
    return name;//ADDED FOR R2
  }
  public void setName(String name){//ADDED FOR R2
    this.name=name;//ADDED FOR R2
  }

  public String getDescription() {
    return description;//ADDED FOR R2
  }

  public void setDescription(String description){//ADDED FOR R2
    this.description=description;//ADDED FOR R2
  }

  public Network getNetwork() { //ADDED FOR R4
        return network; //ADDED FOR R4
    }//ADDED FOR R4

    public void setNetwork(Network network) { //ADDED FOR R4
        this.network = network;//ADDED FOR R4
    }//ADDED FOR R4

  //HELPER FUNCTIONS

  public void addParameter(Parameter p) {//ADDED FOR R2
    p.setGateway(this);//ADDED FOR R2
    parameters.add(p);//ADDED FOR R2
  }

  public void updateParameter(Parameter oldP, Parameter newP) {//ADDED FOR R2
    parameters.remove(oldP);//ADDED FOR R2
    parameters.add(newP);//ADDED FOR R2
  }

  @Override
  public boolean equals(Object o) {    //ADDED FOR R2                              
    if (this == o) return true;         //ADDED FOR R2                             
    if (o == null || getClass() != o.getClass()) return false;   //ADDED FOR R2    
    Gateway gateway = (Gateway) o;                  //ADDED FOR R2                 
    return Objects.equals(code, gateway.code);     //ADDED FOR R2                  
  }                                                                  

  @Override                                                          
  public int hashCode() {         //ADDED FOR R2                                   
    return Objects.hash(code);    //ADDED FOR R2                                   
  } 
}