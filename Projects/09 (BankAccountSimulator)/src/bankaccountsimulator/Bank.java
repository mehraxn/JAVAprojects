package bankaccountsimulator;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * In-memory service layer for accounts, deposits, withdrawals, transfers, lookup,
 * and reports. This is the only class outside callers use to change state.
 *
 * <p>Every query returns immutable snapshots ({@link AccountSnapshot},
 * {@link TransactionSnapshot}) in unmodifiable lists, so live {@link Account}
 * objects are never leaked and cannot be mutated to bypass the bank.
 *
 * <h2>Behaviour notes</h2>
 * <ul>
 *   <li>Money uses {@link BigDecimal}; balances never go negative and amounts must
 *       be strictly positive.</li>
 *   <li>Transaction IDs are generated deterministically as {@code T0001},
 *       {@code T0002}, … A transfer creates two of them (out, then in).</li>
 *   <li>Timestamps come from an injectable {@link Clock}, so tests can pin them.</li>
 *   <li>Transfers are <strong>all-or-nothing</strong>: every check runs before any
 *       balance changes, so a failed transfer leaves both accounts untouched.</li>
 * </ul>
 */
public final class Bank {

    private final Clock clock;
    private final Map<String, Account> accounts = new LinkedHashMap<>();
    private int nextTransactionNumber = 1;

    /** Uses the system clock for transaction timestamps. */
    public Bank() {
        this(Clock.systemDefaultZone());
    }

    /** Uses the supplied clock for transaction timestamps (deterministic in tests). */
    public Bank(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "clock cannot be null");
    }

    // ------------------------------------------------------- account management

    public AccountSnapshot createAccount(String accountNumber, String ownerName,
                                         BigDecimal initialBalance) {
        Account account = new Account(accountNumber, ownerName, initialBalance);
        if (accounts.containsKey(account.getAccountNumber())) {
            throw new IllegalArgumentException(
                    "Account number already exists: " + account.getAccountNumber());
        }
        accounts.put(account.getAccountNumber(), account);
        return account.toSnapshot();
    }

    /** Snapshot of an account, or {@link Optional#empty()} if unknown. */
    public Optional<AccountSnapshot> findAccount(String accountNumber) {
        Account account = accounts.get(requireAccountNumber(accountNumber));
        return account == null ? Optional.empty() : Optional.of(account.toSnapshot());
    }

    /** Snapshots of all accounts, in creation order. */
    public List<AccountSnapshot> listAccounts() {
        List<AccountSnapshot> views = new ArrayList<>();
        for (Account account : accounts.values()) {
            views.add(account.toSnapshot());
        }
        return Collections.unmodifiableList(views);
    }

    /** Snapshots of an account's transaction history. */
    public List<TransactionSnapshot> getTransactionHistory(String accountNumber) {
        return Collections.unmodifiableList(requireAccount(accountNumber).toSnapshot().getTransactions());
    }

    // ---------------------------------------------------------------- deposit

    public AccountSnapshot deposit(String accountNumber, BigDecimal amount) {
        Account account = requireAccount(accountNumber);
        Account.requirePositiveAmount(amount);
        account.deposit(amount, nextTransactionId(), now());
        return account.toSnapshot();
    }

    // --------------------------------------------------------------- withdraw

    public AccountSnapshot withdraw(String accountNumber, BigDecimal amount) {
        Account account = requireAccount(accountNumber);
        Account.requirePositiveAmount(amount);
        account.withdraw(amount, nextTransactionId(), now());
        return account.toSnapshot();
    }

    // --------------------------------------------------------------- transfer

    /**
     * Transfers funds between two accounts as a single all-or-nothing operation.
     *
     * <p>All validation (both accounts exist, not the same account, positive
     * amount, sufficient funds) runs before any balance changes, so a failure
     * leaves both accounts and their histories untouched.
     *
     * @throws IllegalArgumentException if an account is unknown, the accounts are
     *         the same, or the amount is invalid
     * @throws IllegalStateException if the source has insufficient funds
     */
    public void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        String fromNumber = requireAccountNumber(fromAccountNumber);
        String toNumber = requireAccountNumber(toAccountNumber);
        if (fromNumber.equals(toNumber)) {
            throw new IllegalArgumentException("Source and target accounts must be different");
        }
        Account.requirePositiveAmount(amount);
        Account source = requireAccount(fromNumber);
        Account target = requireAccount(toNumber);
        if (!source.hasFunds(amount)) {
            throw new IllegalStateException("Insufficient funds in account " + fromNumber);
        }

        // All checks passed — commit both legs with their own timestamps/IDs.
        LocalDateTime timestamp = now();
        source.transferOut(amount, nextTransactionId(), timestamp, toNumber);
        target.transferIn(amount, nextTransactionId(), timestamp, fromNumber);
    }

    // --------------------------------------------------------------- reports

    public int getAccountCount() {
        return accounts.size();
    }

    public BigDecimal getTotalBalance() {
        BigDecimal total = BigDecimal.ZERO;
        for (Account account : accounts.values()) {
            total = total.add(account.getBalance());
        }
        return total;
    }

    /** Snapshots of accounts owned by the given name (case-insensitive). */
    public List<AccountSnapshot> findAccountsByOwner(String ownerName) {
        if (ownerName == null || ownerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Owner name must not be blank");
        }
        String needle = ownerName.trim();
        List<AccountSnapshot> views = new ArrayList<>();
        for (Account account : accounts.values()) {
            if (account.getOwnerName().equalsIgnoreCase(needle)) {
                views.add(account.toSnapshot());
            }
        }
        return Collections.unmodifiableList(views);
    }

    // ------------------------------------------------------------------ helpers

    private LocalDateTime now() {
        return LocalDateTime.now(clock);
    }

    private String nextTransactionId() {
        return String.format("T%04d", nextTransactionNumber++);
    }

    private Account requireAccount(String accountNumber) {
        Account account = accounts.get(requireAccountNumber(accountNumber));
        if (account == null) {
            throw new IllegalArgumentException("Unknown account number: " + accountNumber.trim());
        }
        return account;
    }

    private static String requireAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number must not be blank");
        }
        return accountNumber.trim();
    }
}
