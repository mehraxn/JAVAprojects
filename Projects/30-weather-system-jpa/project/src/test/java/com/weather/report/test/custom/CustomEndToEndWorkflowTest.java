package com.weather.report.test.custom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.weather.report.reports.GatewayReport;
import com.weather.report.reports.NetworkReport;
import com.weather.report.reports.SensorReport;
import com.weather.report.test.BasePersistenceTest;

/**
 * Phase 6 end-to-end smoke test: build the full topology, import measurements from
 * a CSV, and produce all three reports, verifying a coherent non-empty result and
 * no exceptions in the normal workflow.
 */
class CustomEndToEndWorkflowTest extends BasePersistenceTest {

  @TempDir
  Path tempDir;

  @Test
  void fullWorkflowBuildsTopologyImportsDataAndProducesReports() throws Exception {
    // 1. Topology
    createNetwork(NET_01);
    createGateway(GW_0101);
    createSensor(SENSOR_010101);
    connectGateway(NET_01, GW_0101);
    connectSensor(SENSOR_010101, GW_0101);

    // 2. Import three measurements for this sensor/gateway/network
    Path csv = tempDir.resolve("e2e.csv");
    Files.writeString(csv, String.join("\n",
        "date, networkCode, gatewayCode, sensorCode, value",
        "2025-11-16 08:00:00, NET_01, GW_0101, S_010101, 10.0",
        "2025-11-16 09:00:00, NET_01, GW_0101, S_010101, 20.0",
        "2025-11-16 10:00:00, NET_01, GW_0101, S_010101, 30.0"),
        StandardCharsets.UTF_8);
    facade.importDataFromFile(csv.toString());

    // 3. Reports
    NetworkReport networkReport = facade.networks().getNetworkReport(NET_01, null, null);
    GatewayReport gatewayReport = facade.gateways().getGatewayReport(GW_0101, null, null);
    SensorReport sensorReport = facade.sensors().getSensorReport(SENSOR_010101, null, null);

    assertEquals(3, networkReport.getNumberOfMeasurements());
    assertEquals(3, gatewayReport.getNumberOfMeasurements());
    assertEquals(3, sensorReport.getNumberOfMeasurements());

    assertFalse(networkReport.getGatewaysLoadRatio().isEmpty());
    assertFalse(gatewayReport.getSensorsLoadRatio().isEmpty());
    assertFalse(sensorReport.getHistogram().isEmpty());
    // mean of {10,20,30} = 20
    assertEquals(20.0, sensorReport.getMean(), 1e-9);
  }
}
