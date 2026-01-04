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

    private final CRUDRepository<Gateway, String> gatewayRepo =
            new CRUDRepository<>(Gateway.class);

    private final CRUDRepository<User, String> userRepo =
            new CRUDRepository<>(User.class);


    @Override
    public Gateway createGateway(
            String code,
            String name,
            String description,
            String username)
            throws IdAlreadyInUseException,
                   InvalidInputDataException,
                   UnauthorizedException {

        validateMaintainer(username); //is the user maintainer 
        checkCodeFormat(code); //is the code in the format GWXXXX 

        Gateway existingGateway = gatewayRepo.read(code);//does the code exist                  
        if (existingGateway != null) {                                          
            throw new IdAlreadyInUseException("Gateway code already in use.");  
        }


        Gateway g = new Gateway(code, name, description);
        g.setCreatedAt(LocalDateTime.now());
        g.setCreatedBy(username);
        Gateway createdGateway =  gatewayRepo.create(g);
        return createdGateway;
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

        validateMaintainer(username);
        checkCodeFormat(code);

        Gateway g = findGateway(code);

        gatewayRepo.delete(code);
        AlertingService.notifyDeletion(username, code, Gateway.class);

        return g;
    }

    @Override
    public Collection<Gateway> getGateways(String... gatewayCodes) {

        if (gatewayCodes == null || gatewayCodes.length == 0) {
            return gatewayRepo.read();
        }

        Collection<Gateway> result = new HashSet<>();
        for (String code : gatewayCodes) {
            Gateway g = gatewayRepo.read(code);
            if (g != null) {
                result.add(g);
            }
        }
        return result;
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

        Gateway g = validateParameterInput(gatewayCode, code, username);

        for (Parameter p : g.getParameters()) {
            if (p.getCode().equals(code)) {
                throw new IdAlreadyInUseException(
                        "Parameter with code " + code + " already exists"
                );
            }
        }

        Parameter p = new Parameter(code, name, description, value);
        g.addParameter(p);
        gatewayRepo.update(g);
        return p;
  
    }

    @Override
    public Parameter updateParameter(
            String gatewayCode,
            String code,
            double value,
            String username)
            throws InvalidInputDataException,
                   ElementNotFoundException,
                   UnauthorizedException {

        Gateway g = validateParameterInput(gatewayCode, code, username);
        Parameter p = g.getParameters()
            .stream()
            .filter(param -> param.getCode().equals(code))
            .findFirst()
            .orElseThrow(() ->
                new ElementNotFoundException(
                    "Parameter " + code + " not found in gateway " + gatewayCode
                ));

        p.setValue(value);
        gatewayRepo.update(g);

        return p;
    }


    @Override
    public GatewayReport getGatewayReport(
            String code,
            String startDate,
            String endDate) throws InvalidInputDataException, ElementNotFoundException {

        if (code == null) {                                              
            throw new InvalidInputDataException("Gateway code null");          
        }                                                                       
                                                                                
        Gateway gateway = gatewayRepo.read(code);                  
                                                                                
        if (gateway == null) {                                                  
            throw new ElementNotFoundException("Gateway " + code + " not found.");  
        }                                                                       
                                                                                
        GatewayReport report = new GatewayReportImplementation(code, startDate, endDate);  
        return report; 

    }


    private Gateway findGateway(String code)
            throws ElementNotFoundException {

        Gateway g = gatewayRepo.read(code);
        if (g == null) {
            throw new ElementNotFoundException("Gateway not found");
        }
        return g;
    }

    private Gateway validateParameterInput(
            String gatewayCode,
            String code,
            String username)
            throws InvalidInputDataException,
                   ElementNotFoundException,
                   UnauthorizedException {

        validateMaintainer(username);

        if (gatewayCode == null || gatewayCode.isEmpty()) {
            throw new InvalidInputDataException("Gateway code must not be null or empty");
        }
        if (code == null || code.isEmpty()) {
            throw new InvalidInputDataException("Parameter code must not be null or empty");
        }

        return findGateway(gatewayCode);
    }

    private void validateMaintainer(String username)
            throws UnauthorizedException {

        User u = userRepo.read(username);

        if (u == null) {
            throw new UnauthorizedException("User does not exist");
        }
        if (u.getType() != UserType.MAINTAINER) {
            throw new UnauthorizedException("User must be of MAINTAINER type");
        }
    }

    private void checkCodeFormat(String code)
            throws InvalidInputDataException {

        if (code == null || code.isEmpty()) {
            throw new InvalidInputDataException("The code must not be null or empty");
        }
        if (!code.matches("^GW_\\d{4}$")) {
            throw new InvalidInputDataException(
                    "The code must follow the format GW_XXXX"
            );
        }
    }
}


