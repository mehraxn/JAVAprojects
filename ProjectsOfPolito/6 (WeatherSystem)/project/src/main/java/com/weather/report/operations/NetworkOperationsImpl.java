package com.weather.report.operations;

import java.time.LocalDateTime; //ADDED FOR R1
import java.util.ArrayList; //ADDED FOR R1
import java.util.Collection; //ADDED FOR R1
import java.util.List; //ADDED FOR R1

import com.weather.report.exceptions.ElementNotFoundException; //ADDED FOR R1
import com.weather.report.exceptions.IdAlreadyInUseException; //ADDED FOR R1
import com.weather.report.exceptions.InvalidInputDataException; //ADDED FOR R1
import com.weather.report.exceptions.UnauthorizedException; //ADDED FOR R1
import com.weather.report.exceptions.WeatherReportException; //ADDED FOR R1
import com.weather.report.model.UserType; //ADDED FOR R1
import com.weather.report.model.entities.Network; //ADDED FOR R1
import com.weather.report.model.entities.Operator; //ADDED FOR R1
import com.weather.report.model.entities.User; //ADDED FOR R1
import com.weather.report.reports.NetworkReport; //ADDED FOR R1
import com.weather.report.reports.NetworkReportImpl; //ADDED FOR R1
import com.weather.report.repositories.CRUDRepository; //ADDED FOR R1
import com.weather.report.services.AlertingService; //ADDED FOR R1

public class NetworkOperationsImpl implements NetworkOperations {

    private final CRUDRepository<Network, String> networkRepo = new CRUDRepository<>(Network.class); //ADDED FOR R1
    private final CRUDRepository<Operator, String> operatorRepo = new CRUDRepository<>(Operator.class); //ADDED FOR R1
    private final CRUDRepository<User, String> userRepo = new CRUDRepository<>(User.class); //ADDED FOR R1

    private void checkMaintainer(String username) throws UnauthorizedException { //ADDED FOR R1
        if (username == null) { //ADDED FOR R1
            throw new UnauthorizedException("Username is null"); //ADDED FOR R1
        } //ADDED FOR R1
        User u = userRepo.read(username); //ADDED FOR R1
        if (u == null || u.getType() != UserType.MAINTAINER) { //ADDED FOR R1
            throw new UnauthorizedException("User " + username + " is not authorized."); //ADDED FOR R1
        } //ADDED FOR R1
    } //ADDED FOR R1

    @Override //ADDED FOR R1
    public Network createNetwork(String code, String name, String description, String username) throws WeatherReportException { //ADDED FOR R1
        checkMaintainer(username); //ADDED FOR R1
        if (code == null || code.isEmpty()) { //ADDED FOR R1
            throw new InvalidInputDataException("Network code is missing."); //ADDED FOR R1
        } //ADDED FOR R1
        if (!code.matches("NET_\\d{2}")) { //ADDED FOR R1
            throw new InvalidInputDataException("Invalid network code format."); //ADDED FOR R1
        } //ADDED FOR R1
        if (networkRepo.read(code) != null) { //ADDED FOR R1
            throw new IdAlreadyInUseException("Network code already in use."); //ADDED FOR R1
        } //ADDED FOR R1
        
        Network network = new Network(code, name, description); //ADDED FOR R1
        network.setCreatedBy(username); //ADDED FOR R1
        network.setCreatedAt(LocalDateTime.now()); //ADDED FOR R1
        network.setModifiedBy(username); //ADDED FOR R1
        network.setModifiedAt(LocalDateTime.now()); //ADDED FOR R1
        
        return networkRepo.create(network); //ADDED FOR R1
    } //ADDED FOR R1

    @Override //ADDED FOR R1
    public Network updateNetwork(String code, String name, String description, String username) throws WeatherReportException { //ADDED FOR R1
        checkMaintainer(username); //ADDED FOR R1
        if (code == null) { //ADDED FOR R1
            throw new InvalidInputDataException("Code is null"); //ADDED FOR R1
        } //ADDED FOR R1

        Network existing = networkRepo.read(code); //ADDED FOR R1
        if (existing == null) { //ADDED FOR R1
            throw new ElementNotFoundException("Network not found."); //ADDED FOR R1
        } //ADDED FOR R1
        
        existing.setName(name); //ADDED FOR R1
        existing.setDescription(description); //ADDED FOR R1
        
        existing.setModifiedBy(username); //ADDED FOR R1
        existing.setModifiedAt(LocalDateTime.now()); //ADDED FOR R1
        
        return networkRepo.update(existing); //ADDED FOR R1
    } //ADDED FOR R1

    @Override //ADDED FOR R1
    public Network deleteNetwork(String networkCode, String username) throws WeatherReportException { //ADDED FOR R1
        checkMaintainer(username); //ADDED FOR R1
        Network existing = networkRepo.read(networkCode); //ADDED FOR R1
        if (existing == null) { //ADDED FOR R1
            throw new ElementNotFoundException("Network not found."); //ADDED FOR R1
        } //ADDED FOR R1
        networkRepo.delete(networkCode); //ADDED FOR R1
        AlertingService.notifyDeletion(username, networkCode, Network.class); //ADDED FOR R1
        return existing; //ADDED FOR R1
    } //ADDED FOR R1

    @Override //ADDED FOR R1
    public Collection<Network> getNetworks(String... codes) { //ADDED FOR R1
        if (codes == null || codes.length == 0) { //ADDED FOR R1
            return networkRepo.read(); //ADDED FOR R1
        } //ADDED FOR R1
        List<Network> result = new ArrayList<>(); //ADDED FOR R1
        for (String c : codes) { //ADDED FOR R1
            Network n = networkRepo.read(c); //ADDED FOR R1
            if (n != null) { //ADDED FOR R1
                result.add(n); //ADDED FOR R1
            } //ADDED FOR R1
        } //ADDED FOR R1
        return result; //ADDED FOR R1
    } //ADDED FOR R1

    @Override //ADDED FOR R1
    public Operator createOperator(String firstName, String lastName, String email, String phoneNumber, String username) throws WeatherReportException { //ADDED FOR R1
        checkMaintainer(username); //ADDED FOR R1
        if (email == null || email.isEmpty()) { //ADDED FOR R1
            throw new InvalidInputDataException("Operator email is missing."); //ADDED FOR R1
        } //ADDED FOR R1
        if (firstName == null || lastName == null) { //ADDED FOR R1
             throw new InvalidInputDataException("Operator name is incomplete."); //ADDED FOR R1
        } //ADDED FOR R1
        if (operatorRepo.read(email) != null) { //ADDED FOR R1
            throw new IdAlreadyInUseException("Operator already exists."); //ADDED FOR R1
        } //ADDED FOR R1
        Operator op = new Operator(email, firstName, lastName, phoneNumber); //ADDED FOR R1
        return operatorRepo.create(op); //ADDED FOR R1
    } //ADDED FOR R1

    @Override //ADDED FOR R1
    public Operator updateOperator(String firstName, String lastName, String email, String phoneNumber, String username) throws WeatherReportException { //ADDED FOR R1
        checkMaintainer(username); //ADDED FOR R1
        Operator existing = operatorRepo.read(email); //ADDED FOR R1
        if (existing == null) { //ADDED FOR R1
            throw new ElementNotFoundException("Operator not found."); //ADDED FOR R1
        } //ADDED FOR R1
        existing.setFirstName(firstName); //ADDED FOR R1
        existing.setLastName(lastName); //ADDED FOR R1
        existing.setPhoneNumber(phoneNumber); //ADDED FOR R1
        return operatorRepo.update(existing); //ADDED FOR R1
    } //ADDED FOR R1

    @Override //ADDED FOR R1
    public Operator deleteOperator(String username, String email) throws WeatherReportException { //ADDED FOR R1
        checkMaintainer(username); //ADDED FOR R1
        Operator existing = operatorRepo.read(email); //ADDED FOR R1
        if (existing == null) { //ADDED FOR R1
            throw new ElementNotFoundException("Operator not found."); //ADDED FOR R1
        } //ADDED FOR R1
        return operatorRepo.delete(email); //ADDED FOR R1
    } //ADDED FOR R1

    @Override //ADDED FOR R1
    public Network addOperatorToNetwork(String networkCode, String operatorEmail, String username) throws WeatherReportException { //ADDED FOR R1
        checkMaintainer(username); //ADDED FOR R1
        if (networkCode == null || operatorEmail == null) { //ADDED FOR R1
            throw new InvalidInputDataException("Null parameters"); //ADDED FOR R1
        } //ADDED FOR R1
        
        Network net = networkRepo.read(networkCode); //ADDED FOR R1
        if (net == null) { //ADDED FOR R1
            throw new ElementNotFoundException("Network not found"); //ADDED FOR R1
        } //ADDED FOR R1
        
        Operator op = operatorRepo.read(operatorEmail); //ADDED FOR R1
        if (op == null) { //ADDED FOR R1
            throw new ElementNotFoundException("Operator not found"); //ADDED FOR R1
        } //ADDED FOR R1
        
        if (net.getOperators() == null) { //ADDED FOR R1
            net.setOperators(new ArrayList<>()); //ADDED FOR R1
        } //ADDED FOR R1
        
        boolean present = false; //ADDED FOR R1
        for (Operator o : net.getOperators()) { //ADDED FOR R1
            if (o.getEmail().equals(op.getEmail())) { //ADDED FOR R1
                present = true; //ADDED FOR R1
                break; //ADDED FOR R1
            } //ADDED FOR R1
        } //ADDED FOR R1

        if (!present) { //ADDED FOR R1
             net.getOperators().add(op); //ADDED FOR R1
             net.setModifiedBy(username); //ADDED FOR R1
             net.setModifiedAt(LocalDateTime.now()); //ADDED FOR R1
             networkRepo.update(net); //ADDED FOR R1
        } //ADDED FOR R1
        return net; //ADDED FOR R1
    } //ADDED FOR R1

    @Override //ADDED FOR R1
    public NetworkReport getNetworkReport(String networkCode, String startDate, String endDate) throws WeatherReportException { //ADDED FOR R1
        if (networkCode == null) { //ADDED FOR R1
            throw new InvalidInputDataException("Network code null"); //ADDED FOR R1
        } //ADDED FOR R1
        Network n = networkRepo.read(networkCode); //ADDED FOR R1
        if (n == null) { //ADDED FOR R1
            throw new ElementNotFoundException("Network " + networkCode + " not found."); //ADDED FOR R1
        } //ADDED FOR R1
        return new NetworkReportImpl(networkCode, startDate, endDate); //ADDED FOR R1
    } //ADDED FOR R1
}