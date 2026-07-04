package librarymanagementsystem;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class Member {
    private final String id;
    private final String name;
    private final Set<String> borrowedBookIsbns = new LinkedHashSet<>();

    public Member(String id, String name) {
        this.id = requireText(id, "Member ID");
        this.name = requireText(name, "Member name");
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void borrowBook(String isbn) {
        String validIsbn = requireText(isbn, "ISBN");
        if (!borrowedBookIsbns.add(validIsbn)) {
            throw new IllegalStateException("Member already has this book: " + validIsbn);
        }
    }

    public void returnBook(String isbn) {
        String validIsbn = requireText(isbn, "ISBN");
        if (!borrowedBookIsbns.remove(validIsbn)) {
            throw new IllegalStateException("Member did not borrow this book: " + validIsbn);
        }
    }

    public boolean hasBorrowedBook(String isbn) {
        return isbn != null && borrowedBookIsbns.contains(isbn.trim());
    }

    public Set<String> getBorrowedBookIsbns() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(borrowedBookIsbns));
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
