package com.weather.report.operations;

import java.util.Collection;                                                                           // ADDED FOR R4

import com.weather.report.exceptions.ElementNotFoundException;                                          // ADDED FOR R4
import com.weather.report.exceptions.InvalidInputDataException;                                         // ADDED FOR R4
import com.weather.report.exceptions.UnauthorizedException;                                            // ADDED FOR R4
import com.weather.report.model.UserType;                                                               // ADDED FOR R4
import com.weather.report.model.entities.Gateway;                                                       // ADDED FOR R4
import com.weather.report.model.entities.Network;                                                       // ADDED FOR R4
import com.weather.report.model.entities.Sensor;                                                        // ADDED FOR R4
import com.weather.report.model.entities.User;                                                          // ADDED FOR R4
import com.weather.report.persistence.PersistenceManager;                                              // ADDED FOR R4
import com.weather.report.repositories.CRUDRepository;                                                  // ADDED FOR R4

import jakarta.persistence.EntityManager;                                                               // ADDED FOR R4
import jakarta.persistence.TypedQuery;                                                                  // ADDED FOR R4
                                                                                                        // ADDED FOR R4
public class TopologyOperationsImpl implements TopologyOperations {                                      // ADDED FOR R4
                                                                                                        // ADDED FOR R4
    private final CRUDRepository<Gateway, String> gatewayRepo = new CRUDRepository<>(Gateway.class);    // ADDED FOR R4
    private final CRUDRepository<Network, String> networkRepo = new CRUDRepository<>(Network.class);    // ADDED FOR R4
    private final CRUDRepository<User, String> userRepo = new CRUDRepository<>(User.class);              // ADDED FOR R4
    private final CRUDRepository<Sensor, String> sensorRepo = new CRUDRepository<>(Sensor.class);        // ADDED FOR R4
                                                                                                        // ADDED FOR R4
                                            
                                                                                                        // ADDED FOR R4
    private boolean isValidNetworkCode(String code) {                                                   // ADDED FOR R4
        if (code == null || code.length() != 6 || !code.startsWith("NET_")) {                           // ADDED FOR R4
            return false;                                                                               // ADDED FOR R4
        }
        return Character.isDigit(code.charAt(4)) && Character.isDigit(code.charAt(5));                  // ADDED FOR R4
    }
                                                                                                        // ADDED FOR R4
    private boolean isValidGatewayCode(String code) {                                                   // ADDED FOR R4
        if (code == null || code.length() != 7 || !code.startsWith("GW_")) {                            // ADDED FOR R4
            return false;                                                                               // ADDED FOR R4
        }
        for (int i = 3; i < 7; i++) {                                                                   // ADDED FOR R4
            if (!Character.isDigit(code.charAt(i))) return false;                                       // ADDED FOR R4
        }
        return true;                                                                                    // ADDED FOR R4
    }
                                                                                                        // ADDED FOR R4
    private boolean isValidSensorCode(String code) {                                                    // ADDED FOR R4
        if (code == null || code.length() != 8 || !code.startsWith("S_")) {                             // ADDED FOR R4
            return false;                                                                               // ADDED FOR R4
        }
        for (int i = 2; i < 8; i++) {                                                                   // ADDED FOR R4
            if (!Character.isDigit(code.charAt(i))) return false;                                       // ADDED FOR R4
        }
        return true;                                                                                    // ADDED FOR R4
    }
                                                                           
                                                                                                        
    @Override                                                                                           
    public Collection<Gateway> getNetworkGateways(String networkCode)                                   
            throws InvalidInputDataException, ElementNotFoundException {                                
        if (!isValidNetworkCode(networkCode)) {                                                         
            throw new InvalidInputDataException("Network code must be NET_ followed by 2 digits");      
        }
                                                                                                        
        Network network = networkRepo.read(networkCode);                                                
        if (network == null) {                                                                          
            throw new ElementNotFoundException("Network not found");                                    
        }                                                                                                        
        EntityManager entityManager = PersistenceManager.getEntityManager();                            
        try {                                                                                           
            TypedQuery<Gateway> q = entityManager.createQuery(                                          
                    "SELECT g FROM Gateway g WHERE g.network.code = :code", Gateway.class);             
            q.setParameter("code", networkCode);                                                        
            return q.getResultList();                                                                   // ADDED FOR R4
        } finally {                                                                                     // ADDED FOR R4
            entityManager.close();                                                                      // ADDED FOR R4
        }
    }
                                                                                                        // ADDED FOR R4
    @Override                                                                                           // ADDED FOR R4
    public Network connectGateway(String networkCode, String gatewayCode, String username)              // ADDED FOR R4
            throws ElementNotFoundException, UnauthorizedException, InvalidInputDataException {         // ADDED FOR R4
                                                                                                        // ADDED FOR R4
        if (!isValidNetworkCode(networkCode) || !isValidGatewayCode(gatewayCode)) {                     // ADDED FOR R4
            throw new InvalidInputDataException("Invalid network or gateway code format");              // ADDED FOR R4
        }
                                                                                                        // ADDED FOR R4
        User user = userRepo.read(username);                                                            // ADDED FOR R4
        if (user == null || user.getType() != UserType.MAINTAINER) {                                    // ADDED FOR R4
            throw new UnauthorizedException("User not authorized or not a maintainer");                 // ADDED FOR R4
        }
                                                                                                        // ADDED FOR R4
        Gateway gateway = gatewayRepo.read(gatewayCode);                                                // ADDED FOR R4
        if (gateway == null) throw new ElementNotFoundException("Gateway not found");                   // ADDED FOR R4
                                                                                                        // ADDED FOR R4
        Network network = networkRepo.read(networkCode);                                                // ADDED FOR R4
        if (network == null) throw new ElementNotFoundException("Network not found");                   // ADDED FOR R4
                                                                                                        // ADDED FOR R4
        gateway.setNetwork(network);                                                                    // ADDED FOR R4
        gatewayRepo.update(gateway);                                                                    // ADDED FOR R4
        return network;                                                                                 // ADDED FOR R4
    }
                                                                                                        // ADDED FOR R4
    @Override                                                                                           // ADDED FOR R4
    public Network disconnectGateway(String networkCode, String gatewayCode, String username)           // ADDED FOR R4
            throws ElementNotFoundException, UnauthorizedException, InvalidInputDataException {         // ADDED FOR R4
                                                                                                        // ADDED FOR R4
        if (!isValidNetworkCode(networkCode) || !isValidGatewayCode(gatewayCode)) {                     // ADDED FOR R4
            throw new InvalidInputDataException("Invalid format for codes");                            // ADDED FOR R4
        }
                                                                                                        // ADDED FOR R4
        User user = userRepo.read(username);                                                            // ADDED FOR R4
        if (user == null || user.getType() != UserType.MAINTAINER) {                                    // ADDED FOR R4
            throw new UnauthorizedException("User not authorized");                                     // ADDED FOR R4
        }
                                                                                                        // ADDED FOR R4
        Gateway gateway = gatewayRepo.read(gatewayCode);                                                // ADDED FOR R4
        if (gateway == null || gateway.getNetwork() == null ||                                          // ADDED FOR R4
            !networkCode.equals(gateway.getNetwork().getCode())) {                                      // ADDED FOR R4
            throw new ElementNotFoundException("Gateway or connection not found");                      // ADDED FOR R4
        }
                                                                                                        // ADDED FOR R4
        gateway.setNetwork(null);                                                                       // ADDED FOR R4
        gatewayRepo.update(gateway);                                                                    // ADDED FOR R4
        return networkRepo.read(networkCode);                                                           // ADDED FOR R4
    }
                                                                                                        // ADDED FOR R4
    @Override                                                                                           // ADDED FOR R4
    public Collection<Sensor> getGatewaySensors(String gatewayCode)                                     // ADDED FOR R4
            throws InvalidInputDataException, ElementNotFoundException {                                // ADDED FOR R4
                                                                                                        // ADDED FOR R4
        if (!isValidGatewayCode(gatewayCode)) {                                                         // ADDED FOR R4
            throw new InvalidInputDataException("Invalid gateway code format");                         // ADDED FOR R4
        }
                                                                                                        // ADDED FOR R4
        Gateway gateway = gatewayRepo.read(gatewayCode);                                                // ADDED FOR R4
        if (gateway == null) throw new ElementNotFoundException("Gateway not found");                   // ADDED FOR R4
                                                                                                        // ADDED FOR R4
        EntityManager entityManager = PersistenceManager.getEntityManager();                            // ADDED FOR R4
        try {                                                                                           // ADDED FOR R4
            TypedQuery<Sensor> q = entityManager.createQuery(                                          // ADDED FOR R4
                    "SELECT s FROM Sensor s WHERE s.gateway.code = :code", Sensor.class);               // ADDED FOR R4
            q.setParameter("code", gatewayCode);                                                        // ADDED FOR R4
            return q.getResultList();                                                                   // ADDED FOR R4
        } finally {                                                                                     // ADDED FOR R4
            entityManager.close();                                                                      // ADDED FOR R4
        }
    }
                                                                                                        // ADDED FOR R4
    @Override                                                                                           // ADDED FOR R4
    public Gateway connectSensor(String sensorCode, String gatewayCode, String username)               // ADDED FOR R4
            throws ElementNotFoundException, UnauthorizedException, InvalidInputDataException {         // ADDED FOR R4
                                                                                                        // ADDED FOR R4
        if (!isValidSensorCode(sensorCode) || !isValidGatewayCode(gatewayCode)) {                      // ADDED FOR R4
            throw new InvalidInputDataException("Invalid sensor or gateway code format");               // ADDED FOR R4
        }
                                                                                                        // ADDED FOR R4
        User user = userRepo.read(username);                                                            // ADDED FOR R4
        if (user == null || user.getType() != UserType.MAINTAINER) {                                    // ADDED FOR R4
            throw new UnauthorizedException("User not authorized");                                     // ADDED FOR R4
        }
                                                                                                        // ADDED FOR R4
        Sensor sensor = sensorRepo.read(sensorCode);                                                    // ADDED FOR R4
        if (sensor == null) throw new ElementNotFoundException("Sensor not found");                     // ADDED FOR R4
                                                                                                        // ADDED FOR R4
        Gateway gateway = gatewayRepo.read(gatewayCode);                                                // ADDED FOR R4
        if (gateway == null) throw new ElementNotFoundException("Gateway not found");                   // ADDED FOR R4
                                                                                                        // ADDED FOR R4
        sensor.setGateway(gateway);                                                                     // ADDED FOR R4
        sensorRepo.update(sensor);                                                                     // ADDED FOR R4
        return gateway;                                                                                 // ADDED FOR R4
    }
                                                                                                        // ADDED FOR R4
    @Override                                                                                           // ADDED FOR R4
    public Gateway disconnectSensor(String sensorCode, String gatewayCode, String username)            // ADDED FOR R4
            throws ElementNotFoundException, UnauthorizedException, InvalidInputDataException {         // ADDED FOR R4
                                                                                                        // ADDED FOR R4
        if (!isValidSensorCode(sensorCode) || !isValidGatewayCode(gatewayCode)) {                      // ADDED FOR R4
            throw new InvalidInputDataException("Invalid format for codes");                            // ADDED FOR R4
        }
                                                                                                        // ADDED FOR R4
        User user = userRepo.read(username);                                                            // ADDED FOR R4
        if (user == null || user.getType() != UserType.MAINTAINER) {                                    // ADDED FOR R4
            throw new UnauthorizedException("User not authorized");                                     // ADDED FOR R4
        }
                                                                                                        // ADDED FOR R4
        Sensor sensor = sensorRepo.read(sensorCode);                                                    // ADDED FOR R4
        if (sensor == null || sensor.getGateway() == null ||                                            // ADDED FOR R4
            !sensor.getGateway().getCode().equals(gatewayCode)) {                                       // ADDED FOR R4
            throw new ElementNotFoundException("Sensor not connected to this gateway");                  // ADDED FOR R4
        }
                                                                                                        // ADDED FOR R4
        sensor.setGateway(null);                                                                        // ADDED FOR R4
        sensorRepo.update(sensor);                                                                      // ADDED FOR R4
        return gatewayRepo.read(gatewayCode);                                                           // ADDED FOR R4
    }
}