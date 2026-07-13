package bankaccountsimulator;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static bankaccountsimulator.TestSupport.assertBigDecimalEquals;
import static bankaccountsimulator.TestSupport.assertEquals;
import static bankaccountsimulator.TestSupport.assertFalse;
import static bankaccountsimulator.TestSupport.assertThrows;
import static bankaccountsimulator.TestSupport.assertTrue;

final class BankTest {

    private BankTest() {
    }

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-01-15T10:00:00Z"), ZoneId.of("UTC"));
    private static final LocalDateTime FIXED_TIME = LocalDateTime.now(FIXED_CLOCK);

    private static Bank bank() {
        return new Bank(FIXED_CLOCK);
    }

    private static TransactionSnapshot lastTxn(AccountSnapshot account) {
        List<TransactionSnapshot> txns = account.getTransactions();
        return txns.get(txns.size() - 1);
    }

    static void register(TestRunner runner) {
        registerManagement(runner);
        registerDeposit(runner);
        registerWithdraw(runner);
        registerReports(runner);
    }

    private static void registerManagement(TestRunner runner) {
        runner.test("Bank: create account works and returns snapshot", () -> {
            AccountSnapshot account = bank().createAccount("A100", "Maya", new BigDecimal("100.00"));
            assertEquals("A100", account.getAccountNumber(), "number");
            assertBigDecimalEquals(new BigDecimal("100.00"), account.getBalance(), "balance");
        });

        runner.test("Bank: duplicate account number rejected", () -> {
            Bank bank = bank();
            bank.createAccount("A100", "Maya", BigDecimal.ZERO);
            assertThrows(IllegalArgumentException.class,
                    () -> bank.createAccount("A100", "Clone", BigDecimal.ZERO), "duplicate");
        });

        runner.test("Bank: account number is trimmed/normalized", () -> {
            Bank bank = bank();
            bank.createAccount("  A100 ", "Maya", BigDecimal.ZERO);
            assertTrue(bank.findAccount("A100").isPresent(), "found trimmed");
            assertThrows(IllegalArgumentException.class,
                    () -> bank.createAccount("A100", "Clone", BigDecimal.ZERO), "dup after trim");
        });

        runner.test("Bank: negative initial balance rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> bank().createAccount("A1", "N", new BigDecimal("-1")), "negative init"));

        runner.test("Bank: find account works; missing returns empty", () -> {
            Bank bank = bank();
            bank.createAccount("A100", "Maya", BigDecimal.ZERO);
            assertTrue(bank.findAccount("A100").isPresent(), "present");
            assertFalse(bank.findAccount("NOPE").isPresent(), "empty");
        });

        runner.test("Bank: list accounts works and is unmodifiable", () -> {
            Bank bank = bank();
            bank.createAccount("A100", "Maya", BigDecimal.ZERO);
            bank.createAccount("A200", "Jonas", BigDecimal.ZERO);
            List<AccountSnapshot> accounts = bank.listAccounts();
            assertEquals(2, accounts.size(), "two accounts");
            assertThrows(UnsupportedOperationException.class, () -> accounts.clear(), "unmodifiable");
        });
    }

    private static void registerDeposit(TestRunner runner) {
        runner.test("Bank: deposit increases balance and records deterministic txn", () -> {
            Bank bank = bank();
            bank.createAccount("A100", "Maya", new BigDecimal("100.00"));
            AccountSnapshot after = bank.deposit("A100", new BigDecimal("250.00"));
            assertBigDecimalEquals(new BigDecimal("350.00"), after.getBalance(), "100+250=350");
            TransactionSnapshot txn = lastTxn(after);
            assertEquals("T0001", txn.getTransactionId(), "first id T0001");
            assertEquals(TransactionType.DEPOSIT, txn.getType(), "type DEPOSIT");
            assertEquals(FIXED_TIME, txn.getTimestamp(), "fixed timestamp");
            assertBigDecimalEquals(new BigDecimal("350.00"), txn.getBalanceAfter(), "balanceAfter");
        });

        runner.test("Bank: sequential deposits get T0001, T0002", () -> {
            Bank bank = bank();
            bank.createAccount("A100", "Maya", BigDecimal.ZERO);
            AccountSnapshot a = bank.deposit("A100", new BigDecimal("10.00"));
            AccountSnapshot b = bank.deposit("A100", new BigDecimal("20.00"));
            assertEquals("T0001", a.getTransactions().get(0).getTransactionId(), "T0001");
            assertEquals("T0002", lastTxn(b).getTransactionId(), "T0002");
        });

        runner.test("Bank: deposit unknown account rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> bank().deposit("NOPE", BigDecimal.TEN), "unknown"));

        runner.test("Bank: null/zero/negative deposit rejected", () -> {
            Bank bank = bank();
            bank.createAccount("A100", "Maya", new BigDecimal("100.00"));
            assertThrows(IllegalArgumentException.class,
                    () -> bank.deposit("A100", null), "null");
            assertThrows(IllegalArgumentException.class,
                    () -> bank.deposit("A100", BigDecimal.ZERO), "zero");
            assertThrows(IllegalArgumentException.class,
                    () -> bank.deposit("A100", new BigDecimal("-1")), "negative");
        });

        runner.test("Bank: failed deposit leaves balance unchanged", () -> {
            Bank bank = bank();
            bank.createAccount("A100", "Maya", new BigDecimal("100.00"));
            assertThrows(IllegalArgumentException.class, () -> bank.deposit("A100", BigDecimal.ZERO), "z");
            assertBigDecimalEquals(new BigDecimal("100.00"),
                    bank.findAccount("A100").orElseThrow().getBalance(), "unchanged");
            assertEquals(0, bank.findAccount("A100").orElseThrow().getTransactionCount(), "no txn");
        });
    }

    private static void registerWithdraw(TestRunner runner) {
        runner.test("Bank: withdrawal decreases balance and records txn", () -> {
            Bank bank = bank();
            bank.createAccount("A100", "Maya", new BigDecimal("100.00"));
            AccountSnapshot after = bank.withdraw("A100", new BigDecimal("30.00"));
            assertBigDecimalEquals(new BigDecimal("70.00"), after.getBalance(), "100-30=70");
            assertBigDecimalEquals(new BigDecimal("70.00"), lastTxn(after).getBalanceAfter(), "balAfter");
        });

        runner.test("Bank: exact-balance withdrawal leaves zero", () -> {
            Bank bank = bank();
            bank.createAccount("A100", "Maya", new BigDecimal("40.00"));
            AccountSnapshot after = bank.withdraw("A100", new BigDecimal("40.00"));
            assertBigDecimalEquals(BigDecimal.ZERO, after.getBalance(), "zero");
        });

        runner.test("Bank: overdraft withdrawal rejected, balance unchanged", () -> {
            Bank bank = bank();
            bank.createAccount("A100", "Maya", new BigDecimal("20.00"));
            assertThrows(IllegalStateException.class,
                    () -> bank.withdraw("A100", new BigDecimal("50.00")), "overdraft");
            assertBigDecimalEquals(new BigDecimal("20.00"),
                    bank.findAccount("A100").orElseThrow().getBalance(), "unchanged");
            assertEquals(0, bank.findAccount("A100").orElseThrow().getTransactionCount(), "no txn");
        });

        runner.test("Bank: withdrawal unknown account rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> bank().withdraw("NOPE", BigDecimal.TEN), "unknown"));
    }

    private static void registerReports(TestRunner runner) {
        runner.test("Bank: total balance sums accounts", () -> {
            Bank bank = bank();
            bank.createAccount("A100", "Maya", new BigDecimal("100.50"));
            bank.createAccount("A200", "Jonas", new BigDecimal("249.50"));
            assertBigDecimalEquals(new BigDecimal("350.00"), bank.getTotalBalance(), "sum");
        });

        runner.test("Bank: transaction history lookup works", () -> {
            Bank bank = bank();
            bank.createAccount("A100", "Maya", BigDecimal.ZERO);
            bank.deposit("A100", new BigDecimal("10.00"));
            bank.withdraw("A100", new BigDecimal("4.00"));
            List<TransactionSnapshot> history = bank.getTransactionHistory("A100");
            assertEquals(2, history.size(), "two txns");
            assertThrows(UnsupportedOperationException.class, () -> history.clear(), "unmodifiable");
        });

        runner.test("Bank: find accounts by owner (case-insensitive)", () -> {
            Bank bank = bank();
            bank.createAccount("A100", "Maya", BigDecimal.ZERO);
            bank.createAccount("A200", "maya", BigDecimal.ZERO);
            bank.createAccount("A300", "Jonas", BigDecimal.ZERO);
            assertEquals(2, bank.findAccountsByOwner("MAYA").size(), "two Mayas");
        });
    }
}
