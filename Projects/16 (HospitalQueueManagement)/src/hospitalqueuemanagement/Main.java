package hospitalqueuemanagement;

import java.io.PrintStream;
import java.time.LocalDateTime;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        System.exit(run(args, System.out, System.err));
    }

    public static int run(String[] args, PrintStream out, PrintStream err) {
        if (args == null || out == null || err == null) {
            throw new IllegalArgumentException("Arguments and output streams must not be null");
        }
        String command = args.length == 0 ? "help" : args[0];
        try {
            switch (command) {
                case "help": printHelp(out); break;
                case "demo": demo(out); break;
                case "queue-demo": queueDemo(out); break;
                case "emergency-demo": emergencyDemo(out); break;
                case "status-demo": statusDemo(out); break;
                case "statistics-demo": statisticsDemo(out); break;
                case "validation-demo": validationDemo(out); break;
                default:
                    err.println("Unknown command: " + command);
                    err.println("Run with 'help' to list commands.");
                    return 2;
            }
            return 0;
        } catch (IllegalArgumentException | IllegalStateException exception) {
            err.println("Command failed: " + exception.getMessage());
            return 1;
        }
    }

    private static void printHelp(PrintStream out) {
        out.println("Hospital Queue Management");
        out.println("Commands: help, demo, queue-demo, emergency-demo, status-demo, statistics-demo, validation-demo");
    }

    private static TriageQueue sampleQueue() {
        TriageQueue queue = new TriageQueue();
        LocalDateTime base = LocalDateTime.of(2026, 7, 4, 9, 0);
        queue.addPatient(new Patient("P001", "Mila", TriageLevel.STANDARD, base));
        queue.addPatient(new Patient("P002", "Leon", TriageLevel.URGENT, base.plusMinutes(5)));
        queue.addPatient(new Patient("P003", "Sara", TriageLevel.NON_URGENT, base.plusMinutes(10)));
        return queue;
    }

    private static void demo(PrintStream out) {
        TriageQueue queue = sampleQueue();
        out.println("Admitted patients: " + queue.totalRecordCount());
        printQueue(queue, out);
        Patient served = queue.serveNextPatient(LocalDateTime.of(2026, 7, 4, 9, 20));
        out.println("Serving: " + served.getName() + " (" + served.getStatus() + ")");
        queue.dischargePatient(served.getId(), LocalDateTime.of(2026, 7, 4, 9, 35));
        out.println("Discharged: " + queue.getPatient(served.getId()).getName());
        out.println("Waiting: " + queue.waitingCount() + ", records: " + queue.totalRecordCount());
    }

    private static void queueDemo(PrintStream out) {
        TriageQueue queue = new TriageQueue();
        LocalDateTime time = LocalDateTime.of(2026, 7, 4, 9, 0);
        queue.addPatient(new Patient("P003", "Standard", TriageLevel.STANDARD, time.minusMinutes(5)));
        queue.addPatient(new Patient("P002", "Urgent B", TriageLevel.URGENT, time));
        queue.addPatient(new Patient("P001", "Urgent A", TriageLevel.URGENT, time));
        queue.addPatient(new Patient("P004", "Emergency", TriageLevel.EMERGENCY, time.plusMinutes(5)));
        out.println("Service order (priority, arrival, ID):");
        printQueue(queue, out);
    }

    private static void emergencyDemo(PrintStream out) {
        TriageQueue queue = sampleQueue();
        out.println("Before override:");
        printQueue(queue, out);
        queue.markAsEmergency("P001");
        out.println("After P001 emergency override:");
        printQueue(queue, out);
        out.println("Waiting count (no duplicate): " + queue.waitingCount());
    }

    private static void statusDemo(PrintStream out) {
        TriageQueue queue = sampleQueue();
        LocalDateTime time = LocalDateTime.of(2026, 7, 4, 9, 20);
        Patient patient = queue.serveNextPatient(time);
        out.println("WAITING -> " + patient.getStatus());
        queue.requeuePatient(patient.getId());
        out.println("IN_TREATMENT -> " + queue.getPatient(patient.getId()).getStatus());
        queue.serveNextPatient(time.plusMinutes(5));
        queue.dischargePatient(patient.getId(), time.plusMinutes(10));
        out.println("IN_TREATMENT -> " + queue.getPatient(patient.getId()).getStatus());
        try { queue.requeuePatient(patient.getId()); }
        catch (IllegalStateException expected) { out.println("Terminal rule: " + expected.getMessage()); }
    }

    private static void statisticsDemo(PrintStream out) {
        TriageQueue queue = sampleQueue();
        LocalDateTime now = LocalDateTime.of(2026, 7, 4, 10, 0);
        out.println("Waiting count: " + queue.waitingCount());
        out.println("By triage: " + queue.countWaitingByTriageLevel());
        out.println("Average current wait minutes: " + queue.calculateAverageCurrentWaitMinutes(now));
        out.println("Longest waiting: " + queue.longestWaitingPatient(now).orElseThrow().getName());
    }

    private static void validationDemo(PrintStream out) {
        expectFailure(out, "blank patient ID", () -> new Patient(" ", "Name", TriageLevel.STANDARD, LocalDateTime.now()));
        expectFailure(out, "blank name", () -> new Patient("P", " ", TriageLevel.STANDARD, LocalDateTime.now()));
        expectFailure(out, "null triage", () -> new Patient("P", "Name", null, LocalDateTime.now()));
        TriageQueue queue = sampleQueue();
        expectFailure(out, "duplicate ID", () -> queue.addPatient(new Patient("P001", "Other", TriageLevel.URGENT, LocalDateTime.now())));
        expectFailure(out, "empty service", () -> new TriageQueue().serveNextPatient());
        expectFailure(out, "invalid time", () -> queue.calculateAverageCurrentWaitMinutes(LocalDateTime.of(2020, 1, 1, 0, 0)));
    }

    private static void expectFailure(PrintStream out, String label, Runnable action) {
        try { action.run(); out.println("Unexpected success: " + label); }
        catch (IllegalArgumentException | IllegalStateException expected) {
            out.println("Rejected " + label + ": " + expected.getMessage());
        }
    }

    private static void printQueue(TriageQueue queue, PrintStream out) {
        for (Patient patient : queue.viewQueue()) {
            out.println("- " + patient.getId() + " " + patient.getName() + " [" + patient.getTriageLevel() + "]");
        }
    }
}
