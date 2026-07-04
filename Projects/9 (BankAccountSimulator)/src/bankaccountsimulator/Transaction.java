package bankaccountsimulator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    public enum Type {
        DEPOSIT,
        WITHDRAWAL
    }

    private final Type type;
    private final BigDecimal amount;
    private final LocalDateTime timestamp;

    public Transaction(Type type, BigDecimal amount, LocalDateTime timestamp) {
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
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
}
