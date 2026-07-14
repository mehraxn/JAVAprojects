package productinventorymanager;

import java.math.BigDecimal;

import static productinventorymanager.TestSupport.assertBigDecimalEquals;
import static productinventorymanager.TestSupport.assertEquals;
import static productinventorymanager.TestSupport.assertFalse;
import static productinventorymanager.TestSupport.assertThrows;
import static productinventorymanager.TestSupport.assertTrue;

final class ProductTest {

    private ProductTest() {
    }

    static void register(TestRunner runner) {
        runner.test("Product: full constructor stores all fields", () -> {
            Product product = new Product("SKU-1", "Keyboard", "Electronics",
                    new BigDecimal("49.99"), 10, 3);
            assertEquals("SKU-1", product.getSku(), "sku stored");
            assertEquals("Keyboard", product.getName(), "name stored");
            assertEquals("Electronics", product.getCategory(), "category stored");
            assertBigDecimalEquals(new BigDecimal("49.99"), product.getUnitPrice(), "price stored");
            assertEquals(10, product.getQuantity(), "quantity stored");
            assertEquals(3, product.getReorderThreshold(), "threshold stored");
        });

        runner.test("Product: simple constructor uses default category and threshold", () -> {
            Product product = new Product("SKU-2", "Mouse", new BigDecimal("9.50"), 4);
            assertEquals(Product.DEFAULT_CATEGORY, product.getCategory(), "default category");
            assertEquals(Product.DEFAULT_REORDER_THRESHOLD, product.getReorderThreshold(),
                    "default threshold");
        });

        runner.test("Product: fields are trimmed", () -> {
            Product product = new Product("  SKU-3 ", "  Cable ", " Wires ",
                    new BigDecimal("2.00"), 1, 0);
            assertEquals("SKU-3", product.getSku(), "sku trimmed");
            assertEquals("Cable", product.getName(), "name trimmed");
            assertEquals("Wires", product.getCategory(), "category trimmed");
        });

        runner.test("Product: null/blank SKU rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new Product(null, "N", new BigDecimal("1"), 1), "null sku");
            assertThrows(IllegalArgumentException.class,
                    () -> new Product("  ", "N", new BigDecimal("1"), 1), "blank sku");
        });

        runner.test("Product: null/blank name rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new Product("S", null, new BigDecimal("1"), 1), "null name");
            assertThrows(IllegalArgumentException.class,
                    () -> new Product("S", "  ", new BigDecimal("1"), 1), "blank name");
        });

        runner.test("Product: null price rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new Product("S", "N", null, 1), "null price"));

        runner.test("Product: zero price rejected (price must be > 0)", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new Product("S", "N", new BigDecimal("0.00"), 1), "zero price"));

        runner.test("Product: negative price rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new Product("S", "N", new BigDecimal("-1.00"), 1), "negative price"));

        runner.test("Product: negative quantity rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new Product("S", "N", new BigDecimal("1.00"), -1), "negative qty"));

        runner.test("Product: zero quantity allowed (out of stock)", () -> {
            Product product = new Product("S", "N", new BigDecimal("1.00"), 0);
            assertEquals(0, product.getQuantity(), "zero qty ok");
            assertTrue(product.isOutOfStock(), "is out of stock");
        });

        runner.test("Product: negative reorder threshold rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new Product("S", "N", "C", new BigDecimal("1.00"), 1, -1),
                        "negative threshold"));

        runner.test("Product: BigDecimal price preserved exactly", () -> {
            Product product = new Product("S", "N", new BigDecimal("19.99"), 3);
            assertBigDecimalEquals(new BigDecimal("19.99"), product.getUnitPrice(), "price exact");
        });

        runner.test("Product: stock value is price times quantity", () -> {
            Product product = new Product("S", "N", new BigDecimal("2.50"), 4);
            assertBigDecimalEquals(new BigDecimal("10.00"), product.calculateStockValue(),
                    "2.50 x 4 = 10.00");
        });

        runner.test("Product: low-stock boundary is quantity <= threshold", () -> {
            Product atThreshold = new Product("S", "N", "C", new BigDecimal("1"), 3, 3);
            Product aboveThreshold = new Product("T", "N", "C", new BigDecimal("1"), 4, 3);
            assertTrue(atThreshold.isLowStock(), "equal to threshold is low");
            assertFalse(aboveThreshold.isLowStock(), "above threshold is not low");
        });
    }
}
