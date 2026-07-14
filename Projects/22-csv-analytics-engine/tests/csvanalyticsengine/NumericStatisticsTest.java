package csvanalyticsengine;

import static csvanalyticsengine.TestSupport.assertEquals;
import static csvanalyticsengine.TestSupport.assertThrows;
import static csvanalyticsengine.TestSupport.assertTrue;
import static csvanalyticsengine.TestSupport.test;

import java.math.BigDecimal;

final class NumericStatisticsTest {

    private NumericStatisticsTest() {
    }

    private static BigDecimal number(String value) {
        return new BigDecimal(value);
    }

    static void run() {
        test("valid statistics expose all values through getters", () -> {
            NumericStatistics statistics = new NumericStatistics("amount", 3, 1, 2,
                    number("-5"), number("35"), number("41.7"), number("13.9"));
            assertEquals("amount", statistics.getColumn(), "column");
            assertEquals(3, statistics.getValidValueCount(), "valid count");
            assertEquals(1, statistics.getMissingValueCount(), "missing count");
            assertEquals(2, statistics.getInvalidValueCount(), "invalid count");
            assertEquals(number("-5"), statistics.getMinimum(), "min");
            assertEquals(number("35"), statistics.getMaximum(), "max");
            assertEquals(number("41.7"), statistics.getSum(), "sum");
            assertEquals(number("13.9"), statistics.getAverage(), "average");
        });

        test("empty statistics allow null min/max/sum/average only", () -> {
            NumericStatistics empty = new NumericStatistics("amount", 0, 2, 1,
                    null, null, null, null);
            assertEquals(null, empty.getMinimum(), "empty min");
            assertThrows(IllegalArgumentException.class,
                    () -> new NumericStatistics("amount", 0, 0, 0,
                            number("1"), null, null, null),
                    "empty statistics with a min value");
        });

        test("nonempty statistics require min, max, sum, and average", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new NumericStatistics("amount", 2, 0, 0,
                            number("1"), number("2"), null, number("1.5")),
                    "missing sum");
        });

        test("invalid constructions are rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new NumericStatistics("  ", 0, 0, 0, null, null, null, null),
                    "blank column");
            assertThrows(IllegalArgumentException.class,
                    () -> new NumericStatistics("amount", -1, 0, 0, null, null, null, null),
                    "negative count");
            assertThrows(IllegalArgumentException.class,
                    () -> new NumericStatistics("amount", 2, 0, 0,
                            number("5"), number("1"), number("6"), number("3")),
                    "min greater than max");
            assertThrows(IllegalArgumentException.class,
                    () -> new NumericStatistics("amount", 2, 0, 0,
                            number("1"), number("5"), number("6"), number("9")),
                    "average outside min/max range");
        });

        test("toString reports every field", () -> {
            String text = new NumericStatistics("amount", 3, 1, 2,
                    number("-5"), number("35"), number("41.7"), number("13.9")).toString();
            assertTrue(text.contains("valid=3"), "toString valid");
            assertTrue(text.contains("missing=1"), "toString missing");
            assertTrue(text.contains("invalid=2"), "toString invalid");
            assertTrue(text.contains("sum=41.7"), "toString sum");
            assertTrue(text.contains("average=13.9"), "toString average");
            String empty = new NumericStatistics("amount", 0, 0, 0,
                    null, null, null, null).toString();
            assertTrue(empty.contains("n/a"), "empty values shown as n/a");
        });
    }
}
