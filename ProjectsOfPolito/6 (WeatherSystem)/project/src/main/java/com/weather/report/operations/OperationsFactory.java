package com.weather.report.operations;

import com.weather.report.reports.SensorOperationsimplement;

/**
 * Central factory providing concrete implementations of the operations
 * interfaces.
 * {@link com.weather.report.WeatherReport} delegates to these methods to obtain
 * the correct instances for requirements R1-R4.
 */
public final class OperationsFactory {

  // FROM ORIGIN/MAIN (Required for R2/R3)
  private static final GatewayOperations GATEWAY_OPERATIONS = new GatewayOperationsImplements(); // ADDED FOR R2
  private static final SensorOperations SENSOR_OPERATIONS = new SensorOperationsimplement();

  private OperationsFactory() {
    // utility class
  }

  /**
   * @return implementation of {@link NetworkOperations} configured for R1/R4
   */
  public static NetworkOperations getNetworkOperations() {
    // YOUR R1 LOGIC
    return new NetworkOperationsImpl(); // ADDED FOR R1
  }

  public static GatewayOperations getGatewayOperations() {
    return GATEWAY_OPERATIONS; // ADDED FOR R2
  }

  public static SensorOperations getSensorOperations() {
    return SENSOR_OPERATIONS;
  }

  public static TopologyOperations getTopologyOperations() {
    return new TopologyOperationsImpl();// ADDED FOR R4
  }
}