package miniecommercebackend;
import java.math.BigDecimal;
final class ProductTest {
    private ProductTest() { }
    static void run() {
        Product p = new Product("P_1", " Tea ", new BigDecimal("2.50"), 4);
        Assertions.assertEquals("P_1", p.getId(), "product ID");
        Assertions.assertEquals("Tea", p.getName(), "trimmed name");
        Assertions.assertBigDecimalEquals(new BigDecimal("2.5"), p.getPrice(), "price");
        Assertions.assertEquals(4, p.getStockQuantity(), "stock");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Product("P", null, BigDecimal.ONE, 0), "null name");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Product("P", " ", BigDecimal.ONE, 0), "blank name");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Product("P", "X", null, 0), "null price");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Product("P", "X", BigDecimal.ZERO, 0), "zero price");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Product("P", "X", BigDecimal.ONE.negate(), 0), "negative price");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Product("P", "X", BigDecimal.ONE, -1), "negative stock");
        p.decreaseStock(2); Assertions.assertEquals(2, p.getStockQuantity(), "decrease stock");
        Assertions.assertThrows(IllegalArgumentException.class, () -> p.decreaseStock(3), "stock below zero");
        p.increaseStock(5); Assertions.assertEquals(7, p.getStockQuantity(), "increase stock");
        Assertions.assertThrows(IllegalArgumentException.class, () -> p.increaseStock(0), "zero increase");
        p.updateDetails("Coffee", new BigDecimal("3.75"));
        Assertions.assertEquals("Coffee", p.getName(), "updated name");
        Assertions.assertBigDecimalEquals(new BigDecimal("3.75"), p.getPrice(), "updated price");
        Assertions.assertThrows(IllegalArgumentException.class, () -> p.updateDetails("", BigDecimal.TEN), "invalid update");
        Product copy = p.copy(); copy.updateDetails("Other", BigDecimal.ONE); copy.increaseStock(1);
        Assertions.assertNotEquals(copy.getName(), p.getName(), "copy details isolated");
        Assertions.assertNotEquals(copy.getStockQuantity(), p.getStockQuantity(), "copy stock isolated");
    }
}
