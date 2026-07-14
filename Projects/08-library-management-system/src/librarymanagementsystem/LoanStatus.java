package librarymanagementsystem;

/**
 * Lifecycle state of a {@link LoanRecord}.
 *
 * <p>A loan starts {@link #ACTIVE} and becomes {@link #RETURNED} exactly once.
 * Returned loans stay in history but no longer hold a book or count toward a
 * member's borrowing limit.
 */
public enum LoanStatus {
    ACTIVE,
    RETURNED
}
