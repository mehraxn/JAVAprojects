package expensetracker;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ExpenseService {
    private final Map<String, Expense> expensesById = new LinkedHashMap<String, Expense>();

    public void addExpense(Expense expense) {
        Objects.requireNonNull(expense, "Expense cannot be null.");
        if (expensesById.containsKey(expense.getId())) {
            throw new IllegalArgumentException("An expense with ID " + expense.getId() + " already exists.");
        }
        expensesById.put(expense.getId(), expense);
    }

    public boolean removeExpense(String expenseId) {
        return expensesById.remove(requireText(expenseId, "Expense ID")) != null;
    }

    public List<Expense> listExpenses() {
        return Collections.unmodifiableList(new ArrayList<Expense>(expensesById.values()));
    }

    public List<Expense> filterByCategory(String category) {
        String requestedCategory = requireText(category, "Category");
        List<Expense> matches = new ArrayList<Expense>();
        for (Expense expense : expensesById.values()) {
            if (expense.getCategory().equalsIgnoreCase(requestedCategory)) {
                matches.add(expense);
            }
        }
        return Collections.unmodifiableList(matches);
    }

    public List<Expense> filterByMonth(YearMonth month) {
        Objects.requireNonNull(month, "Month cannot be null.");
        List<Expense> matches = new ArrayList<Expense>();
        for (Expense expense : expensesById.values()) {
            if (YearMonth.from(expense.getDate()).equals(month)) {
                matches.add(expense);
            }
        }
        return Collections.unmodifiableList(matches);
    }

    public BigDecimal calculateTotalSpending() {
        BigDecimal total = BigDecimal.ZERO;
        for (Expense expense : expensesById.values()) {
            total = total.add(expense.getAmount());
        }
        return total;
    }

    public BigDecimal calculateMonthlyTotal(YearMonth month) {
        BigDecimal total = BigDecimal.ZERO;
        for (Expense expense : filterByMonth(month)) {
            total = total.add(expense.getAmount());
        }
        return total;
    }

    public Map<String, BigDecimal> calculateCategoryTotals() {
        Map<String, BigDecimal> totals = new LinkedHashMap<String, BigDecimal>();
        for (Expense expense : expensesById.values()) {
            BigDecimal current = totals.get(expense.getCategory());
            totals.put(expense.getCategory(), current == null
                    ? expense.getAmount()
                    : current.add(expense.getAmount()));
        }
        return Collections.unmodifiableMap(totals);
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        return value.trim();
    }
}
