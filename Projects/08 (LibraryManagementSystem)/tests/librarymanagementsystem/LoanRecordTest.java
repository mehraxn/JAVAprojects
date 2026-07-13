package librarymanagementsystem;

import java.time.LocalDate;

import static librarymanagementsystem.TestSupport.assertEquals;
import static librarymanagementsystem.TestSupport.assertFalse;
import static librarymanagementsystem.TestSupport.assertNull;
import static librarymanagementsystem.TestSupport.assertThrows;
import static librarymanagementsystem.TestSupport.assertTrue;

final class LoanRecordTest {

    private LoanRecordTest() {
    }

    private static final LocalDate BORROW = LocalDate.of(2026, 1, 15);
    private static final LocalDate DUE = BORROW.plusDays(14);

    private static LoanRecord sample() {
        return new LoanRecord("L0001", "978-1", "M001", BORROW, DUE);
    }

    static void register(TestRunner runner) {
        runner.test("Loan: valid creation stores fields, starts ACTIVE with null returnDate", () -> {
            LoanRecord loan = sample();
            assertEquals("L0001", loan.getLoanId(), "id");
            assertEquals("978-1", loan.getIsbn(), "isbn");
            assertEquals("M001", loan.getMemberId(), "member");
            assertEquals(BORROW, loan.getBorrowDate(), "borrow");
            assertEquals(DUE, loan.getDueDate(), "due");
            assertNull(loan.getReturnDate(), "no return date");
            assertEquals(LoanStatus.ACTIVE, loan.getStatus(), "active");
        });

        runner.test("Loan: null/blank loan ID rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new LoanRecord(null, "i", "m", BORROW, DUE), "null id");
            assertThrows(IllegalArgumentException.class,
                    () -> new LoanRecord("  ", "i", "m", BORROW, DUE), "blank id");
        });

        runner.test("Loan: null/blank ISBN and member ID rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new LoanRecord("L1", "  ", "m", BORROW, DUE), "blank isbn");
            assertThrows(IllegalArgumentException.class,
                    () -> new LoanRecord("L1", "i", null, BORROW, DUE), "null member");
        });

        runner.test("Loan: null borrow/due date rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new LoanRecord("L1", "i", "m", null, DUE), "null borrow");
            assertThrows(IllegalArgumentException.class,
                    () -> new LoanRecord("L1", "i", "m", BORROW, null), "null due");
        });

        runner.test("Loan: due date before borrow date rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new LoanRecord("L1", "i", "m", BORROW, BORROW.minusDays(1)),
                        "due before borrow"));

        runner.test("Loan: markReturned sets returnDate and RETURNED status", () -> {
            LoanRecord loan = sample();
            LocalDate returnedOn = BORROW.plusDays(5);
            loan.markReturned(returnedOn);
            assertEquals(returnedOn, loan.getReturnDate(), "return date");
            assertEquals(LoanStatus.RETURNED, loan.getStatus(), "returned");
            assertTrue(loan.isReturned(), "isReturned");
        });

        runner.test("Loan: markReturned rejects null / before-borrow / already-returned", () -> {
            LoanRecord loan = sample();
            assertThrows(IllegalArgumentException.class, () -> loan.markReturned(null), "null");
            assertThrows(IllegalArgumentException.class,
                    () -> loan.markReturned(BORROW.minusDays(1)), "before borrow");
            loan.markReturned(BORROW.plusDays(3));
            assertThrows(IllegalStateException.class,
                    () -> loan.markReturned(BORROW.plusDays(4)), "already returned");
        });

        runner.test("Loan: active loan overdue after due date, not before", () -> {
            LoanRecord loan = sample();
            assertFalse(loan.isOverdue(DUE), "not overdue on due date");
            assertFalse(loan.isOverdue(DUE.minusDays(1)), "not overdue before");
            assertTrue(loan.isOverdue(DUE.plusDays(1)), "overdue after");
        });

        runner.test("Loan: returned loan is never overdue", () -> {
            LoanRecord loan = sample();
            loan.markReturned(DUE.plusDays(1));
            assertFalse(loan.isOverdue(DUE.plusDays(30)), "returned not overdue");
        });

        runner.test("Loan: snapshot contains expected data (overdue computed)", () -> {
            LoanRecordSnapshot snapshot = sample().toSnapshot(DUE.plusDays(2));
            assertEquals("L0001", snapshot.getLoanId(), "snapshot id");
            assertEquals("978-1", snapshot.getIsbn(), "snapshot isbn");
            assertEquals(LoanStatus.ACTIVE, snapshot.getStatus(), "snapshot status");
            assertTrue(snapshot.isOverdue(), "snapshot overdue");
            assertNull(snapshot.getReturnDate(), "snapshot no return date");
        });
    }
}
