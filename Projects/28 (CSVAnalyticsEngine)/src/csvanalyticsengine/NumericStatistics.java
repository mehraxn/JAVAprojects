package csvanalyticsengine;

import java.math.BigDecimal;

public class NumericStatistics {
    private final String column;
    private final int validValueCount;
    private final int missingValueCount;
    private final int invalidValueCount;
    private final BigDecimal minimum;
    private final BigDecimal maximum;
    private final BigDecimal average;

    public NumericStatistics(String column, int validValueCount, int missingValueCount,
            int invalidValueCount, BigDecimal minimum, BigDecimal maximum, BigDecimal average) {
        this.column = column;
        this.validValueCount = validValueCount;
        this.missingValueCount = missingValueCount;
        this.invalidValueCount = invalidValueCount;
        this.minimum = minimum;
        this.maximum = maximum;
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
                + ", average=" + display(average);
    }

    private String display(BigDecimal value) {
        return value == null ? "n/a" : value.toPlainString();
    }
}
