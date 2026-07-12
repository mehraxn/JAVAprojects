package restaurantorderingsystem;

import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.List;

public final class OrderTest {
    private OrderTest() {
    }

    private static final MenuItem PASTA = new MenuItem("M01", "Pasta", new BigDecimal("18.00"));
    private static final MenuItem SALAD = new MenuItem("M02", "Salad", new BigDecimal("11.00"));
    private static final MenuItem JUICE = new MenuItem("M03", "Juice", new BigDecimal("5.00"));

    static void run(Assert t) {
        t.assertTrue(Modifier.isFinal(Order.class.getModifiers()), "Order is final");

        // Creation and initial status
        Order order = new Order("O0001", "Alina");
        t.assertEquals("O0001", order.getId(), "id is stored");
        t.assertEquals("Alina", order.getCustomerName(), "customer name is stored");
        t.assertEquals(OrderStatus.CREATED, order.getStatus(), "new order starts CREATED");
        t.assertTrue(order.isEditable(), "CREATED order is editable");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Order(null, "X"), "null id rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Order("  ", "X"), "blank id rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Order("O1", "  "), "blank customer rejected");

        // Add items, invalid quantity
        order.addItem(PASTA, 2);
        order.addItem(SALAD, 1);
        t.assertEquals(2, order.getItemCount(), "two distinct items");
        t.assertThrows(IllegalArgumentException.class,
                () -> order.addItem(JUICE, 0), "zero quantity rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> order.addItem(null, 1), "null item rejected");

        // Adding the same item merges quantity instead of duplicating the line
        order.addItem(PASTA, 1);
        t.assertEquals(2, order.getItemCount(), "same item merges, still two lines");
        OrderItem pastaLine = order.getItems().stream()
                .filter(i -> i.getMenuItem().getId().equals("M01"))
                .findFirst().orElseThrow(() -> new AssertionError("pasta line missing"));
        t.assertEquals(3, pastaLine.getQuantity(), "pasta quantity merged to 3");

        // Subtotal / discount / total (3 x 18.00 + 1 x 11.00 = 65.00, >= 50 so 10% off)
        t.assertBigDecimalEquals(new BigDecimal("65.00"), order.calculateSubtotal(),
                "subtotal is correct");
        t.assertBigDecimalEquals(new BigDecimal("6.50"), order.calculateDiscount(),
                "discount is 10% above threshold");
        t.assertBigDecimalEquals(new BigDecimal("58.50"), order.calculateTotal(),
                "total is subtotal minus discount");

        // Money is normalized to 2 decimals
        t.assertEquals(2, order.calculateDiscount().scale(), "discount is scaled to 2 decimals");
        t.assertEquals(2, order.calculateTotal().scale(), "total is scaled to 2 decimals");

        // Decimal subtotal
        Order decimals = new Order("O0009", "Decimals");
        decimals.addItem(new MenuItem("D1", "Coffee", new BigDecimal("2.50")), 3);
        t.assertBigDecimalEquals(new BigDecimal("7.50"), decimals.calculateSubtotal(),
                "decimal subtotal is correct");

        // Discount boundary
        Order below = new Order("O0002", "Below");
        below.addItem(PASTA, 2); // 36.00 < 50 -> no discount
        t.assertBigDecimalEquals(BigDecimal.ZERO, below.calculateDiscount(),
                "below threshold: no discount");
        t.assertBigDecimalEquals(new BigDecimal("36.00"), below.calculateTotal(),
                "below threshold: total equals subtotal");

        Order exact = new Order("O0003", "Exact");
        exact.addItem(JUICE, 10); // 10 x 5.00 = exactly 50.00 -> discount applies (inclusive)
        t.assertBigDecimalEquals(new BigDecimal("50.00"), exact.calculateSubtotal(),
                "exact threshold subtotal");
        t.assertBigDecimalEquals(new BigDecimal("5.00"), exact.calculateDiscount(),
                "exactly at threshold: discount applies (inclusive)");
        t.assertBigDecimalEquals(new BigDecimal("45.00"), exact.calculateTotal(),
                "exact threshold total");

        // Returned items list is unmodifiable; items are immutable so cannot corrupt the order
        List<OrderItem> items = order.getItems();
        t.assertThrows(UnsupportedOperationException.class,
                () -> items.add(new OrderItem(JUICE, 1)), "items list is unmodifiable");
        OrderItem grabbed = items.get(0);
        OrderItem bigger = grabbed.withAdditionalQuantity(100); // returns a new item
        t.assertEquals(3, order.getItems().stream()
                        .filter(i -> i.getMenuItem().getId().equals("M01"))
                        .findFirst().get().getQuantity(),
                "mutating a grabbed item's copy does not change the order");
        t.assertEquals(103, bigger.getQuantity(), "the derived item itself reflects the change");

        // Empty order cannot move to PREPARING
        Order emptyOrder = new Order("O0004", "Empty");
        t.assertThrows(IllegalStateException.class,
                () -> emptyOrder.updateStatus(OrderStatus.PREPARING),
                "empty order cannot move to PREPARING");

        // Valid lifecycle transitions
        Order lifecycle = new Order("O0005", "Life");
        lifecycle.addItem(PASTA, 1);
        lifecycle.updateStatus(OrderStatus.PREPARING);
        t.assertEquals(OrderStatus.PREPARING, lifecycle.getStatus(), "CREATED -> PREPARING");
        lifecycle.updateStatus(OrderStatus.READY);
        t.assertEquals(OrderStatus.READY, lifecycle.getStatus(), "PREPARING -> READY");
        lifecycle.updateStatus(OrderStatus.SERVED);
        t.assertEquals(OrderStatus.SERVED, lifecycle.getStatus(), "READY -> SERVED");

        // Editing after PREPARING is rejected
        Order locked = new Order("O0006", "Locked");
        locked.addItem(PASTA, 1);
        locked.updateStatus(OrderStatus.PREPARING);
        t.assertThrows(IllegalStateException.class,
                () -> locked.addItem(SALAD, 1), "cannot add items after PREPARING");
        t.assertThrows(IllegalStateException.class,
                () -> locked.removeItem("M01"), "cannot remove items after PREPARING");

        // Invalid transitions
        Order invalid = new Order("O0007", "Invalid");
        invalid.addItem(PASTA, 1);
        t.assertThrows(IllegalStateException.class,
                () -> invalid.updateStatus(OrderStatus.READY),
                "cannot skip from CREATED to READY");
        invalid.updateStatus(OrderStatus.PREPARING);
        t.assertThrows(IllegalStateException.class,
                () -> invalid.updateStatus(OrderStatus.CREATED),
                "cannot move backwards to CREATED");
        t.assertThrows(IllegalArgumentException.class,
                () -> invalid.updateStatus(null), "null status rejected");

        // Terminal statuses cannot change
        Order served = new Order("O0008", "Served");
        served.addItem(PASTA, 1);
        served.updateStatus(OrderStatus.PREPARING);
        served.updateStatus(OrderStatus.READY);
        served.updateStatus(OrderStatus.SERVED);
        t.assertThrows(IllegalStateException.class,
                () -> served.updateStatus(OrderStatus.CANCELLED),
                "SERVED is terminal, cannot cancel");

        Order cancelled = new Order("O0010", "Cancelled");
        cancelled.addItem(PASTA, 1);
        cancelled.updateStatus(OrderStatus.CANCELLED);
        t.assertEquals(OrderStatus.CANCELLED, cancelled.getStatus(), "CREATED -> CANCELLED works");
        t.assertThrows(IllegalStateException.class,
                () -> cancelled.updateStatus(OrderStatus.PREPARING),
                "CANCELLED is terminal");

        // PREPARING can be cancelled
        Order prepCancel = new Order("O0011", "PrepCancel");
        prepCancel.addItem(PASTA, 1);
        prepCancel.updateStatus(OrderStatus.PREPARING);
        prepCancel.updateStatus(OrderStatus.CANCELLED);
        t.assertEquals(OrderStatus.CANCELLED, prepCancel.getStatus(),
                "PREPARING -> CANCELLED works");

        // Summary formats money to 2 decimals
        String summary = order.createSummary();
        t.assertContains(summary, "Subtotal: 65.00", "summary shows 2-decimal subtotal");
        t.assertContains(summary, "Discount: 6.50", "summary shows 2-decimal discount");
        t.assertContains(summary, "Total: 58.50", "summary shows 2-decimal total");

        // copy() is an independent snapshot
        Order copy = order.copy();
        copy.updateStatus(OrderStatus.PREPARING);
        t.assertEquals(OrderStatus.CREATED, order.getStatus(),
                "mutating a copy does not change the original");
    }
}
