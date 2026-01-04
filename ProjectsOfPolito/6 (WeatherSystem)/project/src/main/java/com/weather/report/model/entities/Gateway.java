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
@Entity 
@Table(name = "GATEWAYS")   
public class Gateway extends Timestamped {
  
  @Id 
  String code; 
  String name; 
  String description; 
  LocalDateTime now; 

  @OneToMany( 
    mappedBy = "gateway", 
    cascade = CascadeType.ALL, 
    orphanRemoval = true, 
    fetch = FetchType.EAGER 
  )
  private Collection<Parameter> parameters = new ArrayList<>(); 

  @ManyToOne 
  @JoinColumn(name = "network_code") 
  private Network network; 

  public Gateway(){} 

  public Gateway(String code, String name, String description){ 
    this.code = code; 
    this.name = name;
    this.description = description;
  }

  public Collection<Parameter> getParameters() {
    return parameters;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code){
    this.code=code;
  }

  public String getName() {
    return name;
  }
  public void setName(String name){
    this.name=name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description){
    this.description=description;
  }

  public Network getNetwork() { 
        return network; 
    }

    public void setNetwork(Network network) { 
        this.network = network;
    }

  //HELPER FUNCTIONS

  public void addParameter(Parameter p) {
    p.setGateway(this);
    parameters.add(p);
  }

  public void updateParameter(Parameter oldP, Parameter newP) {
    parameters.remove(oldP);
    parameters.add(newP);
  }

  @Override
  public boolean equals(Object o) {                                  
    if (this == o) return true;                                      
    if (o == null || getClass() != o.getClass()) return false;       
    Gateway gateway = (Gateway) o;                                   
    return Objects.equals(code, gateway.code);                       
  }                                                                  

  @Override                                                          
  public int hashCode() {                                            
    return Objects.hash(code);                                       
  } 
}