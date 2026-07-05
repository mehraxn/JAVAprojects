package bankaccountsimulator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Account {
    private final String accountNumber;
    private final String ownerName;
    private BigDecimal balance = BigDecimal.ZERO;
    private final List<Transaction> transactions = new ArrayList<>();

    public Account(String accountNumber, String ownerName) {
        this.accountNumber = requireText(accountNumber, "Account number");
        this.ownerName = requireText(ownerName, "Owner name");
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public List<Transaction> getTransactionHistory() {
        return Collections.unmodifiableList(new ArrayList<>(transactions));
    }

    public void deposit(BigDecimal amount) {
        credit(amount, Transaction.Type.DEPOSIT, "Deposit");
    }

    public void withdraw(BigDecimal amount) {
        debit(amount, Transaction.Type.WITHDRAWAL, "Withdrawal");
    }

    void receiveTransfer(BigDecimal amount, String fromAccountNumber) {
        credit(amount, Transaction.Type.TRANSFER_IN, "Transfer from " + fromAccountNumber);
    }

    void sendTransfer(BigDecimal amount, String toAccountNumber) {
        debit(amount, Transaction.Type.TRANSFER_OUT, "Transfer to " + toAccountNumber);
    }

    private void credit(BigDecimal amount, Transaction.Type type, String description) {
        BigDecimal validAmount = requirePositiveAmount(amount);
        balance = balance.add(validAmount);
        transactions.add(new Transaction(type, validAmount, LocalDateTime.now(), description));
    }

    private void debit(BigDecimal amount, Transaction.Type type, String description) {
        BigDecimal validAmount = requirePositiveAmount(amount);
        if (balance.compareTo(validAmount) < 0) {
            throw new IllegalStateException("Insufficient funds in account " + accountNumber);
        }
        balance = balance.subtract(validAmount);
        transactions.add(new Transaction(type, validAmount, LocalDateTime.now(), description));
    }

    static BigDecimal requirePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
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
