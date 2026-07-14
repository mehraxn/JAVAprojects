package librarymanagementsystem;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static librarymanagementsystem.TestSupport.assertEquals;
import static librarymanagementsystem.TestSupport.assertThrows;
import static librarymanagementsystem.TestSupport.assertTrue;

final class SearchTest {

    private SearchTest() {
    }

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-01-15T10:00:00Z"), ZoneId.of("UTC"));

    private static Library library() {
        Library library = new Library(FIXED_CLOCK);
        library.addBook(new Book("978-1", "Clean Code", "Robert C. Martin"));
        library.addBook(new Book("978-2", "Effective Java", "Joshua Bloch"));
        library.addBook(new Book("978-3", "Clean Architecture", "Robert C. Martin"));
        library.addMember(new Member("M001", "Elena"));
        return library;
    }

    private static List<String> titles(List<BookSnapshot> books) {
        List<String> titles = new ArrayList<>();
        for (BookSnapshot book : books) {
            titles.add(book.getTitle());
        }
        return titles;
    }

    static void register(TestRunner runner) {
        runner.test("Search: by title works", () ->
                assertEquals(List.of("Effective Java"),
                        titles(library().searchBooks("effective")), "title match"));

        runner.test("Search: by author works (both Martin books, sorted by title)", () ->
                assertEquals(List.of("Clean Architecture", "Clean Code"),
                        titles(library().searchBooks("martin")), "author match sorted"));

        runner.test("Search: is case-insensitive", () ->
                assertEquals(List.of("Clean Architecture", "Clean Code"),
                        titles(library().searchBooks("CLEAN")), "case-insensitive"));

        runner.test("Search: partial term works", () ->
                assertEquals(List.of("Effective Java"),
                        titles(library().searchBooks("Jav")), "partial"));

        runner.test("Search: no match returns empty list", () ->
                assertTrue(library().searchBooks("zzz").isEmpty(), "empty"));

        runner.test("Search: null query rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> library().searchBooks(null), "null query"));

        runner.test("Search: available listing excludes borrowed, includes returned", () -> {
            Library library = library();
            assertEquals(3, library.listAvailableBooks().size(), "3 available");
            library.borrowBook("M001", "978-2");
            assertEquals(List.of("Clean Architecture", "Clean Code"),
                    sortedTitles(library.listAvailableBooks()), "borrowed excluded");
            library.returnBook("M001", "978-2");
            assertEquals(3, library.listAvailableBooks().size(), "returned reappears");
        });

        runner.test("Search: results are deterministic and unmodifiable", () -> {
            List<BookSnapshot> results = library().searchBooks("clean");
            assertEquals("Clean Architecture", results.get(0).getTitle(), "deterministic first");
            assertThrows(UnsupportedOperationException.class, () -> results.clear(), "unmodifiable");
        });
    }

    private static List<String> sortedTitles(List<BookSnapshot> books) {
        List<String> titles = titles(books);
        titles.sort(String.CASE_INSENSITIVE_ORDER);
        return titles;
    }
}
