package csvanalyticsengine;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AnalyticsService {
    private static final String MISSING_GROUP = "(missing)";

    public List<DataRow> filter(DataSet dataSet, String column, String expectedValue) {
        requireDataSet(dataSet);
        String resolvedColumn = dataSet.resolveColumn(column);
        String safeExpectedValue = expectedValue == null ? "" : expectedValue;
        List<DataRow> matches = new ArrayList<DataRow>();
        for (DataRow row : dataSet.getRows()) {
            if (row.get(resolvedColumn).equals(safeExpectedValue)) {
                matches.add(row);
            }
        }
        return Collections.unmodifiableList(matches);
    }

    public NumericStatistics calculateStatistics(DataSet dataSet, String column) {
        requireDataSet(dataSet);
        String resolvedColumn = dataSet.resolveColumn(column);
        int validCount = 0;
        int missingCount = 0;
        int invalidCount = 0;
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal minimum = null;
        BigDecimal maximum = null;

        for (DataRow row : dataSet.getRows()) {
            String value = row.get(resolvedColumn);
            if (value == null || value.trim().isEmpty()) {
                missingCount++;
                continue;
            }
            try {
                BigDecimal number = new BigDecimal(value.trim());
                validCount++;
                sum = sum.add(number);
                if (minimum == null || number.compareTo(minimum) < 0) {
                    minimum = number;
                }
                if (maximum == null || number.compareTo(maximum) > 0) {
                    maximum = number;
                }
            } catch (NumberFormatException exception) {
                invalidCount++;
            }
        }

        BigDecimal average = validCount == 0 ? null
                : sum.divide(BigDecimal.valueOf(validCount), 4, RoundingMode.HALF_UP).stripTrailingZeros();
        return new NumericStatistics(resolvedColumn, validCount, missingCount, invalidCount,
                minimum, maximum, validCount == 0 ? null : sum, average);
    }

    public BigDecimal sum(DataSet dataSet, String column) {
        requireDataSet(dataSet);
        String resolvedColumn = dataSet.resolveColumn(column);
        BigDecimal sum = BigDecimal.ZERO;
        for (DataRow row : dataSet.getRows()) {
            String value = row.get(resolvedColumn);
            if (value != null && !value.trim().isEmpty()) {
                try {
                    sum = sum.add(new BigDecimal(value.trim()));
                } catch (NumberFormatException ignored) {
                    // Invalid numeric values are deliberately skipped.
                }
            }
        }
        return sum;
    }

    public BigDecimal average(DataSet dataSet, String column) {
        return calculateStatistics(dataSet, column).getAverage();
    }

    public Map<String, List<DataRow>> groupBy(DataSet dataSet, String column) {
        requireDataSet(dataSet);
        String resolvedColumn = dataSet.resolveColumn(column);
        Map<String, List<DataRow>> mutableGroups = new LinkedHashMap<String, List<DataRow>>();
        for (DataRow row : dataSet.getRows()) {
            String value = row.get(resolvedColumn);
            String groupName = value == null || value.trim().isEmpty() ? MISSING_GROUP : value;
            List<DataRow> group = mutableGroups.get(groupName);
            if (group == null) {
                group = new ArrayList<DataRow>();
                mutableGroups.put(groupName, group);
            }
            group.add(row);
        }

        Map<String, List<DataRow>> result = new LinkedHashMap<String, List<DataRow>>();
        for (Map.Entry<String, List<DataRow>> entry : mutableGroups.entrySet()) {
            result.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
        }
        return Collections.unmodifiableMap(result);
    }

    public Map<String, Integer> countBy(DataSet dataSet, String column) {
        Map<String, Integer> counts = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, List<DataRow>> entry : groupBy(dataSet, column).entrySet()) {
            counts.put(entry.getKey(), entry.getValue().size());
        }
        return Collections.unmodifiableMap(counts);
    }

    private void requireDataSet(DataSet dataSet) {
        if (dataSet == null) {
            throw new IllegalArgumentException("Data set cannot be null.");
        }
    }
}
