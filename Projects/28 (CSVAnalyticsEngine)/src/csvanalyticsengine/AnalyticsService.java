package csvanalyticsengine;

import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Predicate;

public class AnalyticsService {
    public DataSet filter(DataSet dataSet, Predicate<DataRow> condition) {
        // TODO: Validate input and return matching rows.
        throw new UnsupportedOperationException("TODO: filter a data set");
    }

    public BigDecimal sum(DataSet dataSet, String columnName) {
        // TODO: Parse numeric values and sum the selected column.
        throw new UnsupportedOperationException("TODO: sum a column");
    }

    public BigDecimal average(DataSet dataSet, String columnName) {
        // TODO: Calculate a documented decimal average.
        throw new UnsupportedOperationException("TODO: average a column");
    }

    public Map<String, Long> countBy(DataSet dataSet, String columnName) {
        // TODO: Group rows by a column value and count each group.
        throw new UnsupportedOperationException("TODO: group and count rows");
    }
}
