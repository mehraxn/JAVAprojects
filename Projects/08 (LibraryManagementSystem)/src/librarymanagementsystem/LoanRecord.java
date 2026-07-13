package librarymanagementsystem;

import java.time.LocalDate;

/**
 * One borrowing event: which member borrowed which book, when, when it is due,
 * and — once returned — when it came back.
 *
 * <p>Everything is fixed at construction except the {@code returnDate}/{@code
 * status} transition, which happens exactly once via {@link #markReturned}. A loan
 * starts {@link LoanStatus#ACTIVE} with a {@code null} return date. An active loan
 * is overdue when "today" is after its due date; returned loans are never overdue.
 */
public final class LoanRecord {
    private final String loanId;
    private final String isbn;
    private final String memberId;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;
    private LoanStatus status;

    public LoanRecord(String loanId, String isbn, String memberId,
                      LocalDate borrowDate, LocalDate dueDate) {
        this.loanId = requireText(loanId, "Loan ID");
        this.isbn = requireText(isbn, "ISBN");
        this.memberId = requireText(memberId, "Member ID");
        if (borrowDate == null) {
            throw new IllegalArgumentException("Borrow date must not be null");
        }
        if (dueDate == null) {
            throw new IllegalArgumentException("Due date must not be null");
        }
        if (dueDate.isBefore(borrowDate)) {
            throw new IllegalArgumentException("Due date must not be before borrow date");
        }
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = null;
        this.status = LoanStatus.ACTIVE;
    }

    public String getLoanId() { return loanId; }
    public String getIsbn() { return isbn; }
    public String getMemberId() { return memberId; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }

    /** Return date, or {@code null} while the loan is still active. */
    public LocalDate getReturnDate() { return returnDate; }
    public LoanStatus getStatus() { return status; }

    public boolean isActive() { return status == LoanStatus.ACTIVE; }
    public boolean isReturned() { return status == LoanStatus.RETURNED; }

    /** True only for an active loan whose due date is before {@code today}. */
    public boolean isOverdue(LocalDate today) {
        if (today == null) {
            throw new IllegalArgumentException("Today must not be null");
        }
        return status == LoanStatus.ACTIVE && today.isAfter(dueDate);
    }

    /** Completes the loan; rejects a null/early return date or a second return. */
    void markReturned(LocalDate returnedOn) {
        if (returnedOn == null) {
            throw new IllegalArgumentException("Return date must not be null");
        }
        if (status == LoanStatus.RETURNED) {
            throw new IllegalStateException("Loan already returned: " + loanId);
        }
        if (returnedOn.isBefore(borrowDate)) {
            throw new IllegalArgumentException("Return date must not be before borrow date");
        }
        this.returnDate = returnedOn;
        this.status = LoanStatus.RETURNED;
    }

    /** Immutable, read-only view of this loan (overdue computed for {@code today}). */
    public LoanRecordSnapshot toSnapshot(LocalDate today) {
        return new LoanRecordSnapshot(loanId, isbn, memberId, borrowDate, dueDate,
                returnDate, status, isOverdue(today));
    }

    @Override
    public String toString() {
        return loanId + " isbn=" + isbn + " member=" + memberId + " due=" + dueDate
                + " " + status;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
