package dockercomposefullstack;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class NoteRepository {
    private final AppConfig config;

    public NoteRepository(AppConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Configuration is required.");
        }
        this.config = config;
    }

    public List<Note> findAll() throws SQLException {
        String sql = "SELECT id, note_text, created_at FROM notes ORDER BY id";
        List<Note> notes = new ArrayList<>();
        try (Connection connection = openConnection();
                Statement statement = connection.createStatement();
                ResultSet results = statement.executeQuery(sql)) {
            while (results.next()) {
                notes.add(new Note(
                        results.getLong("id"),
                        results.getString("note_text"),
                        results.getTimestamp("created_at").toInstant()));
            }
        }
        return notes;
    }

    public Note add(String text) throws SQLException {
        String cleanText = validateText(text);
        String sql = "INSERT INTO notes (note_text) VALUES (?) "
                + "RETURNING id, note_text, created_at";
        try (Connection connection = openConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cleanText);
            try (ResultSet results = statement.executeQuery()) {
                if (!results.next()) {
                    throw new SQLException("PostgreSQL did not return the created note.");
                }
                return new Note(
                        results.getLong("id"),
                        results.getString("note_text"),
                        results.getTimestamp("created_at").toInstant());
            }
        }
    }

    public boolean databaseIsAvailable() {
        try (Connection connection = openConnection();
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery("SELECT 1")) {
            return result.next();
        } catch (SQLException exception) {
            return false;
        }
    }

    private Connection openConnection() throws SQLException {
        return DriverManager.getConnection(
                config.getDatabaseUrl(),
                config.getDatabaseUser(),
                config.getDatabasePassword());
    }

    private String validateText(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Note text is required.");
        }
        String cleanText = text.trim();
        if (cleanText.length() > 500) {
            throw new IllegalArgumentException("Note text cannot exceed 500 characters.");
        }
        return cleanText;
    }
}
