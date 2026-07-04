package librarymanagementsystem;

public class Book {
    private final String isbn;
    private final String title;
    private final String author;
    private String borrowedByMemberId;

    public Book(String isbn, String title, String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isAvailable() {
        return borrowedByMemberId == null;
    }

    public void borrowTo(String memberId) {
        // TODO: Reject unavailable books and record the borrowing member.
        throw new UnsupportedOperationException("TODO: borrow a book");
    }

    public void returnToLibrary() {
        // TODO: Clear the current borrower after validating the state.
        throw new UnsupportedOperationException("TODO: return a book");
    }
}
