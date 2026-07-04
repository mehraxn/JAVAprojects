package expensetracker;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExpenseService {
    private final Map<String, Expense> expenses = new LinkedHashMap<>();

    public void addExpense(Expense expense) {
        // TODO: Validate the expense and reject duplicate IDs.
        throw new UnsupportedOperationException("TODO: add an expense");
    }

    public boolean removeExpense(String expenseId) {
        // TODO: Validate the ID and remove the matching expense.
        throw new UnsupportedOperationException("TODO: remove an expense");
    }

    public List<Expense> listExpenses() {
        // TODO: Return expenses in a stable, read-only order.
        throw new UnsupportedOperationException("TODO: list expenses");
    }

    public BigDecimal calculateMonthlyTotal(YearMonth month) {
        // TODO: Sum expenses that belong to the requested month.
        throw new UnsupportedOperationException("TODO: calculate a monthly total");
    }

    public Map<String, BigDecimal> calculateTotalsByCategory() {
        // TODO: Group expenses by category and calculate totals.
        throw new UnsupportedOperationException("TODO: calculate category totals");
    }
}
