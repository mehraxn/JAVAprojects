package com.weather.report.operations;

import java.time.LocalDateTime;                                                 //ADDED FOR R1
import java.util.ArrayList;                                                     //ADDED FOR R1
import java.util.Collection;                                                    //ADDED FOR R1
import java.util.List;                                                          //ADDED FOR R1

import com.weather.report.exceptions.ElementNotFoundException;                  //ADDED FOR R1
import com.weather.report.exceptions.IdAlreadyInUseException;                   //ADDED FOR R1
import com.weather.report.exceptions.InvalidInputDataException;                 //ADDED FOR R1
import com.weather.report.exceptions.UnauthorizedException;                     //ADDED FOR R1
import com.weather.report.model.UserType;                                       //ADDED FOR R1
import com.weather.report.model.entities.Network;                               //ADDED FOR R1
import com.weather.report.model.entities.Operator;                              //ADDED FOR R1
import com.weather.report.model.entities.User;                                  //ADDED FOR R1
import com.weather.report.reports.NetworkReport;                                //ADDED FOR R1
import com.weather.report.reports.NetworkReportImpl;                            //ADDED FOR R1
import com.weather.report.repositories.CRUDRepository;                          //ADDED FOR R1
import com.weather.report.services.AlertingService;                             //ADDED FOR R1

public class NetworkOperationsImpl implements NetworkOperations {               //ADDED FOR R1

    private final CRUDRepository<Network, String> networkRepository = new CRUDRepository<>(Network.class);      //ADDED FOR R1
    private final CRUDRepository<Operator, String> operatorRepository = new CRUDRepository<>(Operator.class);   //ADDED FOR R1
    private final CRUDRepository<User, String> userRepository = new CRUDRepository<>(User.class);               //ADDED FOR R1

    private void validateUserIsMaintainer(String username) throws UnauthorizedException {                       //ADDED FOR R1
        if (username == null) {                                                 //ADDED FOR R1
            throw new UnauthorizedException("Username is null");                //ADDED FOR R1
        }                                                                       //ADDED FOR R1
                                                                                //ADDED FOR R1
        User user = userRepository.read(username);                              //ADDED FOR R1
                                                                                //ADDED FOR R1
        if (user == null) {                                                     //ADDED FOR R1
            throw new UnauthorizedException("User " + username + " is not authorized.");  //ADDED FOR R1
        }                                                                       //ADDED FOR R1
                                                                                //ADDED FOR R1
        if (user.getType() != UserType.MAINTAINER) {                            //ADDED FOR R1
            throw new UnauthorizedException("User " + username + " is not authorized.");  //ADDED FOR R1
        }                                                                       //ADDED FOR R1
    }                                                                           //ADDED FOR R1

    @Override                                                                   //ADDED FOR R1
    public Network createNetwork(String code, String name, String description, String username)                 //ADDED FOR R1
            throws IdAlreadyInUseException, InvalidInputDataException, UnauthorizedException {                  //ADDED FOR R1
                                                                                //ADDED FOR R1
        validateUserIsMaintainer(username);                                     //ADDED FOR R1
                                                                                //ADDED FOR R1
        if (code == null || code.isEmpty()) {                                   //ADDED FOR R1
            throw new InvalidInputDataException("Network code is missing.");    //ADDED FOR R1
        }                                                                       //ADDED FOR R1
                                                                                //ADDED FOR R1
        boolean codeMatchesFormat = code.matches("NET_\\d{2}");                 //ADDED FOR R1
        if (!codeMatchesFormat) {                                               //ADDED FOR R1
            throw new InvalidInputDataException("Invalid network code format."); //ADDED FOR R1
        }                                                                       //ADDED FOR R1
                                                                                //ADDED FOR R1
        Network existingNetwork = networkRepository.read(code);                 //ADDED FOR R1
        if (existingNetwork != null) {                                          //ADDED FOR R1
            throw new IdAlreadyInUseException("Network code already in use.");  //ADDED FOR R1
        }                                                                       //ADDED FOR R1
                                                                                //ADDED FOR R1
        Network newNetwork = new Network(code, name, description);              //ADDED FOR R1
                                                                                //ADDED FOR R1
        LocalDateTime currentTime = LocalDateTime.now();                        //ADDED FOR R1
        newNetwork.setCreatedBy(username);                                      //ADDED FOR R1
        newNetwork.setCreatedAt(currentTime);                                   //ADDED FOR R1
        newNetwork.setModifiedBy(username);                                     //ADDED FOR R1
        newNetwork.setModifiedAt(currentTime);                                  //ADDED FOR R1
                                                                                //ADDED FOR R1
        Network createdNetwork = networkRepository.create(newNetwork);          //ADDED FOR R1
        return createdNetwork;                                                  //ADDED FOR R1
    }                                                                           //ADDED FOR R1

    @Override                                                                   //ADDED FOR R1
    public Network updateNetwork(String code, String name, String description, String username)                 //ADDED FOR R1
            throws InvalidInputDataException, ElementNotFoundException, UnauthorizedException {                 //ADDED FOR R1
                                                                                //ADDED FOR R1
        validateUserIsMaintainer(username);                                     //ADDED FOR R1
                                                                                //ADDED FOR R1
        if (code == null) {                                                     //ADDED FOR R1
            throw new InvalidInputDataException("Code is null");                //ADDED FOR R1
        }                                                                       //ADDED FOR R1
                                                                                //ADDED FOR R1
        Network existingNetwork = networkRepository.read(code);                 //ADDED FOR R1
                                                                                //ADDED FOR R1
        if (existingNetwork == null) {                                          //ADDED FOR R1
            throw new ElementNotFoundException("Network not found.");           //ADDED FOR R1
        }                                                                       //ADDED FOR R1
                                                                                //ADDED FOR R1
        existingNetwork.setName(name);                                          //ADDED FOR R1
        existingNetwork.setDescription(description);                            //ADDED FOR R1
                                                                                //ADDED FOR R1
        LocalDateTime currentTime = LocalDateTime.now();                        //ADDED FOR R1
        existingNetwork.setModifiedBy(username);                                //ADDED FOR R1
        existingNetwork.setModifiedAt(currentTime);                             //ADDED FOR R1
                                                                                //ADDED FOR R1
        Network updatedNetwork = networkRepository.update(existingNetwork);     //ADDED FOR R1
        return updatedNetwork;                                                  //ADDED FOR R1
    }                                                                           //ADDED FOR R1

    @Override                                                                   //ADDED FOR R1
    public Network deleteNetwork(String networkCode, String username)           //ADDED FOR R1
            throws InvalidInputDataException, ElementNotFoundException, UnauthorizedException {                 //ADDED FOR R1
                                                                                //ADDED FOR R1
        validateUserIsMaintainer(username);                                     //ADDED FOR R1
                                                                                //ADDED FOR R1
        Network existingNetwork = networkRepository.read(networkCode);          //ADDED FOR R1
                                                                                //ADDED FOR R1
        if (existingNetwork == null) {                                          //ADDED FOR R1
            throw new ElementNotFoundException("Network not found.");           //ADDED FOR R1
        }                                                                       //ADDED FOR R1
                                                                                //ADDED FOR R1
        networkRepository.delete(networkCode);                                  //ADDED FOR R1
                                                                                //ADDED FOR R1
        AlertingService.notifyDeletion(username, networkCode, Network.class);   //ADDED FOR R1
                                                                                //ADDED FOR R1
        return existingNetwork;                                                 //ADDED FOR R1
    }                                                                           //ADDED FOR R1

    @Override                                                                   //ADDED FOR R1
    public Collection<Network> getNetworks(String... codes) {                   //ADDED FOR R1
        boolean noCodesProvided = (codes == null || codes.length == 0);         //ADDED FOR R1
                                                                                //ADDED FOR R1
        if (noCodesProvided) {                                                  //ADDED FOR R1
            List<Network> allNetworks = networkRepository.read();               //ADDED FOR R1
            return allNetworks;                                                 //ADDED FOR R1
        }                                                                       //ADDED FOR R1
                                                                                //ADDED FOR R1
        List<Network> foundNetworks = new ArrayList<>();                        //ADDED FOR R1
                                                                                //ADDED FOR R1
        for (String networkCode : codes) {                                      //ADDED FOR R1
            Network network = networkRepository.read(networkCode);              //ADDED FOR R1
                                                                                //ADDED FOR R1
            if (network != null) {                                              //ADDED FOR R1
                foundNetworks.add(network);                                     //ADDED FOR R1
            }                                                                   //ADDED FOR R1
        }                                                                       //ADDED FOR R1
                                                                                //ADDED FOR R1
        return foundNetworks;                                                   //ADDED FOR R1
    }                                                                           //ADDED FOR R1

    @Override                                                                   //ADDED FOR R1
    public Operator createOperator(String firstName, String lastName, String email, String phoneNumber, String username)  //ADDED FOR R1
            throws InvalidInputDataException, IdAlreadyInUseException, UnauthorizedException {                  //ADDED FOR R1
                                                                                //ADDED FOR R1
        validateUserIsMaintainer(username);                                     //ADDED FOR R1
                                                                                //ADDED FOR R1
        if (email == null || email.isEmpty()) {                                 //ADDED FOR R1
            throw new InvalidInputDataException("Operator email is missing.");  //ADDED FOR R1
        }                                                                       //ADDED FOR R1
                                                                                //ADDED FOR R1
        if (firstName == null || lastName == null) {                            //ADDED FOR R1
            throw new InvalidInputDataException("Operator name is incomplete."); //ADDED FOR R1
        }                                                                       //ADDED FOR R1
                                                                                //ADDED FOR R1
        Operator existingOperator = operatorRepository.read(email);             //ADDED FOR R1
        if (existingOperator != null) {                                         //ADDED FOR R1
            throw new IdAlreadyInUseException("Operator already exists.");      //ADDED FOR R1
        }                                                                       //ADDED FOR R1
                                                                                //ADDED FOR R1
        Operator newOperator = new Operator(email, firstName, lastName, phoneNumber);  //ADDED FOR R1
                                                                                //ADDED FOR R1
        Operator createdOperator = operatorRepository.create(newOperator);      //ADDED FOR R1
        return createdOperator;                                                 //ADDED FOR R1
    }                                                                           //ADDED FOR R1

    @Override                                                                   //ADDED FOR R1
    public Network addOperatorToNetwork(String networkCode, String operatorEmail, String username)              //ADDED FOR R1
            throws ElementNotFoundException, InvalidInputDataException, UnauthorizedException {                 //ADDED FOR R1
                                                                                //ADDED FOR R1
        validateUserIsMaintainer(username);                                     //ADDED FOR R1
                                                                                //ADDED FOR R1
        if (networkCode == null || operatorEmail == null) {                     //ADDED FOR R1
            throw new InvalidInputDataException("Null parameters");             //ADDED FOR R1
        }                                                                       //ADDED FOR R1
                                                                                //ADDED FOR R1
        Network network = networkRepository.read(networkCode);                  //ADDED FOR R1
        if (network == null) {                                                  //ADDED FOR R1
            throw new ElementNotFoundException("Network not found");            //ADDED FOR R1
        }                                                                       //ADDED FOR R1
                                                                                //ADDED FOR R1
        Operator operator = operatorRepository.read(operatorEmail);             //ADDED FOR R1
        if (operator == null) {                                                 //ADDED FOR R1
            throw new ElementNotFoundException("Operator not found");           //ADDED FOR R1
        }                                                                       //ADDED FOR R1
                                                                                //ADDED FOR R1
        Collection<Operator> networkOperators = network.getOperators();         //ADDED FOR R1
        if (networkOperators == null) {                                         //ADDED FOR R1
            networkOperators = new ArrayList<>();                               //ADDED FOR R1
            network.setOperators(networkOperators);                             //ADDED FOR R1
        }                                                                       //ADDED FOR R1
                                                                                //ADDED FOR R1
        boolean operatorAlreadyInNetwork = isOperatorInList(operator, networkOperators);  //ADDED FOR R1
                                                                                //ADDED FOR R1
        if (!operatorAlreadyInNetwork) {                                        //ADDED FOR R1
            networkOperators.add(operator);                                     //ADDED FOR R1
                                                                                //ADDED FOR R1
            LocalDateTime currentTime = LocalDateTime.now();                    //ADDED FOR R1
            network.setModifiedBy(username);                                    //ADDED FOR R1
            network.setModifiedAt(currentTime);                                 //ADDED FOR R1
                                                                                //ADDED FOR R1
            networkRepository.update(network);                                  //ADDED FOR R1
        }                                                                       //ADDED FOR R1
                                                                                //ADDED FOR R1
        return network;                                                         //ADDED FOR R1
    }                                                                           //ADDED FOR R1
                                                                                //ADDED FOR R1
    private boolean isOperatorInList(Operator operatorToFind, Collection<Operator> operatorList) {              //ADDED FOR R1
        String emailToFind = operatorToFind.getEmail();                         //ADDED FOR R1
                                                                                //ADDED FOR R1
        for (Operator operator : operatorList) {                                //ADDED FOR R1
            String currentEmail = operator.getEmail();                          //ADDED FOR R1
                                                                                //ADDED FOR R1
            if (currentEmail.equals(emailToFind)) {                             //ADDED FOR R1
                return true;                                                    //ADDED FOR R1
            }                                                                   //ADDED FOR R1
        }                                                                       //ADDED FOR R1
                                                                                //ADDED FOR R1
        return false;                                                           //ADDED FOR R1
    }                                                                           //ADDED FOR R1

    @Override                                                                   //ADDED FOR R1
    public NetworkReport getNetworkReport(String networkCode, String startDate, String endDate)                 //ADDED FOR R1
            throws InvalidInputDataException, ElementNotFoundException {        //ADDED FOR R1
                                                                                //ADDED FOR R1
        if (networkCode == null) {                                              //ADDED FOR R1
            throw new InvalidInputDataException("Network code null");           //ADDED FOR R1
        }                                                                       //ADDED FOR R1
                                                                                //ADDED FOR R1
        Network network = networkRepository.read(networkCode);                  //ADDED FOR R1
                                                                                //ADDED FOR R1
        if (network == null) {                                                  //ADDED FOR R1
            throw new ElementNotFoundException("Network " + networkCode + " not found.");  //ADDED FOR R1
        }                                                                       //ADDED FOR R1
                                                                                //ADDED FOR R1
        NetworkReport report = new NetworkReportImpl(networkCode, startDate, endDate);  //ADDED FOR R1
        return report;                                                          //ADDED FOR R1
    }                                                                           //ADDED FOR R1
}                                                                               //ADDED FOR R1