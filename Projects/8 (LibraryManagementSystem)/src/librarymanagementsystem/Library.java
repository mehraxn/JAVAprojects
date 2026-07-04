package librarymanagementsystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Library {
    private final Map<String, Book> books = new LinkedHashMap<>();
    private final Map<String, Member> members = new LinkedHashMap<>();

    public void addBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book must not be null");
        }
        if (books.containsKey(book.getIsbn())) {
            throw new IllegalArgumentException("ISBN already exists: " + book.getIsbn());
        }
        books.put(book.getIsbn(), book);
    }

    public void addMember(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Member must not be null");
        }
        if (members.containsKey(member.getId())) {
            throw new IllegalArgumentException("Member ID already exists: " + member.getId());
        }
        members.put(member.getId(), member);
    }

    public Book getBook(String isbn) {
        String validIsbn = requireText(isbn, "ISBN");
        Book book = books.get(validIsbn);
        if (book == null) {
            throw new IllegalArgumentException("Unknown ISBN: " + validIsbn);
        }
        return book;
    }

    public Member getMember(String memberId) {
        String validMemberId = requireText(memberId, "Member ID");
        Member member = members.get(validMemberId);
        if (member == null) {
            throw new IllegalArgumentException("Unknown member ID: " + validMemberId);
        }
        return member;
    }

    public void borrowBook(String isbn, String memberId) {
        Book book = getBook(isbn);
        Member member = getMember(memberId);
        if (!book.isAvailable()) {
            throw new IllegalStateException("Book is unavailable: " + book.getIsbn());
        }
        if (member.hasBorrowedBook(book.getIsbn())) {
            throw new IllegalStateException("Member already has this book: " + book.getIsbn());
        }
        book.borrowTo(member.getId());
        member.borrowBook(book.getIsbn());
    }

    public void returnBook(String isbn, String memberId) {
        Book book = getBook(isbn);
        Member member = getMember(memberId);
        if (book.isAvailable()) {
            throw new IllegalStateException("Book is not currently borrowed: " + book.getIsbn());
        }
        if (!member.getId().equals(book.getBorrowedByMemberId())
                || !member.hasBorrowedBook(book.getIsbn())) {
            throw new IllegalStateException("Book was not borrowed by member: " + member.getId());
        }
        book.returnToLibrary();
        member.returnBook(book.getIsbn());
    }

    public List<Book> searchBooks(String searchText) {
        if (searchText == null) {
            throw new IllegalArgumentException("Search text must not be null");
        }
        String query = searchText.trim().toLowerCase(Locale.ROOT);
        List<Book> matches = new ArrayList<>();
        for (Book book : books.values()) {
            if (book.getTitle().toLowerCase(Locale.ROOT).contains(query)
                    || book.getAuthor().toLowerCase(Locale.ROOT).contains(query)) {
                matches.add(book);
            }
        }
        matches.sort(Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Book::getAuthor, String.CASE_INSENSITIVE_ORDER));
        return Collections.unmodifiableList(matches);
    }

    public List<Book> listAvailableBooks() {
        List<Book> availableBooks = new ArrayList<>();
        for (Book book : books.values()) {
            if (book.isAvailable()) {
                availableBooks.add(book);
            }
        }
        return Collections.unmodifiableList(availableBooks);
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
