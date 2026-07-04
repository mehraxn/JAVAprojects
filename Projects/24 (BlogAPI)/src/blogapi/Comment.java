package blogapi;

import java.time.LocalDateTime;

public class Comment {
    private final String id;
    private final String postId;
    private final String author;
    private final String body;
    private final LocalDateTime createdAt;

    public Comment(String id, String postId,
            String author, String body, LocalDateTime createdAt) {
        this.id = id;
        this.postId = postId;
        this.author = author;
        this.body = body;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getPostId() { return postId; }
    public String getAuthor() { return author; }
    public String getBody() { return body; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
