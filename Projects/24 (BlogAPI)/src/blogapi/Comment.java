package blogapi;

import java.time.LocalDateTime;

public class Comment {
    private final String id;
    private final String postId;
    private final User author;
    private final String body;
    private final LocalDateTime createdAt;

    public Comment(String id, String postId,
            User author, String body, LocalDateTime createdAt) {
        this.id = requireText(id, "Comment ID");
        this.postId = requireText(postId, "Post ID");
        if (author == null) {
            throw new IllegalArgumentException("Comment author cannot be null.");
        }
        this.author = author.copy();
        String validBody = requireText(body, "Comment body");
        if (validBody.length() > 5_000) {
            throw new IllegalArgumentException("Comment body cannot exceed 5000 characters.");
        }
        this.body = validBody;
        if (createdAt == null) {
            throw new IllegalArgumentException("Comment creation time cannot be null.");
        }
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getPostId() {
        return postId;
    }

    public User getAuthor() {
        return author.copy();
    }

    public String getBody() {
        return body;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Comment copy() {
        return new Comment(id, postId, author, body, createdAt);
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        return value.trim();
    }
}
