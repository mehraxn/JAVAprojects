package com.weather.report.operations;

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