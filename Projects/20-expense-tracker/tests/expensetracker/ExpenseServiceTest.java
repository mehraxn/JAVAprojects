package expensetracker;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public final class ExpenseServiceTest {
    private ExpenseServiceTest() {
    }

    private static Expense expense(String id, String title, String amount,
            String category, LocalDate date) {
        return new Expense(id, title, new BigDecimal(amount), category, date);
    }

    static void run(Assert t) {
        ExpenseService service = new ExpenseService();

        // Empty service
        t.assertEquals(0, service.listExpenses().size(), "new service has no expenses");
        t.assertBigDecimalEquals(BigDecimal.ZERO, service.calculateTotalSpending(),
                "empty total is zero");
        t.assertFalse(service.highestExpense().isPresent(),
                "highestExpense on empty service is empty");
        t.assertEquals(0, service.calculateCategoryTotals().size(),
                "empty category totals map");
        t.assertFalse(service.findById("E-999").isPresent(),
                "findById on empty service returns empty Optional");
        t.assertFalse(service.removeExpense("E-999"), "remove on empty service returns false");

        // Add
        service.addExpense(expense("E-001", "Groceries", "42.75", "Food",
                LocalDate.of(2026, 7, 2)));
        service.addExpense(expense("E-002", "Train ticket", "18.50", "Transport",
                LocalDate.of(2026, 7, 3)));
        service.addExpense(expense("E-003", "Coffee beans", "12.90", "Food",
                LocalDate.of(2026, 6, 28)));
        t.assertEquals(3, service.listExpenses().size(), "three expenses stored");
        t.assertThrows(NullPointerException.class,
                () -> service.addExpense(null), "adding null expense rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> service.addExpense(expense("E-001", "Duplicate", "1.00", "Misc",
                        LocalDate.of(2026, 7, 5))), "duplicate ID rejected");
        t.assertEquals(3, service.listExpenses().size(),
                "failed adds do not change stored expenses");

        // Find
        t.assertTrue(service.findById("E-001").isPresent(), "findById finds existing expense");
        t.assertEquals("Groceries", service.findById("E-001").get().getTitle(),
                "found expense has expected data");
        t.assertFalse(service.findById("E-999").isPresent(),
                "findById returns empty Optional for missing ID");
        t.assertThrows(IllegalArgumentException.class,
                () -> service.findById("  "), "blank ID rejected on find");

        // List is unmodifiable and in insertion order
        List<Expense> listed = service.listExpenses();
        t.assertEquals("E-001", listed.get(0).getId(), "list preserves insertion order");
        t.assertThrows(UnsupportedOperationException.class,
                () -> listed.add(listed.get(0)), "listExpenses result is unmodifiable");

        // Category filter (case-insensitive, documented)
        List<Expense> food = service.filterByCategory("FOOD");
        t.assertEquals(2, food.size(), "category filter is case-insensitive");
        t.assertEquals(0, service.filterByCategory("Missing").size(),
                "unmatched category returns empty list");
        t.assertThrows(IllegalArgumentException.class,
                () -> service.filterByCategory(" "), "blank category filter rejected");
        t.assertThrows(UnsupportedOperationException.class,
                () -> food.add(listed.get(0)), "category filter result is unmodifiable");

        // Month filter
        t.assertEquals(2, service.filterByMonth(YearMonth.of(2026, 7)).size(),
                "month filter matches July expenses");
        t.assertEquals(1, service.filterByMonth(YearMonth.of(2026, 6)).size(),
                "month filter matches June expenses");
        t.assertEquals(0, service.filterByMonth(YearMonth.of(2026, 1)).size(),
                "unmatched month returns empty list");
        t.assertThrows(NullPointerException.class,
                () -> service.filterByMonth(null), "null month rejected");

        // Date-range filter
        List<Expense> firstWeek = service.filterByDateRange(
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 2));
        t.assertEquals(1, firstWeek.size(), "date range filter matches inclusively");
        t.assertEquals("E-001", firstWeek.get(0).getId(), "date range finds expected expense");
        t.assertEquals(3, service.filterByDateRange(
                LocalDate.of(2026, 6, 28), LocalDate.of(2026, 7, 3)).size(),
                "date range includes both boundary dates");
        t.assertEquals(1, service.filterByDateRange(
                LocalDate.of(2026, 6, 28), LocalDate.of(2026, 6, 28)).size(),
                "single-day range works");
        t.assertThrows(IllegalArgumentException.class,
                () -> service.filterByDateRange(LocalDate.of(2026, 7, 2), LocalDate.of(2026, 7, 1)),
                "inverted date range rejected");
        t.assertThrows(NullPointerException.class,
                () -> service.filterByDateRange(null, LocalDate.of(2026, 7, 1)),
                "null start date rejected");

        // Amount-range filter
        List<Expense> midRange = service.filterByAmountRange(
                new BigDecimal("12.90"), new BigDecimal("18.50"));
        t.assertEquals(2, midRange.size(), "amount range filter matches inclusively");
        t.assertThrows(IllegalArgumentException.class,
                () -> service.filterByAmountRange(new BigDecimal("50"), new BigDecimal("10")),
                "inverted amount range rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> service.filterByAmountRange(new BigDecimal("-1"), new BigDecimal("10")),
                "negative minimum rejected");
        t.assertThrows(NullPointerException.class,
                () -> service.filterByAmountRange(null, BigDecimal.TEN),
                "null minimum rejected");

        // Totals with decimals (BigDecimal, no floating-point drift)
        t.assertBigDecimalEquals(new BigDecimal("74.15"), service.calculateTotalSpending(),
                "total spending adds decimals exactly");
        t.assertBigDecimalEquals(new BigDecimal("61.25"),
                service.calculateMonthlyTotal(YearMonth.of(2026, 7)),
                "monthly total adds July expenses exactly");
        Map<String, BigDecimal> categoryTotals = service.calculateCategoryTotals();
        t.assertBigDecimalEquals(new BigDecimal("55.65"), categoryTotals.get("Food"),
                "Food category total is exact");
        t.assertBigDecimalEquals(new BigDecimal("18.50"), categoryTotals.get("Transport"),
                "Transport category total is exact");
        t.assertThrows(UnsupportedOperationException.class,
                () -> categoryTotals.put("X", BigDecimal.ONE),
                "category totals map is unmodifiable");

        // Monthly totals map
        Map<YearMonth, BigDecimal> monthlyTotals = service.calculateMonthlyTotals();
        t.assertEquals(2, monthlyTotals.size(), "monthly totals cover both months");
        t.assertBigDecimalEquals(new BigDecimal("61.25"),
                monthlyTotals.get(YearMonth.of(2026, 7)), "July monthly total is exact");
        t.assertBigDecimalEquals(new BigDecimal("12.90"),
                monthlyTotals.get(YearMonth.of(2026, 6)), "June monthly total is exact");
        t.assertThrows(UnsupportedOperationException.class,
                () -> monthlyTotals.put(YearMonth.of(2026, 1), BigDecimal.ONE),
                "monthly totals map is unmodifiable");

        // Ten cents added ten times equals exactly one unit (the classic double bug)
        ExpenseService decimals = new ExpenseService();
        for (int i = 1; i <= 10; i++) {
            decimals.addExpense(expense("D-" + i, "Dime " + i, "0.10", "Misc",
                    LocalDate.of(2026, 7, i)));
        }
        t.assertBigDecimalEquals(new BigDecimal("1.00"), decimals.calculateTotalSpending(),
                "0.10 added ten times is exactly 1.00 with BigDecimal");

        // Highest expense
        t.assertEquals("E-001", service.highestExpense().get().getId(),
                "highestExpense finds the largest amount");
        service.addExpense(expense("E-005", "Laptop", "899.99", "Electronics",
                LocalDate.of(2026, 7, 10)));
        t.assertEquals("E-005", service.highestExpense().get().getId(),
                "highestExpense updates when a larger expense is added");

        // Remove
        t.assertTrue(service.removeExpense("E-005"), "remove of existing expense returns true");
        t.assertFalse(service.findById("E-005").isPresent(), "removed expense is gone");
        t.assertFalse(service.removeExpense("E-005"), "second remove returns false");
        t.assertThrows(IllegalArgumentException.class,
                () -> service.removeExpense(null), "null ID rejected on remove");

        // Internal state cannot be mutated externally (Expense is immutable and
        // the only way in is addExpense, which rejects duplicates)
        t.assertEquals(3, service.listExpenses().size(), "state is consistent after removals");
    }
}
