package com.weather.report.test.custom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.weather.report.model.entities.Measurement;
import com.weather.report.repositories.MeasurementRepository;
import com.weather.report.test.BasePersistenceTest;

/**
 * Phase 3 custom tests for the database-side query methods added to
 * {@link MeasurementRepository}. These verify JPQL filtering by code and by
 * (inclusive) timestamp range, boundary handling, empty results, validation, and
 * safe parameter binding — independent of CSV import (measurements are created
 * directly through the repository).
 */
class CustomMeasurementRepositoryQueryTest extends BasePersistenceTest {

  private static final String NET_A = "NET_01";
  private static final String NET_B = "NET_02";
  private static final String GW_A = "GW_0001";
  private static final String GW_B = "GW_0002";
  private static final String SENSOR_A = "S_000001";
  private static final String SENSOR_B = "S_000002";

  private static final LocalDateTime T1 = LocalDateTime.of(2025, 11, 16, 10, 0, 0);
  private static final LocalDateTime T2 = LocalDateTime.of(2025, 11, 16, 12, 0, 0);
  private static final LocalDateTime T3 = LocalDateTime.of(2025, 11, 16, 14, 0, 0);
  private static final LocalDateTime T4 = LocalDateTime.of(2025, 11, 16, 16, 0, 0);

  private MeasurementRepository repo;

  @BeforeEach
  void setUpRepo() {
    repo = new MeasurementRepository();
    // Sensor A on gateway A / network A: four measurements at T1..T4
    save(NET_A, GW_A, SENSOR_A, 10.0, T1);
    save(NET_A, GW_A, SENSOR_A, 11.0, T2);
    save(NET_A, GW_A, SENSOR_A, 12.0, T3);
    save(NET_A, GW_A, SENSOR_A, 13.0, T4);
    // Sensor B on gateway B / network B: two measurements at T2, T3
    save(NET_B, GW_B, SENSOR_B, 20.0, T2);
    save(NET_B, GW_B, SENSOR_B, 21.0, T3);
  }

  private void save(String net, String gw, String sensor, double value, LocalDateTime ts) {
    repo.create(new Measurement(net, gw, sensor, value, ts));
  }

  @Test
  void findBySensorCodeReturnsOnlyMatchingRows() {
    List<Measurement> result = repo.findBySensorCode(SENSOR_A);
    assertEquals(4, result.size());
    assertTrue(result.stream().allMatch(m -> SENSOR_A.equals(m.getSensorCode())));
  }

  @Test
  void findByGatewayCodeReturnsOnlyMatchingRows() {
    List<Measurement> result = repo.findByGatewayCode(GW_B);
    assertEquals(2, result.size());
    assertTrue(result.stream().allMatch(m -> GW_B.equals(m.getGatewayCode())));
  }

  @Test
  void findByNetworkCodeReturnsOnlyMatchingRows() {
    List<Measurement> result = repo.findByNetworkCode(NET_A);
    assertEquals(4, result.size());
    assertTrue(result.stream().allMatch(m -> NET_A.equals(m.getNetworkCode())));
  }

  @Test
  void dateRangeFiltersInclusivelyOnBothBoundaries() {
    // [T2, T3] must include the measurements exactly at T2 and T3.
    List<Measurement> result = repo.findBySensorCodeAndDateRange(SENSOR_A, T2, T3);
    assertEquals(2, result.size());
    assertEquals(T2, result.get(0).getTimestamp());
    assertEquals(T3, result.get(1).getTimestamp());
  }

  @Test
  void startBoundaryOnlyIncludesFromStartInclusive() {
    List<Measurement> result = repo.findBySensorCodeAndDateRange(SENSOR_A, T2, null);
    assertEquals(3, result.size()); // T2, T3, T4
    assertEquals(T2, result.get(0).getTimestamp());
  }

  @Test
  void endBoundaryOnlyIncludesUpToEndInclusive() {
    List<Measurement> result = repo.findBySensorCodeAndDateRange(SENSOR_A, null, T2);
    assertEquals(2, result.size()); // T1, T2
    assertEquals(T2, result.get(result.size() - 1).getTimestamp());
  }

  @Test
  void noMatchReturnsEmptyListNotNull() {
    List<Measurement> result = repo.findBySensorCode("S_999999");
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void invalidDateRangeIsRejected() {
    assertThrows(IllegalArgumentException.class,
        () -> repo.findBySensorCodeAndDateRange(SENSOR_A, T3, T1));
    assertThrows(IllegalArgumentException.class,
        () -> repo.findByDateRange(T3, T1));
    assertThrows(IllegalArgumentException.class,
        () -> repo.countByDateRange(T3, T1));
  }

  @Test
  void nullOrBlankCodeIsRejected() {
    assertThrows(IllegalArgumentException.class, () -> repo.findBySensorCode(null));
    assertThrows(IllegalArgumentException.class, () -> repo.findByGatewayCode("   "));
    assertThrows(IllegalArgumentException.class, () -> repo.findByNetworkCode(""));
    assertThrows(IllegalArgumentException.class, () -> repo.countBySensorCode(null));
  }

  @Test
  void countQueriesReturnCorrectValues() {
    assertEquals(4L, repo.countBySensorCode(SENSOR_A));
    assertEquals(2L, repo.countByGatewayCode(GW_B));
    assertEquals(4L, repo.countByNetworkCode(NET_A));
    assertEquals(4L, repo.countByDateRange(T2, T3)); // 2 from A (T2,T3) + 2 from B (T2,T3)
    assertEquals(6L, repo.countByDateRange(null, null));
  }

  @Test
  void resultsAreOrderedByAscendingTimestamp() {
    List<Measurement> result = repo.findBySensorCode(SENSOR_A);
    for (int i = 1; i < result.size(); i++) {
      assertFalse(result.get(i).getTimestamp().isBefore(result.get(i - 1).getTimestamp()),
          "results must be ordered by ascending timestamp");
    }
  }

  @Test
  void codeParameterIsBoundSafelyNotConcatenated() {
    // A JPQL/SQL-injection-style string must be treated as a literal code value,
    // matching nothing rather than altering the query.
    List<Measurement> result = repo.findBySensorCode("S_000001' OR '1'='1");
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
}
