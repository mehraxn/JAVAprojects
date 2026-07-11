package csvanalyticsengine;

import static csvanalyticsengine.TestSupport.assertEquals;
import static csvanalyticsengine.TestSupport.assertTrue;
import static csvanalyticsengine.TestSupport.test;
import static csvanalyticsengine.TestSupport.withTempFile;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Smoke tests for the CLI. Main.run is called directly with captured output
 * streams, so no System.exit is involved and exit codes can be asserted.
 */
final class CliSmokeTest {

    private CliSmokeTest() {
    }

    private static final class CliResult {
        final int exitCode;
        final String out;
        final String err;

        CliResult(int exitCode, String out, String err) {
            this.exitCode = exitCode;
            this.out = out;
            this.err = err;
        }
    }

    private static CliResult runCli(String... args) {
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        ByteArrayOutputStream errBytes = new ByteArrayOutputStream();
        int exitCode;
        try (PrintStream out = new PrintStream(outBytes, true, StandardCharsets.UTF_8);
                PrintStream err = new PrintStream(errBytes, true, StandardCharsets.UTF_8)) {
            exitCode = Main.run(args, out, err);
        }
        return new CliResult(exitCode,
                outBytes.toString(StandardCharsets.UTF_8),
                errBytes.toString(StandardCharsets.UTF_8));
    }

    private static void writeSampleCsv(Path file) throws Exception {
        Files.write(file, List.of(
                "id,category,amount",
                "1,Food,12.50",
                "2,Books,35.00",
                "3,Food,4.20"));
    }

    static void run() {
        test("help exits 0 and prints the commands", () -> {
            CliResult result = runCli("help");
            assertEquals(0, result.exitCode, "help exit code");
            assertTrue(result.out.contains("export-filtered"), "help lists commands");
        });

        test("demo exits 0 and reports success", () -> {
            CliResult result = runCli("demo");
            assertEquals(0, result.exitCode, "demo exit code");
            assertTrue(result.out.contains("Demo completed successfully"), "demo success line");
            assertTrue(result.out.contains("Round trip"), "demo round trip line");
        });

        test("summary exits 0 for a valid file", () -> withTempFile(file -> {
            writeSampleCsv(file);
            CliResult result = runCli("summary", file.toString());
            assertEquals(0, result.exitCode, "summary exit code");
            assertTrue(result.out.contains("Rows:    3"), "summary row count");
            assertTrue(result.out.contains("id, category, amount"), "summary column names");
        }));

        test("stats exits 0 for a valid numeric column", () -> withTempFile(file -> {
            writeSampleCsv(file);
            CliResult result = runCli("stats", file.toString(), "amount");
            assertEquals(0, result.exitCode, "stats exit code");
            assertTrue(result.out.contains("sum:     51.70"), "stats sum: " + result.out);
        }));

        test("group exits 0 and prints counts", () -> withTempFile(file -> {
            writeSampleCsv(file);
            CliResult result = runCli("group", file.toString(), "category");
            assertEquals(0, result.exitCode, "group exit code");
            assertTrue(result.out.contains("Food: 2"), "group counts");
        }));

        test("filter exits 0 and prints matching rows", () -> withTempFile(file -> {
            writeSampleCsv(file);
            CliResult result = runCli("filter", file.toString(), "category", "Food");
            assertEquals(0, result.exitCode, "filter exit code");
            assertTrue(result.out.contains("2"), "filter match count");
        }));

        test("export-filtered exits 0 and creates a readable output file", () -> withTempFile(input -> {
            writeSampleCsv(input);
            Path output = Files.createTempFile("csv-analytics-export", ".csv");
            try {
                Files.delete(output);
                CliResult result = runCli("export-filtered", input.toString(),
                        output.toString(), "category", "Food");
                assertEquals(0, result.exitCode, "export exit code");
                assertTrue(Files.exists(output), "output file created");
                DataSet exported = new CsvReader().read(output);
                assertEquals(2, exported.getRowCount(), "exported row count");
            } finally {
                Files.deleteIfExists(output);
            }
        }));

        test("missing file exits non-zero with a clean message", () -> {
            CliResult result = runCli("summary", "no-such-file-anywhere.csv");
            assertEquals(1, result.exitCode, "missing file exit code");
            assertTrue(result.err.contains("Could not read"), "missing file message");
        });

        test("malformed CSV exits non-zero", () -> withTempFile(file -> {
            Files.write(file, List.of("id,name", "1,\"Unclosed"));
            CliResult result = runCli("summary", file.toString());
            assertEquals(1, result.exitCode, "malformed exit code");
            assertTrue(result.err.contains("line 2"), "malformed message names the line");
        }));

        test("unknown column exits non-zero", () -> withTempFile(file -> {
            writeSampleCsv(file);
            CliResult result = runCli("stats", file.toString(), "price");
            assertEquals(1, result.exitCode, "unknown column exit code");
            assertTrue(result.err.contains("Unknown column"), "unknown column message");
        }));

        test("unknown command, no command, and missing arguments exit non-zero", () -> {
            assertEquals(1, runCli("frobnicate").exitCode, "unknown command");
            assertEquals(1, runCli().exitCode, "no command");
            assertEquals(1, runCli("stats").exitCode, "stats without arguments");
            assertEquals(1, runCli("filter", "file.csv").exitCode, "filter without value");
            assertTrue(runCli("stats").err.contains("Usage:"), "missing-argument usage hint");
        });
    }
}
