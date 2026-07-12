package urlshortenerbackend;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

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
            case "service-demo":
                runDemo(out);
                return 0;
            case "csv-demo":
                return runCsvDemo(out, err);
            case "http-demo":
                printHttpDemo(out);
                return 0;
            case "server":
                return runServer(args, out, err);
            default:
                err.println("Unknown command: " + args[0]);
                printUsage(err);
                return 1;
        }
    }

    private static void printUsage(PrintStream out) {
        out.println("URL Shortener Backend - educational Java HttpServer project");
        out.println();
        out.println("Usage: java -cp out urlshortenerbackend.Main <command>");
        out.println();
        out.println("Commands:");
        out.println("  help            Show this usage text.");
        out.println("  demo            Run the service-layer demo (codes, redirects, hits).");
        out.println("  service-demo    Alias for demo.");
        out.println("  csv-demo        Save and reload links through a temporary CSV file.");
        out.println("  http-demo       Print example curl commands for the HTTP API.");
        out.println("  server <port>   Start the HTTP server on the given port (e.g. 8080).");
    }

    private static void runDemo(PrintStream out) {
        ShortenerService service = new ShortenerService(new CodeGenerator());

        out.println("== Create generated link ==");
        UrlEntry generated = service.shorten("https://example.com/articles/java-basics");
        out.println("Created: " + generated);

        out.println("== Create custom link ==");
        UrlEntry custom = service.shorten("https://openjdk.org/", "openjdk");
        out.println("Created: " + custom);

        out.println("== Duplicate custom code is rejected ==");
        try {
            service.shorten("https://example.com/other", "openjdk");
            out.println("ERROR: duplicate was accepted (this should not happen)");
        } catch (IllegalArgumentException exception) {
            out.println("Rejected as expected: " + exception.getMessage());
        }

        out.println("== Resolve and count hits ==");
        out.println("Resolved: " + service.resolve(generated.getShortCode()));
        out.println("Resolved: " + service.resolve(generated.getShortCode()));
        out.println("Resolved: " + service.resolve(custom.getShortCode()));

        out.println("== List links ==");
        for (UrlEntry entry : service.listEntries()) {
            out.println(entry);
        }
    }

    private static int runCsvDemo(PrintStream out, PrintStream err) {
        ShortenerService service = new ShortenerService(new CodeGenerator());
        service.shorten("https://example.com/first");
        service.shorten("https://example.com/a,b", "tricky");
        service.resolve("tricky");

        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("urlshortener-demo", ".csv");
            FileUrlStore store = new FileUrlStore();

            out.println("== Save " + service.listEntries().size()
                    + " links to temporary CSV ==");
            store.save(tempFile, service.snapshot());
            out.println("Saved to: " + tempFile);

            out.println("== Reload from CSV ==");
            Map<String, UrlEntry> loaded = store.load(tempFile);
            ShortenerService reloaded = new ShortenerService(new CodeGenerator());
            reloaded.replaceEntries(loaded);
            for (UrlEntry entry : reloaded.listEntries()) {
                out.println("Loaded: " + entry);
            }
            if (reloaded.listEntries().size() != service.listEntries().size()) {
                err.println("CSV round trip lost entries.");
                return 1;
            }
            if (reloaded.find("tricky").getHitCount() != 1) {
                err.println("CSV round trip lost hit counts.");
                return 1;
            }
            out.println("Round trip preserved all entries and hit counts.");
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

    private static void printHttpDemo(PrintStream out) {
        out.println("Start the server first:");
        out.println("  java -cp out urlshortenerbackend.Main server 8080");
        out.println();
        out.println("Then try these curl commands:");
        out.println("  curl -i -X POST -d \"url=https%3A%2F%2Fexample.com\" http://localhost:8080/links");
        out.println("  curl -i -X POST -d \"url=https%3A%2F%2Fexample.com%2Fdocs&code=docs\" http://localhost:8080/links");
        out.println("  curl -i http://localhost:8080/links");
        out.println("  curl -i http://localhost:8080/r/docs   (returns 302 redirect)");
        out.println("  curl -i http://localhost:8080/unknown  (returns JSON 404)");
    }

    private static int runServer(String[] args, PrintStream out, PrintStream err) {
        if (args.length < 2) {
            err.println("Missing port. Usage: java -cp out urlshortenerbackend.Main server <port>");
            return 1;
        }
        int port;
        try {
            port = Integer.parseInt(args[1].trim());
        } catch (NumberFormatException exception) {
            err.println("Port must be a number, got: " + args[1]);
            return 1;
        }
        ShortenerService service = new ShortenerService(new CodeGenerator());
        UrlShortenerHttpServer server = new UrlShortenerHttpServer(service);
        try {
            server.start(port);
        } catch (Exception exception) {
            err.println("Could not start server: " + exception.getMessage());
            return 1;
        }
        out.println("URL shortener listening on http://localhost:" + server.getPort());
        out.println("Stop the process with Ctrl+C.");
        try {
            new CountDownLatch(1).await();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        } finally {
            server.stop();
        }
        return 0;
    }
}
