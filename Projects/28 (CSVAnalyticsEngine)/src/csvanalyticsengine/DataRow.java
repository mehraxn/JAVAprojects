package csvanalyticsengine;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class DataRow {
    private final Map<String, String> values = new LinkedHashMap<String, String>();

    public void put(String column, String value) {
        String validColumn = requireColumn(column);
        if (values.containsKey(validColumn)) {
            throw new IllegalArgumentException("Column already exists in this row: " + validColumn);
        }
        values.put(validColumn, value == null ? "" : value);
    }

    public String get(String column) {
        String validColumn = requireColumn(column);
        if (!values.containsKey(validColumn)) {
            throw new IllegalArgumentException("Unknown column: " + validColumn);
        }
        return values.get(validColumn);
    }

    public Map<String, String> asMap() {
        return Collections.unmodifiableMap(new LinkedHashMap<String, String>(values));
    }

    public DataRow copy() {
        DataRow copy = new DataRow();
        for (Map.Entry<String, String> entry : values.entrySet()) {
            copy.put(entry.getKey(), entry.getValue());
        }
        return copy;
    }

    private String requireColumn(String column) {
        if (column == null || column.trim().isEmpty()) {
            throw new IllegalArgumentException("Column name cannot be empty.");
        }
        return column.trim();
    }
}
