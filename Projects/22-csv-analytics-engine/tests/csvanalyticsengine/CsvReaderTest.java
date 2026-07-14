package csvanalyticsengine;

import static csvanalyticsengine.TestSupport.assertEquals;
import static csvanalyticsengine.TestSupport.assertThrows;
import static csvanalyticsengine.TestSupport.test;
import static csvanalyticsengine.TestSupport.withTempFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

final class CsvReaderTest {
    private static final CsvReader READER = new CsvReader();

    private CsvReaderTest() {
    }

    private static void writeLines(Path file, String... lines) throws IOException {
        Files.write(file, Arrays.asList(lines), StandardCharsets.UTF_8);
    }

    static void run() {
        test("basic CSV with headers and rows is parsed", () -> withTempFile(file -> {
            writeLines(file, "id,name,city", "1,Alice,Berlin", "2,Bob,Paris");
            DataSet dataSet = READER.read(file);
            assertEquals(List.of("id", "name", "city"), dataSet.getColumns(), "columns");
            assertEquals(2, dataSet.getRowCount(), "row count");
            assertEquals("Alice", dataSet.getRows().get(0).get("name"), "first row value");
            assertEquals("Paris", dataSet.getRows().get(1).get("city"), "second row value");
        }));

        test("quoted commas stay inside one field", () -> withTempFile(file -> {
            writeLines(file, "id,product", "1,\"Apples, red\"");
            assertEquals("Apples, red", READER.read(file).getRows().get(0).get("product"),
                    "quoted comma");
        }));

        test("escaped double quotes are unescaped", () -> withTempFile(file -> {
            writeLines(file, "id,product", "1,\"Bread \"\"whole grain\"\"\"");
            assertEquals("Bread \"whole grain\"", READER.read(file).getRows().get(0).get("product"),
                    "escaped quotes");
        }));

        test("empty cells load as empty strings", () -> withTempFile(file -> {
            writeLines(file, "id,amount,region", "1,,South");
            DataRow row = READER.read(file).getRows().get(0);
            assertEquals("", row.get("amount"), "empty middle cell");
            assertEquals("South", row.get("region"), "cell after empty");
        }));

        test("blank lines before, between, and after rows are skipped", () -> withTempFile(file -> {
            writeLines(file, "", "id,name", "1,Alice", "   ", "2,Bob", "");
            assertEquals(2, READER.read(file).getRowCount(), "rows around blank lines");
        }));

        test("UTF-8 values are read correctly", () -> withTempFile(file -> {
            writeLines(file, "id,name", "1,Café Zürich");
            assertEquals("Café Zürich", READER.read(file).getRows().get(0).get("name"), "UTF-8");
        }));

        test("empty and whitespace-only files load as an empty data set", () -> withTempFile(file -> {
            assertEquals(0, READER.read(file).getColumns().size(), "zero-byte file columns");
            writeLines(file, "   ", "");
            assertEquals(0, READER.read(file).getRowCount(), "whitespace-only file rows");
        }));

        test("duplicate headers are rejected", () -> withTempFile(file -> {
            writeLines(file, "id,name,ID", "1,Alice,2");
            assertThrows(IOException.class, () -> READER.read(file),
                    "duplicate header (case-insensitive)");
        }));

        test("empty header names are rejected", () -> withTempFile(file -> {
            writeLines(file, "id,,city", "1,Alice,Berlin");
            assertThrows(IOException.class, () -> READER.read(file), "empty header cell");
        }));

        test("short and long rows are rejected with the line number", () -> withTempFile(file -> {
            writeLines(file, "id,name,city", "1,Alice");
            try {
                READER.read(file);
                throw new AssertionError("expected IOException for short row");
            } catch (IOException expected) {
                TestSupport.assertTrue(expected.getMessage().contains("2"),
                        "short-row message names line 2: " + expected.getMessage());
            }
            writeLines(file, "id,name,city", "1,Alice,Berlin,Extra");
            assertThrows(IOException.class, () -> READER.read(file), "long row");
        }));

        test("malformed quoting is rejected", () -> withTempFile(file -> {
            writeLines(file, "id,name", "1,\"Unclosed");
            assertThrows(IOException.class, () -> READER.read(file), "unclosed quote");
            writeLines(file, "id,name", "1,\"Alice\"X");
            assertThrows(IOException.class, () -> READER.read(file), "text after closing quote");
            writeLines(file, "id,name", "1,Ali\"ce\"");
            assertThrows(IOException.class, () -> READER.read(file), "quote inside unquoted field");
        }));

        test("missing file and null path are rejected cleanly", () -> {
            Path missing = Files.createTempFile("csv-analytics-missing", ".csv");
            Files.delete(missing);
            assertThrows(IOException.class, () -> READER.read(missing), "missing file");
            assertThrows(IllegalArgumentException.class, () -> READER.read(null), "null path");
        });
    }
}
