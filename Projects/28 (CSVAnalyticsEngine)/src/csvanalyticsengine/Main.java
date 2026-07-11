package csvanalyticsengine;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Command-line entry point. Main only parses arguments, calls the reader,
 * writer, and analytics service, prints results, and returns an exit code.
 * The {@link #run(String[], PrintStream, PrintStream)} method contains the
 * whole CLI so tests can call it directly without System.exit.
 */
public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        System.exit(run(args, System.out, System.err));
    }

    /** Runs one CLI command; returns 0 on success and 1 on any user error. */
    static int run(String[] args, PrintStream out, PrintStream err) {
        if (args.length == 0) {
            err.println("No command given.");
            printUsage(err);
            return 1;
        }
        String command = args[0].toLowerCase(Locale.ROOT);
        try {
            switch (command) {
                case "help":
                    printUsage(out);
                    return 0;
                case "demo":
                    return runDemo(out);
                case "summary":
                    return runSummary(args, out);
                case "stats":
                    return runStats(args, out);
                case "group":
                    return runGroup(args, out);
                case "filter":
                    return runFilter(args, out);
                case "export-filtered":
                    return runExportFiltered(args, out);
                default:
                    err.println("Unknown command: " + args[0]);
                    printUsage(err);
                    return 1;
            }
        } catch (IOException exception) {
            err.println("Could not read or write CSV data: " + exception.getMessage());
            return 1;
        } catch (IllegalArgumentException exception) {
            err.println("Invalid request: " + exception.getMessage());
            return 1;
        }
    }

    private static int runSummary(String[] args, PrintStream out) throws IOException {
        requireArgCount(args, 2, "summary <file.csv>");
        DataSet dataSet = new CsvReader().read(Paths.get(args[1]));
        out.println("Rows:    " + dataSet.getRowCount());
        out.println("Columns: " + dataSet.getColumns().size());
        out.println("Names:   " + String.join(", ", dataSet.getColumns()));
        return 0;
    }

    private static int runStats(String[] args, PrintStream out) throws IOException {
        requireArgCount(args, 3, "stats <file.csv> <numeric-column>");
        DataSet dataSet = new CsvReader().read(Paths.get(args[1]));
        NumericStatistics statistics =
                new AnalyticsService().calculateStatistics(dataSet, args[2]);
        out.println("Statistics for column '" + statistics.getColumn() + "':");
        out.println("  count (valid numbers): " + statistics.getValidValueCount());
        out.println("  missing values:        " + statistics.getMissingValueCount());
        out.println("  invalid values:        " + statistics.getInvalidValueCount());
        out.println("  min:     " + display(statistics.getMinimum()));
        out.println("  max:     " + display(statistics.getMaximum()));
        out.println("  sum:     " + display(statistics.getSum()));
        out.println("  average: " + display(statistics.getAverage()));
        return 0;
    }

    private static int runGroup(String[] args, PrintStream out) throws IOException {
        requireArgCount(args, 3, "group <file.csv> <column>");
        DataSet dataSet = new CsvReader().read(Paths.get(args[1]));
        Map<String, Integer> counts = new AnalyticsService().countBy(dataSet, args[2]);
        out.println("Counts per '" + args[2] + "' value:");
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
        return 0;
    }

    private static int runFilter(String[] args, PrintStream out) throws IOException {
        requireArgCount(args, 4, "filter <file.csv> <column> <value>");
        DataSet dataSet = new CsvReader().read(Paths.get(args[1]));
        List<DataRow> matches = new AnalyticsService().filter(dataSet, args[2], args[3]);
        out.println("Rows where " + args[2] + " = '" + args[3] + "': " + matches.size());
        printRows(out, dataSet.getColumns(), matches);
        return 0;
    }

    private static int runExportFiltered(String[] args, PrintStream out) throws IOException {
        requireArgCount(args, 5, "export-filtered <in.csv> <out.csv> <column> <value>");
        DataSet dataSet = new CsvReader().read(Paths.get(args[1]));
        List<DataRow> matches = new AnalyticsService().filter(dataSet, args[3], args[4]);
        DataSet filtered = new DataSet(dataSet.getColumns());
        for (DataRow row : matches) {
            filtered.addRow(row);
        }
        Path target = Paths.get(args[2]);
        new CsvWriter().write(target, filtered);
        out.println("Wrote " + matches.size() + " matching rows to " + target + ".");
        return 0;
    }

    private static int runDemo(PrintStream out) throws IOException {
        Path input = Files.createTempFile("csv-analytics-demo", ".csv");
        Path exported = Files.createTempFile("csv-analytics-demo-out", ".csv");
        try {
            Files.write(input, List.of(
                    "id,category,product,amount,region",
                    "1,Food,\"Apples, red\",12.50,North",
                    "2,Books,Clean Code,35.00,South",
                    "3,Food,\"Bread \"\"whole grain\"\"\",4.20,North",
                    "4,Electronics,Keyboard,invalid,West",
                    "5,Food,Milk,,South"));
            out.println("Demo: loading a temporary sample CSV (5 rows)...");

            DataSet dataSet = new CsvReader().read(input);
            out.println();
            out.println("Summary: " + dataSet.getRowCount() + " rows, columns "
                    + String.join(", ", dataSet.getColumns()));

            AnalyticsService analytics = new AnalyticsService();
            out.println();
            out.println(analytics.calculateStatistics(dataSet, "amount"));

            out.println();
            out.println("Counts per category:");
            for (Map.Entry<String, Integer> entry
                    : analytics.countBy(dataSet, "category").entrySet()) {
                out.println("  " + entry.getKey() + ": " + entry.getValue());
            }

            out.println();
            List<DataRow> food = analytics.filter(dataSet, "category", "Food");
            out.println("Rows where category = 'Food': " + food.size());
            printRows(out, dataSet.getColumns(), food);

            DataSet filtered = new DataSet(dataSet.getColumns());
            for (DataRow row : food) {
                filtered.addRow(row);
            }
            new CsvWriter().write(exported, filtered);
            DataSet roundTrip = new CsvReader().read(exported);
            out.println();
            out.println("Round trip: exported " + filtered.getRowCount()
                    + " filtered rows and read " + roundTrip.getRowCount() + " back.");
            out.println();
            out.println("Demo completed successfully (temporary files deleted).");
            return 0;
        } finally {
            Files.deleteIfExists(input);
            Files.deleteIfExists(exported);
        }
    }

    private static void printRows(PrintStream out, List<String> columns, List<DataRow> rows) {
        if (rows.isEmpty()) {
            out.println("  (no matching rows)");
            return;
        }
        out.println("  " + String.join(" | ", columns));
        for (DataRow row : rows) {
            StringBuilder line = new StringBuilder();
            for (String column : columns) {
                if (line.length() > 0) {
                    line.append(" | ");
                }
                String value = row.get(column);
                line.append(value.isEmpty() ? "(empty)" : value);
            }
            out.println("  " + line);
        }
    }

    private static String display(BigDecimal value) {
        return value == null ? "n/a" : value.toPlainString();
    }

    private static void requireArgCount(String[] args, int expected, String usage) {
        if (args.length != expected) {
            throw new IllegalArgumentException("Usage: " + usage);
        }
    }

    private static void printUsage(PrintStream stream) {
        stream.println("CSV Analytics Engine - commands:");
        stream.println("  help                                              Show this help");
        stream.println("  demo                                              Run a self-contained sample workflow");
        stream.println("  summary <file.csv>                                Row count, column count, column names");
        stream.println("  stats <file.csv> <numeric-column>                 Count/missing/invalid/min/max/sum/average");
        stream.println("  group <file.csv> <column>                         Count rows per column value");
        stream.println("  filter <file.csv> <column> <value>                Print rows matching an exact value");
        stream.println("  export-filtered <in.csv> <out.csv> <column> <value>   Write matching rows to a new CSV");
        stream.println();
        stream.println("Column names are matched case-insensitively. Filter values are matched exactly.");
    }
}
