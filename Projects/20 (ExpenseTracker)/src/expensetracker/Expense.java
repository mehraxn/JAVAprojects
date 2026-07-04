package expensetracker;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Expense {
    private final String id;
    private final String description;
    private final String category;
    private final BigDecimal amount;
    private final LocalDate date;

    public Expense(String id, String description, String category,
            BigDecimal amount, LocalDate date) {
        this.id = id;
        this.description = description;
        this.category = category;
        this.amount = amount;
        this.date = date;
    }

    public String getId() { return id; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public BigDecimal getAmount() { return amount; }
    public LocalDate getDate() { return date; }
}
