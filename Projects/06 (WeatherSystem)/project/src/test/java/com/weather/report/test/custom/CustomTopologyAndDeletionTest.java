package com.weather.report.test.custom;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import com.weather.report.exceptions.ElementNotFoundException;
import com.weather.report.model.entities.Gateway;
import com.weather.report.model.entities.Sensor;
import com.weather.report.test.BasePersistenceTest;

/**
 * Phase 6 topology-consistency and deletion tests exercised through the facade.
 */
class CustomTopologyAndDeletionTest extends BasePersistenceTest {

  private static boolean containsCode(Collection<Gateway> gateways, String code) {
    return gateways.stream().anyMatch(g -> code.equals(g.getCode()));
  }

  private static boolean containsSensorCode(Collection<Sensor> sensors, String code) {
    return sensors.stream().anyMatch(s -> code.equals(s.getCode()));
  }

  @Test
  void connectGatewayIsReflectedInNetworkGateways() throws Exception {
    createNetwork(NET_01);
    createGateway(GW_0101);
    connectGateway(NET_01, GW_0101);

    Collection<Gateway> gateways = facade.topology().getNetworkGateways(NET_01);
    assertTrue(containsCode(gateways, GW_0101));
  }

  @Test
  void disconnectGatewayRemovesItFromNetwork() throws Exception {
    createNetwork(NET_01);
    createGateway(GW_0101);
    connectGateway(NET_01, GW_0101);

    facade.topology().disconnectGateway(NET_01, GW_0101, MAINTAINER_USERNAME);

    Collection<Gateway> gateways = facade.topology().getNetworkGateways(NET_01);
    assertTrue(gateways.stream().noneMatch(g -> GW_0101.equals(g.getCode())));
  }

  @Test
  void connectSensorIsReflectedInGatewaySensors() throws Exception {
    createGateway(GW_0101);
    createSensor(SENSOR_010101);
    connectSensor(SENSOR_010101, GW_0101);

    Collection<Sensor> sensors = facade.topology().getGatewaySensors(GW_0101);
    assertTrue(containsSensorCode(sensors, SENSOR_010101));
  }

  @Test
  void connectingMissingGatewayIsRejected() throws Exception {
    createNetwork(NET_01);
    assertThrows(ElementNotFoundException.class,
        () -> facade.topology().connectGateway(NET_01, GW_UNKNOWN, MAINTAINER_USERNAME));
  }

  @Test
  void deletingMissingNetworkIsRejected() {
    assertThrows(ElementNotFoundException.class,
        () -> facade.networks().deleteNetwork(NET_99, MAINTAINER_USERNAME));
  }

  @Test
  void deletingMissingSensorIsRejected() {
    assertThrows(ElementNotFoundException.class,
        () -> facade.sensors().deleteSensor(SENSOR_UNKNOWN, MAINTAINER_USERNAME));
  }

  @Test
  void deleteSensorRemovesIt() throws Exception {
    createSensor(SENSOR_010101);
    facade.sensors().deleteSensor(SENSOR_010101, MAINTAINER_USERNAME);
    assertTrue(facade.sensors().getSensors(SENSOR_010101).isEmpty());
  }
}
