package trainticketreservationsystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static trainticketreservationsystem.TestSupport.assertEquals;
import static trainticketreservationsystem.TestSupport.assertFalse;
import static trainticketreservationsystem.TestSupport.assertNotNull;
import static trainticketreservationsystem.TestSupport.assertThrows;
import static trainticketreservationsystem.TestSupport.assertTrue;

final class TrainTest {

    private TrainTest() {
    }

    private static Route berlinMunich() {
        return new Route("R-BM", "Berlin", "Munich");
    }

    private static List<Seat> seats(int... numbers) {
        List<Seat> list = new ArrayList<>();
        for (int number : numbers) {
            list.add(new Seat(number));
        }
        return list;
    }

    static void register(TestRunner runner) {
        runner.test("Train: valid creation stores id/route/seats", () -> {
            Train train = new Train("ICE-1", berlinMunich(), seats(1, 2, 3));
            assertEquals("ICE-1", train.getId(), "id stored");
            assertEquals("R-BM", train.getRoute().getId(), "route stored");
            assertEquals(3, train.getTotalSeatCount(), "seat count stored");
        });

        runner.test("Train: null/blank id rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new Train(null, berlinMunich()), "null id");
            assertThrows(IllegalArgumentException.class,
                    () -> new Train("  ", berlinMunich()), "blank id");
        });

        runner.test("Train: null route rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new Train("ICE-1", null), "null route"));

        runner.test("Train: null seat list rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new Train("ICE-1", berlinMunich(), null), "null list"));

        runner.test("Train: empty seat list rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new Train("ICE-1", berlinMunich(), new ArrayList<>()), "empty list"));

        runner.test("Train: null seat in list rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new Train("ICE-1", berlinMunich(), Arrays.asList(new Seat(1), null)),
                        "null seat"));

        runner.test("Train: duplicate seat number rejected", () -> {
            Train train = new Train("ICE-1", berlinMunich(), seats(1, 2));
            assertThrows(IllegalArgumentException.class,
                    () -> train.addSeat(new Seat(2)), "duplicate seat");
        });

        runner.test("Train: findSeat returns live seat, unknown rejected", () -> {
            Train train = new Train("ICE-1", berlinMunich(), seats(1, 2));
            assertNotNull(train.findSeat(2), "found seat");
            assertThrows(IllegalArgumentException.class,
                    () -> train.findSeat(99), "unknown seat");
        });

        runner.test("Train: available count and full detection", () -> {
            Train train = new Train("ICE-1", berlinMunich(), seats(1, 2));
            assertEquals(2, train.getAvailableSeatCount(), "all free");
            assertFalse(train.isFull(), "not full");
            train.findSeat(1).reserve();
            train.findSeat(2).reserve();
            assertEquals(0, train.getAvailableSeatCount(), "none free");
            assertTrue(train.isFull(), "full");
        });

        runner.test("Train: findFirstAvailableSeat is deterministic in seat order", () -> {
            Train train = new Train("ICE-1", berlinMunich(), seats(1, 2, 3));
            train.findSeat(1).reserve();
            assertEquals(2, train.findFirstAvailableSeat().getNumber(), "first free is 2");
        });

        runner.test("Train: seats() view is unmodifiable", () -> {
            Train train = new Train("ICE-1", berlinMunich(), seats(1, 2));
            assertThrows(UnsupportedOperationException.class,
                    () -> train.seats().add(new Seat(3)), "cannot add to view");
        });
    }
}
