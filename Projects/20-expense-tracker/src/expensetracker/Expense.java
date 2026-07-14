package expensetracker;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public final class Expense {
    private final String id;
    private final String title;
    private final BigDecimal amount;
    private final String category;
    private final LocalDate date;

    public Expense(String id, String title, BigDecimal amount, String category, LocalDate date) {
        this.id = requireText(id, "Expense ID");
        this.title = requireText(title, "Title");
        this.category = requireText(category, "Category");
        this.amount = Objects.requireNonNull(amount, "Amount cannot be null.");
        this.date = Objects.requireNonNull(date, "Date cannot be null.");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        if (value.contains("\n") || value.contains("\r")) {
            throw new IllegalArgumentException(fieldName + " cannot contain line breaks.");
        }
        return value.trim();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        return date + " | " + title + " | " + category + " | " + amount;
    }
}
