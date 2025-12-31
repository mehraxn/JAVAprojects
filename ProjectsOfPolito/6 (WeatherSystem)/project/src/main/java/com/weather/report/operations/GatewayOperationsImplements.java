package com.weather.report.operations;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;

import com.weather.report.exceptions.ElementNotFoundException;
import com.weather.report.exceptions.IdAlreadyInUseException;
import com.weather.report.exceptions.InvalidInputDataException;
import com.weather.report.exceptions.UnauthorizedException;
import com.weather.report.model.UserType;
import com.weather.report.model.entities.Gateway;
import com.weather.report.model.entities.Parameter;
import com.weather.report.model.entities.User;
import com.weather.report.reports.GatewayReport;
import com.weather.report.reports.GatewayReportImplementation;
import com.weather.report.repositories.CRUDRepository;
import com.weather.report.services.AlertingService;

public class GatewayOperationsImplements implements GatewayOperations {

    private final CRUDRepository<Gateway, String> gatewayRepo =//ADDED FOR R2
            new CRUDRepository<>(Gateway.class);//ADDED FOR R2

    private final CRUDRepository<User, String> userRepo =//ADDED FOR R2
            new CRUDRepository<>(User.class);//ADDED FOR R2


    @Override//ADDED FOR R2
    public Gateway createGateway(//ADDED FOR R2
            String code,
            String name,
            String description,
            String username)
            throws IdAlreadyInUseException,
                   InvalidInputDataException,
                   UnauthorizedException {

        validateMaintainer(username); //is the user maintainer //ADDED FOR R2
        checkCodeFormat(code); //is the code in the format GWXXXX //ADDED FOR R2

        Gateway existingGateway = gatewayRepo.read(code);//does the code exist      //ADDED FOR R2            
        if (existingGateway != null) {                  //ADDED FOR R2                        
            throw new IdAlreadyInUseException("Gateway code already in use.");  //ADDED FOR R2
        }


        Gateway g = new Gateway(code, name, description);//ADDED FOR R2
        g.setCreatedAt(LocalDateTime.now());//ADDED FOR R2
        g.setCreatedBy(username);//ADDED FOR R2
        Gateway createdGateway =  gatewayRepo.create(g);//ADDED FOR R2
        return createdGateway;//ADDED FOR R2
    }

    @Override
    public Gateway updateGateway(
            String code,
            String name,
            String description,
            String username)
            throws InvalidInputDataException,
                   ElementNotFoundException,
                   UnauthorizedException {

    validateMaintainer(username);
    checkCodeFormat(code);

    Gateway gateway = findGateway(code);

    // Update only mutable fields
    gateway.setName(name);
    gateway.setDescription(description);

    gateway.setModifiedBy(username);
    gateway.setModifiedAt(LocalDateTime.now());

    gatewayRepo.update(gateway);
    return gateway;
    }

    @Override
    public Gateway deleteGateway(
            String code,
            String username)
            throws InvalidInputDataException,
                   ElementNotFoundException,
                   UnauthorizedException {

        validateMaintainer(username);//ADDED FOR R2
        checkCodeFormat(code);//ADDED FOR R2

        Gateway g = findGateway(code);//ADDED FOR R2

        gatewayRepo.delete(code);//ADDED FOR R2
        AlertingService.notifyDeletion(username, code, Gateway.class);//ADDED FOR R2

        return g;//ADDED FOR R2
    }

    @Override
    public Collection<Gateway> getGateways(String... gatewayCodes) {

        if (gatewayCodes == null || gatewayCodes.length == 0) {//ADDED FOR R2
            return gatewayRepo.read();//ADDED FOR R2
        }

        Collection<Gateway> result = new HashSet<>();//ADDED FOR R2
        for (String code : gatewayCodes) {//ADDED FOR R2
            Gateway g = gatewayRepo.read(code);//ADDED FOR R2
            if (g != null) {//ADDED FOR R2
                result.add(g);//ADDED FOR R2
            }
        }
        return result;//ADDED FOR R2
    }


    @Override
    public Parameter createParameter(
            String gatewayCode,
            String code,
            String name,
            String description,
            double value,
            String username)
            throws IdAlreadyInUseException,
                   InvalidInputDataException,
                   ElementNotFoundException,
                   UnauthorizedException {

        Gateway g = validateParameterInput(gatewayCode, code, username);//ADDED FOR R2

        for (Parameter p : g.getParameters()) {//ADDED FOR R2
            if (p.getCode().equals(code)) {//ADDED FOR R2
                throw new IdAlreadyInUseException(//ADDED FOR R2
                        "Parameter with code " + code + " already exists"//ADDED FOR R2
                );
            }
        }

        Parameter p = new Parameter(code, name, description, value);//ADDED FOR R2
        g.addParameter(p);//ADDED FOR R2
        gatewayRepo.update(g);//ADDED FOR R2
        return p;//ADDED FOR R2
  
    }

    @Override
    public Parameter updateParameter(//ADDED FOR R2
            String gatewayCode,
            String code,
            double value,
            String username)
            throws InvalidInputDataException,
                   ElementNotFoundException,
                   UnauthorizedException {

        Gateway g = validateParameterInput(gatewayCode, code, username);//ADDED FOR R2
        Parameter p = g.getParameters()//ADDED FOR R2
            .stream()
            .filter(param -> param.getCode().equals(code))
            .findFirst()
            .orElseThrow(() ->
                new ElementNotFoundException(
                    "Parameter " + code + " not found in gateway " + gatewayCode
                ));

        p.setValue(value);//ADDED FOR R2
        gatewayRepo.update(g);//ADDED FOR R2

        return p;//ADDED FOR R2
    }


    @Override
    public GatewayReport getGatewayReport(
            String code,
            String startDate,
            String endDate) throws InvalidInputDataException, ElementNotFoundException {

        if (code == null) {                                              
            throw new InvalidInputDataException("Gateway code null");    //ADDED FOR R2      
        }                                                                       
                                                                                
        Gateway gateway = gatewayRepo.read(code);             //ADDED FOR R2     
                                                                                
        if (gateway == null) {                                                  //ADDED FOR R2
            throw new ElementNotFoundException("Gateway " + code + " not found.");  //ADDED FOR R2
        }                                                                       
                                                                                
        GatewayReport report = new GatewayReportImplementation(code, startDate, endDate);  //ADDED FOR R2
        return report; //ADDED FOR R2

    }


    private Gateway findGateway(String code)
            throws ElementNotFoundException {

        Gateway g = gatewayRepo.read(code);//ADDED FOR R2
        if (g == null) {//ADDED FOR R2
            throw new ElementNotFoundException("Gateway not found");//ADDED FOR R2
        }
        return g;//ADDED FOR R2
    }

    private Gateway validateParameterInput(//ADDED FOR R2
            String gatewayCode,
            String code,
            String username)
            throws InvalidInputDataException,
                   ElementNotFoundException,
                   UnauthorizedException {

        validateMaintainer(username);//ADDED FOR R2

        if (gatewayCode == null || gatewayCode.isEmpty()) {//ADDED FOR R2
            throw new InvalidInputDataException("Gateway code must not be null or empty");//ADDED FOR R2
        }
        if (code == null || code.isEmpty()) {//ADDED FOR R2
            throw new InvalidInputDataException("Parameter code must not be null or empty");//ADDED FOR R2
        }

        return findGateway(gatewayCode);//ADDED FOR R2
    }

    private void validateMaintainer(String username)//ADDED FOR R2
            throws UnauthorizedException {

        User u = userRepo.read(username);//ADDED FOR R2

        if (u == null) {//ADDED FOR R2
            throw new UnauthorizedException("User does not exist");//ADDED FOR R2
        }
        if (u.getType() != UserType.MAINTAINER) {//ADDED FOR R2
            throw new UnauthorizedException("User must be of MAINTAINER type");//ADDED FOR R2
        }
    }

    private void checkCodeFormat(String code)//ADDED FOR R2
            throws InvalidInputDataException {//ADDED FOR R2

        if (code == null || code.isEmpty()) {//ADDED FOR R2
            throw new InvalidInputDataException("The code must not be null or empty");//ADDED FOR R2
        }
        if (!code.matches("^GW_\\d{4}$")) {//ADDED FOR R2
            throw new InvalidInputDataException(//ADDED FOR R2
                    "The code must follow the format GW_XXXX"//ADDED FOR R2
            );
        }
    }
}


