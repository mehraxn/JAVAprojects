package com.weather.report.reports;

import java.time.LocalDateTime; //ADDED FOR R1
import java.time.format.DateTimeFormatter; //ADDED FOR R1
import java.time.temporal.ChronoUnit; //ADDED FOR R1
import java.util.ArrayList; //ADDED FOR R1
import java.util.Collection; //ADDED FOR R1
import java.util.HashMap; //ADDED FOR R1
import java.util.List; //ADDED FOR R1
import java.util.Map; //ADDED FOR R1
import java.util.SortedMap; //ADDED FOR R1
import java.util.TreeMap; //ADDED FOR R1

import com.weather.report.WeatherReport; //ADDED FOR R1
import com.weather.report.model.entities.Measurement; //ADDED FOR R1
import com.weather.report.repositories.CRUDRepository; //ADDED FOR R1

public class NetworkReportImpl implements NetworkReport {

    private String code; //ADDED FOR R1
    private String startDateStr; //ADDED FOR R1
    private String endDateStr; //ADDED FOR R1
    private List<Measurement> measurements; //ADDED FOR R1

    public NetworkReportImpl(String networkCode, String startDate, String endDate) { //ADDED FOR R1
        this.code = networkCode; //ADDED FOR R1
        this.startDateStr = startDate; //ADDED FOR R1
        this.endDateStr = endDate; //ADDED FOR R1

        CRUDRepository<Measurement, Long> repo = new CRUDRepository<>(Measurement.class); //ADDED FOR R1
        List<Measurement> all = repo.read(); //ADDED FOR R1
        
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(WeatherReport.DATE_FORMAT); //ADDED FOR R1
        LocalDateTime start = null; //ADDED FOR R1
        if (startDate != null) { //ADDED FOR R1
             start = LocalDateTime.parse(startDate, dtf); //ADDED FOR R1
        } //ADDED FOR R1
        LocalDateTime end = null; //ADDED FOR R1
        if (endDate != null) { //ADDED FOR R1
             end = LocalDateTime.parse(endDate, dtf); //ADDED FOR R1
        } //ADDED FOR R1

        this.measurements = new ArrayList<>(); //ADDED FOR R1
        for (Measurement m : all) { //ADDED FOR R1
            if (!m.getNetworkCode().equals(networkCode)) { //ADDED FOR R1
                continue; //ADDED FOR R1
            } //ADDED FOR R1
            
            boolean afterStart = true; //ADDED FOR R1
            if (start != null && m.getTimestamp().isBefore(start)) { //ADDED FOR R1
                afterStart = false; //ADDED FOR R1
            } //ADDED FOR R1

            boolean beforeEnd = true; //ADDED FOR R1
            if (end != null && m.getTimestamp().isAfter(end)) { //ADDED FOR R1
                beforeEnd = false; //ADDED FOR R1
            } //ADDED FOR R1

            if (afterStart && beforeEnd) { //ADDED FOR R1
                this.measurements.add(m); //ADDED FOR R1
            } //ADDED FOR R1
        } //ADDED FOR R1
    } //ADDED FOR R1

    @Override //ADDED FOR R1
    public String getCode() { return code; } //ADDED FOR R1

    @Override //ADDED FOR R1
    public String getStartDate() { return startDateStr; } //ADDED FOR R1

    @Override //ADDED FOR R1
    public String getEndDate() { return endDateStr; } //ADDED FOR R1

    @Override //ADDED FOR R1
    public long getNumberOfMeasurements() { return measurements.size(); } //ADDED FOR R1

    @Override //ADDED FOR R1
    public Collection<String> getMostActiveGateways() { //ADDED FOR R1
        if (measurements.isEmpty()) { //ADDED FOR R1
             return new ArrayList<>(); //ADDED FOR R1
        } //ADDED FOR R1
        Map<String, Long> counts = new HashMap<>(); //ADDED FOR R1
        for (Measurement m : measurements) { //ADDED FOR R1
            String gw = m.getGatewayCode(); //ADDED FOR R1
            counts.put(gw, counts.getOrDefault(gw, 0L) + 1); //ADDED FOR R1
        } //ADDED FOR R1

        long max = 0; //ADDED FOR R1
        for (Long val : counts.values()) { //ADDED FOR R1
            if (val > max) { //ADDED FOR R1
                max = val; //ADDED FOR R1
            } //ADDED FOR R1
        } //ADDED FOR R1
        
        List<String> result = new ArrayList<>(); //ADDED FOR R1
        for (Map.Entry<String, Long> entry : counts.entrySet()) { //ADDED FOR R1
            if (entry.getValue() == max) { //ADDED FOR R1
                result.add(entry.getKey()); //ADDED FOR R1
            } //ADDED FOR R1
        } //ADDED FOR R1
        return result; //ADDED FOR R1
    } //ADDED FOR R1

    @Override //ADDED FOR R1
    public Collection<String> getLeastActiveGateways() { //ADDED FOR R1
        if (measurements.isEmpty()) { //ADDED FOR R1
             return new ArrayList<>(); //ADDED FOR R1
        } //ADDED FOR R1
        Map<String, Long> counts = new HashMap<>(); //ADDED FOR R1
        for (Measurement m : measurements) { //ADDED FOR R1
            String gw = m.getGatewayCode(); //ADDED FOR R1
            counts.put(gw, counts.getOrDefault(gw, 0L) + 1); //ADDED FOR R1
        } //ADDED FOR R1

        long min = Long.MAX_VALUE; //ADDED FOR R1
        for (Long val : counts.values()) { //ADDED FOR R1
            if (val < min) { //ADDED FOR R1
                min = val; //ADDED FOR R1
            } //ADDED FOR R1
        } //ADDED FOR R1
        
        List<String> result = new ArrayList<>(); //ADDED FOR R1
        for (Map.Entry<String, Long> entry : counts.entrySet()) { //ADDED FOR R1
            if (entry.getValue() == min) { //ADDED FOR R1
                result.add(entry.getKey()); //ADDED FOR R1
            } //ADDED FOR R1
        } //ADDED FOR R1
        return result; //ADDED FOR R1
    } //ADDED FOR R1

    @Override //ADDED FOR R1
    public Map<String, Double> getGatewaysLoadRatio() { //ADDED FOR R1
        Map<String, Double> ratio = new HashMap<>(); //ADDED FOR R1
        if (measurements.isEmpty()) { //ADDED FOR R1
             return ratio; //ADDED FOR R1
        } //ADDED FOR R1
        double total = measurements.size(); //ADDED FOR R1
        Map<String, Long> counts = new HashMap<>(); //ADDED FOR R1
        for (Measurement m : measurements) { //ADDED FOR R1
            String gw = m.getGatewayCode(); //ADDED FOR R1
            counts.put(gw, counts.getOrDefault(gw, 0L) + 1); //ADDED FOR R1
        } //ADDED FOR R1
        
        for (Map.Entry<String, Long> entry : counts.entrySet()) { //ADDED FOR R1
             double val = (entry.getValue() / total) * 100.0; //ADDED FOR R1
             ratio.put(entry.getKey(), val); //ADDED FOR R1
        } //ADDED FOR R1
        return ratio; //ADDED FOR R1
    } //ADDED FOR R1

    @Override //ADDED FOR R1
    public SortedMap<Range<LocalDateTime>, Long> getHistogram() { //ADDED FOR R1
        SortedMap<Range<LocalDateTime>, Long> map = new TreeMap<>((r1, r2) -> r1.getStart().compareTo(r2.getStart())); //ADDED FOR R1
        if (measurements.isEmpty()) { //ADDED FOR R1
             return map; //ADDED FOR R1
        } //ADDED FOR R1

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(WeatherReport.DATE_FORMAT); //ADDED FOR R1
        
        LocalDateTime minMeas = measurements.get(0).getTimestamp(); //ADDED FOR R1
        LocalDateTime maxMeas = measurements.get(0).getTimestamp(); //ADDED FOR R1
        for (Measurement m : measurements) { //ADDED FOR R1
            if (m.getTimestamp().compareTo(minMeas) < 0) minMeas = m.getTimestamp(); //ADDED FOR R1
            if (m.getTimestamp().compareTo(maxMeas) > 0) maxMeas = m.getTimestamp(); //ADDED FOR R1
        } //ADDED FOR R1

        LocalDateTime effStart = (startDateStr != null) ? LocalDateTime.parse(startDateStr, dtf) : minMeas; //ADDED FOR R1
        LocalDateTime effEnd = (endDateStr != null) ? LocalDateTime.parse(endDateStr, dtf) : maxMeas; //ADDED FOR R1

        long diffHours = ChronoUnit.HOURS.between(effStart, effEnd); //ADDED FOR R1
        boolean hourly = (diffHours <= 48); //ADDED FOR R1

        LocalDateTime bucketStart = effStart; //ADDED FOR R1
        while (bucketStart.isBefore(effEnd) || bucketStart.equals(effEnd)) { //ADDED FOR R1
            LocalDateTime bucketEnd; //ADDED FOR R1
            if (hourly) { //ADDED FOR R1
                LocalDateTime nextHour = bucketStart.plusHours(1).truncatedTo(ChronoUnit.HOURS); //ADDED FOR R1
                if (!nextHour.isAfter(bucketStart)) { //ADDED FOR R1
                    nextHour = nextHour.plusHours(1); //ADDED FOR R1
                } //ADDED FOR R1
                bucketEnd = nextHour.isAfter(effEnd) ? effEnd : nextHour; //ADDED FOR R1
            } else { //ADDED FOR R1
                LocalDateTime nextDay = bucketStart.plusDays(1).truncatedTo(ChronoUnit.DAYS); //ADDED FOR R1
                if (!nextDay.isAfter(bucketStart)) { //ADDED FOR R1
                    nextDay = nextDay.plusDays(1); //ADDED FOR R1
                } //ADDED FOR R1
                bucketEnd = nextDay.isAfter(effEnd) ? effEnd : nextDay; //ADDED FOR R1
            } //ADDED FOR R1

            final LocalDateTime startRef = bucketStart; //ADDED FOR R1
            final LocalDateTime endRef = bucketEnd; //ADDED FOR R1
            final boolean isLast = bucketEnd.equals(effEnd); //ADDED FOR R1

            Range<LocalDateTime> range = new Range<>() { //ADDED FOR R1
                @Override public LocalDateTime getStart() { return startRef; } //ADDED FOR R1
                @Override public LocalDateTime getEnd() { return endRef; } //ADDED FOR R1
                @Override public boolean contains(LocalDateTime t) { //ADDED FOR R1
                    if (t.isBefore(startRef)) return false; //ADDED FOR R1
                    if (isLast) return !t.isAfter(endRef); //ADDED FOR R1
                    return t.isBefore(endRef); //ADDED FOR R1
                } //ADDED FOR R1
            }; //ADDED FOR R1

            long count = 0; //ADDED FOR R1
            for (Measurement m : measurements) { //ADDED FOR R1
                if (range.contains(m.getTimestamp())) { //ADDED FOR R1
                    count++; //ADDED FOR R1
                } //ADDED FOR R1
            } //ADDED FOR R1
            map.put(range, count); //ADDED FOR R1
            if (bucketEnd.equals(effEnd)) break; //ADDED FOR R1
            bucketStart = bucketEnd; //ADDED FOR R1
        } //ADDED FOR R1
        return map; //ADDED FOR R1
    } //ADDED FOR R1
}