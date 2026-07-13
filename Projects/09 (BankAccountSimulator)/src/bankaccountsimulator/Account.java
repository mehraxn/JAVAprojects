package bankaccountsimulator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An account: identity, owner, current balance, and transaction history.
 *
 * <p>Account number and owner are fixed at construction; only the balance and
 * history change, and only through the package-private mutators driven by
 * {@link Bank} (which owns transaction IDs and timestamps). Balances never go
 * negative. Outside callers receive an immutable {@link AccountSnapshot}, so live
 * accounts are never leaked and cannot be mutated to bypass {@link Bank}.
 */
public final class Account {
    private final String accountNumber;
    private final String ownerName;
    private BigDecimal balance;
    private final List<Transaction> transactions = new ArrayList<>();

    public Account(String accountNumber, String ownerName, BigDecimal initialBalance) {
        this.accountNumber = requireText(accountNumber, "Account number");
        this.ownerName = requireText(ownerName, "Owner name");
        if (initialBalance == null) {
            throw new IllegalArgumentException("Initial balance must not be null");
        }
        if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance must not be negative");
        }
        this.balance = initialBalance;
    }

    public String getAccountNumber() { return accountNumber; }
    public String getOwnerName() { return ownerName; }
    public BigDecimal getBalance() { return balance; }

    public int getTransactionCount() { return transactions.size(); }

    // -------------------------------------------------------- package-private ops

    /** Adds funds and records a transaction; returns the new balance. */
    void deposit(BigDecimal amount, String transactionId, LocalDateTime timestamp) {
        BigDecimal validAmount = requirePositiveAmount(amount);
        balance = balance.add(validAmount);
        record(transactionId, TransactionType.DEPOSIT, validAmount, timestamp, "Deposit", null);
    }

    /** Removes funds (rejecting overdraft) and records a transaction. */
    void withdraw(BigDecimal amount, String transactionId, LocalDateTime timestamp) {
        BigDecimal validAmount = requirePositiveAmount(amount);
        requireSufficientFunds(validAmount);
        balance = balance.subtract(validAmount);
        record(transactionId, TransactionType.WITHDRAWAL, validAmount, timestamp, "Withdrawal", null);
    }

    /** Sends funds to another account (rejecting overdraft) and records TRANSFER_OUT. */
    void transferOut(BigDecimal amount, String transactionId, LocalDateTime timestamp,
                     String toAccountNumber) {
        BigDecimal validAmount = requirePositiveAmount(amount);
        requireSufficientFunds(validAmount);
        balance = balance.subtract(validAmount);
        record(transactionId, TransactionType.TRANSFER_OUT, validAmount, timestamp,
                "Transfer to " + toAccountNumber, toAccountNumber);
    }

    /** Receives funds from another account and records TRANSFER_IN. */
    void transferIn(BigDecimal amount, String transactionId, LocalDateTime timestamp,
                    String fromAccountNumber) {
        BigDecimal validAmount = requirePositiveAmount(amount);
        balance = balance.add(validAmount);
        record(transactionId, TransactionType.TRANSFER_IN, validAmount, timestamp,
                "Transfer from " + fromAccountNumber, fromAccountNumber);
    }

    /** True if this account has at least {@code amount} available. */
    boolean hasFunds(BigDecimal amount) {
        return balance.compareTo(amount) >= 0;
    }

    private void record(String transactionId, TransactionType type, BigDecimal amount,
                        LocalDateTime timestamp, String description, String relatedAccountNumber) {
        transactions.add(new Transaction(transactionId, type, amount, timestamp,
                description, balance, relatedAccountNumber));
    }

    private void requireSufficientFunds(BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds in account " + accountNumber);
        }
    }

    /** Immutable, read-only view of this account and its history. */
    public AccountSnapshot toSnapshot() {
        List<TransactionSnapshot> history = new ArrayList<>();
        for (Transaction transaction : transactions) {
            history.add(transaction.toSnapshot());
        }
        return new AccountSnapshot(accountNumber, ownerName, balance,
                Collections.unmodifiableList(history), history.size());
    }

    static BigDecimal requirePositiveAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount must not be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        return amount;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
