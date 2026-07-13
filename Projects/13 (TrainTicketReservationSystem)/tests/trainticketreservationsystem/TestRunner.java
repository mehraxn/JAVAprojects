package trainticketreservationsystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Dependency-free test runner. Each test class contributes named cases via
 * {@link #test(String, Runnable)}; the runner prints PASS/FAIL per case and a
 * summary, then exits 0 only when every case and check passed.
 */
public final class TestRunner {
    private final List<String> failures = new ArrayList<>();
    private int passed;
    private int total;

    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        RouteTest.register(runner);
        SeatTest.register(runner);
        TrainTest.register(runner);
        ReservationTest.register(runner);
        ReservationStatusTest.register(runner);
        ReservationSystemTest.register(runner);
        SnapshotTest.register(runner);
        MainTest.register(runner);
        System.exit(runner.summarize());
    }

    /** Runs a single named test case, capturing pass/fail. */
    public void test(String name, Runnable body) {
        total++;
        try {
            body.run();
            passed++;
            System.out.println("PASS  " + name);
        } catch (Throwable error) {
            failures.add(name + " -> " + error.getMessage());
            System.out.println("FAIL  " + name + " -> " + error.getMessage());
        }
    }

    private int summarize() {
        System.out.println();
        System.out.println("---------------------------------------------");
        System.out.println("Test cases : " + passed + "/" + total + " passed");
        System.out.println("Assertions : " + TestSupport.checkCount() + " checks executed");
        if (failures.isEmpty()) {
            System.out.println("Result     : ALL TESTS PASSED");
            return 0;
        }
        System.out.println("Result     : " + failures.size() + " FAILURE(S)");
        for (String failure : failures) {
            System.out.println("  - " + failure);
        }
        return 1;
    }
}
