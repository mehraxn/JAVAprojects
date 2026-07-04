package expensetracker;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ExpenseService service = new ExpenseService();
        service.addExpense(new Expense("E-001", "Groceries", new BigDecimal("42.75"),
                "Food", LocalDate.of(2026, 7, 2)));
        service.addExpense(new Expense("E-002", "Train ticket", new BigDecimal("18.50"),
                "Transport", LocalDate.of(2026, 7, 3)));
        service.addExpense(new Expense("E-003", "Coffee beans", new BigDecimal("12.90"),
                "Food", LocalDate.of(2026, 6, 28)));

        System.out.println("All expenses:");
        printExpenses(service.listExpenses());
        System.out.println("\nFood expenses:");
        printExpenses(service.filterByCategory("food"));
        System.out.println("\nJuly expenses:");
        printExpenses(service.filterByMonth(YearMonth.of(2026, 7)));
        System.out.println("\nTotal spending: " + service.calculateTotalSpending());

        if (args.length > 0) {
            saveAndReload(Paths.get(args[0]), service);
        } else {
            System.out.println("\nPass a CSV file path to demonstrate saving and loading.");
        }
    }

    private static void saveAndReload(Path path, ExpenseService service) {
        ExpenseStore store = new CsvExpenseStore();
        try {
            store.save(path, service.listExpenses());
            List<Expense> loadedExpenses = store.load(path);
            System.out.println("Saved and loaded " + loadedExpenses.size() + " expenses from " + path + ".");
        } catch (IOException exception) {
            System.err.println("Could not use the CSV file: " + exception.getMessage());
        }
    }

    private static void printExpenses(List<Expense> expenses) {
        if (expenses.isEmpty()) {
            System.out.println("No expenses found.");
            return;
        }
        for (Expense expense : expenses) {
            System.out.println(expense);
        }
    }
}
