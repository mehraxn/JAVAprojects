package bankaccountsimulator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static bankaccountsimulator.TestSupport.assertBigDecimalEquals;
import static bankaccountsimulator.TestSupport.assertEquals;
import static bankaccountsimulator.TestSupport.assertNull;
import static bankaccountsimulator.TestSupport.assertThrows;

final class TransactionTest {

    private TransactionTest() {
    }

    private static final LocalDateTime TS = LocalDateTime.of(2026, 1, 15, 10, 0);

    private static Transaction sample() {
        return new Transaction("T0001", TransactionType.TRANSFER_OUT, new BigDecimal("75.00"),
                TS, "Transfer to A200", new BigDecimal("25.00"), "A200");
    }

    static void register(TestRunner runner) {
        runner.test("Transaction: valid creation stores all fields", () -> {
            Transaction txn = sample();
            assertEquals("T0001", txn.getTransactionId(), "id");
            assertEquals(TransactionType.TRANSFER_OUT, txn.getType(), "type");
            assertBigDecimalEquals(new BigDecimal("75.00"), txn.getAmount(), "amount");
            assertEquals(TS, txn.getTimestamp(), "timestamp");
            assertEquals("Transfer to A200", txn.getDescription(), "description");
            assertBigDecimalEquals(new BigDecimal("25.00"), txn.getBalanceAfter(), "balanceAfter");
            assertEquals("A200", txn.getRelatedAccountNumber(), "related");
        });

        runner.test("Transaction: related account is optional (null allowed)", () -> {
            Transaction txn = new Transaction("T0001", TransactionType.DEPOSIT, BigDecimal.TEN,
                    TS, "Deposit", BigDecimal.TEN, null);
            assertNull(txn.getRelatedAccountNumber(), "null related ok");
        });

        runner.test("Transaction: blank related account rejected when present", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new Transaction("T0001", TransactionType.DEPOSIT, BigDecimal.TEN,
                                TS, "Deposit", BigDecimal.TEN, "  "), "blank related"));

        runner.test("Transaction: null/blank transaction ID rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new Transaction(null, TransactionType.DEPOSIT, BigDecimal.TEN, TS,
                            "d", BigDecimal.TEN, null), "null id");
            assertThrows(IllegalArgumentException.class,
                    () -> new Transaction("  ", TransactionType.DEPOSIT, BigDecimal.TEN, TS,
                            "d", BigDecimal.TEN, null), "blank id");
        });

        runner.test("Transaction: null type rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new Transaction("T1", null, BigDecimal.TEN, TS, "d",
                                BigDecimal.TEN, null), "null type"));

        runner.test("Transaction: null/zero/negative amount rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new Transaction("T1", TransactionType.DEPOSIT, null, TS, "d",
                            BigDecimal.TEN, null), "null amount");
            assertThrows(IllegalArgumentException.class,
                    () -> new Transaction("T1", TransactionType.DEPOSIT, BigDecimal.ZERO, TS, "d",
                            BigDecimal.TEN, null), "zero amount");
            assertThrows(IllegalArgumentException.class,
                    () -> new Transaction("T1", TransactionType.DEPOSIT, new BigDecimal("-1"), TS,
                            "d", BigDecimal.TEN, null), "negative amount");
        });

        runner.test("Transaction: null timestamp rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new Transaction("T1", TransactionType.DEPOSIT, BigDecimal.TEN, null,
                                "d", BigDecimal.TEN, null), "null timestamp"));

        runner.test("Transaction: null/blank description rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new Transaction("T1", TransactionType.DEPOSIT, BigDecimal.TEN, TS, null,
                            BigDecimal.TEN, null), "null description");
            assertThrows(IllegalArgumentException.class,
                    () -> new Transaction("T1", TransactionType.DEPOSIT, BigDecimal.TEN, TS, "  ",
                            BigDecimal.TEN, null), "blank description");
        });

        runner.test("Transaction: null/negative balanceAfter rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new Transaction("T1", TransactionType.DEPOSIT, BigDecimal.TEN, TS, "d",
                            null, null), "null balanceAfter");
            assertThrows(IllegalArgumentException.class,
                    () -> new Transaction("T1", TransactionType.DEPOSIT, BigDecimal.TEN, TS, "d",
                            new BigDecimal("-0.01"), null), "negative balanceAfter");
        });

        runner.test("Transaction: zero balanceAfter allowed", () -> {
            Transaction txn = new Transaction("T1", TransactionType.WITHDRAWAL, BigDecimal.TEN,
                    TS, "Withdrawal", BigDecimal.ZERO, null);
            assertBigDecimalEquals(BigDecimal.ZERO, txn.getBalanceAfter(), "zero balanceAfter ok");
        });

        runner.test("Transaction: snapshot contains expected data", () -> {
            TransactionSnapshot snapshot = sample().toSnapshot();
            assertEquals("T0001", snapshot.getTransactionId(), "snapshot id");
            assertEquals(TransactionType.TRANSFER_OUT, snapshot.getType(), "snapshot type");
            assertBigDecimalEquals(new BigDecimal("75.00"), snapshot.getAmount(), "snapshot amount");
            assertBigDecimalEquals(new BigDecimal("25.00"), snapshot.getBalanceAfter(), "snapshot balAfter");
            assertEquals("A200", snapshot.getRelatedAccountNumber(), "snapshot related");
        });
    }
}
