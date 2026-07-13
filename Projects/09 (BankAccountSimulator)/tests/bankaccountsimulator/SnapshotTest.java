package bankaccountsimulator;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static bankaccountsimulator.TestSupport.assertBigDecimalEquals;
import static bankaccountsimulator.TestSupport.assertEquals;
import static bankaccountsimulator.TestSupport.assertThrows;

/**
 * Proves that data leaving {@link Bank} is safe: returned lists are unmodifiable,
 * snapshots carry no mutators, and holding a snapshot cannot change bank state.
 */
final class SnapshotTest {

    private SnapshotTest() {
    }

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-01-15T10:00:00Z"), ZoneId.of("UTC"));

    private static Bank bank() {
        Bank bank = new Bank(FIXED_CLOCK);
        bank.createAccount("A100", "Maya", new BigDecimal("100.00"));
        bank.createAccount("A200", "Jonas", new BigDecimal("50.00"));
        return bank;
    }

    static void register(TestRunner runner) {
        runner.test("Snapshot: listAccounts result is unmodifiable", () -> {
            List<AccountSnapshot> accounts = bank().listAccounts();
            assertThrows(UnsupportedOperationException.class, () -> accounts.clear(), "accounts");
        });

        runner.test("Snapshot: transaction history list is unmodifiable", () -> {
            Bank bank = bank();
            bank.deposit("A100", new BigDecimal("10.00"));
            List<TransactionSnapshot> history = bank.getTransactionHistory("A100");
            assertThrows(UnsupportedOperationException.class, () -> history.clear(), "history");
        });

        runner.test("Snapshot: AccountSnapshot transaction list is unmodifiable", () -> {
            Bank bank = bank();
            bank.deposit("A100", new BigDecimal("10.00"));
            AccountSnapshot account = bank.findAccount("A100").orElseThrow();
            assertThrows(UnsupportedOperationException.class,
                    () -> account.getTransactions().clear(), "account txns");
        });

        runner.test("Snapshot: createAccount return value cannot mutate bank internals", () -> {
            Bank bank = new Bank(FIXED_CLOCK);
            AccountSnapshot account = bank.createAccount("A100", "Maya", new BigDecimal("100.00"));
            // A snapshot has no deposit/withdraw; balance is a value copy.
            assertBigDecimalEquals(new BigDecimal("100.00"), account.getBalance(), "snapshot balance");
            assertBigDecimalEquals(new BigDecimal("100.00"),
                    bank.findAccount("A100").orElseThrow().getBalance(), "internal intact");
        });

        runner.test("Snapshot: findAccount snapshot is decoupled from later changes", () -> {
            Bank bank = bank();
            AccountSnapshot before = bank.findAccount("A100").orElseThrow();
            bank.deposit("A100", new BigDecimal("500.00"));
            assertBigDecimalEquals(new BigDecimal("100.00"), before.getBalance(), "old snapshot unchanged");
            assertBigDecimalEquals(new BigDecimal("600.00"),
                    bank.findAccount("A100").orElseThrow().getBalance(), "fresh snapshot updated");
        });

        runner.test("Snapshot: deposit return value reflects a copy, internal stays consistent", () -> {
            Bank bank = bank();
            AccountSnapshot after = bank.deposit("A100", new BigDecimal("25.00"));
            assertBigDecimalEquals(new BigDecimal("125.00"), after.getBalance(), "returned balance");
            assertEquals(1, after.getTransactionCount(), "one txn in snapshot");
            assertBigDecimalEquals(new BigDecimal("125.00"),
                    bank.findAccount("A100").orElseThrow().getBalance(), "internal matches");
        });

        runner.test("Snapshot: transfer snapshots cannot corrupt internal balances", () -> {
            Bank bank = bank();
            bank.transfer("A100", "A200", new BigDecimal("40.00"));
            List<AccountSnapshot> accounts = bank.listAccounts();
            // Attempt to mutate the returned list; internal state must be unaffected.
            try {
                accounts.add(null);
            } catch (UnsupportedOperationException ignored) {
                // expected
            }
            assertBigDecimalEquals(new BigDecimal("60.00"),
                    bank.findAccount("A100").orElseThrow().getBalance(), "source intact");
            assertBigDecimalEquals(new BigDecimal("90.00"),
                    bank.findAccount("A200").orElseThrow().getBalance(), "target intact");
        });

        runner.test("Snapshot: reading snapshots leaves balances correct", () -> {
            Bank bank = bank();
            bank.listAccounts();
            bank.findAccount("A100");
            bank.getTransactionHistory("A100");
            assertBigDecimalEquals(new BigDecimal("150.00"), bank.getTotalBalance(), "total intact");
        });
    }
}
