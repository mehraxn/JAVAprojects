package com.weather.report.reports;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.weather.report.model.entities.Measurement;
import com.weather.report.repositories.MeasurementRepository;
import com.weather.report.util.DateParsingUtils;

public class NetworkReportImpl implements NetworkReport {

    private String code;
    private String startDateStr;
    private String endDateStr;
    private List<Measurement> measurements;
    // Measurement counts per gateway, computed once from the (immutable) subset.
    private Map<String, Long> countByGateway;

    public NetworkReportImpl(String networkCode, String startDate, String endDate) {
        this.code = networkCode;
        this.startDateStr = startDate;
        this.endDateStr = endDate;

        MeasurementRepository repo = new MeasurementRepository();

        // Centralised date parsing (DATE_FORMAT); null bound stays unbounded.
        LocalDateTime startDateTime = DateParsingUtils.parseNullable(startDate);
        LocalDateTime endDateTime = DateParsingUtils.parseNullable(endDate);

        // Database-side filtering (inclusive range, null bound = unbounded), replacing
        // the previous "read all measurements then filter in Java" loop. Behaviour is
        // identical: network code match + timestamp >= start + timestamp <= end.
        this.measurements = new ArrayList<>(repo.findByNetworkCodeAndDateRange(networkCode, startDateTime, endDateTime));
        // Compute per-gateway counts once (report data is immutable after construction),
        // instead of recomputing on every most/least-active and load-ratio call.
        this.countByGateway = countMeasurementsByGateway();
    }

    private Map<String, Long> countMeasurementsByGateway() {
        Map<String, Long> counts = new HashMap<>();
        for (Measurement measurement : measurements) {
            String gatewayCode = measurement.getGatewayCode();
            long currentCount = counts.getOrDefault(gatewayCode, 0L);
            counts.put(gatewayCode, currentCount + 1);
        }
        return counts;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getStartDate() {
        return startDateStr;
    }

    @Override
    public String getEndDate() {
        return endDateStr;
    }

    @Override
    public long getNumberOfMeasurements() {
        return measurements.size();
    }

    @Override
    public Collection<String> getMostActiveGateways() {
        if (measurements.isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, Long> gatewayMeasurementCounts = countByGateway;

        long maximumCount = 0;
        for (Long count : gatewayMeasurementCounts.values()) {
            if (count > maximumCount) {
                maximumCount = count;
            }
        }

        List<String> mostActiveGateways = new ArrayList<>();
        for (Map.Entry<String, Long> entry : gatewayMeasurementCounts.entrySet()) {
            if (entry.getValue() == maximumCount) {
                mostActiveGateways.add(entry.getKey());
            }
        }

        return Collections.unmodifiableList(mostActiveGateways);
    }

    @Override
    public Collection<String> getLeastActiveGateways() {
        if (measurements.isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, Long> gatewayMeasurementCounts = countByGateway;

        long minimumCount = Long.MAX_VALUE;
        for (Long count : gatewayMeasurementCounts.values()) {
            if (count < minimumCount) {
                minimumCount = count;
            }
        }

        List<String> leastActiveGateways = new ArrayList<>();
        for (Map.Entry<String, Long> entry : gatewayMeasurementCounts.entrySet()) {
            if (entry.getValue() == minimumCount) {
                leastActiveGateways.add(entry.getKey());
            }
        }

        return Collections.unmodifiableList(leastActiveGateways);
    }

    @Override
    public Map<String, Double> getGatewaysLoadRatio() {
        Map<String, Double> loadRatios = new HashMap<>();

        if (measurements.isEmpty()) {
            return loadRatios;
        }

        double totalMeasurements = measurements.size();
        Map<String, Long> gatewayMeasurementCounts = countByGateway;

        // Percentage (0-100) of the network's measurements produced by each gateway.
        for (Map.Entry<String, Long> entry : gatewayMeasurementCounts.entrySet()) {
            double percentage = (entry.getValue() / totalMeasurements) * 100.0;
            loadRatios.put(entry.getKey(), percentage);
        }

        return Collections.unmodifiableMap(loadRatios);
    }

    @Override
    public SortedMap<Range<LocalDateTime>, Long> getHistogram() {
        RangeComparator comparator = new RangeComparator();
        SortedMap<Range<LocalDateTime>, Long> histogramMap = new TreeMap<>(comparator);

        if (measurements.isEmpty()) {
            return Collections.unmodifiableSortedMap(histogramMap);
        }

        LocalDateTime earliestTimestamp = measurements.get(0).getTimestamp();
        LocalDateTime latestTimestamp = measurements.get(0).getTimestamp();

        for (Measurement measurement : measurements) {
            LocalDateTime currentTimestamp = measurement.getTimestamp();
            if (currentTimestamp.isBefore(earliestTimestamp)) {
                earliestTimestamp = currentTimestamp;
            }
            if (currentTimestamp.isAfter(latestTimestamp)) {
                latestTimestamp = currentTimestamp;
            }
        }

        LocalDateTime effectiveStartDate = earliestTimestamp;
        if (startDateStr != null) {
            effectiveStartDate = DateParsingUtils.parseDateTime(startDateStr);
        }

        LocalDateTime effectiveEndDate = latestTimestamp;
        if (endDateStr != null) {
            effectiveEndDate = DateParsingUtils.parseDateTime(endDateStr);
        }

        long totalHours = ChronoUnit.HOURS.between(effectiveStartDate, effectiveEndDate);
        boolean useHourlyBuckets = (totalHours <= 48);

        LocalDateTime currentBucketStart = effectiveStartDate;

        while (true) {
            if (currentBucketStart.isAfter(effectiveEndDate)) {
                break;
            }

            LocalDateTime currentBucketEnd;

            if (useHourlyBuckets) {
                LocalDateTime nextHourBoundary = currentBucketStart.plusHours(1).truncatedTo(ChronoUnit.HOURS);
                
                if (!nextHourBoundary.isAfter(currentBucketStart)) {
                    nextHourBoundary = nextHourBoundary.plusHours(1);
                }

                if (nextHourBoundary.isAfter(effectiveEndDate)) {
                    currentBucketEnd = effectiveEndDate;
                } else {
                    currentBucketEnd = nextHourBoundary;
                }

            } else {
                LocalDateTime nextDayBoundary = currentBucketStart.plusDays(1).truncatedTo(ChronoUnit.DAYS);

                if (!nextDayBoundary.isAfter(currentBucketStart)) {
                    nextDayBoundary = nextDayBoundary.plusDays(1);
                }

                if (nextDayBoundary.isAfter(effectiveEndDate)) {
                    currentBucketEnd = effectiveEndDate;
                } else {
                    currentBucketEnd = nextDayBoundary;
                }
            }

            boolean isLastBucket = currentBucketEnd.equals(effectiveEndDate);
            SimpleRange bucketRange = new SimpleRange(currentBucketStart, currentBucketEnd, isLastBucket);

            long measurementsInBucket = 0;

            for (Measurement measurement : measurements) {
                if (bucketRange.contains(measurement.getTimestamp())) {
                    measurementsInBucket++;
                }
            }

            histogramMap.put(bucketRange, measurementsInBucket);

            if (currentBucketEnd.equals(effectiveEndDate)) {
                break;
            }

            currentBucketStart = currentBucketEnd;
        }

        return Collections.unmodifiableSortedMap(histogramMap);
    }

    private class RangeComparator implements Comparator<Range<LocalDateTime>> {
        @Override
        public int compare(Range<LocalDateTime> range1, Range<LocalDateTime> range2) {
            return range1.getStart().compareTo(range2.getStart());
        }
    }

    private class SimpleRange implements Range<LocalDateTime> {
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private boolean isLastBucket;

        public SimpleRange(LocalDateTime start, LocalDateTime end, boolean isLast) {
            this.startTime = start;
            this.endTime = end;
            this.isLastBucket = isLast;
        }

        @Override
        public LocalDateTime getStart() {
            return startTime;
        }

        @Override
        public LocalDateTime getEnd() {
            return endTime;
        }

        @Override
        public boolean contains(LocalDateTime timestamp) {
            if (timestamp.isBefore(startTime)) {
                return false;
            }

            if (isLastBucket) {
                return !timestamp.isAfter(endTime);
            }

            return timestamp.isBefore(endTime);
        }
    }
}