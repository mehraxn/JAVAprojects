package csvanalyticsengine;

import static csvanalyticsengine.TestSupport.assertEquals;
import static csvanalyticsengine.TestSupport.assertThrows;
import static csvanalyticsengine.TestSupport.assertTrue;
import static csvanalyticsengine.TestSupport.test;
import static csvanalyticsengine.TestSupport.withTempFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

final class CsvWriterTest {
    private static final CsvWriter WRITER = new CsvWriter();
    private static final CsvReader READER = new CsvReader();

    private CsvWriterTest() {
    }

    private static DataSet sampleDataSet(String... productValues) {
        DataSet dataSet = new DataSet(List.of("id", "product"));
        for (int index = 0; index < productValues.length; index++) {
            DataRow row = new DataRow();
            row.put("id", String.valueOf(index + 1));
            row.put("product", productValues[index]);
            dataSet.addRow(row);
        }
        return dataSet;
    }

    static void run() {
        test("basic data set is written with header and rows", () -> withTempFile(file -> {
            WRITER.write(file, sampleDataSet("Apples", "Bread"));
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            assertEquals("id,product", lines.get(0), "header line");
            assertEquals("1,Apples", lines.get(1), "first row line");
            assertEquals(3, lines.size(), "line count");
        }));

        test("values with commas are quoted in the file", () -> withTempFile(file -> {
            WRITER.write(file, sampleDataSet("Apples, red"));
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            assertEquals("1,\"Apples, red\"", lines.get(1), "quoted comma in raw output");
        }));

        test("values with double quotes are escaped in the file", () -> withTempFile(file -> {
            WRITER.write(file, sampleDataSet("Bread \"whole grain\""));
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            assertEquals("1,\"Bread \"\"whole grain\"\"\"", lines.get(1),
                    "escaped quotes in raw output");
        }));

        test("write then read round trip preserves all values", () -> withTempFile(file -> {
            DataSet original = sampleDataSet("Apples, red", "Bread \"whole grain\"", "  spaced  ");
            WRITER.write(file, original);
            DataSet loaded = READER.read(file);
            assertEquals(original.getColumns(), loaded.getColumns(), "columns");
            assertEquals(original.getRowCount(), loaded.getRowCount(), "row count");
            for (int index = 0; index < original.getRowCount(); index++) {
                assertEquals(original.getRows().get(index).get("product"),
                        loaded.getRows().get(index).get("product"), "row " + index + " product");
            }
        }));

        test("values with line breaks are rejected (documented limitation)", () -> withTempFile(file -> {
            assertThrows(IOException.class,
                    () -> WRITER.write(file, sampleDataSet("line one\nline two")),
                    "newline in value");
        }));

        test("data set with columns but no rows writes only the header", () -> withTempFile(file -> {
            WRITER.write(file, new DataSet(List.of("id", "product")));
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            assertEquals(1, lines.size(), "header only");
            assertEquals(0, READER.read(file).getRowCount(), "read back zero rows");
        }));

        test("completely empty data set writes an empty file", () -> withTempFile(file -> {
            WRITER.write(file, new DataSet(List.of()));
            assertTrue(Files.readAllLines(file, StandardCharsets.UTF_8).isEmpty(),
                    "no lines for empty data set");
            assertEquals(0, READER.read(file).getColumns().size(), "read back empty data set");
        }));

        test("null path and null data set are rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> WRITER.write(null, new DataSet(List.of("id"))), "null path");
            assertThrows(IllegalArgumentException.class, () -> withTempFile(
                    file -> WRITER.write(file, null)), "null data set");
        });
    }
}
