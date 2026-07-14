package com.weather.report.test.custom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.weather.report.model.entities.Measurement;
import com.weather.report.reports.Report;
import com.weather.report.reports.SensorReportImpl;

/**
 * Phase 4 statistics/outlier/histogram edge-case tests for {@link SensorReportImpl}.
 * The report computes everything from the list handed to its constructor, so these
 * tests need no database.
 *
 * Behaviour reference (README section 3.3):
 *  - sample variance = sum((x-mean)^2)/(n-1);
 *  - outlier iff |x - mean| >= 2 * stdDev;
 *  - variance/stdDev = 0 and no outliers when n < 2.
 */
class CustomSensorReportStatisticsTest {

  private static final double EPS = 1e-9;
  private static final LocalDateTime BASE = LocalDateTime.of(2025, 1, 1, 0, 0, 0);

  private static List<Measurement> measurements(double... values) {
    List<Measurement> list = new ArrayList<>();
    for (int i = 0; i < values.length; i++) {
      list.add(new Measurement("NET_01", "GW_0001", "S_000001", values[i], BASE.plusMinutes(i)));
    }
    return list;
  }

  private static SensorReportImpl report(double... values) {
    return new SensorReportImpl("S_000001", null, null, measurements(values));
  }

  // ---- empty / null ----

  @Test
  void emptyMeasurementsProducesZeroedStats() {
    SensorReportImpl r = report();
    assertEquals(0, r.getNumberOfMeasurements());
    assertEquals(0.0, r.getMean(), EPS);
    assertEquals(0.0, r.getVariance(), EPS);
    assertEquals(0.0, r.getStdDev(), EPS);
    assertEquals(0.0, r.getMinimumMeasuredValue(), EPS);
    assertEquals(0.0, r.getMaximumMeasuredValue(), EPS);
    assertTrue(r.getOutliers().isEmpty());
    assertTrue(r.getHistogram().isEmpty());
  }

  @Test
  void nullMeasurementListIsTreatedAsEmpty() {
    SensorReportImpl r = new SensorReportImpl("S_000001", null, null, null);
    assertEquals(0, r.getNumberOfMeasurements());
    assertTrue(r.getOutliers().isEmpty());
    assertTrue(r.getHistogram().isEmpty());
  }

  // ---- one measurement ----

  @Test
  void oneMeasurementHasMeanEqualToValueAndZeroSpread() {
    SensorReportImpl r = report(42.0);
    assertEquals(1, r.getNumberOfMeasurements());
    assertEquals(42.0, r.getMean(), EPS); // previously (incorrectly) 0.0
    assertEquals(0.0, r.getVariance(), EPS);
    assertEquals(0.0, r.getStdDev(), EPS);
    assertEquals(42.0, r.getMinimumMeasuredValue(), EPS);
    assertEquals(42.0, r.getMaximumMeasuredValue(), EPS);
    assertTrue(r.getOutliers().isEmpty());
    assertEquals(1, r.getHistogram().size()); // single bucket (min == max)
  }

  // ---- multiple measurements ----

  @Test
  void meanIsArithmeticAverage() {
    assertEquals(3.0, report(1, 2, 3, 4, 5).getMean(), EPS);
  }

  @Test
  void sampleVarianceAndStdDevAreComputedWithNMinusOne() {
    // {2,4,4,4,5,5,7,9}: mean=5, sum sq=32, sample variance=32/7.
    SensorReportImpl r = report(2, 4, 4, 4, 5, 5, 7, 9);
    double expectedVariance = 32.0 / 7.0;
    assertEquals(5.0, r.getMean(), EPS);
    assertEquals(expectedVariance, r.getVariance(), 1e-9);
    assertEquals(Math.sqrt(expectedVariance), r.getStdDev(), 1e-9);
  }

  @Test
  void twoDistinctValuesUseSampleVariance() {
    // {10,20}: mean=15, sample variance=((25)+(25))/1=50.
    SensorReportImpl r = report(10, 20);
    assertEquals(15.0, r.getMean(), EPS);
    assertEquals(50.0, r.getVariance(), EPS);
    assertEquals(Math.sqrt(50.0), r.getStdDev(), EPS);
    assertTrue(r.getOutliers().isEmpty());
  }

  // ---- all equal values ----

  @Test
  void allEqualValuesHaveZeroVarianceAndNoOutliers() {
    SensorReportImpl r = report(7, 7, 7, 7);
    assertEquals(7.0, r.getMean(), EPS);
    assertEquals(0.0, r.getVariance(), EPS);
    assertEquals(0.0, r.getStdDev(), EPS);
    assertTrue(r.getOutliers().isEmpty(), "zero std dev must not flag every value as an outlier");
    assertEquals(7.0, r.getMinimumMeasuredValue(), EPS);
    assertEquals(7.0, r.getMaximumMeasuredValue(), EPS);
    assertEquals(1, r.getHistogram().size()); // single bucket for identical values
  }

  // ---- outliers ----

  @Test
  void clearOutlierIsDetected() {
    // nine 10s and one 100: mean=19, stdDev~28.46, 2*stdDev~56.9 -> only 100 qualifies.
    SensorReportImpl r = report(10, 10, 10, 10, 10, 10, 10, 10, 10, 100);
    List<Measurement> outliers = r.getOutliers();
    assertEquals(1, outliers.size());
    assertEquals(100.0, outliers.get(0).getValue(), EPS);
  }

  @Test
  void zeroStdDevProducesNoOutliers() {
    assertTrue(report(5, 5, 5).getOutliers().isEmpty());
  }

  // ---- histogram ----

  @Test
  void histogramCoversAllNonOutliersAndIncludesMaxValue() {
    // 1..21 uniform spread: no outliers (max deviation 10 < 2*stdDev ~12.4).
    double[] values = new double[21];
    for (int i = 0; i < 21; i++) {
      values[i] = i + 1;
    }
    SensorReportImpl r = report(values);
    assertTrue(r.getOutliers().isEmpty());

    long totalInHistogram = r.getHistogram().values().stream().mapToLong(Long::longValue).sum();
    assertEquals(21L, totalInHistogram, "every non-outlier (incl. the max value) must fall in a bucket");
    assertTrue(r.getMaximumMeasuredValue() >= 21.0 - EPS);
  }

  @Test
  void histogramKeysAreInAscendingStartOrder() {
    SensorReportImpl r = report(1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23);
    Double previousStart = null;
    for (Report.Range<Double> range : r.getHistogram().keySet()) {
      if (previousStart != null) {
        assertTrue(range.getStart() >= previousStart, "bucket starts must be ascending");
      }
      previousStart = range.getStart();
    }
  }

  // ---- immutability ----

  @Test
  void returnedOutlierListIsUnmodifiable() {
    SensorReportImpl r = report(10, 10, 10, 10, 10, 10, 10, 10, 10, 100);
    assertThrows(UnsupportedOperationException.class, () -> r.getOutliers().clear());
  }

  @Test
  void returnedHistogramIsUnmodifiable() {
    SensorReportImpl r = report(1, 2, 3, 4, 5);
    assertThrows(UnsupportedOperationException.class, () -> r.getHistogram().clear());
  }
}
