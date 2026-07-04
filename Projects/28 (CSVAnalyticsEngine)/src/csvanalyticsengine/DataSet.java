package csvanalyticsengine;

import java.util.ArrayList;
import java.util.List;

public class DataSet {
    private final List<String> columns = new ArrayList<>();
    private final List<DataRow> rows = new ArrayList<>();

    public void addColumn(String columnName) {
        // TODO: Validate and reject duplicate column names.
        throw new UnsupportedOperationException("TODO: add a column");
    }

    public void addRow(DataRow row) {
        // TODO: Validate row shape against the columns.
        throw new UnsupportedOperationException("TODO: add a row");
    }

    public List<String> getColumns() {
        // TODO: Return an unmodifiable column list.
        throw new UnsupportedOperationException("TODO: list columns");
    }

    public List<DataRow> getRows() {
        // TODO: Return an unmodifiable row list.
        throw new UnsupportedOperationException("TODO: list rows");
    }
}
