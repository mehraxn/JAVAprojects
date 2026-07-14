package productinventorymanager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static productinventorymanager.TestSupport.assertBigDecimalEquals;
import static productinventorymanager.TestSupport.assertEquals;
import static productinventorymanager.TestSupport.assertFalse;
import static productinventorymanager.TestSupport.assertThrows;
import static productinventorymanager.TestSupport.assertTrue;

final class InventoryTest {

    private InventoryTest() {
    }

    private static Product product(String sku, String name, String category, String price,
                                   int qty, int threshold) {
        return new Product(sku, name, category, new BigDecimal(price), qty, threshold);
    }

    /** Sample inventory used across many tests. */
    private static Inventory sample() {
        Inventory inventory = new Inventory();
        inventory.addProduct(product("P100", "Keyboard", "Electronics", "49.90", 8, 5));
        inventory.addProduct(product("P200", "Mouse", "Electronics", "24.50", 4, 5));
        inventory.addProduct(product("P300", "Monitor", "Electronics", "189.00", 2, 3));
        inventory.addProduct(product("P400", "Desk Lamp", "Home", "15.75", 0, 2));
        inventory.addProduct(product("P500", "Notebook", "Stationery", "3.20", 40, 10));
        return inventory;
    }

    private static List<String> skusOf(List<ProductSnapshot> products) {
        List<String> skus = new java.util.ArrayList<>();
        for (ProductSnapshot product : products) {
            skus.add(product.getSku());
        }
        return skus;
    }

    static void register(TestRunner runner) {
        registerManagement(runner);
        registerStock(runner);
        registerSearch(runner);
        registerSort(runner);
        registerReports(runner);
        registerDefensive(runner);
    }

    private static void registerManagement(TestRunner runner) {
        runner.test("Inventory: add product then list works", () -> {
            Inventory inventory = new Inventory();
            inventory.addProduct(product("P1", "A", "C", "1.00", 1, 1));
            assertEquals(1, inventory.listProducts().size(), "one product");
            assertEquals("P1", inventory.listProducts().get(0).getSku(), "sku present");
        });

        runner.test("Inventory: null product rejected", () -> {
            Inventory inventory = new Inventory();
            assertThrows(IllegalArgumentException.class,
                    () -> inventory.addProduct(null), "null product");
        });

        runner.test("Inventory: duplicate SKU rejected", () -> {
            Inventory inventory = sample();
            assertThrows(IllegalArgumentException.class,
                    () -> inventory.addProduct(product("P100", "Clone", "C", "1.00", 1, 1)),
                    "duplicate sku");
        });

        runner.test("Inventory: duplicate SKU detection is case-insensitive", () -> {
            Inventory inventory = sample();
            assertThrows(IllegalArgumentException.class,
                    () -> inventory.addProduct(product("p100", "Clone", "C", "1.00", 1, 1)),
                    "case-insensitive duplicate");
        });

        runner.test("Inventory: find by SKU works and is case-insensitive", () -> {
            Inventory inventory = sample();
            Optional<ProductSnapshot> found = inventory.findProductBySku("p100");
            assertTrue(found.isPresent(), "found lowercase");
            assertEquals("Keyboard", found.get().getName(), "correct product");
        });

        runner.test("Inventory: find missing SKU returns empty Optional", () -> {
            Inventory inventory = sample();
            assertFalse(inventory.findProductBySku("NOPE").isPresent(), "empty optional");
        });

        runner.test("Inventory: remove product works and is case-insensitive", () -> {
            Inventory inventory = sample();
            assertTrue(inventory.removeProduct("p300"), "removed");
            assertFalse(inventory.findProductBySku("P300").isPresent(), "gone");
        });

        runner.test("Inventory: remove missing product returns false", () -> {
            Inventory inventory = sample();
            assertFalse(inventory.removeProduct("NOPE"), "nothing removed");
        });

        runner.test("Inventory: blank SKU rejected on lookup", () -> {
            Inventory inventory = sample();
            assertThrows(IllegalArgumentException.class,
                    () -> inventory.findProductBySku("  "), "blank sku");
        });
    }

    private static void registerStock(TestRunner runner) {
        runner.test("Inventory: increase stock works", () -> {
            Inventory inventory = sample();
            inventory.adjustStock("P200", 6);
            assertEquals(10, inventory.findProductBySku("P200").get().getQuantity(), "4+6=10");
        });

        runner.test("Inventory: decrease stock works", () -> {
            Inventory inventory = sample();
            inventory.adjustStock("P100", -3);
            assertEquals(5, inventory.findProductBySku("P100").get().getQuantity(), "8-3=5");
        });

        runner.test("Inventory: setStock sets absolute quantity", () -> {
            Inventory inventory = sample();
            inventory.setStock("P200", 25);
            assertEquals(25, inventory.findProductBySku("P200").get().getQuantity(), "set to 25");
        });

        runner.test("Inventory: stock underflow rejected and leaves quantity unchanged", () -> {
            Inventory inventory = sample();
            assertThrows(IllegalArgumentException.class,
                    () -> inventory.adjustStock("P200", -100), "underflow");
            assertEquals(4, inventory.findProductBySku("P200").get().getQuantity(), "unchanged");
        });

        runner.test("Inventory: stock overflow rejected and leaves quantity unchanged", () -> {
            Inventory inventory = sample();
            assertThrows(IllegalArgumentException.class,
                    () -> inventory.adjustStock("P200", Integer.MAX_VALUE), "overflow");
            assertEquals(4, inventory.findProductBySku("P200").get().getQuantity(), "unchanged");
        });

        runner.test("Inventory: setStock negative rejected and leaves quantity unchanged", () -> {
            Inventory inventory = sample();
            assertThrows(IllegalArgumentException.class,
                    () -> inventory.setStock("P200", -1), "negative set");
            assertEquals(4, inventory.findProductBySku("P200").get().getQuantity(), "unchanged");
        });

        runner.test("Inventory: adjust missing product fails cleanly", () -> {
            Inventory inventory = sample();
            assertThrows(IllegalArgumentException.class,
                    () -> inventory.adjustStock("NOPE", 1), "missing product");
        });

        runner.test("Inventory: adjust by zero is a no-op", () -> {
            Inventory inventory = sample();
            inventory.adjustStock("P200", 0);
            assertEquals(4, inventory.findProductBySku("P200").get().getQuantity(), "unchanged");
        });
    }

    private static void registerSearch(TestRunner runner) {
        runner.test("Inventory: search by SKU works", () -> {
            Inventory inventory = sample();
            assertEquals(List.of("P300"), skusOf(inventory.searchBySku("p3")), "sku match");
        });

        runner.test("Inventory: search by name is case-insensitive", () -> {
            Inventory inventory = sample();
            assertEquals(List.of("P200"), skusOf(inventory.searchByName("MOUSE")), "name match");
        });

        runner.test("Inventory: search results sorted by name deterministically", () -> {
            Inventory inventory = sample();
            // Names containing 'o': Keyboard, Monitor, Mouse, Notebook -> alphabetical
            assertEquals(List.of("P100", "P300", "P200", "P500"),
                    skusOf(inventory.searchByName("o")), "sorted by name");
        });

        runner.test("Inventory: missing search returns empty list", () -> {
            Inventory inventory = sample();
            assertTrue(inventory.searchBySku("zzz").isEmpty(), "empty");
        });

        runner.test("Inventory: null search text rejected", () -> {
            Inventory inventory = sample();
            assertThrows(IllegalArgumentException.class,
                    () -> inventory.searchByName(null), "null query");
        });
    }

    private static void registerSort(TestRunner runner) {
        runner.test("Inventory: sort by name ascending", () -> {
            Inventory inventory = sample();
            assertEquals(List.of("P400", "P100", "P300", "P200", "P500"),
                    skusOf(inventory.sortProducts(ProductSortField.NAME)), "name asc");
        });

        runner.test("Inventory: sort by price ascending", () -> {
            Inventory inventory = sample();
            assertEquals(List.of("P500", "P400", "P200", "P100", "P300"),
                    skusOf(inventory.sortProducts(ProductSortField.PRICE)), "price asc");
        });

        runner.test("Inventory: sort by price descending", () -> {
            Inventory inventory = sample();
            assertEquals(List.of("P300", "P100", "P200", "P400", "P500"),
                    skusOf(inventory.sortProducts(ProductSortField.PRICE, false)), "price desc");
        });

        runner.test("Inventory: sort by quantity ascending", () -> {
            Inventory inventory = sample();
            assertEquals(List.of("P400", "P300", "P200", "P100", "P500"),
                    skusOf(inventory.sortProducts(ProductSortField.QUANTITY)), "qty asc");
        });

        runner.test("Inventory: null sort field rejected", () -> {
            Inventory inventory = sample();
            assertThrows(IllegalArgumentException.class,
                    () -> inventory.sortProducts(null), "null field");
        });
    }

    private static void registerReports(TestRunner runner) {
        runner.test("Inventory: low-stock report uses per-product threshold", () -> {
            Inventory inventory = sample();
            // Low: P400(0<=2), P300(2<=3), P200(4<=5). Not: P100(8<=5 no), P500(40<=10 no)
            assertEquals(List.of("P400", "P300", "P200"),
                    skusOf(inventory.findLowStockProducts()), "low stock set");
        });

        runner.test("Inventory: out-of-stock report", () -> {
            Inventory inventory = sample();
            assertEquals(List.of("P400"), skusOf(inventory.findOutOfStockProducts()), "out of stock");
        });

        runner.test("Inventory: total inventory value with decimals", () -> {
            Inventory inventory = sample();
            // 399.20 + 98.00 + 378.00 + 0.00 + 128.00 = 1003.20
            assertBigDecimalEquals(new BigDecimal("1003.20"),
                    inventory.calculateTotalInventoryValue(), "total value");
        });

        runner.test("Inventory: empty inventory total is zero", () -> {
            Inventory inventory = new Inventory();
            assertBigDecimalEquals(BigDecimal.ZERO,
                    inventory.calculateTotalInventoryValue(), "zero total");
        });

        runner.test("Inventory: value by category", () -> {
            Inventory inventory = sample();
            Map<String, BigDecimal> byCategory = inventory.calculateInventoryValueByCategory();
            assertBigDecimalEquals(new BigDecimal("875.20"),
                    byCategory.get("Electronics"), "electronics total");
            assertBigDecimalEquals(new BigDecimal("0.00"), byCategory.get("Home"), "home total");
            assertBigDecimalEquals(new BigDecimal("128.00"),
                    byCategory.get("Stationery"), "stationery total");
        });

        runner.test("Inventory: highest-value products (top N)", () -> {
            Inventory inventory = sample();
            // Values: P100 399.20, P300 378.00, P500 128.00, P200 98.00, P400 0.00
            assertEquals(List.of("P100", "P300", "P500"),
                    skusOf(inventory.findHighestValueProducts(3)), "top 3");
        });

        runner.test("Inventory: highest-value limit larger than size returns all", () -> {
            Inventory inventory = sample();
            assertEquals(5, inventory.findHighestValueProducts(99).size(), "all products");
        });

        runner.test("Inventory: highest-value invalid limit rejected", () -> {
            Inventory inventory = sample();
            assertThrows(IllegalArgumentException.class,
                    () -> inventory.findHighestValueProducts(0), "limit 0");
        });
    }

    private static void registerDefensive(TestRunner runner) {
        runner.test("Inventory: failed add (duplicate) leaves inventory unchanged", () -> {
            Inventory inventory = sample();
            int before = inventory.size();
            assertThrows(IllegalArgumentException.class,
                    () -> inventory.addProduct(product("P100", "Clone", "C", "1.00", 1, 1)), "dup");
            assertEquals(before, inventory.size(), "size unchanged");
        });
    }
}
