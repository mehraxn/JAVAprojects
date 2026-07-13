package trainticketreservationsystem;

import java.io.PrintStream;
import java.util.List;

/**
 * Command-line entry point and demo driver for the train ticket reservation
 * system.
 *
 * <p>All real work happens in {@link #run(String[], PrintStream, PrintStream)},
 * which returns an exit code and never calls {@link System#exit}. Only
 * {@link #main(String[])} exits the JVM, so the CLI can be tested in-process.
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        int exitCode = run(args, System.out, System.err);
        System.exit(exitCode);
    }

    /**
     * Runs one CLI command.
     *
     * @return {@code 0} for a recognised command (including {@code validation-demo},
     *         whose failures are intentional), non-zero for an unknown command.
     */
    public static int run(String[] args, PrintStream out, PrintStream err) {
        String command = (args == null || args.length == 0) ? "help" : args[0];
        switch (command) {
            case "help":
            case "--help":
            case "-h":
                printHelp(out);
                return 0;
            case "demo":
                runDemo(out);
                return 0;
            case "reservation-demo":
                runReservationDemo(out);
                return 0;
            case "cancellation-demo":
                runCancellationDemo(out);
                return 0;
            case "search-demo":
                runSearchDemo(out);
                return 0;
            case "full-train-demo":
                runFullTrainDemo(out);
                return 0;
            case "validation-demo":
                runValidationDemo(out);
                return 0;
            default:
                err.println("Unknown command: " + command);
                err.println("Run 'help' to see available commands.");
                return 2;
        }
    }

    private static void printHelp(PrintStream out) {
        out.println("Train Ticket Reservation System");
        out.println();
        out.println("Usage: java -cp out trainticketreservationsystem.Main <command>");
        out.println();
        out.println("Commands:");
        out.println("  help               Show this help text.");
        out.println("  demo               End-to-end walkthrough of the main features.");
        out.println("  reservation-demo   Specific + automatic seat reservation and double-booking rejection.");
        out.println("  cancellation-demo  Cancel a reservation, release the seat, and re-reserve it.");
        out.println("  search-demo        Case-insensitive, direction-sensitive route search.");
        out.println("  full-train-demo    Full-train rejection leaves state unchanged.");
        out.println("  validation-demo    Intentional validation failures, handled cleanly.");
    }

    // ------------------------------------------------------------------ demos

    private static ReservationSystem sampleSystem() {
        ReservationSystem system = new ReservationSystem();
        system.addRoute(new Route("R-BM", "Berlin", "Munich"));
        system.addRoute(new Route("R-PL", "Paris", "Lyon"));
        Train ice = new Train("ICE-101", new Route("R-BM", "Berlin", "Munich"));
        for (int seatNumber = 1; seatNumber <= 5; seatNumber++) {
            ice.addSeat(new Seat(seatNumber));
        }
        system.addTrain(ice);
        Train tgv = new Train("TGV-200", new Route("R-PL", "Paris", "Lyon"));
        for (int seatNumber = 1; seatNumber <= 3; seatNumber++) {
            tgv.addSeat(new Seat(seatNumber));
        }
        system.addTrain(tgv);
        return system;
    }

    private static void runDemo(PrintStream out) {
        out.println("== Train Ticket Reservation Demo ==");
        ReservationSystem system = sampleSystem();

        out.println();
        out.println("Registered routes:");
        for (RouteSnapshot route : system.listRoutes()) {
            out.println("  " + route);
        }
        out.println("Registered trains:");
        for (TrainSnapshot train : system.listTrains()) {
            out.println("  " + train);
        }

        out.println();
        ReservationSnapshot specific = system.reserveSeat("ICE-101", 3, "Sofia");
        out.println("Reserved specific seat: " + specific);
        ReservationSnapshot auto = system.reserveFirstAvailableSeat("ICE-101", "Marco");
        out.println("Reserved first available seat: " + auto);
        out.println("Available seats on ICE-101: " + system.getAvailableSeats("ICE-101").size());

        out.println();
        out.println("Berlin -> Munich trains: " + system.searchByRoute("berlin", "MUNICH").size());

        out.println();
        system.cancelReservation(specific.getReservationId());
        out.println("Cancelled " + specific.getReservationId()
                + "; available seats on ICE-101: " + system.getAvailableSeats("ICE-101").size());

        out.println();
        out.println("Reservation history for ICE-101:");
        for (ReservationSnapshot reservation : system.listReservationsForTrain("ICE-101")) {
            out.println("  " + reservation);
        }
    }

    private static void runReservationDemo(PrintStream out) {
        out.println("== Reservation Demo ==");
        ReservationSystem system = sampleSystem();

        ReservationSnapshot specific = system.reserveSeat("ICE-101", 2, "Sofia");
        out.println("Specific reservation: " + specific);

        ReservationSnapshot auto = system.reserveFirstAvailableSeat("ICE-101", "Marco");
        out.println("Automatic reservation took seat " + auto.getSeatNumber() + ": " + auto);

        out.println();
        out.println("Attempting to double-book seat 2...");
        try {
            system.reserveSeat("ICE-101", 2, "Lena");
            out.println("  ERROR: double booking was NOT rejected");
        } catch (IllegalStateException expected) {
            out.println("  Rejected as expected: " + expected.getMessage());
        }
        out.println("Available seats on ICE-101: " + system.getAvailableSeats("ICE-101").size());
    }

    private static void runCancellationDemo(PrintStream out) {
        out.println("== Cancellation Demo ==");
        ReservationSystem system = sampleSystem();

        ReservationSnapshot reservation = system.reserveSeat("ICE-101", 4, "Sofia");
        out.println("Reserved: " + reservation);
        out.println("Available seats: " + system.getAvailableSeats("ICE-101").size());

        system.cancelReservation(reservation.getReservationId());
        out.println("Cancelled " + reservation.getReservationId());
        out.println("Seat 4 available again: " + isSeatAvailable(system, "ICE-101", 4));

        ReservationSnapshot reReserved = system.reserveSeat("ICE-101", 4, "Marco");
        out.println("Re-reserved seat 4: " + reReserved);

        out.println();
        out.println("Cancelling an already-cancelled reservation...");
        try {
            system.cancelReservation(reservation.getReservationId());
            out.println("  ERROR: double cancellation was NOT rejected");
        } catch (IllegalStateException expected) {
            out.println("  Rejected as expected: " + expected.getMessage());
        }
        out.println("Cancelling a missing reservation...");
        try {
            system.cancelReservation("R9999");
            out.println("  ERROR: missing cancellation was NOT rejected");
        } catch (IllegalArgumentException expected) {
            out.println("  Rejected as expected: " + expected.getMessage());
        }
    }

    private static void runSearchDemo(PrintStream out) {
        out.println("== Search Demo ==");
        ReservationSystem system = sampleSystem();

        out.println("Search 'berlin' -> 'munich' (case-insensitive): "
                + describeSearch(system.searchByRoute("berlin", "munich")));
        out.println("Search 'BERLIN' -> 'MUNICH' (case-insensitive): "
                + describeSearch(system.searchByRoute("BERLIN", "MUNICH")));
        out.println("Search reverse 'Munich' -> 'Berlin' (direction-sensitive): "
                + describeSearch(system.searchByRoute("Munich", "Berlin")));
        out.println("Search unknown 'Rome' -> 'Naples': "
                + describeSearch(system.searchByRoute("Rome", "Naples")));
    }

    private static void runFullTrainDemo(PrintStream out) {
        out.println("== Full Train Demo ==");
        ReservationSystem system = sampleSystem();

        int total = system.getTrain("TGV-200").getTotalSeats();
        out.println("Filling TGV-200 (" + total + " seats)...");
        for (int i = 0; i < total; i++) {
            ReservationSnapshot reservation =
                    system.reserveFirstAvailableSeat("TGV-200", "Passenger" + (i + 1));
            out.println("  " + reservation);
        }
        out.println("Available seats now: " + system.getAvailableSeats("TGV-200").size());

        out.println();
        out.println("Attempting one more reservation on a full train...");
        try {
            system.reserveFirstAvailableSeat("TGV-200", "Latecomer");
            out.println("  ERROR: full-train reservation was NOT rejected");
        } catch (IllegalStateException expected) {
            out.println("  Rejected as expected: " + expected.getMessage());
        }
        out.println("State unchanged; available seats still: "
                + system.getAvailableSeats("TGV-200").size());
    }

    private static void runValidationDemo(PrintStream out) {
        out.println("== Validation Demo (failures below are intentional) ==");
        ReservationSystem system = sampleSystem();

        expectFailure(out, "blank route origin", () -> new Route("R-X", "  ", "Cologne"));
        expectFailure(out, "duplicate route pair",
                () -> system.addRoute(new Route("R-DUP", "Berlin", "Munich")));
        expectFailure(out, "duplicate train ID", () -> {
            Train dup = new Train("ICE-101", new Route("R-BM", "Berlin", "Munich"));
            dup.addSeat(new Seat(1));
            system.addTrain(dup);
        });
        expectFailure(out, "duplicate seat number", () -> {
            Train train = new Train("ICE-999", new Route("R-BM", "Berlin", "Munich"));
            train.addSeat(new Seat(1));
            train.addSeat(new Seat(1));
        });
        expectFailure(out, "blank passenger name",
                () -> system.reserveSeat("ICE-101", 1, "   "));
        expectFailure(out, "unknown train",
                () -> system.reserveFirstAvailableSeat("NO-SUCH", "Sofia"));
        expectFailure(out, "unknown seat",
                () -> system.reserveSeat("ICE-101", 99, "Sofia"));

        out.println();
        out.println("All validation failures were handled cleanly.");
    }

    // --------------------------------------------------------------- helpers

    private static boolean isSeatAvailable(ReservationSystem system, String trainId, int seatNumber) {
        for (SeatSnapshot seat : system.getAvailableSeats(trainId)) {
            if (seat.getNumber() == seatNumber) {
                return true;
            }
        }
        return false;
    }

    private static String describeSearch(List<TrainSnapshot> results) {
        if (results.isEmpty()) {
            return "no matches";
        }
        StringBuilder builder = new StringBuilder(results.size() + " match(es): ");
        for (int i = 0; i < results.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(results.get(i).getTrainId());
        }
        return builder.toString();
    }

    private static void expectFailure(PrintStream out, String label, Runnable action) {
        try {
            action.run();
            out.println("  [" + label + "] ERROR: expected a failure but none occurred");
        } catch (IllegalArgumentException | IllegalStateException expected) {
            out.println("  [" + label + "] rejected: " + expected.getMessage());
        }
    }
}
