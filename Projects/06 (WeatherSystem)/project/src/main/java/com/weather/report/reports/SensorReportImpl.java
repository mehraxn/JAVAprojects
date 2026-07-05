package com.weather.report.reports;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.weather.report.model.entities.Measurement;
import com.weather.report.reports.Report.Range;

public class SensorReportImpl implements SensorReport {
    private final String code;
    private final String startDate;
    private final String endDate;
    private final long numberOfMeasurements;

    // Calculated fields
    private final double mean;
    private final double variance;
    private final double stdDev;
    private final double minValue;
    private final double maxValue;
    private final List<Measurement> outliers;
    private final SortedMap<Range<Double>, Long> histogram;

    public SensorReportImpl(String code, String startDate, String endDate, List<Measurement> measurements) {
        this.code = code;
        this.startDate = startDate;
        this.endDate = endDate;

        // check for null list
        List<Measurement> safeMeasurements = (measurements == null) ? new ArrayList<>() : measurements;
        this.numberOfMeasurements = safeMeasurements.size();

        // Calculate Statistics (Mean, Variance, StdDev)
        if (numberOfMeasurements < 2) {
            this.mean = 0.0;
            this.variance = 0.0;
            this.stdDev = 0.0;
            this.outliers = new ArrayList<>();
        } else {
            double sum = safeMeasurements.stream().mapToDouble(Measurement::getValue).sum();
            this.mean = sum / numberOfMeasurements;

            double sqSum = safeMeasurements.stream()
                    .mapToDouble(m -> Math.pow(m.getValue() - mean, 2))
                    .sum();
            this.variance = sqSum / (numberOfMeasurements - 1);
            this.stdDev = Math.sqrt(variance);

            // Identify outliers (value differs from mean by >= 2 * stdDev)
            double limit = 2.0 * stdDev;
            this.outliers = safeMeasurements.stream()
                    .filter(m -> Math.abs(m.getValue() - mean) >= limit)
                    .collect(Collectors.toList());
        }

        // Filter Non-Outliers
        List<Measurement> nonOutliers;
        if (outliers.isEmpty()) {
            nonOutliers = new ArrayList<>(safeMeasurements);
        } else {
            nonOutliers = safeMeasurements.stream()
                    .filter(m -> !outliers.contains(m))
                    .collect(Collectors.toList());
        }

        // Calculate Min/Max of Non-Outliers
        if (!nonOutliers.isEmpty()) {
            this.minValue = nonOutliers.stream().mapToDouble(Measurement::getValue).min().orElse(0.0);
            this.maxValue = nonOutliers.stream().mapToDouble(Measurement::getValue).max().orElse(0.0);
        } else {
            this.minValue = 0.0;
            this.maxValue = 0.0;
        }

        // Build Histogram
        this.histogram = buildHistogram(nonOutliers, minValue, maxValue);
    }

    private SortedMap<Range<Double>, Long> buildHistogram(List<Measurement> nonOutliers, double min, double max) {
        // We sort the map based on the start value of the Range
        SortedMap<Range<Double>, Long> map = new TreeMap<>(Comparator.comparing(Range::getStart));

        if (nonOutliers == null || nonOutliers.isEmpty())
            return map;

        // Edge case: All values are identical (min == max)
        if (min == max) {
            DoubleRange singleBucket = new DoubleRange(min, max, true); // true = is last bucket
            map.put(singleBucket, (long) nonOutliers.size());
            return map;
        }

        // Standard case: 20 buckets
        final int BUCKET_COUNT = 20;
        double span = max - min;
        double width = span / BUCKET_COUNT;
        List<DoubleRange> buckets = new ArrayList<>();

        for (int i = 0; i < BUCKET_COUNT; i++) {
            double start = min + i * width;
            // Ensure the last bucket ends exactly at max
            double end = (i == BUCKET_COUNT - 1) ? max : min + (i + 1) * width;
            boolean isLast = (i == BUCKET_COUNT - 1);

            DoubleRange range = new DoubleRange(start, end, isLast);
            buckets.add(range);
            map.put(range, 0L); // Initialize count to 0
        }

        // Assign measurements to buckets
        for (Measurement m : nonOutliers) {
            for (DoubleRange bucket : buckets) {
                if (bucket.contains(m.getValue())) {
                    map.put(bucket, map.get(bucket) + 1);
                    break;
                }
            }
        }
        return map;
    }

    // --- Inner Class: Implementation of Report.Range<Double> ---
    // This implements the generic interface defined in Report.java
    public static class DoubleRange implements Report.Range<Double> {
        private final double start;
        private final double end;
        private final boolean last; // Marks if this is the last bucket (closed interval)

        public DoubleRange(double start, double end, boolean last) {
            this.start = start;
            this.end = end;
            this.last = last;
        }

        @Override
        public Double getStart() {
            return start;
        }

        @Override
        public Double getEnd() {
            return end;
        }

        @Override
        public boolean contains(Double value) {
            if (value == null)
                return false;
            if (last) {
                // Last bucket is [start, end]
                return value >= start && value <= end;
            } else {
                // Other buckets are [start, end)
                return value >= start && value < end;
            }
        }

        @Override
        public String toString() {
            return String.format(last ? "[%.2f, %.2f]" : "[%.2f, %.2f)", start, end);
        }
    }

    // --- Getters Implementation ---
    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getStartDate() {
        return startDate;
    }

    @Override
    public String getEndDate() {
        return endDate;
    }

    @Override
    public long getNumberOfMeasurements() {
        return numberOfMeasurements;
    }

    @Override
    public SortedMap<Range<Double>, Long> getHistogram() {
        return histogram;
    }

    @Override
    public double getMean() {
        return mean;
    }

    @Override
    public double getVariance() {
        return variance;
    }

    @Override
    public double getStdDev() {
        return stdDev;
    }

    @Override
    public double getMinimumMeasuredValue() {
        return minValue;
    }

    @Override
    public double getMaximumMeasuredValue() {
        return maxValue;
    }

    @Override
    public List<Measurement> getOutliers() {
        return outliers;
    }
}