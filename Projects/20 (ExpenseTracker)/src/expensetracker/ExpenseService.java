package expensetracker;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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

    public Optional<Expense> findById(String expenseId) {
        return Optional.ofNullable(expensesById.get(requireText(expenseId, "Expense ID")));
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

    public List<Expense> filterByDateRange(LocalDate start, LocalDate end) {
        Objects.requireNonNull(start, "Start date cannot be null.");
        Objects.requireNonNull(end, "End date cannot be null.");
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date cannot be after the end date.");
        }
        List<Expense> matches = new ArrayList<Expense>();
        for (Expense expense : expensesById.values()) {
            LocalDate date = expense.getDate();
            if (!date.isBefore(start) && !date.isAfter(end)) {
                matches.add(expense);
            }
        }
        return Collections.unmodifiableList(matches);
    }

    public List<Expense> filterByAmountRange(BigDecimal min, BigDecimal max) {
        Objects.requireNonNull(min, "Minimum amount cannot be null.");
        Objects.requireNonNull(max, "Maximum amount cannot be null.");
        if (min.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Minimum amount cannot be negative.");
        }
        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("Minimum amount cannot exceed the maximum amount.");
        }
        List<Expense> matches = new ArrayList<Expense>();
        for (Expense expense : expensesById.values()) {
            BigDecimal amount = expense.getAmount();
            if (amount.compareTo(min) >= 0 && amount.compareTo(max) <= 0) {
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

    public Map<YearMonth, BigDecimal> calculateMonthlyTotals() {
        Map<YearMonth, BigDecimal> totals = new LinkedHashMap<YearMonth, BigDecimal>();
        for (Expense expense : expensesById.values()) {
            YearMonth month = YearMonth.from(expense.getDate());
            BigDecimal current = totals.get(month);
            totals.put(month, current == null
                    ? expense.getAmount()
                    : current.add(expense.getAmount()));
        }
        return Collections.unmodifiableMap(totals);
    }

    public Optional<Expense> highestExpense() {
        Expense highest = null;
        for (Expense expense : expensesById.values()) {
            if (highest == null || expense.getAmount().compareTo(highest.getAmount()) > 0) {
                highest = expense;
            }
        }
        return Optional.ofNullable(highest);
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        return value.trim();
    }
}
