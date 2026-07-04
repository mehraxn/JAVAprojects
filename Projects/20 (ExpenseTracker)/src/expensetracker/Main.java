package expensetracker;

public class Main {
    public static void main(String[] args) {
        ExpenseService expenseService = new ExpenseService();
        ExpenseStore expenseStore = new CsvExpenseStore();
        // TODO: Add sample expenses, reports, and optional file save/load.
        System.out.println("Expense Tracker skeleton ready.");
    }
}
