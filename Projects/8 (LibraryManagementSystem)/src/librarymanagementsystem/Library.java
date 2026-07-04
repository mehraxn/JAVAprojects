package librarymanagementsystem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Library {
    private final Map<String, Book> books = new HashMap<>();
    private final Map<String, Member> members = new HashMap<>();

    public void addBook(Book book) {
        // TODO: Validate and add a unique book.
        throw new UnsupportedOperationException("TODO: add a book");
    }

    public void addMember(Member member) {
        // TODO: Validate and add a unique member.
        throw new UnsupportedOperationException("TODO: add a member");
    }

    public void borrowBook(String isbn, String memberId) {
        // TODO: Coordinate book and member state changes.
        throw new UnsupportedOperationException("TODO: borrow a book");
    }

    public void returnBook(String isbn, String memberId) {
        // TODO: Coordinate the return and validate ownership.
        throw new UnsupportedOperationException("TODO: return a book");
    }

    public List<Book> searchBooks(String searchText) {
        // TODO: Search case-insensitively by title or author.
        throw new UnsupportedOperationException("TODO: search books");
    }
}
