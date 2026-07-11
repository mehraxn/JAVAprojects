package csvanalyticsengine;

import static csvanalyticsengine.TestSupport.assertEquals;
import static csvanalyticsengine.TestSupport.assertThrows;
import static csvanalyticsengine.TestSupport.test;

final class DataRowTest {

    private DataRowTest() {
    }

    static void run() {
        test("put and get return the stored value", () -> {
            DataRow row = new DataRow();
            row.put("name", "Alice");
            assertEquals("Alice", row.get("name"), "stored value");
            assertEquals("Alice", row.get("  name  "), "column lookup is trimmed");
        });

        test("null values are stored as empty strings", () -> {
            DataRow row = new DataRow();
            row.put("notes", null);
            assertEquals("", row.get("notes"), "null becomes empty");
        });

        test("empty string values are preserved", () -> {
            DataRow row = new DataRow();
            row.put("amount", "");
            assertEquals("", row.get("amount"), "empty value kept");
        });

        test("duplicate column put is rejected", () -> {
            DataRow row = new DataRow();
            row.put("id", "1");
            assertThrows(IllegalArgumentException.class,
                    () -> row.put("id", "2"), "duplicate column");
            assertEquals("1", row.get("id"), "original value kept");
        });

        test("unknown column lookup fails cleanly", () -> {
            DataRow row = new DataRow();
            row.put("id", "1");
            assertThrows(IllegalArgumentException.class,
                    () -> row.get("missing"), "unknown column");
        });

        test("blank column names are rejected", () -> {
            DataRow row = new DataRow();
            assertThrows(IllegalArgumentException.class,
                    () -> row.put("  ", "value"), "blank column on put");
            assertThrows(IllegalArgumentException.class,
                    () -> row.get(null), "null column on get");
        });

        test("asMap is an unmodifiable snapshot", () -> {
            DataRow row = new DataRow();
            row.put("id", "1");
            assertThrows(UnsupportedOperationException.class,
                    () -> row.asMap().put("x", "y"), "asMap unmodifiable");
            var snapshot = row.asMap();
            row.put("name", "Alice");
            assertEquals(1, snapshot.size(), "snapshot not affected by later put");
        });

        test("copy is an independent object with the same values", () -> {
            DataRow original = new DataRow();
            original.put("id", "1");
            DataRow copy = original.copy();
            assertEquals("1", copy.get("id"), "copied value");
            copy.put("name", "Alice");
            assertEquals(1, original.asMap().size(), "original unchanged after copy mutation");
        });
    }
}
