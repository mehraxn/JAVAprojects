package csvanalyticsengine;

import java.util.LinkedHashMap;
import java.util.Map;

public class DataRow {
    private final Map<String, String> values = new LinkedHashMap<>();

    public String get(String columnName) {
        // TODO: Validate the column and return its value.
        throw new UnsupportedOperationException("TODO: read a row value");
    }

    public void put(String columnName, String value) {
        // TODO: Validate and store a column value.
        throw new UnsupportedOperationException("TODO: store a row value");
    }

    public Map<String, String> asMap() {
        // TODO: Return an unmodifiable snapshot.
        throw new UnsupportedOperationException("TODO: expose row values");
    }
}
