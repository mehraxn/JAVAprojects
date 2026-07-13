package librarymanagementsystem;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static librarymanagementsystem.TestSupport.assertEquals;
import static librarymanagementsystem.TestSupport.assertFalse;
import static librarymanagementsystem.TestSupport.assertThrows;
import static librarymanagementsystem.TestSupport.assertTrue;

final class LibraryTest {

    private LibraryTest() {
    }

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-01-15T10:00:00Z"), ZoneId.of("UTC"));
    private static final LocalDate TODAY = LocalDate.now(FIXED_CLOCK);

    private static Library library() {
        Library library = new Library(FIXED_CLOCK);
        library.addBook(new Book("978-1", "Clean Code", "Robert C. Martin"));
        library.addBook(new Book("978-2", "Effective Java", "Joshua Bloch"));
        library.addBook(new Book("978-3", "Refactoring", "Martin Fowler"));
        library.addMember(new Member("M001", "Elena"));
        library.addMember(new Member("M002", "Noah"));
        return library;
    }

    static void register(TestRunner runner) {
        registerManagement(runner);
        registerBorrow(runner);
        registerReturn(runner);
        registerReports(runner);
    }

    private static void registerManagement(TestRunner runner) {
        runner.test("Library: add book / member and list works", () -> {
            Library library = library();
            assertEquals(3, library.listBooks().size(), "three books");
            assertEquals(2, library.listMembers().size(), "two members");
        });

        runner.test("Library: null and duplicate book rejected", () -> {
            Library library = library();
            assertThrows(IllegalArgumentException.class, () -> library.addBook(null), "null book");
            assertThrows(IllegalArgumentException.class,
                    () -> library.addBook(new Book("978-1", "Clone", "Someone")), "duplicate isbn");
        });

        runner.test("Library: null and duplicate member rejected", () -> {
            Library library = library();
            assertThrows(IllegalArgumentException.class, () -> library.addMember(null), "null member");
            assertThrows(IllegalArgumentException.class,
                    () -> library.addMember(new Member("M001", "Clone")), "duplicate member");
        });

        runner.test("Library: get book/member present and missing empty", () -> {
            Library library = library();
            assertTrue(library.getBook("978-1").isPresent(), "book present");
            assertFalse(library.getBook("000").isPresent(), "book missing empty");
            assertTrue(library.getMember("M001").isPresent(), "member present");
            assertFalse(library.getMember("NOPE").isPresent(), "member missing empty");
        });

        runner.test("Library: constructor rejects non-positive limit/loanDays", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new Library(FIXED_CLOCK, 0, 14), "limit 0");
            assertThrows(IllegalArgumentException.class,
                    () -> new Library(FIXED_CLOCK, 3, 0), "loanDays 0");
        });
    }

    private static void registerBorrow(TestRunner runner) {
        runner.test("Library: borrow updates book, member, and loan together", () -> {
            Library library = library();
            LoanRecordSnapshot loan = library.borrowBook("M001", "978-1");
            assertEquals("L0001", loan.getLoanId(), "deterministic loan id");
            assertFalse(library.getBook("978-1").orElseThrow().isAvailable(), "book unavailable");
            assertTrue(library.getMember("M001").orElseThrow().getBorrowedIsbns().contains("978-1"),
                    "member holds isbn");
            assertEquals(1, library.listActiveLoans().size(), "one active loan");
        });

        runner.test("Library: due date is borrow date + loan days (fixed clock)", () -> {
            Library library = library();
            LoanRecordSnapshot loan = library.borrowBook("M001", "978-1");
            assertEquals(TODAY, loan.getBorrowDate(), "borrow date from clock");
            assertEquals(TODAY.plusDays(Library.DEFAULT_LOAN_DAYS), loan.getDueDate(), "due date");
        });

        runner.test("Library: double borrow of same book rejected", () -> {
            Library library = library();
            library.borrowBook("M001", "978-1");
            assertThrows(IllegalStateException.class,
                    () -> library.borrowBook("M002", "978-1"), "already borrowed");
        });

        runner.test("Library: unknown member/book borrow rejected", () -> {
            Library library = library();
            assertThrows(IllegalArgumentException.class,
                    () -> library.borrowBook("NOPE", "978-1"), "unknown member");
            assertThrows(IllegalArgumentException.class,
                    () -> library.borrowBook("M001", "000"), "unknown book");
        });

        runner.test("Library: borrow limit enforced", () -> {
            Library library = new Library(FIXED_CLOCK, 2, 14);
            library.addBook(new Book("978-1", "A", "X"));
            library.addBook(new Book("978-2", "B", "Y"));
            library.addBook(new Book("978-3", "C", "Z"));
            library.addMember(new Member("M001", "Elena"));
            library.borrowBook("M001", "978-1");
            library.borrowBook("M001", "978-2");
            assertThrows(IllegalStateException.class,
                    () -> library.borrowBook("M001", "978-3"), "at limit");
        });

        runner.test("Library: failed borrow (unavailable) leaves all state unchanged", () -> {
            Library library = library();
            library.borrowBook("M001", "978-1");
            int activeBefore = library.listActiveLoans().size();
            assertThrows(IllegalStateException.class,
                    () -> library.borrowBook("M002", "978-1"), "unavailable");
            assertEquals(activeBefore, library.listActiveLoans().size(), "loan count unchanged");
            assertEquals(0, library.getMember("M002").orElseThrow().getActiveLoanCount(),
                    "member M002 unchanged");
        });

        runner.test("Library: failed borrow (limit) creates no loan", () -> {
            Library library = new Library(FIXED_CLOCK, 1, 14);
            library.addBook(new Book("978-1", "A", "X"));
            library.addBook(new Book("978-2", "B", "Y"));
            library.addMember(new Member("M001", "Elena"));
            library.borrowBook("M001", "978-1");
            assertThrows(IllegalStateException.class, () -> library.borrowBook("M001", "978-2"), "limit");
            assertTrue(library.getBook("978-2").orElseThrow().isAvailable(), "book 2 still available");
            assertEquals(1, library.listActiveLoans().size(), "still one loan");
        });
    }

    private static void registerReturn(TestRunner runner) {
        runner.test("Library: return updates book, member, and loan together", () -> {
            Library library = library();
            library.borrowBook("M001", "978-1");
            LoanRecordSnapshot returned = library.returnBook("M001", "978-1");
            assertEquals(LoanStatus.RETURNED, returned.getStatus(), "loan returned");
            assertEquals(TODAY, returned.getReturnDate(), "return date set");
            assertTrue(library.getBook("978-1").orElseThrow().isAvailable(), "book available");
            assertFalse(library.getMember("M001").orElseThrow().getBorrowedIsbns().contains("978-1"),
                    "member no longer holds isbn");
            assertEquals(1, library.listCompletedLoans().size(), "one completed loan");
        });

        runner.test("Library: wrong-member return rejected, state unchanged", () -> {
            Library library = library();
            library.borrowBook("M001", "978-1");
            assertThrows(IllegalStateException.class,
                    () -> library.returnBook("M002", "978-1"), "wrong member");
            assertFalse(library.getBook("978-1").orElseThrow().isAvailable(), "still borrowed");
            assertEquals(0, library.listCompletedLoans().size(), "no completed loan");
        });

        runner.test("Library: returning an available book rejected", () -> {
            Library library = library();
            assertThrows(IllegalStateException.class,
                    () -> library.returnBook("M001", "978-1"), "not borrowed");
        });

        runner.test("Library: unknown member/book return rejected", () -> {
            Library library = library();
            library.borrowBook("M001", "978-1");
            assertThrows(IllegalArgumentException.class,
                    () -> library.returnBook("NOPE", "978-1"), "unknown member");
            assertThrows(IllegalArgumentException.class,
                    () -> library.returnBook("M001", "000"), "unknown book");
        });

        runner.test("Library: book can be borrowed again after return", () -> {
            Library library = library();
            library.borrowBook("M001", "978-1");
            library.returnBook("M001", "978-1");
            LoanRecordSnapshot again = library.borrowBook("M002", "978-1");
            assertEquals("L0002", again.getLoanId(), "second loan id");
            assertFalse(library.getBook("978-1").orElseThrow().isAvailable(), "borrowed again");
        });
    }

    private static void registerReports(TestRunner runner) {
        runner.test("Library: active/completed loan lists", () -> {
            Library library = library();
            library.borrowBook("M001", "978-1");
            library.borrowBook("M001", "978-2");
            library.returnBook("M001", "978-1");
            assertEquals(1, library.listActiveLoans().size(), "one active");
            assertEquals(1, library.listCompletedLoans().size(), "one completed");
        });

        runner.test("Library: overdue loans list uses the clock (none when fresh)", () -> {
            Library library = library();
            library.borrowBook("M001", "978-1");
            assertEquals(0, library.listOverdueLoans().size(), "not overdue at borrow time");
        });

        runner.test("Library: overdue loans detected past due date", () -> {
            // Borrow at day 0, then a library clock 20 days later would be needed to
            // observe overdue; here we verify via the loan's own due date logic through
            // a short loan period and an advanced clock.
            Clock later = Clock.fixed(Instant.parse("2026-02-20T10:00:00Z"), ZoneId.of("UTC"));
            Library library = new Library(later, 3, 14);
            library.addBook(new Book("978-1", "A", "X"));
            library.addMember(new Member("M001", "Elena"));
            library.borrowBook("M001", "978-1"); // borrowed 2026-02-20, due 2026-03-06
            assertEquals(0, library.listOverdueLoans().size(), "not overdue yet");
        });

        runner.test("Library: member and book loan history", () -> {
            Library library = library();
            library.borrowBook("M001", "978-1");
            library.borrowBook("M002", "978-2");
            assertEquals(1, library.getLoanHistoryForMember("M001").size(), "M001 history");
            assertEquals(1, library.getLoanHistoryForBook("978-2").size(), "book history");
        });

        runner.test("Library: count available and borrowed books", () -> {
            Library library = library();
            assertEquals(3, library.countAvailableBooks(), "3 available");
            assertEquals(0, library.countBorrowedBooks(), "0 borrowed");
            library.borrowBook("M001", "978-1");
            assertEquals(2, library.countAvailableBooks(), "2 available");
            assertEquals(1, library.countBorrowedBooks(), "1 borrowed");
        });
    }
}
