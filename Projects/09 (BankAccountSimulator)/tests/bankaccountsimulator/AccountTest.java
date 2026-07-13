package bankaccountsimulator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static bankaccountsimulator.TestSupport.assertBigDecimalEquals;
import static bankaccountsimulator.TestSupport.assertEquals;
import static bankaccountsimulator.TestSupport.assertThrows;

final class AccountTest {

    private AccountTest() {
    }

    private static final LocalDateTime TS = LocalDateTime.of(2026, 1, 15, 10, 0);

    static void register(TestRunner runner) {
        runner.test("Account: valid creation stores fields", () -> {
            Account account = new Account("A100", "Maya", new BigDecimal("50.00"));
            assertEquals("A100", account.getAccountNumber(), "number stored");
            assertEquals("Maya", account.getOwnerName(), "owner stored");
            assertBigDecimalEquals(new BigDecimal("50.00"), account.getBalance(), "balance stored");
        });

        runner.test("Account: number and owner are trimmed", () -> {
            Account account = new Account("  A100 ", "  Maya ", BigDecimal.ZERO);
            assertEquals("A100", account.getAccountNumber(), "number trimmed");
            assertEquals("Maya", account.getOwnerName(), "owner trimmed");
        });

        runner.test("Account: zero initial balance allowed", () ->
                assertBigDecimalEquals(BigDecimal.ZERO,
                        new Account("A1", "N", BigDecimal.ZERO).getBalance(), "zero ok"));

        runner.test("Account: null/blank account number rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new Account(null, "N", BigDecimal.ZERO), "null number");
            assertThrows(IllegalArgumentException.class,
                    () -> new Account("  ", "N", BigDecimal.ZERO), "blank number");
        });

        runner.test("Account: null/blank owner name rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new Account("A1", null, BigDecimal.ZERO), "null owner");
            assertThrows(IllegalArgumentException.class,
                    () -> new Account("A1", "  ", BigDecimal.ZERO), "blank owner");
        });

        runner.test("Account: null initial balance rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new Account("A1", "N", null), "null balance"));

        runner.test("Account: negative initial balance rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new Account("A1", "N", new BigDecimal("-0.01")), "negative balance"));

        runner.test("Account: deposit increases balance and records transaction", () -> {
            Account account = new Account("A1", "N", new BigDecimal("10.00"));
            account.deposit(new BigDecimal("15.00"), "T0001", TS);
            assertBigDecimalEquals(new BigDecimal("25.00"), account.getBalance(), "10+15=25");
            assertEquals(1, account.getTransactionCount(), "one txn");
        });

        runner.test("Account: withdraw decreases balance", () -> {
            Account account = new Account("A1", "N", new BigDecimal("30.00"));
            account.withdraw(new BigDecimal("12.00"), "T0001", TS);
            assertBigDecimalEquals(new BigDecimal("18.00"), account.getBalance(), "30-12=18");
        });

        runner.test("Account: exact-balance withdrawal leaves zero", () -> {
            Account account = new Account("A1", "N", new BigDecimal("40.00"));
            account.withdraw(new BigDecimal("40.00"), "T0001", TS);
            assertBigDecimalEquals(BigDecimal.ZERO, account.getBalance(), "leaves zero");
        });

        runner.test("Account: overdraft withdrawal rejected and balance unchanged", () -> {
            Account account = new Account("A1", "N", new BigDecimal("20.00"));
            assertThrows(IllegalStateException.class,
                    () -> account.withdraw(new BigDecimal("50.00"), "T0001", TS), "overdraft");
            assertBigDecimalEquals(new BigDecimal("20.00"), account.getBalance(), "unchanged");
            assertEquals(0, account.getTransactionCount(), "no txn recorded");
        });

        runner.test("Account: zero/negative deposit rejected", () -> {
            Account account = new Account("A1", "N", new BigDecimal("20.00"));
            assertThrows(IllegalArgumentException.class,
                    () -> account.deposit(BigDecimal.ZERO, "T0001", TS), "zero deposit");
            assertThrows(IllegalArgumentException.class,
                    () -> account.deposit(new BigDecimal("-1.00"), "T0001", TS), "negative deposit");
        });

        runner.test("Account: snapshot contains balance and unmodifiable history", () -> {
            Account account = new Account("A1", "Maya", new BigDecimal("100.00"));
            account.deposit(new BigDecimal("50.00"), "T0001", TS);
            AccountSnapshot snapshot = account.toSnapshot();
            assertEquals("A1", snapshot.getAccountNumber(), "snapshot number");
            assertEquals("Maya", snapshot.getOwnerName(), "snapshot owner");
            assertBigDecimalEquals(new BigDecimal("150.00"), snapshot.getBalance(), "snapshot balance");
            assertEquals(1, snapshot.getTransactionCount(), "snapshot count");
            assertThrows(UnsupportedOperationException.class,
                    () -> snapshot.getTransactions().clear(), "unmodifiable history");
        });

        runner.test("Account: snapshot is decoupled from live account", () -> {
            Account account = new Account("A1", "Maya", new BigDecimal("100.00"));
            AccountSnapshot before = account.toSnapshot();
            account.deposit(new BigDecimal("25.00"), "T0001", TS);
            assertBigDecimalEquals(new BigDecimal("100.00"), before.getBalance(), "old snapshot unchanged");
            assertBigDecimalEquals(new BigDecimal("125.00"), account.toSnapshot().getBalance(),
                    "fresh snapshot updated");
        });
    }
}
