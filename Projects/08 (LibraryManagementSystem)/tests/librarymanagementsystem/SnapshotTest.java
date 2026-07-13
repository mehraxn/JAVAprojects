package librarymanagementsystem;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

import static librarymanagementsystem.TestSupport.assertEquals;
import static librarymanagementsystem.TestSupport.assertFalse;
import static librarymanagementsystem.TestSupport.assertThrows;
import static librarymanagementsystem.TestSupport.assertTrue;

/**
 * Proves that data leaving {@link Library} is safe: returned lists are
 * unmodifiable, snapshots carry no mutators, and holding a snapshot cannot change
 * book, member, or loan state.
 */
final class SnapshotTest {

    private SnapshotTest() {
    }

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-01-15T10:00:00Z"), ZoneId.of("UTC"));

    private static Library library() {
        Library library = new Library(FIXED_CLOCK);
        library.addBook(new Book("978-1", "Clean Code", "Robert C. Martin"));
        library.addBook(new Book("978-2", "Effective Java", "Joshua Bloch"));
        library.addMember(new Member("M001", "Elena"));
        return library;
    }

    static void register(TestRunner runner) {
        runner.test("Snapshot: listBooks result is unmodifiable", () -> {
            List<BookSnapshot> books = library().listBooks();
            assertThrows(UnsupportedOperationException.class, () -> books.clear(), "books");
        });

        runner.test("Snapshot: listMembers result is unmodifiable", () -> {
            List<MemberSnapshot> members = library().listMembers();
            assertThrows(UnsupportedOperationException.class, () -> members.clear(), "members");
        });

        runner.test("Snapshot: listAvailableBooks result is unmodifiable", () -> {
            List<BookSnapshot> books = library().listAvailableBooks();
            assertThrows(UnsupportedOperationException.class, () -> books.clear(), "available");
        });

        runner.test("Snapshot: searchBooks result is unmodifiable", () -> {
            List<BookSnapshot> books = library().searchBooks("clean");
            assertThrows(UnsupportedOperationException.class, () -> books.clear(), "search");
        });

        runner.test("Snapshot: active/completed loan results are unmodifiable", () -> {
            Library library = library();
            library.borrowBook("M001", "978-1");
            assertThrows(UnsupportedOperationException.class,
                    () -> library.listActiveLoans().clear(), "active");
            library.returnBook("M001", "978-1");
            assertThrows(UnsupportedOperationException.class,
                    () -> library.listCompletedLoans().clear(), "completed");
        });

        runner.test("Snapshot: MemberSnapshot borrowed set is unmodifiable and decoupled", () -> {
            Library library = library();
            library.borrowBook("M001", "978-1");
            MemberSnapshot member = library.getMember("M001").orElseThrow();
            Set<String> isbns = member.getBorrowedIsbns();
            assertThrows(UnsupportedOperationException.class, () -> isbns.add("X"), "unmodifiable");
            // Returning updates the library but not the previously captured snapshot.
            library.returnBook("M001", "978-1");
            assertEquals(1, member.getActiveLoanCount(), "old snapshot unchanged");
            assertEquals(0, library.getMember("M001").orElseThrow().getActiveLoanCount(),
                    "fresh snapshot updated");
        });

        runner.test("Snapshot: BookSnapshot cannot mutate internal availability", () -> {
            Library library = library();
            BookSnapshot before = library.getBook("978-1").orElseThrow();
            assertTrue(before.isAvailable(), "captured available");
            library.borrowBook("M001", "978-1");
            assertTrue(before.isAvailable(), "old snapshot still available");
            assertFalse(library.getBook("978-1").orElseThrow().isAvailable(), "fresh snapshot borrowed");
        });

        runner.test("Snapshot: borrow/return return values cannot corrupt library state", () -> {
            Library library = library();
            LoanRecordSnapshot loan = library.borrowBook("M001", "978-1");
            // The snapshot exposes only getters; there is no way to flip status.
            assertEquals(LoanStatus.ACTIVE, loan.getStatus(), "snapshot active");
            assertEquals(1, library.listActiveLoans().size(), "one active loan intact");
            assertFalse(library.getBook("978-1").orElseThrow().isAvailable(), "book still borrowed");
        });

        runner.test("Snapshot: reading snapshots leaves internal state correct", () -> {
            Library library = library();
            library.borrowBook("M001", "978-1");
            library.listBooks();
            library.listMembers();
            library.getBook("978-1");
            assertEquals(1, library.countBorrowedBooks(), "still one borrowed");
            assertEquals(1, library.getMember("M001").orElseThrow().getActiveLoanCount(),
                    "member still holds one");
        });
    }
}
