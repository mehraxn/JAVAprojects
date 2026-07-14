package com.weather.report.operations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.weather.report.exceptions.ElementNotFoundException;
import com.weather.report.exceptions.IdAlreadyInUseException;
import com.weather.report.exceptions.InvalidInputDataException;
import com.weather.report.exceptions.UnauthorizedException;
import com.weather.report.model.UserType;
import com.weather.report.model.entities.Network;
import com.weather.report.model.entities.Operator;
import com.weather.report.model.entities.User;
import com.weather.report.reports.NetworkReport;
import com.weather.report.reports.NetworkReportImpl;
import com.weather.report.repositories.CRUDRepository;
import com.weather.report.services.AlertingService;

public class NetworkOperationsImpl implements NetworkOperations {

    private final CRUDRepository<Network, String> networkRepository = new CRUDRepository<>(Network.class);
    private final CRUDRepository<Operator, String> operatorRepository = new CRUDRepository<>(Operator.class);
    private final CRUDRepository<User, String> userRepository = new CRUDRepository<>(User.class);

    private void validateUserIsMaintainer(String username) throws UnauthorizedException {
        if (username == null) {
            throw new UnauthorizedException("Username is null");
        }
        
        User user = userRepository.read(username);
        
        if (user == null) {
            throw new UnauthorizedException("User " + username + " is not authorized.");
        }
        
        if (user.getType() != UserType.MAINTAINER) {
            throw new UnauthorizedException("User " + username + " is not authorized.");
        }
    }


    private boolean isValidNetworkCode(String code) {
        if (code == null || code.length() != 6 || !code.startsWith("NET_")) {
            return false;
        }
        return Character.isDigit(code.charAt(4)) && Character.isDigit(code.charAt(5));
    }

    @Override
    public Network createNetwork(String code, String name, String description, String username)
            throws IdAlreadyInUseException, InvalidInputDataException, UnauthorizedException {
        
        validateUserIsMaintainer(username);
        
        if (code == null || code.isEmpty()) {
            throw new InvalidInputDataException("Network code is missing.");
        }
        

        boolean codeMatchesFormat = isValidNetworkCode(code);
        if (!codeMatchesFormat) {
            throw new InvalidInputDataException("Invalid network code format.");
        }
        
        Network existingNetwork = networkRepository.read(code);
        if (existingNetwork != null) {
            throw new IdAlreadyInUseException("Network code already in use.");
        }
        
        Network newNetwork = new Network(code, name, description);
        
        LocalDateTime currentTime = LocalDateTime.now();
        newNetwork.setCreatedBy(username);
        newNetwork.setCreatedAt(currentTime);
        newNetwork.setModifiedBy(username);
        newNetwork.setModifiedAt(currentTime);
        
        Network createdNetwork = networkRepository.create(newNetwork);
        return createdNetwork;
    }

    @Override
    public Network updateNetwork(String code, String name, String description, String username)
            throws InvalidInputDataException, ElementNotFoundException, UnauthorizedException {
        
        validateUserIsMaintainer(username);
        
        if (code == null) {
            throw new InvalidInputDataException("Code is null");
        }
        
        Network existingNetwork = networkRepository.read(code);
        
        if (existingNetwork == null) {
            throw new ElementNotFoundException("Network not found.");
        }
        
        existingNetwork.setName(name);
        existingNetwork.setDescription(description);
        
        LocalDateTime currentTime = LocalDateTime.now();
        existingNetwork.setModifiedBy(username);
        existingNetwork.setModifiedAt(currentTime);
        
        Network updatedNetwork = networkRepository.update(existingNetwork);
        return updatedNetwork;
    }

    @Override
    public Network deleteNetwork(String networkCode, String username)
            throws InvalidInputDataException, ElementNotFoundException, UnauthorizedException {
        
        validateUserIsMaintainer(username);
        
        Network existingNetwork = networkRepository.read(networkCode);
        
        if (existingNetwork == null) {
            throw new ElementNotFoundException("Network not found.");
        }
        
        networkRepository.delete(networkCode);
        
        AlertingService.notifyDeletion(username, networkCode, Network.class);
        
        return existingNetwork;
    }

    @Override
    public Collection<Network> getNetworks(String... codes) {
        boolean noCodesProvided = (codes == null || codes.length == 0);
        
        if (noCodesProvided) {
            List<Network> allNetworks = networkRepository.read();
            return allNetworks;
        }
        
        List<Network> foundNetworks = new ArrayList<>();
        
        for (String networkCode : codes) {
            Network network = networkRepository.read(networkCode);
            
            if (network != null) {
                foundNetworks.add(network);
            }
        }
        
        return foundNetworks;
    }

    @Override
    public Operator createOperator(String firstName, String lastName, String email, String phoneNumber, String username)
            throws InvalidInputDataException, IdAlreadyInUseException, UnauthorizedException {
        
        validateUserIsMaintainer(username);
        
        if (email == null || email.isEmpty()) {
            throw new InvalidInputDataException("Operator email is missing.");
        }
        
        if (firstName == null || lastName == null) {
            throw new InvalidInputDataException("Operator name is incomplete.");
        }
        
        Operator existingOperator = operatorRepository.read(email);
        if (existingOperator != null) {
            throw new IdAlreadyInUseException("Operator already exists.");
        }
        
        Operator newOperator = new Operator(email, firstName, lastName, phoneNumber);
        
        Operator createdOperator = operatorRepository.create(newOperator);
        return createdOperator;
    }

    @Override
    public Network addOperatorToNetwork(String networkCode, String operatorEmail, String username)
            throws ElementNotFoundException, InvalidInputDataException, UnauthorizedException {
        
        validateUserIsMaintainer(username);
        
        if (networkCode == null || operatorEmail == null) {
            throw new InvalidInputDataException("Null parameters");
        }
        
        Network network = networkRepository.read(networkCode);
        if (network == null) {
            throw new ElementNotFoundException("Network not found");
        }
        
        Operator operator = operatorRepository.read(operatorEmail);
        if (operator == null) {
            throw new ElementNotFoundException("Operator not found");
        }
        
        Collection<Operator> networkOperators = network.getOperators();
        if (networkOperators == null) {
            networkOperators = new ArrayList<>();
            network.setOperators(networkOperators);
        }
        
        boolean operatorAlreadyInNetwork = isOperatorInList(operator, networkOperators);
        
        if (!operatorAlreadyInNetwork) {
            networkOperators.add(operator);
            
            LocalDateTime currentTime = LocalDateTime.now();
            network.setModifiedBy(username);
            network.setModifiedAt(currentTime);
            
            networkRepository.update(network);
        }
        
        return network;
    }

    private boolean isOperatorInList(Operator operatorToFind, Collection<Operator> operatorList) {
        String emailToFind = operatorToFind.getEmail();
        
        for (Operator operator : operatorList) {
            String currentEmail = operator.getEmail();
            
            if (currentEmail.equals(emailToFind)) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public NetworkReport getNetworkReport(String networkCode, String startDate, String endDate)
            throws InvalidInputDataException, ElementNotFoundException {
        
        if (networkCode == null) {
            throw new InvalidInputDataException("Network code null");
        }
        
        Network network = networkRepository.read(networkCode);
        
        if (network == null) {
            throw new ElementNotFoundException("Network " + networkCode + " not found.");
        }
        
        // This is where you would apply the "inline return" fix we discussed earlier if needed
        return new NetworkReportImpl(networkCode, startDate, endDate);
    }
}