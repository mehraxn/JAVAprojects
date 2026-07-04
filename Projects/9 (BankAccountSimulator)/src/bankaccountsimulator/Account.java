package bankaccountsimulator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Account {
    private final String accountNumber;
    private final String ownerName;
    private BigDecimal balance = BigDecimal.ZERO;
    private final List<Transaction> transactions = new ArrayList<>();

    public Account(String accountNumber, String ownerName) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
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

    public void deposit(BigDecimal amount) {
        // TODO: Validate the amount, update the balance, and record a transaction.
        throw new UnsupportedOperationException("TODO: deposit money");
    }

    public void withdraw(BigDecimal amount) {
        // TODO: Validate the amount and available balance before withdrawing.
        throw new UnsupportedOperationException("TODO: withdraw money");
    }
}
