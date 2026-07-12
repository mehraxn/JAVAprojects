package restaurantorderingsystem;

import java.lang.reflect.Modifier;
import java.math.BigDecimal;

public final class MenuItemTest {
    private MenuItemTest() {
    }

    static void run(Assert t) {
        // Valid creation and getters
        MenuItem item = new MenuItem("M01", "Pasta", new BigDecimal("18.00"));
        t.assertEquals("M01", item.getId(), "id is stored");
        t.assertEquals("Pasta", item.getName(), "name is stored");
        t.assertBigDecimalEquals(new BigDecimal("18.00"), item.getPrice(), "price is stored");
        t.assertEquals("18.00", item.getPrice().toPlainString(), "BigDecimal scale is preserved");

        t.assertTrue(Modifier.isFinal(MenuItem.class.getModifiers()), "MenuItem is final");

        // Trimming
        t.assertEquals("M02", new MenuItem("  M02 ", "Salad", BigDecimal.TEN).getId(),
                "id is trimmed");
        t.assertEquals("Salad", new MenuItem("M02", "  Salad  ", BigDecimal.TEN).getName(),
                "name is trimmed");

        // ID validation
        t.assertThrows(IllegalArgumentException.class,
                () -> new MenuItem(null, "Pasta", BigDecimal.TEN), "null id rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new MenuItem("  ", "Pasta", BigDecimal.TEN), "blank id rejected");

        // Name validation
        t.assertThrows(IllegalArgumentException.class,
                () -> new MenuItem("M01", null, BigDecimal.TEN), "null name rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new MenuItem("M01", "   ", BigDecimal.TEN), "blank name rejected");

        // Price validation
        t.assertThrows(IllegalArgumentException.class,
                () -> new MenuItem("M01", "Pasta", null), "null price rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new MenuItem("M01", "Pasta", BigDecimal.ZERO), "zero price rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new MenuItem("M01", "Pasta", new BigDecimal("-0.01")),
                "negative price rejected");
        t.assertBigDecimalEquals(new BigDecimal("0.01"),
                new MenuItem("M01", "Pasta", new BigDecimal("0.01")).getPrice(),
                "smallest positive price accepted");

        // Immutability: all fields private final, no setters
        boolean anySetter = false;
        for (java.lang.reflect.Method m : MenuItem.class.getDeclaredMethods()) {
            if (m.getName().startsWith("set")) {
                anySetter = true;
            }
        }
        t.assertFalse(anySetter, "MenuItem has no setters (immutable)");
        for (java.lang.reflect.Field f : MenuItem.class.getDeclaredFields()) {
            t.assertTrue(Modifier.isFinal(f.getModifiers()),
                    "field " + f.getName() + " is final");
        }
    }
}
