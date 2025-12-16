package com.weather.report.operations;

import com.weather.report.operations.NetworkOperations; //ADDED FOR R1
import com.weather.report.operations.NetworkOperationsImpl; //ADDED FOR R1

public class OperationsFactory {

  public static NetworkOperations getNetworkOperations() {
    return new NetworkOperationsImpl(); //ADDED FOR R1
  }

  public static GatewayOperations getGatewayOperations() {
    return null;
  }

  public static SensorOperations getSensorOperations() {
    return null;
  }

  public static TopologyOperations getTopologyOperations() {
    return null;
  }
}