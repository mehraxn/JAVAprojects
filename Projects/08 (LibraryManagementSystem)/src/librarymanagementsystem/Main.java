package librarymanagementsystem;

import java.io.PrintStream;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/**
 * Command-line entry point and demo driver.
 *
 * <p>All work happens in {@link #run(String[], PrintStream, PrintStream)}, which
 * returns an exit code and never calls {@link System#exit}. Only
 * {@link #main(String[])} exits the JVM, so the CLI can be tested in-process.
 */
public final class Main {

    // Fixed clock so demo borrow/due dates are stable and reproducible.
    private static final Clock DEMO_CLOCK =
            Clock.fixed(Instant.parse("2026-01-15T10:00:00Z"), ZoneId.of("UTC"));

    private Main() {
    }

    public static void main(String[] args) {
        int exitCode = run(args, System.out, System.err);
        System.exit(exitCode);
    }

    /**
     * Runs one CLI command.
     *
     * @return {@code 0} for a recognised command (including {@code validation-demo},
     *         whose failures are intentional), non-zero for an unknown command.
     */
    public static int run(String[] args, PrintStream out, PrintStream err) {
        String command = (args == null || args.length == 0) ? "help" : args[0];
        switch (command) {
            case "help":
            case "--help":
            case "-h":
                printHelp(out);
                return 0;
            case "demo":
                runDemo(out);
                return 0;
            case "borrow-demo":
                runBorrowDemo(out);
                return 0;
            case "return-demo":
                runReturnDemo(out);
                return 0;
            case "search-demo":
                runSearchDemo(out);
                return 0;
            case "overdue-demo":
                runOverdueDemo(out);
                return 0;
            case "history-demo":
                runHistoryDemo(out);
                return 0;
            case "validation-demo":
                runValidationDemo(out);
                return 0;
            default:
                err.println("Unknown command: " + command);
                err.println("Run 'help' to see available commands.");
                return 2;
        }
    }

    private static void printHelp(PrintStream out) {
        out.println("Library Management System");
        out.println();
        out.println("Usage: java -cp out librarymanagementsystem.Main <command>");
        out.println();
        out.println("Commands:");
        out.println("  help             Show this help text.");
        out.println("  demo             End-to-end add/borrow/return walkthrough.");
        out.println("  borrow-demo      Borrowing, double-borrow, limit, and unknown rejections.");
        out.println("  return-demo      Returning, wrong-member, and already-available rejections.");
        out.println("  search-demo      Case-insensitive title/author search and availability.");
        out.println("  overdue-demo     Due-date tracking and overdue detection with fixed dates.");
        out.println("  history-demo     Active, completed, member, and book loan history.");
        out.println("  validation-demo  Intentional validation failures, handled cleanly.");
    }

    // ------------------------------------------------------------------- demos

    private static Library sampleLibrary() {
        Library library = new Library(DEMO_CLOCK);
        library.addBook(new Book("978-1", "Clean Code", "Robert C. Martin", "Programming", 2008));
        library.addBook(new Book("978-2", "Effective Java", "Joshua Bloch", "Programming", 2018));
        library.addBook(new Book("978-3", "The Pragmatic Programmer", "Andrew Hunt", "Programming", 1999));
        library.addBook(new Book("978-4", "Refactoring", "Martin Fowler", "Programming", 2018));
        library.addMember(new Member("M001", "Elena", "elena@example.com"));
        library.addMember(new Member("M002", "Noah", "noah@example.com"));
        return library;
    }

    private static void runDemo(PrintStream out) {
        out.println("== Library Demo ==");
        Library library = sampleLibrary();
        out.println("Books: " + library.listBooks().size() + ", members: "
                + library.listMembers().size());

        out.println();
        out.println("Elena borrows 'Effective Java' (978-2)...");
        LoanRecordSnapshot loan = library.borrowBook("M001", "978-2");
        out.println("  Loan " + loan.getLoanId() + " borrowed " + loan.getBorrowDate()
                + ", due " + loan.getDueDate());
        out.println("  'Effective Java' available: "
                + library.getBook("978-2").orElseThrow().isAvailable());
        out.println("  Available books now: " + library.countAvailableBooks());

        out.println();
        out.println("Elena returns 'Effective Java'...");
        library.returnBook("M001", "978-2");
        out.println("  Available books now: " + library.countAvailableBooks());

        out.println();
        out.println("Loan history for 978-2:");
        printLoans(out, library.getLoanHistoryForBook("978-2"));
    }

    private static void runBorrowDemo(PrintStream out) {
        out.println("== Borrow Demo ==");
        Library library = new Library(DEMO_CLOCK, 2, Library.DEFAULT_LOAN_DAYS);
        library.addBook(new Book("978-1", "Clean Code", "Robert C. Martin"));
        library.addBook(new Book("978-2", "Effective Java", "Joshua Bloch"));
        library.addBook(new Book("978-3", "Refactoring", "Martin Fowler"));
        library.addMember(new Member("M001", "Elena"));

        LoanRecordSnapshot loan = library.borrowBook("M001", "978-1");
        out.println("Borrowed 978-1 -> loan " + loan.getLoanId() + " due " + loan.getDueDate());

        out.println();
        expectFailure(out, "double borrow of same book",
                () -> library.borrowBook("M001", "978-1"));

        library.borrowBook("M001", "978-2");
        out.println("Borrowed 978-2 (member now at limit of 2)");
        expectFailure(out, "borrow beyond limit of 2",
                () -> library.borrowBook("M001", "978-3"));

        out.println();
        expectFailure(out, "unknown member", () -> library.borrowBook("NOPE", "978-3"));
        expectFailure(out, "unknown book", () -> library.borrowBook("M001", "000-0"));
    }

    private static void runReturnDemo(PrintStream out) {
        out.println("== Return Demo ==");
        Library library = sampleLibrary();
        library.borrowBook("M001", "978-1");
        out.println("Elena borrowed 978-1. Available books: " + library.countAvailableBooks());

        out.println();
        expectFailure(out, "wrong-member return (Noah returns Elena's book)",
                () -> library.returnBook("M002", "978-1"));
        expectFailure(out, "return of a book that is available (978-2)",
                () -> library.returnBook("M001", "978-2"));

        out.println();
        LoanRecordSnapshot returned = library.returnBook("M001", "978-1");
        out.println("Elena returned 978-1 -> loan " + returned.getLoanId()
                + " status " + returned.getStatus() + ", returnDate " + returned.getReturnDate());
        out.println("Available books: " + library.countAvailableBooks());
        out.println("Elena active loans: "
                + library.getMember("M001").orElseThrow().getActiveLoanCount());
    }

    private static void runSearchDemo(PrintStream out) {
        out.println("== Search Demo ==");
        Library library = sampleLibrary();

        out.println("Search 'java': " + titles(library.searchBooks("java")));
        out.println("Search 'MARTIN' (author, case-insensitive): "
                + titles(library.searchBooks("MARTIN")));
        out.println("Search 'zzz' (no match): " + titles(library.searchBooks("zzz")));

        out.println();
        out.println("Available before borrow: " + library.countAvailableBooks());
        library.borrowBook("M001", "978-2");
        out.println("After borrowing 'Effective Java', available list: "
                + titles(library.listAvailableBooks()));
        library.returnBook("M001", "978-2");
        out.println("After returning it, available count: " + library.countAvailableBooks());
    }

    private static void runOverdueDemo(PrintStream out) {
        out.println("== Overdue Demo (fixed dates) ==");
        LocalDate borrow = LocalDate.of(2026, 1, 15);
        LocalDate due = borrow.plusDays(14);
        LoanRecord loan = new LoanRecord("L0001", "978-1", "M001", borrow, due);
        out.println("Loan " + loan.getLoanId() + " borrowed " + borrow + ", due " + due);

        LocalDate beforeDue = due.minusDays(1);
        LocalDate afterDue = due.plusDays(3);
        out.println("Overdue on " + beforeDue + " (before due)? " + loan.isOverdue(beforeDue));
        out.println("Overdue on " + afterDue + " (after due)?  " + loan.isOverdue(afterDue));

        out.println();
        out.println("Member returns the book on " + afterDue + " ...");
        loan.markReturned(afterDue);
        out.println("Status: " + loan.getStatus());
        out.println("Overdue after return? " + loan.isOverdue(afterDue.plusDays(10))
                + " (returned loans are never overdue)");
    }

    private static void runHistoryDemo(PrintStream out) {
        out.println("== History Demo ==");
        Library library = sampleLibrary();
        library.borrowBook("M001", "978-1");
        library.borrowBook("M001", "978-2");
        library.borrowBook("M002", "978-3");
        library.returnBook("M001", "978-1");

        out.println("Active loans:");
        printLoans(out, library.listActiveLoans());
        out.println("Completed loans:");
        printLoans(out, library.listCompletedLoans());

        out.println();
        out.println("Elena (M001) loan history:");
        printLoans(out, library.getLoanHistoryForMember("M001"));
        out.println("Book 978-1 loan history:");
        printLoans(out, library.getLoanHistoryForBook("978-1"));
    }

    private static void runValidationDemo(PrintStream out) {
        out.println("== Validation Demo (failures below are intentional) ==");
        Library library = sampleLibrary();

        expectFailure(out, "blank ISBN", () -> new Book("  ", "T", "A"));
        expectFailure(out, "blank title", () -> new Book("X-1", "  ", "A"));
        expectFailure(out, "blank author", () -> new Book("X-1", "T", "  "));
        expectFailure(out, "duplicate ISBN",
                () -> library.addBook(new Book("978-1", "Clone", "Someone")));
        expectFailure(out, "blank member ID", () -> new Member("  ", "Name"));
        expectFailure(out, "blank member name", () -> new Member("M9", "  "));
        expectFailure(out, "invalid email", () -> new Member("M9", "Name", "not-an-email"));
        expectFailure(out, "duplicate member",
                () -> library.addMember(new Member("M001", "Clone")));
        expectFailure(out, "unknown member borrow",
                () -> library.borrowBook("NOPE", "978-1"));
        expectFailure(out, "unknown book borrow",
                () -> library.borrowBook("M001", "000-0"));
        expectFailure(out, "wrong-member return", () -> {
            library.borrowBook("M001", "978-1");
            library.returnBook("M002", "978-1");
        });

        out.println();
        out.println("All validation failures were handled cleanly.");
    }

    // ----------------------------------------------------------------- helpers

    private static String titles(List<BookSnapshot> books) {
        if (books.isEmpty()) {
            return "(none)";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < books.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(books.get(i).getTitle());
        }
        return builder.toString();
    }

    private static void printLoans(PrintStream out, List<LoanRecordSnapshot> loans) {
        if (loans.isEmpty()) {
            out.println("  (none)");
            return;
        }
        for (LoanRecordSnapshot loan : loans) {
            out.println("  " + loan.getLoanId() + "  isbn " + loan.getIsbn()
                    + "  member " + loan.getMemberId() + "  due " + loan.getDueDate()
                    + "  " + loan.getStatus()
                    + (loan.isOverdue() ? " (OVERDUE)" : ""));
        }
    }

    private static void expectFailure(PrintStream out, String label, Runnable action) {
        try {
            action.run();
            out.println("  [" + label + "] ERROR: expected a failure but none occurred");
        } catch (IllegalArgumentException | IllegalStateException expected) {
            out.println("  [" + label + "] rejected: " + expected.getMessage());
        }
    }
}
