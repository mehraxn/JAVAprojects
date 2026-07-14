package csvanalyticsengine;

import static csvanalyticsengine.TestSupport.assertEquals;
import static csvanalyticsengine.TestSupport.assertThrows;
import static csvanalyticsengine.TestSupport.test;

import java.util.ArrayList;
import java.util.List;

final class DataSetTest {

    private DataSetTest() {
    }

    private static DataRow row(String id, String name) {
        DataRow row = new DataRow();
        row.put("id", id);
        row.put("name", name);
        return row;
    }

    static void run() {
        test("valid headers are accepted and trimmed", () -> {
            DataSet dataSet = new DataSet(List.of("  id  ", "name"));
            assertEquals(List.of("id", "name"), dataSet.getColumns(), "trimmed columns");
        });

        test("duplicate headers are rejected case-insensitively", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new DataSet(List.of("id", "name", "ID")), "duplicate header");
        });

        test("empty and null headers are rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new DataSet(List.of("id", "  ")), "blank header");
            List<String> withNull = new ArrayList<>();
            withNull.add("id");
            withNull.add(null);
            assertThrows(IllegalArgumentException.class,
                    () -> new DataSet(withNull), "null header");
            assertThrows(IllegalArgumentException.class,
                    () -> new DataSet(null), "null header list");
        });

        test("rows must exactly match the data set columns", () -> {
            DataSet dataSet = new DataSet(List.of("id", "name"));
            dataSet.addRow(row("1", "Alice"));
            assertEquals(1, dataSet.getRowCount(), "matching row added");

            DataRow shortRow = new DataRow();
            shortRow.put("id", "2");
            assertThrows(IllegalArgumentException.class,
                    () -> dataSet.addRow(shortRow), "short row rejected");

            DataRow wrongColumns = new DataRow();
            wrongColumns.put("id", "2");
            wrongColumns.put("city", "Berlin");
            assertThrows(IllegalArgumentException.class,
                    () -> dataSet.addRow(wrongColumns), "wrong column rejected");
            assertThrows(IllegalArgumentException.class,
                    () -> dataSet.addRow(null), "null row rejected");
        });

        test("getRows returns defensive copies", () -> {
            DataSet dataSet = new DataSet(List.of("id", "name"));
            dataSet.addRow(row("1", "Alice"));
            // Mutating a returned copy must not change the stored row.
            dataSet.getRows().get(0).put("extra", "value");
            assertEquals(2, dataSet.getRows().get(0).asMap().size(),
                    "stored row unchanged after mutating a returned copy");
        });

        test("addRow stores a copy, not the caller's object", () -> {
            DataSet dataSet = new DataSet(List.of("id", "name"));
            DataRow original = row("1", "Alice");
            dataSet.addRow(original);
            original.put("extra", "value");
            assertEquals(2, dataSet.getRows().get(0).asMap().size(),
                    "stored row unchanged after mutating the original");
        });

        test("returned row list is unmodifiable", () -> {
            DataSet dataSet = new DataSet(List.of("id", "name"));
            dataSet.addRow(row("1", "Alice"));
            assertThrows(UnsupportedOperationException.class,
                    () -> dataSet.getRows().clear(), "row list unmodifiable");
            assertThrows(UnsupportedOperationException.class,
                    () -> dataSet.getColumns().add("x"), "column list unmodifiable");
        });

        test("resolveColumn matches case-insensitively and rejects unknowns", () -> {
            DataSet dataSet = new DataSet(List.of("id", "Name"));
            assertEquals("Name", dataSet.resolveColumn("  name  "), "case-insensitive resolve");
            assertThrows(IllegalArgumentException.class,
                    () -> dataSet.resolveColumn("city"), "unknown column");
            assertThrows(IllegalArgumentException.class,
                    () -> dataSet.resolveColumn("   "), "blank column");
        });
    }
}
