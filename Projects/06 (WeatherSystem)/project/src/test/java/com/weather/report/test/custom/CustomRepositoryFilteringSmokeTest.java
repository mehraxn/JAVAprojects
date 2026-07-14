package com.weather.report.test.custom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.weather.report.model.entities.Measurement;
import com.weather.report.repositories.MeasurementRepository;
import com.weather.report.test.BasePersistenceTest;

/**
 * Phase 3 smoke test: proves the database-side query methods behave correctly
 * with a larger, realistic sample (200 rows across two sensors and a 100-hour
 * span). This is a correctness check, not a benchmark — there are no timing
 * assertions.
 */
class CustomRepositoryFilteringSmokeTest extends BasePersistenceTest {

  private static final String SENSOR_A = "S_000001";
  private static final String SENSOR_B = "S_000002";
  private static final String GW_A = "GW_0001";
  private static final String NET_A = "NET_01";
  private static final LocalDateTime BASE = LocalDateTime.of(2025, 1, 1, 0, 0, 0);

  private MeasurementRepository repo;

  @BeforeEach
  void seedData() {
    repo = new MeasurementRepository();
    // 100 measurements per sensor, one per hour starting at BASE.
    for (int hour = 0; hour < 100; hour++) {
      LocalDateTime ts = BASE.plusHours(hour);
      repo.create(new Measurement(NET_A, GW_A, SENSOR_A, hour, ts));
      repo.create(new Measurement(NET_A, GW_A, SENSOR_B, 1000 + hour, ts));
    }
  }

  @Test
  void totalRowsPersisted() {
    assertEquals(200L, repo.countByDateRange(null, null));
    assertEquals(100L, repo.countBySensorCode(SENSOR_A));
  }

  @Test
  void sensorAndRangeQueryReturnsOnlyExpectedRows() {
    // Hours [10, 19] inclusive -> 10 rows for sensor A only.
    LocalDateTime start = BASE.plusHours(10);
    LocalDateTime end = BASE.plusHours(19);

    List<Measurement> result = repo.findBySensorCodeAndDateRange(SENSOR_A, start, end);

    assertEquals(10, result.size());
    assertTrue(result.stream().allMatch(m -> SENSOR_A.equals(m.getSensorCode())),
        "range query must not leak the other sensor's rows");
    assertTrue(result.stream().allMatch(
        m -> !m.getTimestamp().isBefore(start) && !m.getTimestamp().isAfter(end)),
        "every row must fall inside the inclusive range");
    // Ordered ascending, so first is hour 10 and last is hour 19.
    assertEquals(start, result.get(0).getTimestamp());
    assertEquals(end, result.get(result.size() - 1).getTimestamp());
  }

  @Test
  void countByDateRangeMatchesFindSize() {
    LocalDateTime start = BASE.plusHours(30);
    LocalDateTime end = BASE.plusHours(59);
    long counted = repo.countByDateRange(start, end);
    int found = repo.findByDateRange(start, end).size();
    assertEquals(found, counted);
    assertEquals(60L, counted); // 30 hours x 2 sensors
  }
}
