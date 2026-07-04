package com.weather.report.operations;

import java.util.Collection;                                                                           

import com.weather.report.exceptions.ElementNotFoundException;
import com.weather.report.exceptions.InvalidInputDataException;
import com.weather.report.exceptions.UnauthorizedException;
import com.weather.report.model.UserType;
import com.weather.report.model.entities.Gateway;
import com.weather.report.model.entities.Network;
import com.weather.report.model.entities.Sensor;
import com.weather.report.model.entities.User;
import com.weather.report.persistence.PersistenceManager;
import com.weather.report.repositories.CRUDRepository;                                                  

import jakarta.persistence.EntityManager;                                                               
import jakarta.persistence.TypedQuery;                                                                  
                                                                                                        
public class TopologyOperationsImpl implements TopologyOperations {                                      
                                                                                                        
    private final CRUDRepository<Gateway, String> gatewayRepo = new CRUDRepository<>(Gateway.class);    
    private final CRUDRepository<Network, String> networkRepo = new CRUDRepository<>(Network.class);    
    private final CRUDRepository<User, String> userRepo = new CRUDRepository<>(User.class);              
    private final CRUDRepository<Sensor, String> sensorRepo = new CRUDRepository<>(Sensor.class);        
                                                                                                        
                                            
                                                                                                        
    private boolean isValidNetworkCode(String code) {                                                   
        if (code == null || code.length() != 6 || !code.startsWith("NET_")) {                           
            return false;                                                                               
        }
        return Character.isDigit(code.charAt(4)) && Character.isDigit(code.charAt(5));                  
    }
                                                                                                        
    private boolean isValidGatewayCode(String code) {                                                   
        if (code == null || code.length() != 7 || !code.startsWith("GW_")) {                            
            return false;                                                                               
        }
        for (int i = 3; i < 7; i++) {                                                                   
            if (!Character.isDigit(code.charAt(i))) return false;                                       
        }
        return true;                                                                                    
    }
                                                                                                        
    private boolean isValidSensorCode(String code) {                                                    
        if (code == null || code.length() != 8 || !code.startsWith("S_")) {                             
            return false;                                                                               
        }
        for (int i = 2; i < 8; i++) {                                                                   
            if (!Character.isDigit(code.charAt(i))) return false;                                       
        }
        return true;                                                                                    
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
            return q.getResultList();                                                                   
        } finally {                                                                                     
            entityManager.close();                                                                      
        }
    }
                                                                                                        
    @Override                                                                                           
    public Network connectGateway(String networkCode, String gatewayCode, String username)              
            throws ElementNotFoundException, UnauthorizedException, InvalidInputDataException {         
                                                                                                        
        if (!isValidNetworkCode(networkCode) || !isValidGatewayCode(gatewayCode)) {                     
            throw new InvalidInputDataException("Invalid network or gateway code format");              
        }
                                                                                                        
        User user = userRepo.read(username);                                                            
        if (user == null || user.getType() != UserType.MAINTAINER) {                                    
            throw new UnauthorizedException("User not authorized or not a maintainer");                 
        }
                                                                                                        
        Gateway gateway = gatewayRepo.read(gatewayCode);                                                
        if (gateway == null) throw new ElementNotFoundException("Gateway not found");                   
                                                                                                        
        Network network = networkRepo.read(networkCode);                                                
        if (network == null) throw new ElementNotFoundException("Network not found");                   
                                                                                                        
        gateway.setNetwork(network);                                                                    
        gatewayRepo.update(gateway);                                                                    
        return network;                                                                                 
    }
                                                                                                        
    @Override                                                                                           
    public Network disconnectGateway(String networkCode, String gatewayCode, String username)           
            throws ElementNotFoundException, UnauthorizedException, InvalidInputDataException {         
                                                                                                        
        if (!isValidNetworkCode(networkCode) || !isValidGatewayCode(gatewayCode)) {                     
            throw new InvalidInputDataException("Invalid format for codes");                            
        }
                                                                                                        
        User user = userRepo.read(username);                                                            
        if (user == null || user.getType() != UserType.MAINTAINER) {                                    
            throw new UnauthorizedException("User not authorized");                                     
        }
                                                                                                        
        Gateway gateway = gatewayRepo.read(gatewayCode);                                                
        if (gateway == null || gateway.getNetwork() == null ||                                          
            !networkCode.equals(gateway.getNetwork().getCode())) {                                      
            throw new ElementNotFoundException("Gateway or connection not found");                      
        }
                                                                                                        
        gateway.setNetwork(null);                                                                       
        gatewayRepo.update(gateway);                                                                    
        return networkRepo.read(networkCode);                                                           
    }
                                                                                                        
    @Override                                                                                           
    public Collection<Sensor> getGatewaySensors(String gatewayCode)                                     
            throws InvalidInputDataException, ElementNotFoundException {                                
                                                                                                        
        if (!isValidGatewayCode(gatewayCode)) {                                                         
            throw new InvalidInputDataException("Invalid gateway code format");                         
        }
                                                                                                        
        Gateway gateway = gatewayRepo.read(gatewayCode);                                                
        if (gateway == null) throw new ElementNotFoundException("Gateway not found");                   
                                                                                                        
        EntityManager entityManager = PersistenceManager.getEntityManager();                            
        try {                                                                                           
            TypedQuery<Sensor> q = entityManager.createQuery(                                          
                    "SELECT s FROM Sensor s WHERE s.gateway.code = :code", Sensor.class);               
            q.setParameter("code", gatewayCode);                                                        
            return q.getResultList();                                                                   
        } finally {                                                                                     
            entityManager.close();                                                                      
        }
    }
                                                                                                        
    @Override                                                                                           
    public Gateway connectSensor(String sensorCode, String gatewayCode, String username)               
            throws ElementNotFoundException, UnauthorizedException, InvalidInputDataException {         
                                                                                                        
        if (!isValidSensorCode(sensorCode) || !isValidGatewayCode(gatewayCode)) {                      
            throw new InvalidInputDataException("Invalid sensor or gateway code format");               
        }
                                                                                                        
        User user = userRepo.read(username);                                                            
        if (user == null || user.getType() != UserType.MAINTAINER) {                                    
            throw new UnauthorizedException("User not authorized");                                     
        }
                                                                                                        
        Sensor sensor = sensorRepo.read(sensorCode);                                                    
        if (sensor == null) throw new ElementNotFoundException("Sensor not found");                     
                                                                                                        
        Gateway gateway = gatewayRepo.read(gatewayCode);                                                
        if (gateway == null) throw new ElementNotFoundException("Gateway not found");                   
                                                                                                        
        sensor.setGateway(gateway);                                                                     
        sensorRepo.update(sensor);                                                                     
        return gateway;                                                                                 
    }
                                                                                                        
    @Override                                                                                           
    public Gateway disconnectSensor(String sensorCode, String gatewayCode, String username)            
            throws ElementNotFoundException, UnauthorizedException, InvalidInputDataException {         
                                                                                                        
        if (!isValidSensorCode(sensorCode) || !isValidGatewayCode(gatewayCode)) {                      
            throw new InvalidInputDataException("Invalid format for codes");                            
        }
                                                                                                        
        User user = userRepo.read(username);                                                            
        if (user == null || user.getType() != UserType.MAINTAINER) {                                    
            throw new UnauthorizedException("User not authorized");                                     
        }
                                                                                                        
        Sensor sensor = sensorRepo.read(sensorCode);                                                    
        if (sensor == null || sensor.getGateway() == null ||                                            
            !sensor.getGateway().getCode().equals(gatewayCode)) {                                       
            throw new ElementNotFoundException("Sensor not connected to this gateway");                  
        }
                                                                                                        
        sensor.setGateway(null);                                                                        
        sensorRepo.update(sensor);                                                                      
        return gatewayRepo.read(gatewayCode);                                                           
    }
}