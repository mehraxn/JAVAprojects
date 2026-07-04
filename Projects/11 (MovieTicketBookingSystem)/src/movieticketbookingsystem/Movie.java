package movieticketbookingsystem;

public class Movie {
    private final String id;
    private final String title;
    private final int durationMinutes;

    public Movie(String id, String title, int durationMinutes) {
        this.id = requireText(id, "Movie ID");
        this.title = requireText(title, "Movie title");
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Movie duration must be greater than zero");
        }
        this.durationMinutes = durationMinutes;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public int getDurationMinutes() { return durationMinutes; }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
