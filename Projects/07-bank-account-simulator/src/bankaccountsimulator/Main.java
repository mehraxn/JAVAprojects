package bankaccountsimulator;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

/**
 * Command-line entry point and demo driver.
 *
 * <p>All work happens in {@link #run(String[], PrintStream, PrintStream)}, which
 * returns an exit code and never calls {@link System#exit}. Only
 * {@link #main(String[])} exits the JVM, so the CLI can be tested in-process.
 */
public final class Main {

    // Fixed clock so demo transaction timestamps are stable and reproducible.
    private static final Clock DEMO_CLOCK =
            Clock.fixed(Instant.parse("2026-01-15T10:00:00Z"), ZoneId.of("UTC"));

    private Main() {
    }

    public static void main(String[] args) {
        int exitCode = run(args, System.out, System.err);
        System.exit(exitCode);
    }

    /**
     * Runs one CLI command.
     *
     * @return {@code 0} for a recognised command (including {@code validation-demo},
     *         whose failures are intentional), non-zero for an unknown command.
     */
    public static int run(String[] args, PrintStream out, PrintStream err) {
        String command = (args == null || args.length == 0) ? "help" : args[0];
        switch (command) {
            case "help":
            case "--help":
            case "-h":
                printHelp(out);
                return 0;
            case "demo":
                runDemo(out);
                return 0;
            case "deposit-demo":
                runDepositDemo(out);
                return 0;
            case "withdraw-demo":
                runWithdrawDemo(out);
                return 0;
            case "transfer-demo":
                runTransferDemo(out);
                return 0;
            case "statement-demo":
                runStatementDemo(out);
                return 0;
            case "validation-demo":
                runValidationDemo(out);
                return 0;
            default:
                err.println("Unknown command: " + command);
                err.println("Run 'help' to see available commands.");
                return 2;
        }
    }

    private static void printHelp(PrintStream out) {
        out.println("Bank Account Simulator");
        out.println();
        out.println("Usage: java -cp out bankaccountsimulator.Main <command>");
        out.println();
        out.println("Commands:");
        out.println("  help             Show this help text.");
        out.println("  demo             End-to-end deposit/withdraw/transfer walkthrough.");
        out.println("  deposit-demo     Deposit workflow with transaction ID and balanceAfter.");
        out.println("  withdraw-demo    Withdrawal, exact-balance, and overdraft rejection.");
        out.println("  transfer-demo    Transfer with TRANSFER_OUT/TRANSFER_IN and all-or-nothing.");
        out.println("  statement-demo   Full account statement after several operations.");
        out.println("  validation-demo  Intentional validation failures, handled cleanly.");
    }

    // ------------------------------------------------------------------- demos

    private static void runDemo(PrintStream out) {
        out.println("== Bank Account Demo ==");
        Bank bank = new Bank(DEMO_CLOCK);
        bank.createAccount("A100", "Maya", new BigDecimal("0.00"));
        bank.createAccount("A200", "Jonas", new BigDecimal("0.00"));

        bank.deposit("A100", new BigDecimal("500.00"));
        bank.deposit("A200", new BigDecimal("125.00"));
        bank.withdraw("A100", new BigDecimal("40.00"));
        bank.transfer("A100", "A200", new BigDecimal("75.00"));

        out.println();
        out.println("Final balances:");
        for (AccountSnapshot account : bank.listAccounts()) {
            out.println("  " + account.getAccountNumber() + " (" + account.getOwnerName()
                    + "): " + formatMoney(account.getBalance()));
        }
        out.println("Total bank balance: " + formatMoney(bank.getTotalBalance()));

        out.println();
        out.println("A100 history:");
        printHistory(out, bank.getTransactionHistory("A100"));
    }

    private static void runDepositDemo(PrintStream out) {
        out.println("== Deposit Demo ==");
        Bank bank = new Bank(DEMO_CLOCK);
        bank.createAccount("A100", "Maya", new BigDecimal("100.00"));

        out.println("Balance before: " + formatMoney(bank.findAccount("A100").orElseThrow().getBalance()));
        AccountSnapshot after = bank.deposit("A100", new BigDecimal("250.00"));
        out.println("Balance after:  " + formatMoney(after.getBalance()));
        TransactionSnapshot txn = lastTransaction(after);
        out.println("Transaction:    " + txn.getTransactionId() + " " + txn.getType()
                + " " + formatMoney(txn.getAmount()) + " -> balanceAfter " + formatMoney(txn.getBalanceAfter()));
    }

    private static void runWithdrawDemo(PrintStream out) {
        out.println("== Withdraw Demo ==");
        Bank bank = new Bank(DEMO_CLOCK);
        bank.createAccount("A100", "Maya", new BigDecimal("100.00"));

        AccountSnapshot afterFirst = bank.withdraw("A100", new BigDecimal("30.00"));
        out.println("After withdrawing 30.00: " + formatMoney(afterFirst.getBalance()));

        AccountSnapshot afterExact = bank.withdraw("A100", new BigDecimal("70.00"));
        out.println("After exact-balance withdrawal of 70.00: " + formatMoney(afterExact.getBalance()));

        out.println();
        out.println("Attempting overdraft (withdraw 10.00 from 0.00)...");
        BigDecimal before = bank.findAccount("A100").orElseThrow().getBalance();
        expectFailure(out, "overdraft withdrawal",
                () -> bank.withdraw("A100", new BigDecimal("10.00")));
        BigDecimal now = bank.findAccount("A100").orElseThrow().getBalance();
        out.println("Balance unchanged: " + formatMoney(before) + " -> " + formatMoney(now));
    }

    private static void runTransferDemo(PrintStream out) {
        out.println("== Transfer Demo ==");
        Bank bank = new Bank(DEMO_CLOCK);
        bank.createAccount("A100", "Maya", new BigDecimal("300.00"));
        bank.createAccount("A200", "Jonas", new BigDecimal("50.00"));

        out.println("Transferring 120.00 from A100 to A200...");
        bank.transfer("A100", "A200", new BigDecimal("120.00"));
        out.println("  A100 balance: " + formatMoney(bank.findAccount("A100").orElseThrow().getBalance()));
        out.println("  A200 balance: " + formatMoney(bank.findAccount("A200").orElseThrow().getBalance()));

        out.println();
        out.println("A100 history (note TRANSFER_OUT):");
        printHistory(out, bank.getTransactionHistory("A100"));
        out.println("A200 history (note TRANSFER_IN):");
        printHistory(out, bank.getTransactionHistory("A200"));

        out.println();
        out.println("All-or-nothing: transfer 999.00 (insufficient funds) changes nothing...");
        BigDecimal a100Before = bank.findAccount("A100").orElseThrow().getBalance();
        int a100Txns = bank.findAccount("A100").orElseThrow().getTransactionCount();
        expectFailure(out, "insufficient-funds transfer",
                () -> bank.transfer("A100", "A200", new BigDecimal("999.00")));
        out.println("  A100 balance unchanged: " + formatMoney(a100Before)
                + " -> " + formatMoney(bank.findAccount("A100").orElseThrow().getBalance()));
        out.println("  A100 transaction count unchanged: " + a100Txns
                + " -> " + bank.findAccount("A100").orElseThrow().getTransactionCount());
    }

    private static void runStatementDemo(PrintStream out) {
        out.println("== Statement Demo ==");
        Bank bank = new Bank(DEMO_CLOCK);
        bank.createAccount("A100", "Maya", new BigDecimal("0.00"));
        bank.createAccount("A200", "Jonas", new BigDecimal("500.00"));
        bank.deposit("A100", new BigDecimal("1000.00"));
        bank.withdraw("A100", new BigDecimal("250.00"));
        bank.transfer("A200", "A100", new BigDecimal("125.50"));

        AccountSnapshot account = bank.findAccount("A100").orElseThrow();
        out.println("Statement for " + account.getAccountNumber() + " (" + account.getOwnerName() + ")");
        out.println("Current balance: " + formatMoney(account.getBalance()));
        out.println("Transactions (" + account.getTransactionCount() + "):");
        printHistory(out, account.getTransactions());
    }

    private static void runValidationDemo(PrintStream out) {
        out.println("== Validation Demo (failures below are intentional) ==");
        Bank bank = new Bank(DEMO_CLOCK);
        bank.createAccount("A100", "Maya", new BigDecimal("100.00"));
        bank.createAccount("A200", "Jonas", new BigDecimal("100.00"));

        expectFailure(out, "blank account number",
                () -> bank.createAccount("  ", "Nobody", new BigDecimal("0.00")));
        expectFailure(out, "blank owner name",
                () -> bank.createAccount("A300", "  ", new BigDecimal("0.00")));
        expectFailure(out, "negative initial balance",
                () -> bank.createAccount("A300", "Ann", new BigDecimal("-1.00")));
        expectFailure(out, "duplicate account number",
                () -> bank.createAccount("A100", "Clone", new BigDecimal("0.00")));
        expectFailure(out, "zero deposit",
                () -> bank.deposit("A100", new BigDecimal("0.00")));
        expectFailure(out, "negative withdrawal",
                () -> bank.withdraw("A100", new BigDecimal("-5.00")));
        expectFailure(out, "overdraft withdrawal",
                () -> bank.withdraw("A100", new BigDecimal("9999.00")));
        expectFailure(out, "self-transfer",
                () -> bank.transfer("A100", "A100", new BigDecimal("10.00")));
        expectFailure(out, "unknown account deposit",
                () -> bank.deposit("NOPE", new BigDecimal("10.00")));

        out.println();
        out.println("All validation failures were handled cleanly.");
    }

    // ----------------------------------------------------------------- helpers

    private static TransactionSnapshot lastTransaction(AccountSnapshot account) {
        List<TransactionSnapshot> txns = account.getTransactions();
        return txns.get(txns.size() - 1);
    }

    private static void printHistory(PrintStream out, List<TransactionSnapshot> history) {
        if (history.isEmpty()) {
            out.println("  (no transactions)");
            return;
        }
        for (TransactionSnapshot txn : history) {
            String related = txn.getRelatedAccountNumber() == null
                    ? "" : " [" + txn.getRelatedAccountNumber() + "]";
            out.println("  " + txn.getTransactionId() + "  " + pad(txn.getType().name(), 12)
                    + " " + pad(formatMoney(txn.getAmount()), 10)
                    + " balanceAfter " + pad(formatMoney(txn.getBalanceAfter()), 10)
                    + " " + txn.getTimestamp() + related);
        }
    }

    private static String formatMoney(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private static String pad(String text, int width) {
        StringBuilder builder = new StringBuilder(text);
        while (builder.length() < width) {
            builder.append(' ');
        }
        return builder.toString();
    }

    private static void expectFailure(PrintStream out, String label, Runnable action) {
        try {
            action.run();
            out.println("  [" + label + "] ERROR: expected a failure but none occurred");
        } catch (IllegalArgumentException | IllegalStateException expected) {
            out.println("  [" + label + "] rejected: " + expected.getMessage());
        }
    }
}
