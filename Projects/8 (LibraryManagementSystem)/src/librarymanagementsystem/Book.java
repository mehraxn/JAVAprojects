package librarymanagementsystem;

public class Book {
    private final String isbn;
    private final String title;
    private final String author;
    private String borrowedByMemberId;

    public Book(String isbn, String title, String author) {
        this.isbn = requireText(isbn, "ISBN");
        this.title = requireText(title, "Title");
        this.author = requireText(author, "Author");
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

    public String getBorrowedByMemberId() {
        return borrowedByMemberId;
    }

    public boolean isAvailable() {
        return borrowedByMemberId == null;
    }

    public void borrowTo(String memberId) {
        if (!isAvailable()) {
            throw new IllegalStateException("Book is already borrowed: " + isbn);
        }
        borrowedByMemberId = requireText(memberId, "Member ID");
    }

    public void returnToLibrary() {
        if (isAvailable()) {
            throw new IllegalStateException("Book is not currently borrowed: " + isbn);
        }
        borrowedByMemberId = null;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
