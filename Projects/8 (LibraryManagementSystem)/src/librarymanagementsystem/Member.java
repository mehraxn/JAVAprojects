package librarymanagementsystem;

import java.util.HashSet;
import java.util.Set;

public class Member {
    private final String id;
    private final String name;
    private final Set<String> borrowedBookIsbns = new HashSet<>();

    public Member(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void borrowBook(String isbn) {
        // TODO: Add the ISBN after checking the member's borrowing rules.
        throw new UnsupportedOperationException("TODO: track borrowed book");
    }

    public void returnBook(String isbn) {
        // TODO: Remove the ISBN when the member returns the book.
        throw new UnsupportedOperationException("TODO: track returned book");
    }
}
