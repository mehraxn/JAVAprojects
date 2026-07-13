package librarymanagementsystem;

/** Immutable, read-only view of a {@link Book}. */
public final class BookSnapshot {
    private final String isbn;
    private final String title;
    private final String author;
    private final String category;
    private final int publicationYear;
    private final boolean available;

    BookSnapshot(String isbn, String title, String author, String category,
                 int publicationYear, boolean available) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.category = category;
        this.publicationYear = publicationYear;
        this.available = available;
    }

    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public int getPublicationYear() { return publicationYear; }
    public boolean isAvailable() { return available; }

    @Override
    public String toString() {
        return isbn + " \"" + title + "\" by " + author + (available ? " (available)" : " (borrowed)");
    }
}
