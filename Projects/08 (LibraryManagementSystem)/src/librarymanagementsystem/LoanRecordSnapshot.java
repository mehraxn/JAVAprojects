package librarymanagementsystem;

import java.time.LocalDate;

/** Immutable, read-only view of a {@link LoanRecord}. */
public final class LoanRecordSnapshot {
    private final String loanId;
    private final String isbn;
    private final String memberId;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private final LocalDate returnDate;
    private final LoanStatus status;
    private final boolean overdue;

    LoanRecordSnapshot(String loanId, String isbn, String memberId, LocalDate borrowDate,
                       LocalDate dueDate, LocalDate returnDate, LoanStatus status, boolean overdue) {
        this.loanId = loanId;
        this.isbn = isbn;
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
        this.overdue = overdue;
    }

    public String getLoanId() { return loanId; }
    public String getIsbn() { return isbn; }
    public String getMemberId() { return memberId; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }

    /** Return date, or {@code null} while the loan is still active. */
    public LocalDate getReturnDate() { return returnDate; }
    public LoanStatus getStatus() { return status; }
    public boolean isOverdue() { return overdue; }

    @Override
    public String toString() {
        return loanId + " isbn=" + isbn + " member=" + memberId + " due=" + dueDate
                + " " + status + (overdue ? " (OVERDUE)" : "");
    }
}
