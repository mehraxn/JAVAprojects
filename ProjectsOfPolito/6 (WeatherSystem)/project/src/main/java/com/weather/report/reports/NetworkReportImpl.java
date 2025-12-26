package com.weather.report.reports;                                                 //ADDED FOR R1

import java.time.LocalDateTime;                                                     //ADDED FOR R1
import java.time.temporal.ChronoUnit;                                               //ADDED FOR R1
import java.util.ArrayList;                                                         //ADDED FOR R1
import java.util.Collection;                                                        //ADDED FOR R1
import java.util.Comparator;                                                        //ADDED FOR R1
import java.util.HashMap;                                                          //ADDED FOR R1
import java.util.List;                                                              //ADDED FOR R1
import java.util.Map;                                                               //ADDED FOR R1
import java.util.SortedMap;                                                         //ADDED FOR R1
import java.util.TreeMap;                                                           //ADDED FOR R1

import com.weather.report.model.entities.Measurement;                               //ADDED FOR R1
import com.weather.report.repositories.CRUDRepository;                              //ADDED FOR R1

public class NetworkReportImpl implements NetworkReport {                           //ADDED FOR R1

    private String code;                                                            //ADDED FOR R1
    private String startDateStr;                                                    //ADDED FOR R1
    private String endDateStr;                                                      //ADDED FOR R1
    private List<Measurement> measurements;                                         //ADDED FOR R1

    public NetworkReportImpl(String networkCode, String startDate, String endDate) { //ADDED FOR R1
        this.code = networkCode;                                                    //ADDED FOR R1
        this.startDateStr = startDate;                                              //ADDED FOR R1
        this.endDateStr = endDate;                                                  //ADDED FOR R1

        CRUDRepository<Measurement, Long> repo = new CRUDRepository<>(Measurement.class);//ADDED FOR R1
        List<Measurement> allMeasurements = repo.read();                            //ADDED FOR R1

        LocalDateTime startDateTime = null;                                         //ADDED FOR R1
        if (startDate != null) {                                                    //ADDED FOR R1
            startDateTime = parseDateString(startDate);                             //ADDED FOR R1
        }                                                                           //ADDED FOR R1

        LocalDateTime endDateTime = null;                                           //ADDED FOR R1
        if (endDate != null) {                                                      //ADDED FOR R1
            endDateTime = parseDateString(endDate);                                 //ADDED FOR R1
        }                                                                           //ADDED FOR R1

        this.measurements = new ArrayList<>();                                      //ADDED FOR R1

        for (Measurement measurement : allMeasurements) {                           //ADDED FOR R1
            boolean belongsToNetwork = measurement.getNetworkCode().equals(networkCode);//ADDED FOR R1
            if (!belongsToNetwork) {                                                //ADDED FOR R1
                continue;                                                           //ADDED FOR R1
            }                                                                       //ADDED FOR R1

            boolean isAfterStart = true;                                            //ADDED FOR R1
            if (startDateTime != null) {                                            //ADDED FOR R1
                if (measurement.getTimestamp().isBefore(startDateTime)) {           //ADDED FOR R1
                    isAfterStart = false;                                           //ADDED FOR R1
                }                                                                   //ADDED FOR R1
            }                                                                       //ADDED FOR R1

            boolean isBeforeEnd = true;                                             //ADDED FOR R1
            if (endDateTime != null) {                                              //ADDED FOR R1
                if (measurement.getTimestamp().isAfter(endDateTime)) {              //ADDED FOR R1
                    isBeforeEnd = false;                                            //ADDED FOR R1
                }                                                                   //ADDED FOR R1
            }                                                                       //ADDED FOR R1

            if (isAfterStart && isBeforeEnd) {                                      //ADDED FOR R1
                this.measurements.add(measurement);                                 //ADDED FOR R1
            }                                                                       //ADDED FOR R1
        }                                                                           //ADDED FOR R1
    }                                                                               //ADDED FOR R1

    private LocalDateTime parseDateString(String dateString) {                      //ADDED FOR R1
        String[] parts = dateString.split(" ");                                     //ADDED FOR R1
        String datePart = parts[0];                                                 //ADDED FOR R1
        String timePart = parts[1];                                                 //ADDED FOR R1

        String[] dateParts = datePart.split("-");                                   //ADDED FOR R1
        int year = Integer.parseInt(dateParts[0]);                                  //ADDED FOR R1
        int month = Integer.parseInt(dateParts[1]);                                 //ADDED FOR R1
        int day = Integer.parseInt(dateParts[2]);                                   //ADDED FOR R1

        String[] timeParts = timePart.split(":");                                   //ADDED FOR R1
        int hour = Integer.parseInt(timeParts[0]);                                  //ADDED FOR R1
        int minute = Integer.parseInt(timeParts[1]);                                //ADDED FOR R1
        int second = Integer.parseInt(timeParts[2]);                                //ADDED FOR R1

        return LocalDateTime.of(year, month, day, hour, minute, second);            //ADDED FOR R1
    }                                                                               //ADDED FOR R1

    @Override                                                                       //ADDED FOR R1
    public String getCode() {                                                       //ADDED FOR R1
        return code;                                                                //ADDED FOR R1
    }                                                                               //ADDED FOR R1

    @Override                                                                       //ADDED FOR R1
    public String getStartDate() {                                                  //ADDED FOR R1
        return startDateStr;                                                        //ADDED FOR R1
    }                                                                               //ADDED FOR R1

    @Override                                                                       //ADDED FOR R1
    public String getEndDate() {                                                    //ADDED FOR R1
        return endDateStr;                                                          //ADDED FOR R1
    }                                                                               //ADDED FOR R1

    @Override                                                                       //ADDED FOR R1
    public long getNumberOfMeasurements() {                                         //ADDED FOR R1
        return measurements.size();                                                 //ADDED FOR R1
    }                                                                               //ADDED FOR R1

    @Override                                                                       //ADDED FOR R1
    public Collection<String> getMostActiveGateways() {                             //ADDED FOR R1
        if (measurements.isEmpty()) {                                               //ADDED FOR R1
            return new ArrayList<>();                                               //ADDED FOR R1
        }                                                                           //ADDED FOR R1

        Map<String, Long> gatewayMeasurementCounts = new HashMap<>();               //ADDED FOR R1

        for (Measurement measurement : measurements) {                              //ADDED FOR R1
            String gatewayCode = measurement.getGatewayCode();                      //ADDED FOR R1
            long currentCount = gatewayMeasurementCounts.getOrDefault(gatewayCode, 0L);//ADDED FOR R1
            long newCount = currentCount + 1;                                       //ADDED FOR R1
            gatewayMeasurementCounts.put(gatewayCode, newCount);                    //ADDED FOR R1
        }                                                                           //ADDED FOR R1

        long maximumCount = 0;                                                      //ADDED FOR R1

        for (Long count : gatewayMeasurementCounts.values()) {                      //ADDED FOR R1
            if (count > maximumCount) {                                             //ADDED FOR R1
                maximumCount = count;                                               //ADDED FOR R1
            }                                                                       //ADDED FOR R1
        }                                                                           //ADDED FOR R1

        List<String> mostActiveGateways = new ArrayList<>();                        //ADDED FOR R1

        for (Map.Entry<String, Long> entry : gatewayMeasurementCounts.entrySet()) {  //ADDED FOR R1
            String gatewayCode = entry.getKey();                                    //ADDED FOR R1
            long count = entry.getValue();                                          //ADDED FOR R1

            if (count == maximumCount) {                                            //ADDED FOR R1
                mostActiveGateways.add(gatewayCode);                                //ADDED FOR R1
            }                                                                       //ADDED FOR R1
        }                                                                           //ADDED FOR R1

        return mostActiveGateways;                                                  //ADDED FOR R1
    }                                                                               //ADDED FOR R1

    @Override                                                                       //ADDED FOR R1
    public Collection<String> getLeastActiveGateways() {                            //ADDED FOR R1
        if (measurements.isEmpty()) {                                               //ADDED FOR R1
            return new ArrayList<>();                                               //ADDED FOR R1
        }                                                                           //ADDED FOR R1

        Map<String, Long> gatewayMeasurementCounts = new HashMap<>();               //ADDED FOR R1

        for (Measurement measurement : measurements) {                              //ADDED FOR R1
            String gatewayCode = measurement.getGatewayCode();                      //ADDED FOR R1
            long currentCount = gatewayMeasurementCounts.getOrDefault(gatewayCode, 0L);//ADDED FOR R1
            long newCount = currentCount + 1;                                       //ADDED FOR R1
            gatewayMeasurementCounts.put(gatewayCode, newCount);                    //ADDED FOR R1
        }                                                                           //ADDED FOR R1

        long minimumCount = Long.MAX_VALUE;                                         //ADDED FOR R1

        for (Long count : gatewayMeasurementCounts.values()) {                      //ADDED FOR R1
            if (count < minimumCount) {                                             //ADDED FOR R1
                minimumCount = count;                                               //ADDED FOR R1
            }                                                                       //ADDED FOR R1
        }                                                                           //ADDED FOR R1

        List<String> leastActiveGateways = new ArrayList<>();                       //ADDED FOR R1

        for (Map.Entry<String, Long> entry : gatewayMeasurementCounts.entrySet()) {  //ADDED FOR R1
            String gatewayCode = entry.getKey();                                    //ADDED FOR R1
            long count = entry.getValue();                                          //ADDED FOR R1

            if (count == minimumCount) {                                            //ADDED FOR R1
                leastActiveGateways.add(gatewayCode);                               //ADDED FOR R1
            }                                                                       //ADDED FOR R1
        }                                                                           //ADDED FOR R1

        return leastActiveGateways;                                                 //ADDED FOR R1
    }                                                                               //ADDED FOR R1

    @Override                                                                       //ADDED FOR R1
    public Map<String, Double> getGatewaysLoadRatio() {                             //ADDED FOR R1
        Map<String, Double> loadRatios = new HashMap<>();                           //ADDED FOR R1

        if (measurements.isEmpty()) {                                               //ADDED FOR R1
            return loadRatios;                                                      //ADDED FOR R1
        }                                                                           //ADDED FOR R1

        double totalMeasurements = measurements.size();                             //ADDED FOR R1

        Map<String, Long> gatewayMeasurementCounts = new HashMap<>();               //ADDED FOR R1

        for (Measurement measurement : measurements) {                              //ADDED FOR R1
            String gatewayCode = measurement.getGatewayCode();                      //ADDED FOR R1
            long currentCount = gatewayMeasurementCounts.getOrDefault(gatewayCode, 0L);//ADDED FOR R1
            long newCount = currentCount + 1;                                       //ADDED FOR R1
            gatewayMeasurementCounts.put(gatewayCode, newCount);                    //ADDED FOR R1
        }                                                                           //ADDED FOR R1

        for (Map.Entry<String, Long> entry : gatewayMeasurementCounts.entrySet()) {  //ADDED FOR R1
            String gatewayCode = entry.getKey();                                    //ADDED FOR R1
            long measurementCount = entry.getValue();                               //ADDED FOR R1
            double percentage = (measurementCount / totalMeasurements) * 100.0;     //ADDED FOR R1
            loadRatios.put(gatewayCode, percentage);                                //ADDED FOR R1
        }                                                                           //ADDED FOR R1

        return loadRatios;                                                          //ADDED FOR R1
    }                                                                               //ADDED FOR R1

    @Override                                                                       //ADDED FOR R1
    public SortedMap<Range<LocalDateTime>, Long> getHistogram() {                   //ADDED FOR R1
        RangeComparator comparator = new RangeComparator();                         //ADDED FOR R1
        SortedMap<Range<LocalDateTime>, Long> histogramMap = new TreeMap<>(comparator);//ADDED FOR R1

        if (measurements.isEmpty()) {                                               //ADDED FOR R1
            return histogramMap;                                                    //ADDED FOR R1
        }                                                                           //ADDED FOR R1

        LocalDateTime earliestTimestamp = measurements.get(0).getTimestamp();        //ADDED FOR R1
        LocalDateTime latestTimestamp = measurements.get(0).getTimestamp();          //ADDED FOR R1

        for (Measurement measurement : measurements) {                              //ADDED FOR R1
            LocalDateTime currentTimestamp = measurement.getTimestamp();            //ADDED FOR R1
            if (currentTimestamp.isBefore(earliestTimestamp)) {                     //ADDED FOR R1
                earliestTimestamp = currentTimestamp;                               //ADDED FOR R1
            }                                                                       //ADDED FOR R1
            if (currentTimestamp.isAfter(latestTimestamp)) {                        //ADDED FOR R1
                latestTimestamp = currentTimestamp;                                 //ADDED FOR R1
            }                                                                       //ADDED FOR R1
        }                                                                           //ADDED FOR R1

        LocalDateTime effectiveStartDate = earliestTimestamp;                       //ADDED FOR R1
        if (startDateStr != null) {                                                 //ADDED FOR R1
            effectiveStartDate = parseDateString(startDateStr);                     //ADDED FOR R1
        }                                                                           //ADDED FOR R1

        LocalDateTime effectiveEndDate = latestTimestamp;                           //ADDED FOR R1
        if (endDateStr != null) {                                                   //ADDED FOR R1
            effectiveEndDate = parseDateString(endDateStr);                         //ADDED FOR R1
        }                                                                           //ADDED FOR R1

        long totalHours = ChronoUnit.HOURS.between(effectiveStartDate, effectiveEndDate);//ADDED FOR R1
        boolean useHourlyBuckets = (totalHours <= 48);                              //ADDED FOR R1

        LocalDateTime currentBucketStart = effectiveStartDate;                      //ADDED FOR R1

        while (true) {                                                              //ADDED FOR R1
            if (currentBucketStart.isAfter(effectiveEndDate)) {                     //ADDED FOR R1
                break;                                                              //ADDED FOR R1
            }                                                                       //ADDED FOR R1

            LocalDateTime currentBucketEnd;                                         //ADDED FOR R1

            if (useHourlyBuckets) {                                                 //ADDED FOR R1
                LocalDateTime nextHourBoundary = currentBucketStart.plusHours(1);   //ADDED FOR R1
                nextHourBoundary = nextHourBoundary.truncatedTo(ChronoUnit.HOURS);  //ADDED FOR R1

                boolean didNotMoveForward = nextHourBoundary.isBefore(currentBucketStart) || 
                                           nextHourBoundary.equals(currentBucketStart);//ADDED FOR R1
                if (didNotMoveForward) {                                            //ADDED FOR R1
                    nextHourBoundary = nextHourBoundary.plusHours(1);               //ADDED FOR R1
                }                                                                   //ADDED FOR R1

                if (nextHourBoundary.isAfter(effectiveEndDate)) {                   //ADDED FOR R1
                    currentBucketEnd = effectiveEndDate;                            //ADDED FOR R1
                } else {                                                            //ADDED FOR R1
                    currentBucketEnd = nextHourBoundary;                            //ADDED FOR R1
                }                                                                   //ADDED FOR R1

            } else {                                                                //ADDED FOR R1
                LocalDateTime nextDayBoundary = currentBucketStart.plusDays(1);     //ADDED FOR R1
                nextDayBoundary = nextDayBoundary.truncatedTo(ChronoUnit.DAYS);     //ADDED FOR R1

                boolean didNotMoveForward = nextDayBoundary.isBefore(currentBucketStart) || 
                                           nextDayBoundary.equals(currentBucketStart);//ADDED FOR R1
                if (didNotMoveForward) {                                            //ADDED FOR R1
                    nextDayBoundary = nextDayBoundary.plusDays(1);                  //ADDED FOR R1
                }                                                                   //ADDED FOR R1

                if (nextDayBoundary.isAfter(effectiveEndDate)) {                   //ADDED FOR R1
                    currentBucketEnd = effectiveEndDate;                            //ADDED FOR R1
                } else {                                                            //ADDED FOR R1
                    currentBucketEnd = nextDayBoundary;                             //ADDED FOR R1
                }                                                                   //ADDED FOR R1
            }                                                                       //ADDED FOR R1

            boolean isLastBucket = currentBucketEnd.equals(effectiveEndDate);       //ADDED FOR R1
            SimpleRange bucketRange = new SimpleRange(currentBucketStart, currentBucketEnd, isLastBucket);//ADDED FOR R1

            long measurementsInBucket = 0;                                          //ADDED FOR R1

            for (Measurement measurement : measurements) {                          //ADDED FOR R1
                if (bucketRange.contains(measurement.getTimestamp())) {              //ADDED FOR R1
                    measurementsInBucket++;                                         //ADDED FOR R1
                }                                                                   //ADDED FOR R1
            }                                                                       //ADDED FOR R1

            histogramMap.put(bucketRange, measurementsInBucket);                    //ADDED FOR R1

            if (currentBucketEnd.equals(effectiveEndDate)) {                        //ADDED FOR R1
                break;                                                              //ADDED FOR R1
            }                                                                       //ADDED FOR R1

            currentBucketStart = currentBucketEnd;                                  //ADDED FOR R1
        }                                                                           //ADDED FOR R1

        return histogramMap;                                                        //ADDED FOR R1
    }                                                                               //ADDED FOR R1

    private class RangeComparator implements Comparator<Range<LocalDateTime>> {     //ADDED FOR R1
        @Override                                                                   //ADDED FOR R1
        public int compare(Range<LocalDateTime> range1, Range<LocalDateTime> range2){//ADDED FOR R1
            LocalDateTime start1 = range1.getStart();                               //ADDED FOR R1
            LocalDateTime start2 = range2.getStart();                               //ADDED FOR R1
            return start1.compareTo(start2);                                        //ADDED FOR R1
        }                                                                           //ADDED FOR R1
    }                                                                               //ADDED FOR R1

    private class SimpleRange implements Range<LocalDateTime> {                    //ADDED FOR R1
        private LocalDateTime startTime;                                            //ADDED FOR R1
        private LocalDateTime endTime;                                              //ADDED FOR R1
        private boolean isLastBucket;                                               //ADDED FOR R1

        public SimpleRange(LocalDateTime start, LocalDateTime end, boolean isLast) { //ADDED FOR R1
            this.startTime = start;                                                 //ADDED FOR R1
            this.endTime = end;                                                     //ADDED FOR R1
            this.isLastBucket = isLast;                                             //ADDED FOR R1
        }                                                                           //ADDED FOR R1

        @Override                                                                   //ADDED FOR R1
        public LocalDateTime getStart() {                                           //ADDED FOR R1
            return startTime;                                                       //ADDED FOR R1
        }                                                                           //ADDED FOR R1

        @Override                                                                   //ADDED FOR R1
        public LocalDateTime getEnd() {                                             //ADDED FOR R1
            return endTime;                                                         //ADDED FOR R1
        }                                                                           //ADDED FOR R1

        @Override                                                                   //ADDED FOR R1
        public boolean contains(LocalDateTime timestamp) {                         //ADDED FOR R1
            if (timestamp.isBefore(startTime)) {                                    //ADDED FOR R1
                return false;                                                       //ADDED FOR R1
            }                                                                       //ADDED FOR R1

            if (isLastBucket) {                                                     //ADDED FOR R1
                if (timestamp.isAfter(endTime)) {                                   //ADDED FOR R1
                    return false;                                                   //ADDED FOR R1
                } else {                                                            //ADDED FOR R1
                    return true;                                                    //ADDED FOR R1
                }                                                                   //ADDED FOR R1
            }                                                                       //ADDED FOR R1

            if (timestamp.isBefore(endTime)) {                                      //ADDED FOR R1
                return true;                                                        //ADDED FOR R1
            } else {                                                                //ADDED FOR R1
                return false;                                                       //ADDED FOR R1
            }                                                                       //ADDED FOR R1
        }                                                                           //ADDED FOR R1
    }                                                                               //ADDED FOR R1
}                                                                                   //ADDED FOR R1