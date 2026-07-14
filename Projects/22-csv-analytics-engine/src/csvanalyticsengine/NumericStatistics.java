package csvanalyticsengine;

import java.math.BigDecimal;

public class NumericStatistics {
    private final String column;
    private final int validValueCount;
    private final int missingValueCount;
    private final int invalidValueCount;
    private final BigDecimal minimum;
    private final BigDecimal maximum;
    private final BigDecimal sum;
    private final BigDecimal average;

    public NumericStatistics(String column, int validValueCount, int missingValueCount,
            int invalidValueCount, BigDecimal minimum, BigDecimal maximum, BigDecimal sum,
            BigDecimal average) {
        if (column == null || column.trim().isEmpty()) {
            throw new IllegalArgumentException("Statistics column cannot be empty.");
        }
        if (validValueCount < 0 || missingValueCount < 0 || invalidValueCount < 0) {
            throw new IllegalArgumentException("Statistics counts cannot be negative.");
        }
        if (validValueCount == 0 && (minimum != null || maximum != null || sum != null
                || average != null)) {
            throw new IllegalArgumentException("Empty numeric statistics cannot contain values.");
        }
        if (validValueCount > 0 && (minimum == null || maximum == null || sum == null
                || average == null)) {
            throw new IllegalArgumentException(
                    "Nonempty numeric statistics require min, max, sum, and average.");
        }
        if (minimum != null && maximum != null && minimum.compareTo(maximum) > 0) {
            throw new IllegalArgumentException("Minimum cannot exceed maximum.");
        }
        if (average != null && (average.compareTo(minimum) < 0 || average.compareTo(maximum) > 0)) {
            throw new IllegalArgumentException("Average must be between minimum and maximum.");
        }
        this.column = column.trim();
        this.validValueCount = validValueCount;
        this.missingValueCount = missingValueCount;
        this.invalidValueCount = invalidValueCount;
        this.minimum = minimum;
        this.maximum = maximum;
        this.sum = sum;
        this.average = average;
    }

    public String getColumn() {
        return column;
    }

    public int getValidValueCount() {
        return validValueCount;
    }

    public int getMissingValueCount() {
        return missingValueCount;
    }

    public int getInvalidValueCount() {
        return invalidValueCount;
    }

    public BigDecimal getMinimum() {
        return minimum;
    }

    public BigDecimal getMaximum() {
        return maximum;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public BigDecimal getAverage() {
        return average;
    }

    @Override
    public String toString() {
        return "Statistics for " + column
                + ": valid=" + validValueCount
                + ", missing=" + missingValueCount
                + ", invalid=" + invalidValueCount
                + ", min=" + display(minimum)
                + ", max=" + display(maximum)
                + ", sum=" + display(sum)
                + ", average=" + display(average);
    }

    private String display(BigDecimal value) {
        return value == null ? "n/a" : value.toPlainString();
    }
}
