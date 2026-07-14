package eventregistrationsystem;

import java.io.PrintStream;
import java.time.LocalDate;
import java.util.List;

/** Command-line demonstrations. Business rules remain in the service and domain classes. */
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
                case "registration-demo": registrationDemo(out); break;
                case "capacity-demo": capacityDemo(out); break;
                case "cancellation-demo": cancellationDemo(out); break;
                case "search-demo": searchDemo(out); break;
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
        out.println("Event Registration System commands:");
        out.println("  help, demo, registration-demo, capacity-demo");
        out.println("  cancellation-demo, search-demo, validation-demo");
    }

    private static EventRegistrationSystem sampleSystem() {
        EventRegistrationSystem system = new EventRegistrationSystem();
        system.createEvent(new Event("E1", "Java Workshop", LocalDate.of(2026, 9, 15), "Technology", 2));
        system.createEvent(new Event("E2", "Community Meetup", LocalDate.of(2026, 8, 10), "Community", 2));
        return system;
    }

    private static void demo(PrintStream out) {
        EventRegistrationSystem system = sampleSystem();
        RegistrationSnapshot first = system.registerParticipant("E1", attendee("A1", "Lea"));
        system.registerParticipant("E1", attendee("A2", "Omar"));
        out.println("Created registration: " + first.getId());
        expectFailure(() -> system.registerParticipant("E1", attendee("A3", "Mia")), out);
        system.cancelRegistration("E1", "A1");
        out.println("Technology search: " + system.searchEventsByCategory("tech").size());
        out.println("Final available spots: " + system.findEvent("E1").getAvailableSpots());
    }

    private static void registrationDemo(PrintStream out) {
        EventRegistrationSystem system = sampleSystem();
        Attendee attendee = attendee("A1", "Lea");
        RegistrationSnapshot registration = system.registerParticipant("E1", attendee);
        out.println("Registered: " + registration.getId());
        expectFailure(() -> system.registerParticipant("E1", attendee), out);
        system.registerParticipant("E2", attendee);
        out.println("Same attendee registered for a different event.");
    }

    private static void capacityDemo(PrintStream out) {
        EventRegistrationSystem system = new EventRegistrationSystem();
        system.createEvent(new Event("E1", "Small Workshop", LocalDate.of(2026, 9, 15), "Tech", 1));
        system.registerParticipant("E1", attendee("A1", "Lea"));
        int before = system.findEvent("E1").getRegisteredCount();
        expectFailure(() -> system.registerParticipant("E1", attendee("A2", "Omar")), out);
        out.println("Registered count unchanged: " + (before == system.findEvent("E1").getRegisteredCount()));
    }

    private static void cancellationDemo(PrintStream out) {
        EventRegistrationSystem system = sampleSystem();
        system.registerParticipant("E1", attendee("A1", "Lea"));
        RegistrationSnapshot second = system.registerParticipant("E1", attendee("A2", "Omar"));
        system.cancelRegistration("E1", "A1");
        out.println("After attendee cancellation: " + system.findEvent("E1").getAvailableSpots());
        system.cancelRegistrationByRegistrationId("E1", second.getId());
        out.println("After registration cancellation: " + system.findEvent("E1").getAvailableSpots());
        expectFailure(() -> system.cancelRegistration("E1", "missing"), out);
    }

    private static void searchDemo(PrintStream out) {
        EventRegistrationSystem system = sampleSystem();
        system.createEvent(new Event("E3", "Advanced Java", LocalDate.of(2026, 8, 10), "Technology", 5));
        printResults("Name", system.searchEvents("java"), out);
        printResults("Date", system.searchEventsByDate(LocalDate.of(2026, 8, 10)), out);
        printResults("Category", system.searchEventsByCategory("TECH"), out);
    }

    private static void validationDemo(PrintStream out) {
        expectFailure(() -> new Event("E", " ", LocalDate.now(), "Tech", 1), out);
        expectFailure(() -> new Event("E", "Name", LocalDate.now(), "Tech", 0), out);
        expectFailure(() -> new Event("E", "Name", LocalDate.now(), "Tech", -1), out);
        expectFailure(() -> new Attendee("A", "Name", "bad@domain"), out);
        EventRegistrationSystem system = sampleSystem();
        expectFailure(() -> system.createEvent(new Event("E1", "Other", LocalDate.now(), "Tech", 1)), out);
        Attendee attendee = attendee("A1", "Lea");
        system.registerParticipant("E1", attendee);
        expectFailure(() -> system.registerParticipant("E1", attendee), out);
        expectFailure(() -> system.findEvent("missing"), out);
    }

    private static Attendee attendee(String id, String name) {
        return new Attendee(id, name, id.toLowerCase() + "@example.com");
    }

    private static void printResults(String label, List<EventSnapshot> events, PrintStream out) {
        out.println(label + " results:");
        for (EventSnapshot event : events) {
            out.println("  " + event.getDate() + " - " + event.getName());
        }
    }

    private static void expectFailure(Runnable action, PrintStream out) {
        try {
            action.run();
            out.println("Unexpected success");
        } catch (IllegalArgumentException | IllegalStateException expected) {
            out.println("Expected rejection: " + expected.getMessage());
        }
    }
}
