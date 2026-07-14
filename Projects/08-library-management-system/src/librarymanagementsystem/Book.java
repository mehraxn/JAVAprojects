package librarymanagementsystem;

/**
 * A catalogue book and its availability state.
 *
 * <p>Identity ({@code isbn}), title, author, category, and publication year are
 * fixed at construction; only availability changes, and only through the
 * package-private {@link #markBorrowed()} / {@link #markReturned()} methods driven
 * by {@link Library}. Outside callers receive an immutable {@link BookSnapshot},
 * so a live {@code Book} is never leaked and cannot be mutated to bypass
 * {@link Library}.
 */
public final class Book {
    /** Category used by the simpler constructor. */
    public static final String DEFAULT_CATEGORY = "General";
    /** Publication year used by the simpler constructor (0 = unspecified). */
    public static final int UNSPECIFIED_YEAR = 0;

    private final String isbn;
    private final String title;
    private final String author;
    private final String category;
    private final int publicationYear;
    private boolean available = true;

    /** Creates a book with an explicit category and publication year. */
    public Book(String isbn, String title, String author, String category, int publicationYear) {
        this.isbn = requireText(isbn, "ISBN");
        this.title = requireText(title, "Title");
        this.author = requireText(author, "Author");
        this.category = requireText(category, "Category");
        if (publicationYear < 0) {
            throw new IllegalArgumentException("Publication year must not be negative");
        }
        this.publicationYear = publicationYear;
    }

    /** Creates a book with the default category and an unspecified year. */
    public Book(String isbn, String title, String author) {
        this(isbn, title, author, DEFAULT_CATEGORY, UNSPECIFIED_YEAR);
    }

    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public int getPublicationYear() { return publicationYear; }
    public boolean isAvailable() { return available; }

    /** Marks an available book as borrowed; rejects double borrowing. */
    void markBorrowed() {
        if (!available) {
            throw new IllegalStateException("Book is already borrowed: " + isbn);
        }
        available = false;
    }

    /** Marks a borrowed book as available; rejects returning an available book. */
    void markReturned() {
        if (available) {
            throw new IllegalStateException("Book is not currently borrowed: " + isbn);
        }
        available = true;
    }

    /** Immutable, read-only view of this book at the current availability. */
    public BookSnapshot toSnapshot() {
        return new BookSnapshot(isbn, title, author, category, publicationYear, available);
    }

    @Override
    public String toString() {
        return isbn + " \"" + title + "\" by " + author + (available ? " (available)" : " (borrowed)");
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
