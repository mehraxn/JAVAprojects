package bankaccountsimulator;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static bankaccountsimulator.TestSupport.assertBigDecimalEquals;
import static bankaccountsimulator.TestSupport.assertEquals;
import static bankaccountsimulator.TestSupport.assertThrows;

final class TransferTest {

    private TransferTest() {
    }

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-01-15T10:00:00Z"), ZoneId.of("UTC"));
    private static final LocalDateTime FIXED_TIME = LocalDateTime.now(FIXED_CLOCK);

    /** A100=300.00, A200=50.00. */
    private static Bank bank() {
        Bank bank = new Bank(FIXED_CLOCK);
        bank.createAccount("A100", "Maya", new BigDecimal("300.00"));
        bank.createAccount("A200", "Jonas", new BigDecimal("50.00"));
        return bank;
    }

    private static BigDecimal balance(Bank bank, String number) {
        return bank.findAccount(number).orElseThrow().getBalance();
    }

    private static TransactionSnapshot only(Bank bank, String number) {
        List<TransactionSnapshot> txns = bank.getTransactionHistory(number);
        return txns.get(txns.size() - 1);
    }

    static void register(TestRunner runner) {
        runner.test("Transfer: moves funds between accounts", () -> {
            Bank bank = bank();
            bank.transfer("A100", "A200", new BigDecimal("120.00"));
            assertBigDecimalEquals(new BigDecimal("180.00"), balance(bank, "A100"), "source -120");
            assertBigDecimalEquals(new BigDecimal("170.00"), balance(bank, "A200"), "target +120");
        });

        runner.test("Transfer: records TRANSFER_OUT and TRANSFER_IN with related accounts", () -> {
            Bank bank = bank();
            bank.transfer("A100", "A200", new BigDecimal("75.00"));
            TransactionSnapshot out = only(bank, "A100");
            TransactionSnapshot in = only(bank, "A200");
            assertEquals(TransactionType.TRANSFER_OUT, out.getType(), "out type");
            assertEquals("A200", out.getRelatedAccountNumber(), "out related");
            assertEquals(TransactionType.TRANSFER_IN, in.getType(), "in type");
            assertEquals("A100", in.getRelatedAccountNumber(), "in related");
        });

        runner.test("Transfer: two transactions get deterministic sequential IDs", () -> {
            Bank bank = bank();
            bank.transfer("A100", "A200", new BigDecimal("75.00"));
            assertEquals("T0001", only(bank, "A100").getTransactionId(), "out T0001");
            assertEquals("T0002", only(bank, "A200").getTransactionId(), "in T0002");
        });

        runner.test("Transfer: both legs share the fixed-clock timestamp", () -> {
            Bank bank = bank();
            bank.transfer("A100", "A200", new BigDecimal("75.00"));
            assertEquals(FIXED_TIME, only(bank, "A100").getTimestamp(), "out ts");
            assertEquals(FIXED_TIME, only(bank, "A200").getTimestamp(), "in ts");
        });

        runner.test("Transfer: balanceAfter recorded on both legs", () -> {
            Bank bank = bank();
            bank.transfer("A100", "A200", new BigDecimal("100.00"));
            assertBigDecimalEquals(new BigDecimal("200.00"), only(bank, "A100").getBalanceAfter(), "out bal");
            assertBigDecimalEquals(new BigDecimal("150.00"), only(bank, "A200").getBalanceAfter(), "in bal");
        });

        runner.test("Transfer: self-transfer rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> bank().transfer("A100", "A100", new BigDecimal("10.00")), "self"));

        runner.test("Transfer: unknown source rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> bank().transfer("NOPE", "A200", new BigDecimal("10.00")), "source"));

        runner.test("Transfer: unknown target rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> bank().transfer("A100", "NOPE", new BigDecimal("10.00")), "target"));

        runner.test("Transfer: insufficient funds rejected", () ->
                assertThrows(IllegalStateException.class,
                        () -> bank().transfer("A100", "A200", new BigDecimal("999.00")), "funds"));

        runner.test("Transfer: null/zero/negative amount rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> bank().transfer("A100", "A200", null), "null");
            assertThrows(IllegalArgumentException.class,
                    () -> bank().transfer("A100", "A200", BigDecimal.ZERO), "zero");
            assertThrows(IllegalArgumentException.class,
                    () -> bank().transfer("A100", "A200", new BigDecimal("-1")), "negative");
        });

        runner.test("Transfer: all-or-nothing for missing target (no state change)", () -> {
            Bank bank = bank();
            assertThrows(IllegalArgumentException.class,
                    () -> bank.transfer("A100", "NOPE", new BigDecimal("50.00")), "missing target");
            assertBigDecimalEquals(new BigDecimal("300.00"), balance(bank, "A100"), "source unchanged");
            assertEquals(0, bank.findAccount("A100").orElseThrow().getTransactionCount(),
                    "no source txn");
        });

        runner.test("Transfer: all-or-nothing for insufficient funds (no state change)", () -> {
            Bank bank = bank();
            assertThrows(IllegalStateException.class,
                    () -> bank.transfer("A100", "A200", new BigDecimal("999.00")), "insufficient");
            assertBigDecimalEquals(new BigDecimal("300.00"), balance(bank, "A100"), "source unchanged");
            assertBigDecimalEquals(new BigDecimal("50.00"), balance(bank, "A200"), "target unchanged");
            assertEquals(0, bank.findAccount("A100").orElseThrow().getTransactionCount(), "no source txn");
            assertEquals(0, bank.findAccount("A200").orElseThrow().getTransactionCount(), "no target txn");
        });

        runner.test("Transfer: successful transfer creates exactly two transactions", () -> {
            Bank bank = bank();
            bank.transfer("A100", "A200", new BigDecimal("75.00"));
            assertEquals(1, bank.findAccount("A100").orElseThrow().getTransactionCount(), "one on source");
            assertEquals(1, bank.findAccount("A200").orElseThrow().getTransactionCount(), "one on target");
        });
    }
}
