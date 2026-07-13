package movieticketbookingsystem;

/**
 * Immutable, read-only view of a {@link Movie} handed to outside callers.
 */
public final class MovieSnapshot {
    private final String movieId;
    private final String title;
    private final String genre;
    private final int durationMinutes;

    MovieSnapshot(String movieId, String title, String genre, int durationMinutes) {
        this.movieId = movieId;
        this.title = title;
        this.genre = genre;
        this.durationMinutes = durationMinutes;
    }

    public String getMovieId() { return movieId; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public int getDurationMinutes() { return durationMinutes; }

    @Override
    public String toString() {
        return movieId + " " + title + " (" + genre + ", " + durationMinutes + " min)";
    }
}
