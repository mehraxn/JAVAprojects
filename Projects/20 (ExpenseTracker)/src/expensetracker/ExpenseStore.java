package expensetracker;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface ExpenseStore {
    List<Expense> load(Path path) throws IOException;

    void save(Path path, List<Expense> expenses) throws IOException;
}
