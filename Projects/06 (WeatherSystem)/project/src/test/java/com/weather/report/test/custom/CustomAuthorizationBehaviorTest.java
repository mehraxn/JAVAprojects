package com.weather.report.test.custom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.weather.report.exceptions.UnauthorizedException;
import com.weather.report.model.entities.Network;
import com.weather.report.test.BasePersistenceTest;

/**
 * Phase 6 authorization tests: only MAINTAINER users may perform write operations;
 * VIEWER and unknown users are rejected consistently with {@link UnauthorizedException}.
 */
class CustomAuthorizationBehaviorTest extends BasePersistenceTest {

  private static final String UNKNOWN_USER = "ghost";

  @Test
  void maintainerCanCreateNetwork() throws Exception {
    Network network = facade.networks().createNetwork(NET_01, "n", "d", MAINTAINER_USERNAME);
    assertEquals(NET_01, network.getCode());
  }

  @Test
  void viewerCannotCreateNetwork() {
    assertThrows(UnauthorizedException.class,
        () -> facade.networks().createNetwork(NET_01, "n", "d", VIEWER_USERNAME));
  }

  @Test
  void unknownUserCannotCreateNetwork() {
    assertThrows(UnauthorizedException.class,
        () -> facade.networks().createNetwork(NET_01, "n", "d", UNKNOWN_USER));
  }

  @Test
  void viewerCannotCreateGateway() {
    assertThrows(UnauthorizedException.class,
        () -> facade.gateways().createGateway(GW_0101, "g", "d", VIEWER_USERNAME));
  }

  @Test
  void viewerCannotCreateSensor() {
    assertThrows(UnauthorizedException.class,
        () -> facade.sensors().createSensor(SENSOR_010101, "s", "d", VIEWER_USERNAME));
  }

  @Test
  void viewerCannotConnectTopology() throws Exception {
    createNetwork(NET_01);
    createGateway(GW_0101);
    assertThrows(UnauthorizedException.class,
        () -> facade.topology().connectGateway(NET_01, GW_0101, VIEWER_USERNAME));
  }

  @Test
  void viewerCannotDeleteNetwork() throws Exception {
    createNetwork(NET_01);
    assertThrows(UnauthorizedException.class,
        () -> facade.networks().deleteNetwork(NET_01, VIEWER_USERNAME));
  }
}
