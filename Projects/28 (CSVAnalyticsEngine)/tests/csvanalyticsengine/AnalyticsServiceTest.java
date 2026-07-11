package csvanalyticsengine;

import static csvanalyticsengine.TestSupport.assertEquals;
import static csvanalyticsengine.TestSupport.assertThrows;
import static csvanalyticsengine.TestSupport.test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

final class AnalyticsServiceTest {
    private static final AnalyticsService SERVICE = new AnalyticsService();

    private AnalyticsServiceTest() {
    }

    /** Builds the sample data set used by most tests (mirrors examples/sales.csv). */
    private static DataSet salesData() {
        DataSet dataSet = new DataSet(List.of("id", "category", "product", "amount"));
        String[][] rows = {
                {"1", "Food", "Apples, red", "12.50"},
                {"2", "Books", "Clean Code", "35.00"},
                {"3", "Food", "Bread", "4.20"},
                {"4", "Electronics", "Keyboard", "invalid"},
                {"5", "Food", "Milk", ""},
                {"6", "Books", "Refactoring", "-5.75"},
        };
        for (String[] values : rows) {
            DataRow row = new DataRow();
            row.put("id", values[0]);
            row.put("category", values[1]);
            row.put("product", values[2]);
            row.put("amount", values[3]);
            dataSet.addRow(row);
        }
        return dataSet;
    }

    static void run() {
        test("filter matches exact values only", () -> {
            List<DataRow> food = SERVICE.filter(salesData(), "category", "Food");
            assertEquals(3, food.size(), "Food rows");
            assertEquals(0, SERVICE.filter(salesData(), "category", "food").size(),
                    "filter values are case-sensitive");
            assertEquals(0, SERVICE.filter(salesData(), "category", "Toys").size(),
                    "no match");
        });

        test("filter resolves column names case-insensitively", () -> {
            assertEquals(3, SERVICE.filter(salesData(), "CATEGORY", "Food").size(),
                    "column case-insensitive");
        });

        test("filter can match empty values", () -> {
            assertEquals(1, SERVICE.filter(salesData(), "amount", "").size(),
                    "empty value match");
        });

        test("filter and stats fail cleanly for unknown columns", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> SERVICE.filter(salesData(), "price", "x"), "filter unknown column");
            assertThrows(IllegalArgumentException.class,
                    () -> SERVICE.calculateStatistics(salesData(), "price"),
                    "stats unknown column");
            assertThrows(IllegalArgumentException.class,
                    () -> SERVICE.countBy(salesData(), "price"), "group unknown column");
            assertThrows(IllegalArgumentException.class,
                    () -> SERVICE.filter(null, "category", "Food"), "null data set");
        });

        test("countBy counts every group in first-seen order", () -> {
            Map<String, Integer> counts = SERVICE.countBy(salesData(), "category");
            assertEquals(3, counts.size(), "group count");
            assertEquals(3, counts.get("Food"), "Food count");
            assertEquals(2, counts.get("Books"), "Books count");
            assertEquals(1, counts.get("Electronics"), "Electronics count");
            assertEquals("Food", counts.keySet().iterator().next(), "first-seen order");
        });

        test("groupBy places empty values in the (missing) group", () -> {
            Map<String, List<DataRow>> groups = SERVICE.groupBy(salesData(), "amount");
            assertEquals(1, groups.get("(missing)").size(), "missing group size");
            assertEquals("Milk", groups.get("(missing)").get(0).get("product"),
                    "missing group row");
        });

        test("statistics count valid, missing, and invalid values", () -> {
            NumericStatistics statistics = SERVICE.calculateStatistics(salesData(), "amount");
            assertEquals(4, statistics.getValidValueCount(), "valid count");
            assertEquals(1, statistics.getMissingValueCount(), "missing count");
            assertEquals(1, statistics.getInvalidValueCount(), "invalid count");
        });

        test("statistics compute min, max, sum, and average with decimals and negatives", () -> {
            NumericStatistics statistics = SERVICE.calculateStatistics(salesData(), "amount");
            assertEquals(new BigDecimal("-5.75"), statistics.getMinimum(), "min (negative)");
            assertEquals(new BigDecimal("35.00"), statistics.getMaximum(), "max");
            assertEquals(new BigDecimal("45.95"), statistics.getSum(), "sum");
            assertEquals(0, new BigDecimal("11.4875").compareTo(statistics.getAverage()),
                    "average 45.95 / 4");
        });

        test("statistics for a fully non-numeric column report only invalid values", () -> {
            NumericStatistics statistics = SERVICE.calculateStatistics(salesData(), "product");
            assertEquals(0, statistics.getValidValueCount(), "no valid numbers");
            assertEquals(6, statistics.getInvalidValueCount(), "all invalid");
            assertEquals(null, statistics.getMinimum(), "min n/a");
            assertEquals(null, statistics.getSum(), "sum n/a");
            assertEquals(null, statistics.getAverage(), "average n/a");
        });

        test("statistics on an empty data set report zero counts", () -> {
            DataSet empty = new DataSet(List.of("amount"));
            NumericStatistics statistics = SERVICE.calculateStatistics(empty, "amount");
            assertEquals(0, statistics.getValidValueCount(), "empty valid count");
            assertEquals(0, statistics.getMissingValueCount(), "empty missing count");
        });

        test("sum skips missing and invalid values", () -> {
            assertEquals(new BigDecimal("45.95"), SERVICE.sum(salesData(), "amount"),
                    "sum ignores missing/invalid");
            assertEquals(BigDecimal.ZERO, SERVICE.sum(salesData(), "product"),
                    "all-invalid column sums to zero");
        });

        test("average matches calculateStatistics", () -> {
            assertEquals(SERVICE.calculateStatistics(salesData(), "amount").getAverage(),
                    SERVICE.average(salesData(), "amount"), "average delegation");
        });
    }
}
