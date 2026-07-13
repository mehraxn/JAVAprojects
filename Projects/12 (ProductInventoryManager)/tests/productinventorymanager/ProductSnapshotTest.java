package productinventorymanager;

import java.math.BigDecimal;
import java.util.List;

import static productinventorymanager.TestSupport.assertBigDecimalEquals;
import static productinventorymanager.TestSupport.assertEquals;
import static productinventorymanager.TestSupport.assertThrows;
import static productinventorymanager.TestSupport.assertTrue;

/**
 * Proves that data leaving {@link Inventory} is safe: snapshots carry the right
 * values, returned lists are unmodifiable, and holding a snapshot cannot change
 * inventory state.
 */
final class ProductSnapshotTest {

    private ProductSnapshotTest() {
    }

    private static Inventory sample() {
        Inventory inventory = new Inventory();
        inventory.addProduct(new Product("P100", "Keyboard", "Electronics",
                new BigDecimal("49.90"), 8, 5));
        inventory.addProduct(new Product("P200", "Mouse", "Electronics",
                new BigDecimal("24.50"), 4, 5));
        return inventory;
    }

    static void register(TestRunner runner) {
        runner.test("Snapshot: stores expected fields and inventory value", () -> {
            ProductSnapshot snapshot = sample().findProductBySku("P100").orElseThrow();
            assertEquals("P100", snapshot.getSku(), "sku");
            assertEquals("Keyboard", snapshot.getName(), "name");
            assertEquals("Electronics", snapshot.getCategory(), "category");
            assertBigDecimalEquals(new BigDecimal("49.90"), snapshot.getUnitPrice(), "price");
            assertEquals(8, snapshot.getQuantity(), "quantity");
            assertEquals(5, snapshot.getReorderThreshold(), "threshold");
            assertBigDecimalEquals(new BigDecimal("399.20"), snapshot.getInventoryValue(), "value");
        });

        runner.test("Snapshot: listProducts result is unmodifiable", () -> {
            List<ProductSnapshot> products = sample().listProducts();
            assertThrows(UnsupportedOperationException.class,
                    () -> products.remove(0), "cannot modify list");
        });

        runner.test("Snapshot: search result is unmodifiable", () -> {
            List<ProductSnapshot> products = sample().searchByName("o");
            assertThrows(UnsupportedOperationException.class,
                    () -> products.clear(), "cannot modify search");
        });

        runner.test("Snapshot: sort result is unmodifiable", () -> {
            List<ProductSnapshot> products = sample().sortProducts(ProductSortField.NAME);
            assertThrows(UnsupportedOperationException.class,
                    () -> products.clear(), "cannot modify sort");
        });

        runner.test("Snapshot: report result is unmodifiable", () -> {
            List<ProductSnapshot> products = sample().findLowStockProducts();
            assertThrows(UnsupportedOperationException.class,
                    () -> products.clear(), "cannot modify report");
        });

        runner.test("Snapshot: category value map is unmodifiable", () -> {
            Inventory inventory = sample();
            assertThrows(UnsupportedOperationException.class,
                    () -> inventory.calculateInventoryValueByCategory().put("X", BigDecimal.ONE),
                    "cannot modify map");
        });

        runner.test("Snapshot: is decoupled from live product (stays constant after change)", () -> {
            Inventory inventory = sample();
            ProductSnapshot before = inventory.findProductBySku("P100").orElseThrow();
            assertEquals(8, before.getQuantity(), "captured quantity 8");
            inventory.adjustStock("P100", -5);
            // The previously captured snapshot must not change.
            assertEquals(8, before.getQuantity(), "old snapshot unchanged");
            // A fresh snapshot reflects the new state.
            ProductSnapshot after = inventory.findProductBySku("P100").orElseThrow();
            assertEquals(3, after.getQuantity(), "fresh snapshot updated");
        });

        runner.test("Snapshot: mutating returned list does not affect inventory", () -> {
            Inventory inventory = sample();
            int before = inventory.size();
            List<ProductSnapshot> products = inventory.listProducts();
            try {
                products.remove(0);
            } catch (UnsupportedOperationException ignored) {
                // expected — list is unmodifiable
            }
            assertEquals(before, inventory.size(), "inventory size unchanged");
            assertTrue(inventory.findProductBySku("P100").isPresent(), "product still present");
        });
    }
}
