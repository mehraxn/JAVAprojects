package csvanalyticsengine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class DataSet {
    private final List<String> columns;
    private final List<DataRow> rows = new ArrayList<DataRow>();

    public DataSet(List<String> columns) {
        if (columns == null) {
            throw new IllegalArgumentException("Column list cannot be null.");
        }

        List<String> checkedColumns = new ArrayList<String>();
        Set<String> normalizedColumns = new HashSet<String>();
        for (String column : columns) {
            if (column == null || column.trim().isEmpty()) {
                throw new IllegalArgumentException("Column names cannot be empty.");
            }
            String checkedColumn = column.trim();
            String normalized = checkedColumn.toLowerCase(Locale.ROOT);
            if (!normalizedColumns.add(normalized)) {
                throw new IllegalArgumentException("Duplicate column name: " + checkedColumn);
            }
            checkedColumns.add(checkedColumn);
        }
        this.columns = Collections.unmodifiableList(checkedColumns);
    }

    public void addRow(DataRow row) {
        if (row == null) {
            throw new IllegalArgumentException("Row cannot be null.");
        }
        if (!row.asMap().keySet().equals(new java.util.LinkedHashSet<String>(columns))) {
            throw new IllegalArgumentException("Row columns must exactly match the data set columns.");
        }
        rows.add(row.copy());
    }

    public List<String> getColumns() {
        return columns;
    }

    public List<DataRow> getRows() {
        List<DataRow> copies = new ArrayList<DataRow>();
        for (DataRow row : rows) {
            copies.add(row.copy());
        }
        return Collections.unmodifiableList(copies);
    }

    public int getRowCount() {
        return rows.size();
    }

    public String resolveColumn(String requestedColumn) {
        if (requestedColumn == null || requestedColumn.trim().isEmpty()) {
            throw new IllegalArgumentException("Column name cannot be empty.");
        }
        for (String column : columns) {
            if (column.equalsIgnoreCase(requestedColumn.trim())) {
                return column;
            }
        }
        throw new IllegalArgumentException("Unknown column: " + requestedColumn.trim());
    }
}
