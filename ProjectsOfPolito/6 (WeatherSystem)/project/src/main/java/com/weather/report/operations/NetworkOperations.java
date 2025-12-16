package com.weather.report.operations;

import java.util.Collection;

import com.weather.report.exceptions.WeatherReportException;
import com.weather.report.model.entities.Network;
import com.weather.report.model.entities.Operator;
import com.weather.report.reports.NetworkReport;

public interface NetworkOperations {

    // Matches Test_R1: createNetwork(NET_01, networkName("1"), TEST_NETWORK_DESCRIPTION, MAINTAINER_USERNAME)
    Network createNetwork(String code, String name, String description, String username) throws WeatherReportException;
    
    // Matches Test_R1: updateNetwork(NET_01, UPDATED_NAME, UPDATED_DESCRIPTION, UPDATER_USERNAME)
    Network updateNetwork(String code, String name, String description, String username) throws WeatherReportException;
    
    // Matches Test_R1: deleteNetwork(NET_01, MAINTAINER_USERNAME)
    Network deleteNetwork(String networkCode, String username) throws WeatherReportException;
    
    Collection<Network> getNetworks(String... codes);
    
    // Matches Test_R1: createOperator(FIRST, LAST, EMAIL, PHONE, USERNAME)
    Operator createOperator(String firstName, String lastName, String email, String phoneNumber, String username) throws WeatherReportException;
    
    // inferred signature based on pattern
    Operator updateOperator(String firstName, String lastName, String email, String phoneNumber, String username) throws WeatherReportException;
    
    Operator deleteOperator(String username, String email) throws WeatherReportException;
    
    // Matches Test_R1: addOperatorToNetwork(NET_01, OPERATOR_ALICE_EMAIL, MAINTAINER_USERNAME)
    Network addOperatorToNetwork(String networkCode, String operatorEmail, String username) throws WeatherReportException;

    NetworkReport getNetworkReport(String networkCode, String startDate, String endDate) throws WeatherReportException;
}