package com.weather.report.test.custom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.weather.report.model.entities.Measurement;
import com.weather.report.reports.GatewayReportImplementation;
import com.weather.report.reports.NetworkReportImpl;
import com.weather.report.repositories.MeasurementRepository;
import com.weather.report.test.BasePersistenceTest;

/**
 * Phase 4 tests for load-ratio unit consistency, date-range measurement subsets,
 * and return-value immutability of the Gateway and Network reports. Measurements
 * are seeded directly through the repository, and report implementations are
 * constructed directly (they read their own measurement subset from the database).
 *
 * Load ratio is expressed as a PERCENTAGE (0-100) in both reports (README R1/R2).
 */
class CustomReportLoadRatioAndFilteringTest extends BasePersistenceTest {

  private static final String NET_01 = "NET_01";
  private static final String GW_1 = "GW_0001";
  private static final String GW_2 = "GW_0002";
  private static final String S_A = "S_000001";
  private static final String S_B = "S_000002";
  private static final String S_C = "S_000003";
  private static final LocalDateTime BASE = LocalDateTime.of(2025, 1, 1, 0, 0, 0);

  private MeasurementRepository repo;

  @BeforeEach
  void seed() {
    repo = new MeasurementRepository();
    // Gateway GW_1: sensor A x3, sensor B x1 (total 4). Gateway GW_2: sensor C x2 (total 2).
    save(GW_1, S_A, 10.0, 0);
    save(GW_1, S_A, 11.0, 1);
    save(GW_1, S_A, 12.0, 2);
    save(GW_1, S_B, 20.0, 3);
    save(GW_2, S_C, 30.0, 4);
    save(GW_2, S_C, 31.0, 5);
    // Network NET_01 total = 6.
  }

  private void save(String gateway, String sensor, double value, int hourOffset) {
    repo.create(new Measurement(NET_01, gateway, sensor, value, BASE.plusHours(hourOffset)));
  }

  private static String ts(int hourOffset) {
    return String.format("2025-01-01 %02d:00:00", hourOffset);
  }

  // ---- load ratio unit ----

  @Test
  void gatewaySensorLoadRatioIsPercentageSummingTo100() {
    Map<String, Double> ratio = new GatewayReportImplementation(GW_1, null, null).getSensorsLoadRatio();
    assertEquals(75.0, ratio.get(S_A), 1e-6);
    assertEquals(25.0, ratio.get(S_B), 1e-6);
    assertEquals(100.0, ratio.values().stream().mapToDouble(Double::doubleValue).sum(), 1e-6);
  }

  @Test
  void networkGatewayLoadRatioIsPercentageSummingTo100() {
    Map<String, Double> ratio = new NetworkReportImpl(NET_01, null, null).getGatewaysLoadRatio();
    assertEquals(66.6667, ratio.get(GW_1), 1e-3);
    assertEquals(33.3333, ratio.get(GW_2), 1e-3);
    assertEquals(100.0, ratio.values().stream().mapToDouble(Double::doubleValue).sum(), 1e-6);
  }

  @Test
  void loadRatioUnitIsConsistentAcrossReportTypes() {
    double gatewaySum = new GatewayReportImplementation(GW_1, null, null)
        .getSensorsLoadRatio().values().stream().mapToDouble(Double::doubleValue).sum();
    double networkSum = new NetworkReportImpl(NET_01, null, null)
        .getGatewaysLoadRatio().values().stream().mapToDouble(Double::doubleValue).sum();
    // Both are percentages, so both add up to 100.
    assertEquals(100.0, gatewaySum, 1e-6);
    assertEquals(100.0, networkSum, 1e-6);
  }

  // ---- date-range subsets ----

  @Test
  void gatewayReportUsesInclusiveDateRangeSubset() {
    // GW_1 measurements at hours 0..3; [00:00, 01:00] -> hours 0 and 1 -> 2 rows.
    GatewayReportImplementation report = new GatewayReportImplementation(GW_1, ts(0), ts(1));
    assertEquals(2, report.getNumberOfMeasurements());
  }

  @Test
  void networkReportUsesInclusiveDateRangeSubset() {
    // NET_01 measurements at hours 0..5; [00:00, 02:00] -> hours 0,1,2 -> 3 rows.
    NetworkReportImpl report = new NetworkReportImpl(NET_01, ts(0), ts(2));
    assertEquals(3, report.getNumberOfMeasurements());
  }

  // ---- immutability ----

  @Test
  void gatewayReportCollectionsAreUnmodifiable() {
    GatewayReportImplementation report = new GatewayReportImplementation(GW_1, null, null);
    assertThrows(UnsupportedOperationException.class, () -> report.getSensorsLoadRatio().clear());
    assertThrows(UnsupportedOperationException.class, () -> report.getHistogram().clear());
  }

  @Test
  void networkReportCollectionsAreUnmodifiable() {
    NetworkReportImpl report = new NetworkReportImpl(NET_01, null, null);
    assertThrows(UnsupportedOperationException.class, () -> report.getGatewaysLoadRatio().clear());
    assertThrows(UnsupportedOperationException.class, () -> report.getMostActiveGateways().clear());
    assertThrows(UnsupportedOperationException.class, () -> report.getHistogram().clear());
  }

  // ---- smoke: larger dataset ----

  @Test
  void gatewayReportHandlesLargerDataset() {
    for (int i = 0; i < 100; i++) {
      repo.create(new Measurement(NET_01, "GW_0009", "S_009999", i, BASE.plusHours(10).plusMinutes(i)));
    }
    GatewayReportImplementation report = new GatewayReportImplementation("GW_0009", null, null);
    assertEquals(100, report.getNumberOfMeasurements());
    assertEquals(100.0,
        report.getSensorsLoadRatio().values().stream().mapToDouble(Double::doubleValue).sum(), 1e-6);
  }
}
