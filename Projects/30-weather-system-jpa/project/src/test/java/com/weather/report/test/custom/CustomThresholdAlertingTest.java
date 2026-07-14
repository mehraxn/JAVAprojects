package com.weather.report.test.custom;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;

import com.weather.report.exceptions.WeatherReportException;
import com.weather.report.model.ThresholdType;
import com.weather.report.services.AlertingService;
import com.weather.report.services.DataImportingService;
import com.weather.report.test.BasePersistenceTest;

/**
 * Phase 5 threshold-alerting boundary tests. Sensors, thresholds, networks and
 * operators are created through the facade (real in-memory DB); imports use a
 * temporary CSV; {@link AlertingService} is statically mocked to verify whether a
 * threshold violation notification is (or is not) raised.
 */
class CustomThresholdAlertingTest extends BasePersistenceTest {

  private static final String SENSOR = "S_010101";
  private static final String NETWORK = "NET_01";
  private static final String HEADER = "date, networkCode, gatewayCode, sensorCode, value";

  @TempDir
  Path tempDir;

  private String csvWithValues(String name, double... values) throws IOException {
    StringBuilder sb = new StringBuilder(HEADER);
    int hour = 0;
    for (double v : values) {
      sb.append('\n').append(String.format("2025-11-16 %02d:00:00, %s, GW_0101, %s, %s",
          hour++, NETWORK, SENSOR, v));
    }
    Path file = tempDir.resolve(name);
    Files.writeString(file, sb.toString(), StandardCharsets.UTF_8);
    return file.toString();
  }

  private void createSensorWithThreshold(ThresholdType type, double value) throws WeatherReportException {
    facade.sensors().createSensor(SENSOR, "sensor", "desc", MAINTAINER_USERNAME);
    facade.sensors().createThreshold(SENSOR, type, value, MAINTAINER_USERNAME);
  }

  private void createNetworkWithOperator() throws WeatherReportException {
    facade.networks().createNetwork(NETWORK, "net", "desc", MAINTAINER_USERNAME);
    facade.networks().createOperator("Alice", "Smith", "alice@example.com", "123456789", MAINTAINER_USERNAME);
    facade.networks().addOperatorToNetwork(NETWORK, "alice@example.com", MAINTAINER_USERNAME);
  }

  @Test
  void valueAboveGreaterThanThresholdTriggersAlert() throws Exception {
    createSensorWithThreshold(ThresholdType.GREATER_THAN, 24.0);
    createNetworkWithOperator();
    String csv = csvWithValues("above.csv", 30.0);

    try (MockedStatic<AlertingService> alerting = mockStatic(AlertingService.class)) {
      DataImportingService.storeMeasurements(csv);
      alerting.verify(() -> AlertingService.notifyThresholdViolation(any(), eq(SENSOR)), times(1));
    }
  }

  @Test
  void valueEqualToGreaterThanThresholdDoesNotTrigger() throws Exception {
    createSensorWithThreshold(ThresholdType.GREATER_THAN, 24.0);
    createNetworkWithOperator();
    String csv = csvWithValues("equal.csv", 24.0);

    try (MockedStatic<AlertingService> alerting = mockStatic(AlertingService.class)) {
      DataImportingService.storeMeasurements(csv);
      alerting.verify(() -> AlertingService.notifyThresholdViolation(any(), any()), never());
    }
  }

  @Test
  void valueBelowGreaterThanThresholdDoesNotTrigger() throws Exception {
    createSensorWithThreshold(ThresholdType.GREATER_THAN, 24.0);
    createNetworkWithOperator();
    String csv = csvWithValues("below.csv", 20.0);

    try (MockedStatic<AlertingService> alerting = mockStatic(AlertingService.class)) {
      DataImportingService.storeMeasurements(csv);
      alerting.verify(() -> AlertingService.notifyThresholdViolation(any(), any()), never());
    }
  }

  @Test
  void valueBelowLessThanThresholdTriggersAlert() throws Exception {
    createSensorWithThreshold(ThresholdType.LESS_THAN, 10.0);
    createNetworkWithOperator();
    String csv = csvWithValues("lessthan.csv", 5.0);

    try (MockedStatic<AlertingService> alerting = mockStatic(AlertingService.class)) {
      DataImportingService.storeMeasurements(csv);
      alerting.verify(() -> AlertingService.notifyThresholdViolation(any(), eq(SENSOR)), times(1));
    }
  }

  @Test
  void sensorWithoutThresholdNeverAlerts() throws Exception {
    facade.sensors().createSensor(SENSOR, "sensor", "desc", MAINTAINER_USERNAME);
    createNetworkWithOperator();
    String csv = csvWithValues("nothreshold.csv", 999.0);

    try (MockedStatic<AlertingService> alerting = mockStatic(AlertingService.class)) {
      DataImportingService.storeMeasurements(csv);
      alerting.verify(() -> AlertingService.notifyThresholdViolation(any(), any()), never());
    }
  }

  @Test
  void onlyViolatingRowsTriggerAlerts() throws Exception {
    createSensorWithThreshold(ThresholdType.GREATER_THAN, 24.0);
    createNetworkWithOperator();
    // 20 (no), 24 (no, boundary), 30 (yes) -> exactly one alert.
    String csv = csvWithValues("mixed.csv", 20.0, 24.0, 30.0);

    try (MockedStatic<AlertingService> alerting = mockStatic(AlertingService.class)) {
      DataImportingService.storeMeasurements(csv);
      alerting.verify(() -> AlertingService.notifyThresholdViolation(any(), eq(SENSOR)), times(1));
    }
  }
}
