package movieticketbookingsystem;

/**
 * An immutable movie: identity, title, genre, and runtime.
 *
 * <p>All fields are fixed at construction and validated eagerly. Outside callers
 * usually receive a {@link MovieSnapshot} instead of a live {@code Movie}, but
 * {@code Movie} is itself immutable so sharing it is safe.
 */
public final class Movie {
    /** Genre used by the shorter constructor when none is supplied. */
    public static final String DEFAULT_GENRE = "General";

    private final String movieId;
    private final String title;
    private final String genre;
    private final int durationMinutes;

    /** Creates a movie with an explicit genre. */
    public Movie(String movieId, String title, String genre, int durationMinutes) {
        this.movieId = requireText(movieId, "Movie ID");
        this.title = requireText(title, "Movie title");
        this.genre = requireText(genre, "Genre");
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Movie duration must be greater than zero");
        }
        this.durationMinutes = durationMinutes;
    }

    /** Creates a movie with the {@link #DEFAULT_GENRE default genre}. */
    public Movie(String movieId, String title, int durationMinutes) {
        this(movieId, title, DEFAULT_GENRE, durationMinutes);
    }

    public String getMovieId() { return movieId; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public int getDurationMinutes() { return durationMinutes; }

    /** Immutable, read-only view of this movie. */
    public MovieSnapshot toSnapshot() {
        return new MovieSnapshot(movieId, title, genre, durationMinutes);
    }

    @Override
    public String toString() {
        return movieId + " " + title + " (" + genre + ", " + durationMinutes + " min)";
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
