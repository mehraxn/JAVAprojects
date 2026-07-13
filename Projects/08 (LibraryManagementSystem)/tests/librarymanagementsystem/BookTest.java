package librarymanagementsystem;

import static librarymanagementsystem.TestSupport.assertEquals;
import static librarymanagementsystem.TestSupport.assertFalse;
import static librarymanagementsystem.TestSupport.assertThrows;
import static librarymanagementsystem.TestSupport.assertTrue;

final class BookTest {

    private BookTest() {
    }

    static void register(TestRunner runner) {
        runner.test("Book: full constructor stores all fields, starts available", () -> {
            Book book = new Book("978-1", "Clean Code", "Robert C. Martin", "Programming", 2008);
            assertEquals("978-1", book.getIsbn(), "isbn");
            assertEquals("Clean Code", book.getTitle(), "title");
            assertEquals("Robert C. Martin", book.getAuthor(), "author");
            assertEquals("Programming", book.getCategory(), "category");
            assertEquals(2008, book.getPublicationYear(), "year");
            assertTrue(book.isAvailable(), "starts available");
        });

        runner.test("Book: simple constructor uses default category and unspecified year", () -> {
            Book book = new Book("978-2", "Effective Java", "Joshua Bloch");
            assertEquals(Book.DEFAULT_CATEGORY, book.getCategory(), "default category");
            assertEquals(Book.UNSPECIFIED_YEAR, book.getPublicationYear(), "unspecified year");
        });

        runner.test("Book: fields are trimmed", () -> {
            Book book = new Book("  978-3 ", "  Title ", "  Author ");
            assertEquals("978-3", book.getIsbn(), "isbn trimmed");
            assertEquals("Title", book.getTitle(), "title trimmed");
            assertEquals("Author", book.getAuthor(), "author trimmed");
        });

        runner.test("Book: null/blank ISBN rejected", () -> {
            assertThrows(IllegalArgumentException.class, () -> new Book(null, "T", "A"), "null isbn");
            assertThrows(IllegalArgumentException.class, () -> new Book("  ", "T", "A"), "blank isbn");
        });

        runner.test("Book: null/blank title rejected", () -> {
            assertThrows(IllegalArgumentException.class, () -> new Book("X", null, "A"), "null title");
            assertThrows(IllegalArgumentException.class, () -> new Book("X", "  ", "A"), "blank title");
        });

        runner.test("Book: null/blank author rejected", () -> {
            assertThrows(IllegalArgumentException.class, () -> new Book("X", "T", null), "null author");
            assertThrows(IllegalArgumentException.class, () -> new Book("X", "T", "  "), "blank author");
        });

        runner.test("Book: negative publication year rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new Book("X", "T", "A", "Cat", -1), "negative year"));

        runner.test("Book: borrow makes unavailable; double borrow rejected", () -> {
            Book book = new Book("978-1", "T", "A");
            book.markBorrowed();
            assertFalse(book.isAvailable(), "unavailable");
            assertThrows(IllegalStateException.class, book::markBorrowed, "double borrow");
        });

        runner.test("Book: return makes available; returning available rejected", () -> {
            Book book = new Book("978-1", "T", "A");
            assertThrows(IllegalStateException.class, book::markReturned, "return available");
            book.markBorrowed();
            book.markReturned();
            assertTrue(book.isAvailable(), "available again");
        });

        runner.test("Book: snapshot contains expected data", () -> {
            Book book = new Book("978-1", "Clean Code", "Robert C. Martin", "Programming", 2008);
            book.markBorrowed();
            BookSnapshot snapshot = book.toSnapshot();
            assertEquals("978-1", snapshot.getIsbn(), "snapshot isbn");
            assertEquals("Clean Code", snapshot.getTitle(), "snapshot title");
            assertEquals("Robert C. Martin", snapshot.getAuthor(), "snapshot author");
            assertEquals("Programming", snapshot.getCategory(), "snapshot category");
            assertEquals(2008, snapshot.getPublicationYear(), "snapshot year");
            assertFalse(snapshot.isAvailable(), "snapshot unavailable");
        });
    }
}
