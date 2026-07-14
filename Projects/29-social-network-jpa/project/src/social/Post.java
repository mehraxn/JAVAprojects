package social;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * A post authored by a {@link Person}. The id is a caller-supplied unique string;
 * the timestamp is the creation time in epoch milliseconds.
 * <p>
 * Indexes support paginated author/friend feed queries ordered by timestamp.
 */
@Entity
@Table(
    name = "Post",
    indexes = {
        @Index(name = "idx_post_author", columnList = "author_code"),
        @Index(name = "idx_post_timestamp", columnList = "post_timestamp"),
        @Index(name = "idx_post_author_ts", columnList = "author_code, post_timestamp")
    })
public class Post {

  @Id
  private String id;

  @Column(nullable = false)
  private String text;

  @Column(name = "post_timestamp", nullable = false)
  private long timestamp;

  @ManyToOne(optional = false)
  @JoinColumn(name = "author_code", nullable = false)
  private Person author;

  Post() {
    // default constructor required by JPA
  }

  Post(String id, String text, long timestamp, Person author) {
    this.id = id;
    this.text = text;
    this.timestamp = timestamp;
    this.author = author;
  }

  public String getId() {
    return id;
  }

  public String getText() {
    return text;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public Person getAuthor() {
    return author;
  }
}
