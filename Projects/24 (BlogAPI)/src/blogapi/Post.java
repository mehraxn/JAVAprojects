package blogapi;

import java.time.LocalDateTime;

public class Post {
    private final String id;
    private final User author;
    private String title;
    private String content;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Post(String id, User author, String title, String content,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = requireText(id, "Post ID");
        if (author == null) {
            throw new IllegalArgumentException("Post author cannot be null.");
        }
        if (createdAt == null || updatedAt == null) {
            throw new IllegalArgumentException("Post timestamps cannot be null.");
        }
        if (updatedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("Updated time cannot be before creation time.");
        }
        this.author = author.copy();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        setText(title, content);
    }

    public String getId() {
        return id;
    }

    public User getAuthor() {
        return author.copy();
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void update(String title, String content) {
        setText(title, content);
        updatedAt = LocalDateTime.now();
    }

    public Post copy() {
        return new Post(id, author, title, content, createdAt, updatedAt);
    }

    private void setText(String title, String content) {
        this.title = requireText(title, "Post title");
        this.content = requireText(content, "Post content");
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        return value.trim();
    }

    @Override
    public String toString() {
        return id + ": " + title + " by " + author.getName();
    }
}
