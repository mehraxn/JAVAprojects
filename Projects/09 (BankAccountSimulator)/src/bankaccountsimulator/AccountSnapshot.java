package bankaccountsimulator;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * Immutable, read-only view of an {@link Account}, including a snapshot of its
 * transaction history. The transaction list is unmodifiable and its elements are
 * immutable, so holding a snapshot can never change bank state.
 */
public final class AccountSnapshot {
    private final String accountNumber;
    private final String ownerName;
    private final BigDecimal balance;
    private final List<TransactionSnapshot> transactions;
    private final int transactionCount;

    AccountSnapshot(String accountNumber, String ownerName, BigDecimal balance,
                    List<TransactionSnapshot> transactions, int transactionCount) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = balance;
        this.transactions = Collections.unmodifiableList(transactions);
        this.transactionCount = transactionCount;
    }

    public String getAccountNumber() { return accountNumber; }
    public String getOwnerName() { return ownerName; }
    public BigDecimal getBalance() { return balance; }
    public List<TransactionSnapshot> getTransactions() { return transactions; }
    public int getTransactionCount() { return transactionCount; }

    @Override
    public String toString() {
        return accountNumber + " (" + ownerName + ") balance " + balance.toPlainString()
                + ", " + transactionCount + " txn(s)";
    }
}
