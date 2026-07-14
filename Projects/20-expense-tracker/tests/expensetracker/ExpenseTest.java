package expensetracker;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.time.LocalDate;

public final class ExpenseTest {
    private ExpenseTest() {
    }

    static void run(Assert t) {
        LocalDate date = LocalDate.of(2026, 7, 2);
        BigDecimal amount = new BigDecimal("42.75");

        // Valid creation and getters
        Expense expense = new Expense("E-001", "Groceries", amount, "Food", date);
        t.assertEquals("E-001", expense.getId(), "getId returns constructor value");
        t.assertEquals("Groceries", expense.getTitle(), "getTitle returns constructor value");
        t.assertEquals("Food", expense.getCategory(), "getCategory returns constructor value");
        t.assertEquals(date, expense.getDate(), "getDate returns constructor value");
        t.assertBigDecimalEquals(amount, expense.getAmount(),
                "getAmount preserves the BigDecimal value");
        t.assertEquals("42.75", expense.getAmount().toPlainString(),
                "BigDecimal scale is preserved exactly");

        // Immutability: final class, all fields private final, no setters
        t.assertTrue(Modifier.isFinal(Expense.class.getModifiers()), "Expense is final");
        boolean allFieldsPrivateFinal = true;
        for (Field field : Expense.class.getDeclaredFields()) {
            if (!Modifier.isPrivate(field.getModifiers())
                    || !Modifier.isFinal(field.getModifiers())) {
                allFieldsPrivateFinal = false;
            }
        }
        t.assertTrue(allFieldsPrivateFinal, "all Expense fields are private final");
        boolean hasSetter = false;
        for (java.lang.reflect.Method method : Expense.class.getDeclaredMethods()) {
            if (method.getName().startsWith("set")) {
                hasSetter = true;
            }
        }
        t.assertFalse(hasSetter, "Expense has no setters (immutable)");

        // ID validation
        t.assertThrows(IllegalArgumentException.class,
                () -> new Expense(null, "Title", amount, "Food", date), "null ID rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Expense("  ", "Title", amount, "Food", date), "blank ID rejected");

        // Title validation
        t.assertThrows(IllegalArgumentException.class,
                () -> new Expense("E-1", null, amount, "Food", date), "null title rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Expense("E-1", "", amount, "Food", date), "empty title rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Expense("E-1", "   ", amount, "Food", date),
                "whitespace-only title rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Expense("E-1", "line\nbreak", amount, "Food", date),
                "title with line break rejected");

        // Category validation
        t.assertThrows(IllegalArgumentException.class,
                () -> new Expense("E-1", "Title", amount, null, date), "null category rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Expense("E-1", "Title", amount, " ", date), "blank category rejected");

        // Date and amount validation
        t.assertThrows(NullPointerException.class,
                () -> new Expense("E-1", "Title", amount, "Food", null), "null date rejected");
        t.assertThrows(NullPointerException.class,
                () -> new Expense("E-1", "Title", null, "Food", date), "null amount rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Expense("E-1", "Title", BigDecimal.ZERO, "Food", date),
                "zero amount rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Expense("E-1", "Title", new BigDecimal("-0.01"), "Food", date),
                "negative amount rejected");
        t.assertBigDecimalEquals(new BigDecimal("0.01"),
                new Expense("E-1", "Title", new BigDecimal("0.01"), "Food", date).getAmount(),
                "smallest positive amount accepted");

        // Trimming
        t.assertEquals("Trimmed", new Expense("E-1", "  Trimmed  ", amount, "Food", date).getTitle(),
                "title is trimmed");
        t.assertEquals("E-2", new Expense(" E-2 ", "Title", amount, "Food", date).getId(),
                "ID is trimmed");

        // toString includes the key fields
        String text = expense.toString();
        t.assertContains(text, "Groceries", "toString contains the title");
        t.assertContains(text, "42.75", "toString contains the amount");
        t.assertContains(text, "2026-07-02", "toString contains the date");
    }
}
