package bankaccountsimulator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    public enum Type {
        DEPOSIT,
        WITHDRAWAL,
        TRANSFER_IN,
        TRANSFER_OUT
    }

    private final Type type;
    private final BigDecimal amount;
    private final LocalDateTime timestamp;
    private final String description;

    public Transaction(Type type, BigDecimal amount, LocalDateTime timestamp, String description) {
        if (type == null || amount == null || timestamp == null) {
            throw new IllegalArgumentException("Transaction values must not be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amount must be greater than zero");
        }
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
        this.description = description == null ? "" : description.trim();
    }

    public Type getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return timestamp + " | " + type + " | " + amount.toPlainString() + " | " + description;
    }
}
