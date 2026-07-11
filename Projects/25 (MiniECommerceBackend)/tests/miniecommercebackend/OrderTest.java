package miniecommercebackend;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
final class OrderTest {
    private OrderTest() { }
    static void run() {
        Map<String, Integer> items = new LinkedHashMap<String, Integer>(); items.put("P1", 2);
        Order o = new Order("O1", "C1", items, new BigDecimal("5.00"), LocalDateTime.now(), Order.Status.CREATED);
        Assertions.assertEquals("O1", o.getId(), "order ID");
        Assertions.assertEquals("C1", o.getCartId(), "order cart ID");
        Assertions.assertEquals(2, o.getItems().get("P1"), "order items");
        Assertions.assertBigDecimalEquals(new BigDecimal("5"), o.getTotal(), "order total");
        Assertions.assertEquals(Order.Status.CREATED, o.getStatus(), "initial status");
        items.put("P2", 1); Assertions.assertFalse(o.getItems().containsKey("P2"), "constructor copy");
        Assertions.assertThrows(UnsupportedOperationException.class, () -> o.getItems().put("P3", 1), "unmodifiable order");
        o.setStatus(Order.Status.PAID); Assertions.assertEquals(Order.Status.PAID, o.getStatus(), "paid transition");
        Assertions.assertThrows(IllegalStateException.class, () -> o.setStatus(Order.Status.CANCELLED), "invalid transition");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Order("", "C", items, BigDecimal.ONE, LocalDateTime.now(), Order.Status.CREATED), "invalid ID");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Order("O", "C", new LinkedHashMap<String, Integer>(), BigDecimal.ONE, LocalDateTime.now(), Order.Status.CREATED), "empty order");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Order("O", "C", items, BigDecimal.ONE.negate(), LocalDateTime.now(), Order.Status.CREATED), "negative total");
        Order cancelled = new Order("O2", "C2", items, BigDecimal.ZERO, LocalDateTime.now(), Order.Status.CREATED);
        cancelled.setStatus(Order.Status.CANCELLED);
        Assertions.assertEquals(Order.Status.CANCELLED, cancelled.getStatus(), "cancel transition");
        Assertions.assertNotNull(cancelled.copy(), "order copy");
        Assertions.assertEquals(cancelled.getItems(), cancelled.copy().getItems(), "copy items");
    }
}
