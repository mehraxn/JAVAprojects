package notificationservice;

import java.io.PrintStream;
import java.util.List;
import java.util.Locale;

/**
 * Command-line entry point. Main only parses arguments, drives the service,
 * and prints results. All queue, retry, and validation logic lives in the
 * service and model classes. The {@link #run(String[], PrintStream, PrintStream)}
 * method contains the whole CLI so tests can call it directly; only
 * {@link #main(String[])} calls System.exit.
 */
public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        System.exit(run(args, System.out, System.err));
    }

    /** Runs one CLI command; returns 0 on success and 1 on any user error. */
    public static int run(String[] args, PrintStream out, PrintStream err) {
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
                case "send":
                    return runSend(args, out);
                case "retry-demo":
                    return runRetryDemo(out);
                case "validation-demo":
                    return runValidationDemo(out);
                case "missing-sender-demo":
                    return runMissingSenderDemo(out);
                default:
                    err.println("Unknown command: " + args[0]);
                    printUsage(err);
                    return 1;
            }
        } catch (IllegalArgumentException exception) {
            err.println("Invalid input: " + exception.getMessage());
            return 1;
        }
    }

    private static int runDemo(PrintStream out) {
        out.println("Demo: EMAIL success, SMS retry, APP success (all local mocks).");
        out.println();

        NotificationService service = new NotificationService();
        service.registerChannel(
                new MockNotificationSender(Notification.ChannelType.EMAIL, 0, out));
        service.registerChannel(
                new MockNotificationSender(Notification.ChannelType.SMS, 1, out));
        service.registerChannel(
                new MockNotificationSender(Notification.ChannelType.APP, 0, out));

        service.enqueue("learner@example.com", "Welcome to the course.",
                Notification.ChannelType.EMAIL);
        Notification sms = service.enqueue("+39 333 111 2222", "Your code is 4821.",
                Notification.ChannelType.SMS);
        service.enqueue("user-42", "You have a new in-app message.",
                Notification.ChannelType.APP);
        out.println("Queued " + service.viewQueue().size() + " notifications (FIFO).");

        while (!service.viewQueue().isEmpty()) {
            Notification result = service.processNext();
            if (result.getStatus() == Notification.Status.FAILED) {
                out.println("Mock failure: " + result.getLastError());
            }
        }

        out.println();
        out.println("The SMS failed once on purpose - retrying it (limit 3 attempts):");
        if (service.retry(sms.getId(), 3)) {
            service.processNext();
        }

        out.println();
        printHistory(out, service);
        out.println();
        out.println("Queue is now empty: " + service.viewQueue().isEmpty());
        out.println("Demo completed successfully. All deliveries were local mock demonstrations.");
        return 0;
    }

    private static int runSend(String[] args, PrintStream out) {
        if (args.length != 4) {
            throw new IllegalArgumentException(
                    "Usage: send <EMAIL|SMS|APP> <recipient> <message>");
        }
        Notification.ChannelType channel = parseChannel(args[1]);

        NotificationService service = new NotificationService();
        service.registerChannel(new MockNotificationSender(channel, 0, out));
        Notification queued = service.enqueue(args[2], args[3], channel);
        out.println("Queued " + queued.getId() + " for " + channel + ".");
        Notification result = service.processNext();
        out.println("Result: " + result);
        return result.getStatus() == Notification.Status.SENT ? 0 : 1;
    }

    private static int runRetryDemo(PrintStream out) {
        out.println("Retry demo, part 1: sender fails once, retry succeeds.");
        NotificationService service = new NotificationService();
        service.registerChannel(
                new MockNotificationSender(Notification.ChannelType.SMS, 1, out));
        Notification first = service.enqueue("+39 333 111 2222", "First message.",
                Notification.ChannelType.SMS);
        out.println("  attempt 1: " + service.processNext());
        out.println("  retry accepted (limit 3): " + service.retry(first.getId(), 3));
        out.println("  attempt 2: " + service.processNext());

        out.println();
        out.println("Retry demo, part 2: sender keeps failing, the retry limit stops us.");
        NotificationService failing = new NotificationService();
        failing.registerChannel(
                new MockNotificationSender(Notification.ChannelType.SMS, 99, out));
        Notification doomed = failing.enqueue("+39 333 111 2222", "Second message.",
                Notification.ChannelType.SMS);
        out.println("  attempt 1: " + failing.processNext());
        out.println("  retry accepted (limit 2): " + failing.retry(doomed.getId(), 2));
        out.println("  attempt 2: " + failing.processNext());
        out.println("  retry accepted (limit 2): " + failing.retry(doomed.getId(), 2)
                + "  <- false: the 2-attempt limit is reached");
        out.println("  final state: " + failing.findNotification(doomed.getId()));
        out.println();
        out.println("Retry demo completed successfully.");
        return 0;
    }

    private static int runValidationDemo(PrintStream out) {
        out.println("Recipient validation examples (rules live in the Notification model):");
        out.println();
        String[][] samples = {
                {"EMAIL", "learner@example.com"},
                {"EMAIL", "bad-email"},
                {"EMAIL", "user@nodomain"},
                {"SMS", "+39 333 111 2222"},
                {"SMS", "3331112222"},
                {"SMS", "abc123"},
                {"SMS", "12"},
                {"APP", "user-123"},
        };
        for (String[] sample : samples) {
            Notification.ChannelType channel = parseChannel(sample[0]);
            try {
                new Notification("check", sample[1], "validation probe", channel);
                out.println("  ACCEPTED  " + channel + "  " + sample[1]);
            } catch (IllegalArgumentException exception) {
                out.println("  REJECTED  " + channel + "  " + sample[1]
                        + "  (" + exception.getMessage() + ")");
            }
        }
        out.println();
        out.println("Blank recipients, blank messages, and null channels are always rejected.");
        out.println("Validation demo completed successfully.");
        return 0;
    }

    private static int runMissingSenderDemo(PrintStream out) {
        out.println("Missing-sender demo: no sender is registered for EMAIL.");
        NotificationService service = new NotificationService();
        Notification queued = service.enqueue("learner@example.com", "Hello.",
                Notification.ChannelType.EMAIL);
        Notification result = service.processNext();
        out.println("  " + result);
        out.println("  error: " + result.getLastError());
        out.println("  ID lookup still works: " + service.findNotification(queued.getId()));
        out.println();
        out.println("The failure is handled cleanly - this demo exits 0 on purpose.");
        return 0;
    }

    private static void printHistory(PrintStream out, NotificationService service) {
        List<Notification> history = service.getHistory();
        out.println("Notification history (" + history.size() + "):");
        for (Notification notification : history) {
            out.println("  " + notification);
        }
    }

    private static Notification.ChannelType parseChannel(String text) {
        try {
            return Notification.ChannelType.valueOf(text.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Unknown channel '" + text
                    + "'. Valid channels: EMAIL, SMS, APP");
        }
    }

    private static void printUsage(PrintStream stream) {
        stream.println("Notification Service (local mock) - commands:");
        stream.println("  help                                   Show this help");
        stream.println("  demo                                   Full workflow: EMAIL/SMS/APP, retry, history");
        stream.println("  send <EMAIL|SMS|APP> <recipient> <message>   Send one notification via a mock sender");
        stream.println("  retry-demo                             Failure then retry success, and retry exhaustion");
        stream.println("  validation-demo                        Accepted/rejected recipient examples");
        stream.println("  missing-sender-demo                    Clean failure when a channel has no sender");
        stream.println();
        stream.println("No real email, SMS, or push provider is ever contacted.");
    }
}
