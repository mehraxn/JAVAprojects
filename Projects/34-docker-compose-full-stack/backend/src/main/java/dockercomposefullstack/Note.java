package dockercomposefullstack;

import java.time.Instant;

public class Note {
    private final long id;
    private final String text;
    private final Instant createdAt;

    public Note(long id, String text, Instant createdAt) {
        this.id = id;
        this.text = text;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
