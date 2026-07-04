package bankaccountsimulator;

import java.util.HashMap;
import java.util.Map;

public class Bank {
    private final Map<String, Account> accounts = new HashMap<>();

    public Account createAccount(String accountNumber, String ownerName) {
        // TODO: Validate input, reject duplicates, and store the new account.
        throw new UnsupportedOperationException("TODO: create an account");
    }

    public Account findAccount(String accountNumber) {
        // TODO: Return the account or report that it does not exist.
        throw new UnsupportedOperationException("TODO: find an account");
    }

    public void transfer(String fromAccount, String toAccount, java.math.BigDecimal amount) {
        // TODO: Coordinate a validated withdrawal and deposit.
        throw new UnsupportedOperationException("TODO: transfer money");
    }
}
