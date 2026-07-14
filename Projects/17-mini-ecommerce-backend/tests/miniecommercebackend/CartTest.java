package miniecommercebackend;
import java.util.Map;
final class CartTest {
    private CartTest() { }
    static void run() {
        Cart c = new Cart(" C1 ");
        Assertions.assertEquals("C1", c.getId(), "cart ID");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Cart(null), "null cart ID");
        c.addProduct("P1", 2); Assertions.assertEquals(2, c.getItems().get("P1"), "add item");
        c.addProduct("P1", 3); Assertions.assertEquals(5, c.getItems().get("P1"), "merge item");
        Assertions.assertThrows(IllegalArgumentException.class, () -> c.addProduct("P2", 0), "zero quantity");
        Assertions.assertThrows(IllegalArgumentException.class, () -> c.addProduct("P2", -1), "negative quantity");
        c.updateQuantity("P1", 4); Assertions.assertEquals(4, c.getItems().get("P1"), "update quantity");
        Assertions.assertThrows(IllegalArgumentException.class, () -> c.updateQuantity("missing", 1), "update missing");
        Map<String, Integer> snapshot = c.getItems();
        Assertions.assertThrows(UnsupportedOperationException.class, () -> snapshot.put("P2", 1), "unmodifiable items");
        Cart copy = c.copy(); copy.addProduct("P2", 1);
        Assertions.assertFalse(c.getItems().containsKey("P2"), "copy isolated");
        Assertions.assertTrue(c.removeProduct("P1"), "remove item");
        Assertions.assertFalse(c.removeProduct("P1"), "remove missing");
    }
}
