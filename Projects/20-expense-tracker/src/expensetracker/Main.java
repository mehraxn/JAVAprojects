package expensetracker;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        int exitCode = run(args, System.out, System.err);
        System.exit(exitCode);
    }

    public static int run(String[] args, PrintStream out, PrintStream err) {
        String command = args.length == 0 ? "help" : args[0].toLowerCase(Locale.ROOT);
        switch (command) {
            case "help":
                printUsage(out);
                return 0;
            case "demo":
                runDemo(out);
                return 0;
            case "csv-demo":
                return runCsvDemo(out, err);
            case "report-demo":
                runReportDemo(out);
                return 0;
            case "validation-demo":
                runValidationDemo(out);
                return 0;
            default:
                err.println("Unknown command: " + args[0]);
                printUsage(err);
                return 1;
        }
    }

    private static void printUsage(PrintStream out) {
        out.println("Expense Tracker - educational Java file-I/O and service-layer project");
        out.println();
        out.println("Usage: java -cp out expensetracker.Main <command>");
        out.println();
        out.println("Commands:");
        out.println("  help             Show this usage text (default with no command).");
        out.println("  demo             Create, list, filter, and total sample expenses.");
        out.println("  csv-demo         Save and reload expenses through a temporary CSV file.");
        out.println("  report-demo      Show spending reports (totals, categories, months).");
        out.println("  validation-demo  Show how invalid input is rejected cleanly.");
    }

    private static ExpenseService sampleService() {
        ExpenseService service = new ExpenseService();
        service.addExpense(new Expense("E-001", "Groceries", new BigDecimal("42.75"),
                "Food", LocalDate.of(2026, 7, 2)));
        service.addExpense(new Expense("E-002", "Train ticket", new BigDecimal("18.50"),
                "Transport", LocalDate.of(2026, 7, 3)));
        service.addExpense(new Expense("E-003", "Coffee beans", new BigDecimal("12.90"),
                "Food", LocalDate.of(2026, 6, 28)));
        service.addExpense(new Expense("E-004", "Rent, July", new BigDecimal("650.00"),
                "Housing", LocalDate.of(2026, 7, 1)));
        return service;
    }

    private static void runDemo(PrintStream out) {
        ExpenseService service = sampleService();

        out.println("== All expenses ==");
        printExpenses(out, service.listExpenses());

        out.println("== Filter by category 'food' (case-insensitive) ==");
        printExpenses(out, service.filterByCategory("food"));

        out.println("== Filter by month 2026-07 ==");
        printExpenses(out, service.filterByMonth(YearMonth.of(2026, 7)));

        out.println("== Totals ==");
        out.println("Total spending: " + service.calculateTotalSpending());
        out.println("Category totals: " + service.calculateCategoryTotals());

        out.println("== Highest expense ==");
        out.println(service.highestExpense().map(Expense::toString).orElse("none"));
    }

    private static int runCsvDemo(PrintStream out, PrintStream err) {
        ExpenseService service = sampleService();
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("expensetracker-demo", ".csv");
            ExpenseStore store = new CsvExpenseStore();

            out.println("== Save " + service.listExpenses().size()
                    + " expenses to temporary CSV ==");
            store.save(tempFile, service.listExpenses());
            out.println("Saved to: " + tempFile);

            out.println("== Reload from CSV ==");
            List<Expense> loaded = store.load(tempFile);
            for (Expense expense : loaded) {
                out.println("Loaded: " + expense);
            }
            if (loaded.size() != service.listExpenses().size()) {
                err.println("CSV round trip lost expenses.");
                return 1;
            }
            BigDecimal originalTotal = service.calculateTotalSpending();
            BigDecimal loadedTotal = BigDecimal.ZERO;
            for (Expense expense : loaded) {
                loadedTotal = loadedTotal.add(expense.getAmount());
            }
            if (originalTotal.compareTo(loadedTotal) != 0) {
                err.println("CSV round trip changed the total.");
                return 1;
            }
            out.println("Round trip preserved all " + loaded.size()
                    + " expenses and the total of " + loadedTotal + ".");
            return 0;
        } catch (Exception exception) {
            err.println("CSV demo failed: " + exception.getMessage());
            return 1;
        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                    out.println("Deleted temporary file.");
                } catch (Exception cleanupFailure) {
                    err.println("Could not delete temporary file: " + tempFile);
                }
            }
        }
    }

    private static void runReportDemo(PrintStream out) {
        ExpenseService service = sampleService();

        out.println("== Total spending ==");
        out.println(service.calculateTotalSpending());

        out.println("== Totals by category ==");
        for (Map.Entry<String, BigDecimal> entry : service.calculateCategoryTotals().entrySet()) {
            out.println(entry.getKey() + ": " + entry.getValue());
        }

        out.println("== Totals by month ==");
        for (Map.Entry<YearMonth, BigDecimal> entry : service.calculateMonthlyTotals().entrySet()) {
            out.println(entry.getKey() + ": " + entry.getValue());
        }

        out.println("== Highest expense ==");
        out.println(service.highestExpense().map(Expense::toString).orElse("none"));

        out.println("== Expenses from 2026-07-01 to 2026-07-31 ==");
        printExpenses(out, service.filterByDateRange(
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 31)));

        out.println("== Expenses between 10.00 and 50.00 ==");
        printExpenses(out, service.filterByAmountRange(
                new BigDecimal("10.00"), new BigDecimal("50.00")));
    }

    private static void runValidationDemo(PrintStream out) {
        ExpenseService service = new ExpenseService();
        service.addExpense(new Expense("E-001", "Valid expense", new BigDecimal("10.00"),
                "Misc", LocalDate.of(2026, 7, 1)));

        out.println("== Validation demo: every rejection below is intentional ==");

        out.println("-- Blank title --");
        try {
            new Expense("E-002", "   ", new BigDecimal("5.00"), "Misc", LocalDate.now());
            out.println("ERROR: blank title was accepted (this should not happen)");
        } catch (IllegalArgumentException exception) {
            out.println("Rejected as expected: " + exception.getMessage());
        }

        out.println("-- Zero amount --");
        try {
            new Expense("E-002", "Zero", BigDecimal.ZERO, "Misc", LocalDate.now());
            out.println("ERROR: zero amount was accepted (this should not happen)");
        } catch (IllegalArgumentException exception) {
            out.println("Rejected as expected: " + exception.getMessage());
        }

        out.println("-- Negative amount --");
        try {
            new Expense("E-002", "Negative", new BigDecimal("-1.00"), "Misc", LocalDate.now());
            out.println("ERROR: negative amount was accepted (this should not happen)");
        } catch (IllegalArgumentException exception) {
            out.println("Rejected as expected: " + exception.getMessage());
        }

        out.println("-- Duplicate ID --");
        try {
            service.addExpense(new Expense("E-001", "Duplicate", new BigDecimal("5.00"),
                    "Misc", LocalDate.now()));
            out.println("ERROR: duplicate ID was accepted (this should not happen)");
        } catch (IllegalArgumentException exception) {
            out.println("Rejected as expected: " + exception.getMessage());
        }

        out.println("-- Invalid date range --");
        try {
            service.filterByDateRange(LocalDate.of(2026, 7, 31), LocalDate.of(2026, 7, 1));
            out.println("ERROR: inverted date range was accepted (this should not happen)");
        } catch (IllegalArgumentException exception) {
            out.println("Rejected as expected: " + exception.getMessage());
        }

        out.println("-- Missing expense removal --");
        out.println("removeExpense(\"E-999\") returned: " + service.removeExpense("E-999")
                + " (false = not found)");

        out.println("All validation cases behaved as designed.");
    }

    private static void printExpenses(PrintStream out, List<Expense> expenses) {
        if (expenses.isEmpty()) {
            out.println("No expenses found.");
            return;
        }
        for (Expense expense : expenses) {
            out.println(expense);
        }
    }
}
