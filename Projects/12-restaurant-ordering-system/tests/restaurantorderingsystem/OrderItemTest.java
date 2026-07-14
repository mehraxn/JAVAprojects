package restaurantorderingsystem;

import java.lang.reflect.Modifier;
import java.math.BigDecimal;

public final class OrderItemTest {
    private OrderItemTest() {
    }

    static void run(Assert t) {
        MenuItem pasta = new MenuItem("M01", "Pasta", new BigDecimal("18.00"));

        // Valid creation
        OrderItem item = new OrderItem(pasta, 2);
        t.assertTrue(pasta == item.getMenuItem(), "menu item is stored (immutable, safe to share)");
        t.assertEquals(2, item.getQuantity(), "quantity is stored");
        t.assertBigDecimalEquals(new BigDecimal("36.00"), item.calculateSubtotal(),
                "line total is price x quantity");

        t.assertTrue(Modifier.isFinal(OrderItem.class.getModifiers()), "OrderItem is final");

        // Validation
        t.assertThrows(IllegalArgumentException.class,
                () -> new OrderItem(null, 1), "null menu item rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new OrderItem(pasta, 0), "zero quantity rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new OrderItem(pasta, -3), "negative quantity rejected");

        // Quantity increase produces a NEW item; the original is unchanged (immutable)
        OrderItem increased = item.withAdditionalQuantity(3);
        t.assertEquals(5, increased.getQuantity(), "withAdditionalQuantity increases quantity");
        t.assertEquals(2, item.getQuantity(),
                "the original order item is not mutated by withAdditionalQuantity");
        t.assertFalse(item == increased, "withAdditionalQuantity returns a new instance");
        t.assertThrows(IllegalArgumentException.class,
                () -> item.withAdditionalQuantity(0), "non-positive increase rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> item.withAdditionalQuantity(Integer.MAX_VALUE), "quantity overflow rejected");

        // Decimal line total
        MenuItem juice = new MenuItem("M03", "Juice", new BigDecimal("2.50"));
        t.assertBigDecimalEquals(new BigDecimal("7.50"),
                new OrderItem(juice, 3).calculateSubtotal(), "decimal line total is correct");

        // Immutability: no setters, all fields final
        boolean anySetter = false;
        for (java.lang.reflect.Method m : OrderItem.class.getDeclaredMethods()) {
            if (m.getName().startsWith("set")) {
                anySetter = true;
            }
        }
        t.assertFalse(anySetter, "OrderItem has no setters (immutable)");
        for (java.lang.reflect.Field f : OrderItem.class.getDeclaredFields()) {
            t.assertTrue(Modifier.isFinal(f.getModifiers()),
                    "field " + f.getName() + " is final");
        }
    }
}
