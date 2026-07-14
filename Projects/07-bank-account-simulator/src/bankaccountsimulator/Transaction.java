package bankaccountsimulator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * One immutable money-movement record in an account's history.
 *
 * <p>Every field is fixed at construction and validated eagerly. Money uses
 * {@link BigDecimal}; the amount must be strictly positive and the recorded
 * {@code balanceAfter} must not be negative. The {@code relatedAccountNumber} is
 * optional (used by transfers) but must not be blank when present.
 */
public final class Transaction {
    private final String transactionId;
    private final TransactionType type;
    private final BigDecimal amount;
    private final LocalDateTime timestamp;
    private final String description;
    private final BigDecimal balanceAfter;
    private final String relatedAccountNumber;

    public Transaction(String transactionId, TransactionType type, BigDecimal amount,
                       LocalDateTime timestamp, String description, BigDecimal balanceAfter,
                       String relatedAccountNumber) {
        this.transactionId = requireText(transactionId, "Transaction ID");
        if (type == null) {
            throw new IllegalArgumentException("Transaction type must not be null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount must not be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if (timestamp == null) {
            throw new IllegalArgumentException("Timestamp must not be null");
        }
        this.description = requireText(description, "Description");
        if (balanceAfter == null) {
            throw new IllegalArgumentException("Balance-after must not be null");
        }
        if (balanceAfter.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Balance-after must not be negative");
        }
        if (relatedAccountNumber != null && relatedAccountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Related account number must not be blank when present");
        }
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
        this.balanceAfter = balanceAfter;
        this.relatedAccountNumber = relatedAccountNumber == null ? null : relatedAccountNumber.trim();
    }

    public String getTransactionId() { return transactionId; }
    public TransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getDescription() { return description; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }

    /** Related account for a transfer, or {@code null} for deposits/withdrawals. */
    public String getRelatedAccountNumber() { return relatedAccountNumber; }

    /** Immutable, read-only view of this transaction. */
    public TransactionSnapshot toSnapshot() {
        return new TransactionSnapshot(transactionId, type, amount, timestamp, description,
                balanceAfter, relatedAccountNumber);
    }

    @Override
    public String toString() {
        return transactionId + " | " + timestamp + " | " + type + " | "
                + amount.toPlainString() + " | bal " + balanceAfter.toPlainString()
                + " | " + description;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
