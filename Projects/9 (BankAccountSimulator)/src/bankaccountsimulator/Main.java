package bankaccountsimulator;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        Bank bank = new Bank();
        Account savings = bank.createAccount("A100", "Maya");
        Account current = bank.createAccount("A200", "Jonas");

        savings.deposit(new BigDecimal("500.00"));
        current.deposit(new BigDecimal("125.00"));
        savings.withdraw(new BigDecimal("40.00"));
        bank.transfer("A100", "A200", new BigDecimal("75.00"));

        for (Account account : bank.listAccounts()) {
            System.out.println(account.getAccountNumber() + " balance: " + account.getBalance());
            for (Transaction transaction : account.getTransactionHistory()) {
                System.out.println("  " + transaction);
            }
        }
    }
}
