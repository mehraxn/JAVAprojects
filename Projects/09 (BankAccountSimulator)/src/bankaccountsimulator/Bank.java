package bankaccountsimulator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Bank {
    private final Map<String, Account> accounts = new LinkedHashMap<>();

    public Account createAccount(String accountNumber, String ownerName) {
        Account account = new Account(accountNumber, ownerName);
        if (accounts.containsKey(account.getAccountNumber())) {
            throw new IllegalArgumentException("Account number already exists: "
                    + account.getAccountNumber());
        }
        accounts.put(account.getAccountNumber(), account);
        return account;
    }

    public Account findAccount(String accountNumber) {
        String validNumber = requireAccountNumber(accountNumber);
        Account account = accounts.get(validNumber);
        if (account == null) {
            throw new IllegalArgumentException("Unknown account number: " + validNumber);
        }
        return account;
    }

    public List<Account> listAccounts() {
        return Collections.unmodifiableList(new ArrayList<>(accounts.values()));
    }

    public void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        String fromNumber = requireAccountNumber(fromAccountNumber);
        String toNumber = requireAccountNumber(toAccountNumber);
        if (fromNumber.equals(toNumber)) {
            throw new IllegalArgumentException("Source and destination accounts must be different");
        }

        BigDecimal validAmount = Account.requirePositiveAmount(amount);
        Account source = findAccount(fromNumber);
        Account destination = findAccount(toNumber);
        if (source.getBalance().compareTo(validAmount) < 0) {
            throw new IllegalStateException("Insufficient funds in account " + fromNumber);
        }

        source.sendTransfer(validAmount, toNumber);
        destination.receiveTransfer(validAmount, fromNumber);
    }

    private static String requireAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number must not be blank");
        }
        return accountNumber.trim();
    }
}
