package blogapi;

import java.time.LocalDateTime;

public class Post {
    private final String id;
    private String title;
    private String content;
    private final LocalDateTime createdAt;

    public Post(String id, String title, String content, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
