package expensetracker;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class CsvExpenseStore implements ExpenseStore {
    @Override
    public List<Expense> load(Path path) throws IOException {
        // TODO: Parse validated UTF-8 CSV rows into Expense objects.
        throw new UnsupportedOperationException("TODO: load expenses from CSV");
    }

    @Override
    public void save(Path path, List<Expense> expenses) throws IOException {
        // TODO: Escape CSV fields and write expenses with standard Java file APIs.
        throw new UnsupportedOperationException("TODO: save expenses to CSV");
    }
}
