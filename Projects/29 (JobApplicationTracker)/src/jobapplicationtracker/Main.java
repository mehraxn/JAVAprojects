package jobapplicationtracker;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Command-line entry point. Main only parses arguments, calls TrackerService,
 * prints results, and sets the exit code. All business logic lives in the
 * service, model, and repository classes.
 */
public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        int exitCode = run(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    static int run(String[] args) {
        if (args.length == 0) {
            System.err.println("No command given.");
            printUsage(System.err);
            return 1;
        }
        String command = args[0].toLowerCase(Locale.ROOT);
        try {
            switch (command) {
                case "help":
                    printUsage(System.out);
                    return 0;
                case "demo":
                    return runDemo();
                case "list":
                    return runList(args);
                case "add":
                    return runAdd(args);
                case "update-status":
                    return runUpdateStatus(args);
                case "search":
                    return runSearch(args);
                case "summary":
                    return runSummary(args);
                default:
                    System.err.println("Unknown command: " + args[0]);
                    printUsage(System.err);
                    return 1;
            }
        } catch (IllegalArgumentException exception) {
            System.err.println("Error: " + exception.getMessage());
            return 1;
        } catch (IOException exception) {
            System.err.println("File error: " + exception.getMessage());
            return 1;
        }
    }

    private static int runDemo() {
        TrackerService tracker = new TrackerService(new CsvApplicationRepository());
        JobApplication first = tracker.addApplication(
                "Northwind", "Java Developer", LocalDate.now().minusDays(10),
                JobApplication.Status.APPLIED, "Application sent through company website");
        tracker.addApplication(
                "Contoso", "Backend Engineer", LocalDate.now().minusDays(4),
                JobApplication.Status.SCREENING, "Recruiter call scheduled");
        tracker.updateStatus(first.getId(), JobApplication.Status.INTERVIEW);

        System.out.println("Demo tracker (in-memory only, nothing is written to disk):");
        printApplications(tracker.listApplications());
        System.out.println();
        System.out.println("Search results for 'java':");
        printApplications(tracker.searchByCompanyOrRole("java"));
        System.out.println();
        printSummary(tracker);
        System.out.println();
        System.out.println("Demo completed successfully.");
        return 0;
    }

    private static int runList(String[] args) throws IOException {
        requireArgCount(args, 2, "list <file.csv>");
        TrackerService tracker = loadTracker(args[1]);
        printApplications(tracker.listApplications());
        return 0;
    }

    private static int runAdd(String[] args) throws IOException {
        if (args.length != 5 && args.length != 6) {
            throw new IllegalArgumentException(
                    "Usage: add <file.csv> <company> <role> <status> [notes]");
        }
        Path path = Paths.get(args[1]);
        TrackerService tracker = loadTracker(args[1]);
        String notes = args.length == 6 ? args[5] : "";
        JobApplication created = tracker.addApplication(
                args[2], args[3], LocalDate.now(), parseStatus(args[4]), notes);
        tracker.save(path);
        System.out.println("Added " + created);
        return 0;
    }

    private static int runUpdateStatus(String[] args) throws IOException {
        requireArgCount(args, 4, "update-status <file.csv> <id> <status>");
        Path path = Paths.get(args[1]);
        TrackerService tracker = loadTracker(args[1]);
        long id = parseId(args[2]);
        JobApplication.Status status = parseStatus(args[3]);
        if (!tracker.updateStatus(id, status)) {
            System.err.println("Error: no application with ID " + id + " was found.");
            return 1;
        }
        tracker.save(path);
        System.out.println("Application " + id + " is now " + status + ".");
        return 0;
    }

    private static int runSearch(String[] args) throws IOException {
        requireArgCount(args, 3, "search <file.csv> <keyword>");
        TrackerService tracker = loadTracker(args[1]);
        List<JobApplication> matches = tracker.searchByCompanyOrRole(args[2]);
        System.out.println("Matches for '" + args[2] + "':");
        printApplications(matches);
        return 0;
    }

    private static int runSummary(String[] args) throws IOException {
        requireArgCount(args, 2, "summary <file.csv>");
        TrackerService tracker = loadTracker(args[1]);
        printSummary(tracker);
        return 0;
    }

    private static TrackerService loadTracker(String file) throws IOException {
        TrackerService tracker = new TrackerService(new CsvApplicationRepository());
        tracker.load(Paths.get(file));
        return tracker;
    }

    private static JobApplication.Status parseStatus(String text) {
        try {
            return JobApplication.Status.valueOf(text.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Unknown status '" + text
                    + "'. Valid statuses: " + statusNames());
        }
    }

    private static long parseId(String text) {
        try {
            return Long.parseLong(text.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Application ID must be a number, got '" + text + "'.");
        }
    }

    private static void requireArgCount(String[] args, int expected, String usage) {
        if (args.length != expected) {
            throw new IllegalArgumentException("Usage: " + usage);
        }
    }

    private static String statusNames() {
        StringBuilder names = new StringBuilder();
        for (JobApplication.Status status : JobApplication.Status.values()) {
            if (names.length() > 0) {
                names.append(", ");
            }
            names.append(status.name());
        }
        return names.toString();
    }

    private static void printApplications(List<JobApplication> applications) {
        if (applications.isEmpty()) {
            System.out.println("No applications found.");
            return;
        }
        for (JobApplication application : applications) {
            System.out.println(application);
            if (!application.getNotes().isEmpty()) {
                System.out.println("    notes: " + application.getNotes());
            }
        }
    }

    private static void printSummary(TrackerService tracker) {
        System.out.println("Total applications: " + tracker.getTotalApplications());
        for (Map.Entry<JobApplication.Status, Integer> entry
                : tracker.getStatusSummary().entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
    }

    private static void printUsage(PrintStream stream) {
        stream.println("Job Application Tracker - commands:");
        stream.println("  help                                                  Show this help");
        stream.println("  demo                                                  Run an in-memory sample workflow");
        stream.println("  list <file.csv>                                       List all applications");
        stream.println("  add <file.csv> <company> <role> <status> [notes]     Add an application (dated today)");
        stream.println("  update-status <file.csv> <id> <status>                Change an application's status");
        stream.println("  search <file.csv> <keyword>                           Search company and role text");
        stream.println("  summary <file.csv>                                    Show counts per status");
        stream.println();
        stream.println("Statuses: " + statusNames());
        stream.println("The CSV file is created on the first 'add'. Missing files load as an empty tracker.");
    }
}
