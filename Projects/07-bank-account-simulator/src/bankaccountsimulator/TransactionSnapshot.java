package bankaccountsimulator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Immutable, read-only view of a {@link Transaction} handed to outside callers.
 */
public final class TransactionSnapshot {
    private final String transactionId;
    private final TransactionType type;
    private final BigDecimal amount;
    private final LocalDateTime timestamp;
    private final String description;
    private final BigDecimal balanceAfter;
    private final String relatedAccountNumber;

    TransactionSnapshot(String transactionId, TransactionType type, BigDecimal amount,
                        LocalDateTime timestamp, String description, BigDecimal balanceAfter,
                        String relatedAccountNumber) {
        this.transactionId = transactionId;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
        this.description = description;
        this.balanceAfter = balanceAfter;
        this.relatedAccountNumber = relatedAccountNumber;
    }

    public String getTransactionId() { return transactionId; }
    public TransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getDescription() { return description; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public String getRelatedAccountNumber() { return relatedAccountNumber; }

    @Override
    public String toString() {
        return transactionId + " | " + timestamp + " | " + type + " | "
                + amount.toPlainString() + " | bal " + balanceAfter.toPlainString()
                + " | " + description;
    }
}
