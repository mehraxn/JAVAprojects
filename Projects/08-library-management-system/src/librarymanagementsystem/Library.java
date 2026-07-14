package librarymanagementsystem;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * In-memory service layer for the catalogue, members, and loans. This is the only
 * class outside callers use to change state.
 *
 * <p>Every query returns immutable snapshots ({@link BookSnapshot},
 * {@link MemberSnapshot}, {@link LoanRecordSnapshot}) in unmodifiable lists, so
 * live {@link Book}/{@link Member}/{@link LoanRecord} objects are never leaked and
 * cannot be mutated to bypass the library.
 *
 * <h2>Behaviour notes</h2>
 * <ul>
 *   <li>Borrowing and returning update the book, the member, and the loan record
 *       together; every check runs before any state changes, so a failed operation
 *       leaves all state unchanged.</li>
 *   <li>Dates come from an injectable {@link Clock}; the due date is the borrow
 *       date plus the loan period.</li>
 *   <li>A member may hold at most {@code borrowLimit} active loans.</li>
 *   <li>Loan IDs are generated deterministically as {@code L0001}, {@code L0002}, …</li>
 * </ul>
 */
public final class Library {
    /** Default number of days a loan lasts. */
    public static final int DEFAULT_LOAN_DAYS = 14;
    /** Default maximum number of active loans per member. */
    public static final int DEFAULT_BORROW_LIMIT = 3;

    private final Clock clock;
    private final int borrowLimit;
    private final int loanDays;

    private final Map<String, Book> books = new LinkedHashMap<>();
    private final Map<String, Member> members = new LinkedHashMap<>();
    private final List<LoanRecord> loans = new ArrayList<>();
    private int nextLoanNumber = 1;

    /** Uses the system clock and default borrow limit / loan period. */
    public Library() {
        this(Clock.systemDefaultZone());
    }

    /** Uses the supplied clock and default borrow limit / loan period. */
    public Library(Clock clock) {
        this(clock, DEFAULT_BORROW_LIMIT, DEFAULT_LOAN_DAYS);
    }

    /** Uses the supplied clock, borrow limit, and loan period. */
    public Library(Clock clock, int borrowLimit, int loanDays) {
        this.clock = Objects.requireNonNull(clock, "clock cannot be null");
        if (borrowLimit <= 0) {
            throw new IllegalArgumentException("Borrow limit must be positive");
        }
        if (loanDays <= 0) {
            throw new IllegalArgumentException("Loan days must be positive");
        }
        this.borrowLimit = borrowLimit;
        this.loanDays = loanDays;
    }

    // ---------------------------------------------------------- book management

    public BookSnapshot addBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book must not be null");
        }
        if (books.containsKey(book.getIsbn())) {
            throw new IllegalArgumentException("ISBN already exists: " + book.getIsbn());
        }
        books.put(book.getIsbn(), book);
        return book.toSnapshot();
    }

    public Optional<BookSnapshot> getBook(String isbn) {
        Book book = books.get(requireText(isbn, "ISBN"));
        return book == null ? Optional.empty() : Optional.of(book.toSnapshot());
    }

    public List<BookSnapshot> listBooks() {
        List<BookSnapshot> views = new ArrayList<>();
        for (Book book : books.values()) {
            views.add(book.toSnapshot());
        }
        return Collections.unmodifiableList(views);
    }

    public List<BookSnapshot> listAvailableBooks() {
        List<BookSnapshot> views = new ArrayList<>();
        for (Book book : books.values()) {
            if (book.isAvailable()) {
                views.add(book.toSnapshot());
            }
        }
        return Collections.unmodifiableList(views);
    }

    public int countAvailableBooks() {
        int count = 0;
        for (Book book : books.values()) {
            if (book.isAvailable()) {
                count++;
            }
        }
        return count;
    }

    public int countBorrowedBooks() {
        return books.size() - countAvailableBooks();
    }

    // -------------------------------------------------------- member management

    public MemberSnapshot addMember(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Member must not be null");
        }
        if (members.containsKey(member.getMemberId())) {
            throw new IllegalArgumentException("Member ID already exists: " + member.getMemberId());
        }
        members.put(member.getMemberId(), member);
        return member.toSnapshot();
    }

    public Optional<MemberSnapshot> getMember(String memberId) {
        Member member = members.get(requireText(memberId, "Member ID"));
        return member == null ? Optional.empty() : Optional.of(member.toSnapshot());
    }

    public List<MemberSnapshot> listMembers() {
        List<MemberSnapshot> views = new ArrayList<>();
        for (Member member : members.values()) {
            views.add(member.toSnapshot());
        }
        return Collections.unmodifiableList(views);
    }

    // ------------------------------------------------------------------ search

    /** Case-insensitive substring search over title and author, sorted by title. */
    public List<BookSnapshot> searchBooks(String query) {
        if (query == null) {
            throw new IllegalArgumentException("Search text must not be null");
        }
        String needle = query.trim().toLowerCase(Locale.ROOT);
        List<Book> matches = new ArrayList<>();
        for (Book book : books.values()) {
            if (book.getTitle().toLowerCase(Locale.ROOT).contains(needle)
                    || book.getAuthor().toLowerCase(Locale.ROOT).contains(needle)) {
                matches.add(book);
            }
        }
        matches.sort(Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Book::getIsbn, String.CASE_INSENSITIVE_ORDER));
        List<BookSnapshot> views = new ArrayList<>();
        for (Book book : matches) {
            views.add(book.toSnapshot());
        }
        return Collections.unmodifiableList(views);
    }

    // --------------------------------------------------------------- borrowing

    /**
     * Borrows a book for a member as a single consistent operation.
     *
     * <p>All checks (member exists, book exists, book available, member under the
     * borrow limit) run before any state changes, so a failure leaves the book,
     * the member, and the loan list untouched.
     *
     * @return a snapshot of the new active loan
     */
    public LoanRecordSnapshot borrowBook(String memberId, String isbn) {
        Member member = requireMember(memberId);
        Book book = requireBook(isbn);
        if (!book.isAvailable()) {
            throw new IllegalStateException("Book is unavailable: " + book.getIsbn());
        }
        if (member.getActiveLoanCount() >= borrowLimit) {
            throw new IllegalStateException("Member is at the borrow limit of " + borrowLimit);
        }
        if (member.hasBorrowed(book.getIsbn())) {
            throw new IllegalStateException("Member already has this book: " + book.getIsbn());
        }

        LocalDate borrowDate = LocalDate.now(clock);
        LocalDate dueDate = borrowDate.plusDays(loanDays);
        LoanRecord loan = new LoanRecord(nextLoanId(), book.getIsbn(), member.getMemberId(),
                borrowDate, dueDate);

        // Commit all related state together.
        book.markBorrowed();
        member.addBorrowed(book.getIsbn());
        loans.add(loan);
        return loan.toSnapshot(today());
    }

    /**
     * Returns a borrowed book for a member as a single consistent operation.
     *
     * <p>All checks (member exists, book exists, an active loan exists for this
     * member and book) run before any state changes, so a failure leaves all
     * state unchanged.
     *
     * @return a snapshot of the completed loan
     */
    public LoanRecordSnapshot returnBook(String memberId, String isbn) {
        Member member = requireMember(memberId);
        Book book = requireBook(isbn);
        LoanRecord loan = findActiveLoan(book.getIsbn());
        if (loan == null) {
            throw new IllegalStateException("Book is not currently borrowed: " + book.getIsbn());
        }
        if (!loan.getMemberId().equals(member.getMemberId())) {
            throw new IllegalStateException("Book was not borrowed by member: " + member.getMemberId());
        }

        // Commit all related state together.
        book.markReturned();
        member.removeBorrowed(book.getIsbn());
        loan.markReturned(LocalDate.now(clock));
        return loan.toSnapshot(today());
    }

    // ----------------------------------------------------------------- reports

    public List<LoanRecordSnapshot> listActiveLoans() {
        return loanSnapshots(true, false);
    }

    public List<LoanRecordSnapshot> listCompletedLoans() {
        return loanSnapshots(false, true);
    }

    /** Active loans whose due date is before today (from the library clock). */
    public List<LoanRecordSnapshot> listOverdueLoans() {
        LocalDate today = today();
        List<LoanRecordSnapshot> views = new ArrayList<>();
        for (LoanRecord loan : loans) {
            if (loan.isOverdue(today)) {
                views.add(loan.toSnapshot(today));
            }
        }
        return Collections.unmodifiableList(views);
    }

    public List<LoanRecordSnapshot> getLoanHistoryForMember(String memberId) {
        String id = requireText(memberId, "Member ID");
        LocalDate today = today();
        List<LoanRecordSnapshot> views = new ArrayList<>();
        for (LoanRecord loan : loans) {
            if (loan.getMemberId().equals(id)) {
                views.add(loan.toSnapshot(today));
            }
        }
        return Collections.unmodifiableList(views);
    }

    public List<LoanRecordSnapshot> getLoanHistoryForBook(String isbn) {
        String id = requireText(isbn, "ISBN");
        LocalDate today = today();
        List<LoanRecordSnapshot> views = new ArrayList<>();
        for (LoanRecord loan : loans) {
            if (loan.getIsbn().equals(id)) {
                views.add(loan.toSnapshot(today));
            }
        }
        return Collections.unmodifiableList(views);
    }

    // ------------------------------------------------------------------ helpers

    private List<LoanRecordSnapshot> loanSnapshots(boolean active, boolean returned) {
        LocalDate today = today();
        List<LoanRecordSnapshot> views = new ArrayList<>();
        for (LoanRecord loan : loans) {
            if ((active && loan.isActive()) || (returned && loan.isReturned())) {
                views.add(loan.toSnapshot(today));
            }
        }
        return Collections.unmodifiableList(views);
    }

    private LoanRecord findActiveLoan(String isbn) {
        for (LoanRecord loan : loans) {
            if (loan.isActive() && loan.getIsbn().equals(isbn)) {
                return loan;
            }
        }
        return null;
    }

    private LocalDate today() {
        return LocalDate.now(clock);
    }

    private String nextLoanId() {
        return String.format("L%04d", nextLoanNumber++);
    }

    private Book requireBook(String isbn) {
        Book book = books.get(requireText(isbn, "ISBN"));
        if (book == null) {
            throw new IllegalArgumentException("Unknown ISBN: " + isbn.trim());
        }
        return book;
    }

    private Member requireMember(String memberId) {
        Member member = members.get(requireText(memberId, "Member ID"));
        if (member == null) {
            throw new IllegalArgumentException("Unknown member ID: " + memberId.trim());
        }
        return member;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
