package com.weather.report.model.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import com.weather.report.model.Timestamped;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "NETWORKS")
public class Network extends Timestamped {

    @Id
    private String code;

    private String name;

    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "NETWORK_OPERATORS",
            joinColumns = @JoinColumn(name = "network_code"),
            inverseJoinColumns = @JoinColumn(name = "operator_email")
    )
    private Collection<Operator> operators = new ArrayList<>();

    @OneToMany(mappedBy = "network")
    private Collection<Gateway> gateways = new ArrayList<>();

    public Network() {
    }

    public Network(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public Collection<Operator> getOperators() {
        return this.operators;
    }

    public void setOperators(Collection<Operator> operators) {
        this.operators = operators;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection<Gateway> getGateways() {
        return gateways;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Network network = (Network) o;
        return Objects.equals(code, network.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}