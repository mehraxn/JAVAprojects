package movieticketbookingsystem;

import static movieticketbookingsystem.TestSupport.assertEquals;
import static movieticketbookingsystem.TestSupport.assertThrows;

final class MovieTest {

    private MovieTest() {
    }

    static void register(TestRunner runner) {
        runner.test("Movie: full constructor stores all fields", () -> {
            Movie movie = new Movie("M001", "Inception", "Sci-Fi", 148);
            assertEquals("M001", movie.getMovieId(), "movie id stored");
            assertEquals("Inception", movie.getTitle(), "title stored");
            assertEquals("Sci-Fi", movie.getGenre(), "genre stored");
            assertEquals(148, movie.getDurationMinutes(), "duration stored");
        });

        runner.test("Movie: short constructor uses default genre", () -> {
            Movie movie = new Movie("M002", "Untitled", 90);
            assertEquals(Movie.DEFAULT_GENRE, movie.getGenre(), "default genre");
        });

        runner.test("Movie: fields are trimmed", () -> {
            Movie movie = new Movie("  M003 ", "  Title ", "  Drama ", 100);
            assertEquals("M003", movie.getMovieId(), "id trimmed");
            assertEquals("Title", movie.getTitle(), "title trimmed");
            assertEquals("Drama", movie.getGenre(), "genre trimmed");
        });

        runner.test("Movie: null/blank movie ID rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new Movie(null, "T", "G", 100), "null id");
            assertThrows(IllegalArgumentException.class,
                    () -> new Movie("  ", "T", "G", 100), "blank id");
        });

        runner.test("Movie: null/blank title rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new Movie("M", null, "G", 100), "null title");
            assertThrows(IllegalArgumentException.class,
                    () -> new Movie("M", "  ", "G", 100), "blank title");
        });

        runner.test("Movie: null/blank genre rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new Movie("M", "T", null, 100), "null genre");
            assertThrows(IllegalArgumentException.class,
                    () -> new Movie("M", "T", "  ", 100), "blank genre");
        });

        runner.test("Movie: zero duration rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new Movie("M", "T", "G", 0), "zero duration"));

        runner.test("Movie: negative duration rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new Movie("M", "T", "G", -5), "negative duration"));

        runner.test("Movie: snapshot contains expected data", () -> {
            MovieSnapshot snapshot = new Movie("M001", "Inception", "Sci-Fi", 148).toSnapshot();
            assertEquals("M001", snapshot.getMovieId(), "snapshot id");
            assertEquals("Inception", snapshot.getTitle(), "snapshot title");
            assertEquals("Sci-Fi", snapshot.getGenre(), "snapshot genre");
            assertEquals(148, snapshot.getDurationMinutes(), "snapshot duration");
        });
    }
}
