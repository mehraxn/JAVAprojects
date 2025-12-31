package com.weather.report.model.entities;                                     // ADDED FOR R1

import java.util.ArrayList;                                                     // ADDED FOR R1
import java.util.Collection;                                                    // ADDED FOR R1
import java.util.Objects;                                                       // ADDED FOR R1

import com.weather.report.model.Timestamped;                                    // ADDED FOR R1

import jakarta.persistence.Entity;                                                   // ADDED FOR R1
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity                                                                         // ADDED FOR R1
@Table(name = "NETWORKS")                                                       // ADDED FOR R1
public class Network extends Timestamped {                                      // ADDED FOR R1

    @Id                                                                         // ADDED FOR R1
    private String code;                                                        // ADDED FOR R1

    private String name;                                                        // ADDED FOR R1

    private String description;                                                 // ADDED FOR R1

    @ManyToMany(fetch = FetchType.EAGER)                                        // ADDED FOR R1
    @JoinTable(                                                                 // ADDED FOR R1
        name = "NETWORK_OPERATORS",                                             // ADDED FOR R1
        joinColumns = @JoinColumn(name = "network_code"),                       // ADDED FOR R1
        inverseJoinColumns = @JoinColumn(name = "operator_email")               // ADDED FOR R1
    )                                                                           // ADDED FOR R1
    private Collection<Operator> operators = new ArrayList<>();                 // ADDED FOR R1

    @OneToMany(mappedBy = "network") //ADDED FOR R4
    private Collection<Gateway> gateways = new ArrayList<>(); //ADDED FOR R4


    public Network() {                                                          // ADDED FOR R1
        // default constructor for JPA                                          // ADDED FOR R1
    }                                                                           // ADDED FOR R1

    public Network(String code, String name, String description) {              // ADDED FOR R1
        this.code = code;                                                       // ADDED FOR R1
        this.name = name;                                                       // ADDED FOR R1
        this.description = description;                                         // ADDED FOR R1
    }                                                                           // ADDED FOR R1

    public Collection<Operator> getOperators() {                                // ADDED FOR R1
        return this.operators;                                                  // ADDED FOR R1
    }                                                                           // ADDED FOR R1

    public void setOperators(Collection<Operator> operators) {                  // ADDED FOR R1
        this.operators = operators;                                             // ADDED FOR R1
    }                                                                           // ADDED FOR R1

    public String getCode() {                                                   // ADDED FOR R1
        return this.code;                                                       // ADDED FOR R1
    }                                                                           // ADDED FOR R1

    public void setCode(String code) {                                          // ADDED FOR R1
        this.code = code;                                                       // ADDED FOR R1
    }                                                                           // ADDED FOR R1

    public String getName() {                                                   // ADDED FOR R1
        return this.name;                                                       // ADDED FOR R1
    }                                                                           // ADDED FOR R1

    public void setName(String name) {                                          // ADDED FOR R1
        this.name = name;                                                       // ADDED FOR R1
    }                                                                           // ADDED FOR R1

    public String getDescription() {                                            // ADDED FOR R1
        return this.description;                                                // ADDED FOR R1
    }                                                                           // ADDED FOR R1

    public void setDescription(String description) {                            // ADDED FOR R1
        this.description = description;                                         // ADDED FOR R1
    }                                                                           // ADDED FOR R1

    public Collection<Gateway> getGateways() {//ADDED FOR R4 
        return gateways;//ADDED FOR R4 
    } //ADDED FOR R4 

    @Override                                                                   // ADDED FOR R1
    public boolean equals(Object o) {                                           // ADDED FOR R1
        if (this == o) return true;                                             // ADDED FOR R1
        if (o == null || getClass() != o.getClass()) return false;              // ADDED FOR R1
        Network network = (Network) o;                                          // ADDED FOR R1
        return Objects.equals(code, network.code);                              // ADDED FOR R1
    }                                                                           // ADDED FOR R1

    @Override                                                                   // ADDED FOR R1
    public int hashCode() {                                                     // ADDED FOR R1
        return Objects.hash(code);                                              // ADDED FOR R1
    }                                                                           // ADDED FOR R1
}                                                                               // ADDED FOR R1