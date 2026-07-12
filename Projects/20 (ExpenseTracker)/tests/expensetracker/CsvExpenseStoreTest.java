package expensetracker;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class CsvExpenseStoreTest {
    private CsvExpenseStoreTest() {
    }

    static void run(Assert t) throws Exception {
        CsvExpenseStore store = new CsvExpenseStore();

        t.assertThrows(IllegalArgumentException.class,
                () -> store.load(null), "null load path rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> store.save(null, new ArrayList<Expense>()), "null save path rejected");

        Path tempFile = Files.createTempFile("expensetracker-test", ".csv");
        try {
            t.assertThrows(IllegalArgumentException.class,
                    () -> store.save(tempFile, null), "null expense list rejected");

            // Round trip: values, commas, quotes, BigDecimal scale, dates
            List<Expense> expenses = new ArrayList<Expense>();
            expenses.add(new Expense("E-001", "Groceries", new BigDecimal("42.75"),
                    "Food", LocalDate.of(2026, 7, 2)));
            expenses.add(new Expense("E-002", "Lunch, coffee \"to go\"", new BigDecimal("18.50"),
                    "Food", LocalDate.of(2026, 7, 3)));
            expenses.add(new Expense("E-003", "Rent, July", new BigDecimal("650.00"),
                    "Housing, main", LocalDate.of(2026, 7, 1)));
            store.save(tempFile, expenses);

            List<Expense> loaded = store.load(tempFile);
            t.assertEquals(3, loaded.size(), "save/load preserves all expenses");
            t.assertEquals("Lunch, coffee \"to go\"", loaded.get(1).getTitle(),
                    "commas and quotes in titles survive the round trip");
            t.assertEquals("Housing, main", loaded.get(2).getCategory(),
                    "commas in categories survive the round trip");
            t.assertEquals("650.00", loaded.get(2).getAmount().toPlainString(),
                    "BigDecimal scale survives the round trip");
            t.assertBigDecimalEquals(new BigDecimal("42.75"), loaded.get(0).getAmount(),
                    "amounts survive the round trip numerically");
            t.assertEquals(LocalDate.of(2026, 7, 2), loaded.get(0).getDate(),
                    "dates survive the round trip");
            t.assertThrows(UnsupportedOperationException.class,
                    () -> loaded.add(loaded.get(0)), "loaded list is unmodifiable");

            // Atomic-style save: saving over an existing file replaces it completely
            // and leaves no temporary files behind
            List<Expense> smaller = new ArrayList<Expense>();
            smaller.add(new Expense("E-009", "Only entry", new BigDecimal("1.23"),
                    "Misc", LocalDate.of(2026, 7, 9)));
            store.save(tempFile, smaller);
            t.assertEquals(1, store.load(tempFile).size(),
                    "saving over an existing file replaces its contents");
            int strayTempFiles = 0;
            try (java.util.stream.Stream<Path> siblings =
                    Files.list(tempFile.toAbsolutePath().getParent())) {
                strayTempFiles = (int) siblings
                        .filter(p -> p.getFileName().toString().startsWith("expenses")
                                && p.getFileName().toString().endsWith(".csv.tmp"))
                        .count();
            }
            t.assertEquals(0, strayTempFiles, "atomic save leaves no temporary files behind");

            // Duplicate IDs rejected on save
            List<Expense> duplicates = new ArrayList<Expense>();
            duplicates.add(new Expense("E-001", "One", new BigDecimal("1.00"),
                    "Misc", LocalDate.of(2026, 7, 1)));
            duplicates.add(new Expense("E-001", "Two", new BigDecimal("2.00"),
                    "Misc", LocalDate.of(2026, 7, 2)));
            t.assertThrows(IllegalArgumentException.class,
                    () -> store.save(tempFile, duplicates), "duplicate IDs rejected on save");
            t.assertEquals(1, store.load(tempFile).size(),
                    "failed save leaves the existing file intact");

            // Malformed and invalid content on load
            expectLoadFailure(t, store, tempFile,
                    "id,title,amount,category,date\nE-1,Item,5.00,Misc",
                    "row with too few fields rejected");
            expectLoadFailure(t, store, tempFile,
                    "id,title,amount,category,date\nE-1,Item,5.00,Misc,2026-07-01,extra",
                    "row with too many fields rejected");
            expectLoadFailure(t, store, tempFile,
                    "wrong,header,columns,here,now\nE-1,Item,5.00,Misc,2026-07-01",
                    "unexpected header rejected");
            expectLoadFailure(t, store, tempFile,
                    "id,title,amount,category,date\nE-1,Item,twelve,Misc,2026-07-01",
                    "non-numeric amount rejected");
            expectLoadFailure(t, store, tempFile,
                    "id,title,amount,category,date\nE-1,Item,0,Misc,2026-07-01",
                    "zero amount in CSV rejected");
            expectLoadFailure(t, store, tempFile,
                    "id,title,amount,category,date\nE-1,Item,-5.00,Misc,2026-07-01",
                    "negative amount in CSV rejected");
            expectLoadFailure(t, store, tempFile,
                    "id,title,amount,category,date\nE-1,Item,5.00,Misc,2026-02-30",
                    "invalid calendar date rejected");
            expectLoadFailure(t, store, tempFile,
                    "id,title,amount,category,date\nE-1,Item,5.00,Misc,not-a-date",
                    "non-date text rejected");
            expectLoadFailure(t, store, tempFile,
                    "id,title,amount,category,date\n"
                            + "E-1,Item,5.00,Misc,2026-07-01\nE-1,Other,6.00,Misc,2026-07-02",
                    "duplicate IDs in CSV rejected");
            expectLoadFailure(t, store, tempFile,
                    "id,title,amount,category,date\n\"E-1,Item,5.00,Misc,2026-07-01",
                    "unclosed quote rejected");
            expectLoadFailure(t, store, tempFile,
                    "id,title,amount,category,date\nE-1,Item,5.00,,2026-07-01",
                    "blank category in CSV rejected");

            // Empty, blank, and header-only files load as empty lists
            Files.write(tempFile, new byte[0]);
            t.assertEquals(0, store.load(tempFile).size(), "zero-byte file loads as empty list");
            Files.write(tempFile, "\n   \n".getBytes(StandardCharsets.UTF_8));
            t.assertEquals(0, store.load(tempFile).size(),
                    "whitespace-only file loads as empty list");
            Files.write(tempFile, "id,title,amount,category,date\n"
                    .getBytes(StandardCharsets.UTF_8));
            t.assertEquals(0, store.load(tempFile).size(),
                    "header-only file loads as empty list");
            Files.write(tempFile, ("id,title,amount,category,date\n\n"
                    + "E-1,Item,5.00,Misc,2026-07-01\n\n").getBytes(StandardCharsets.UTF_8));
            t.assertEquals(1, store.load(tempFile).size(),
                    "blank lines between rows are ignored");
        } finally {
            Files.deleteIfExists(tempFile);
        }

        // Missing file loads as an empty list
        Path missing = tempFile.resolveSibling(
                "expensetracker-missing-" + System.nanoTime() + ".csv");
        t.assertEquals(0, store.load(missing).size(), "missing file loads as empty list");

        // Directory paths are rejected
        Path tempDir = Files.createTempDirectory("expensetracker-dir");
        try {
            t.assertThrows(IOException.class, () -> store.load(tempDir),
                    "loading a directory path rejected");
            t.assertThrows(IOException.class,
                    () -> store.save(tempDir, new ArrayList<Expense>()),
                    "saving to a directory path rejected");
        } finally {
            Files.deleteIfExists(tempDir);
        }
    }

    private static void expectLoadFailure(Assert t, CsvExpenseStore store,
            Path file, String content, String message) throws IOException {
        Files.write(file, content.getBytes(StandardCharsets.UTF_8));
        t.assertThrows(IOException.class, () -> store.load(file), message);
    }
}
