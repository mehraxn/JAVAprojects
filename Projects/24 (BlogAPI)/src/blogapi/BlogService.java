package blogapi;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BlogService {
    private final Map<String, User> users = new LinkedHashMap<String, User>();
    private final Map<String, Post> posts = new LinkedHashMap<String, Post>();
    private final Map<String, List<Comment>> commentsByPost =
            new LinkedHashMap<String, List<Comment>>();
    private long nextUserId = 1;
    private long nextPostId = 1;
    private long nextCommentId = 1;

    public synchronized User createUser(String name) {
        User user = new User("U-" + nextUserId, name);
        users.put(user.getId(), user);
        nextUserId++;
        return user.copy();
    }

    public synchronized List<User> listUsers() {
        List<User> result = new ArrayList<User>();
        for (User user : users.values()) {
            result.add(user.copy());
        }
        return Collections.unmodifiableList(result);
    }

    public synchronized Post createPost(String authorId, String title, String content) {
        User author = requireUser(authorId);
        LocalDateTime now = LocalDateTime.now();
        Post post = new Post("P-" + nextPostId, author, title, content, now, now);
        posts.put(post.getId(), post);
        commentsByPost.put(post.getId(), new ArrayList<Comment>());
        nextPostId++;
        return post.copy();
    }

    public synchronized Post findPost(String postId) {
        Post post = posts.get(requireId(postId, "Post ID"));
        return post == null ? null : post.copy();
    }

    public synchronized List<Post> listPosts() {
        List<Post> result = new ArrayList<Post>();
        for (Post post : posts.values()) {
            result.add(post.copy());
        }
        return Collections.unmodifiableList(result);
    }

    public synchronized Post updatePost(String postId, String title, String content) {
        Post post = posts.get(requireId(postId, "Post ID"));
        if (post == null) {
            return null;
        }
        post.update(title, content);
        return post.copy();
    }

    public synchronized boolean deletePost(String postId) {
        String id = requireId(postId, "Post ID");
        if (posts.remove(id) == null) {
            return false;
        }
        commentsByPost.remove(id);
        return true;
    }

    public synchronized List<Post> searchPosts(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            throw new IllegalArgumentException("Search text cannot be empty.");
        }
        String query = searchText.trim().toLowerCase(Locale.ROOT);
        List<Post> result = new ArrayList<Post>();
        for (Post post : posts.values()) {
            if (post.getTitle().toLowerCase(Locale.ROOT).contains(query)
                    || post.getAuthor().getName().toLowerCase(Locale.ROOT).contains(query)) {
                result.add(post.copy());
            }
        }
        return Collections.unmodifiableList(result);
    }

    public synchronized Comment addComment(String postId, String authorId, String body) {
        String validPostId = requireId(postId, "Post ID");
        if (!posts.containsKey(validPostId)) {
            throw new IllegalArgumentException("Post does not exist: " + validPostId);
        }
        User author = requireUser(authorId);
        Comment comment = new Comment(
                "C-" + nextCommentId, validPostId, author, body, LocalDateTime.now());
        commentsByPost.get(validPostId).add(comment);
        nextCommentId++;
        return comment.copy();
    }

    public synchronized List<Comment> listComments(String postId) {
        String id = requireId(postId, "Post ID");
        if (!posts.containsKey(id)) {
            throw new IllegalArgumentException("Post does not exist: " + id);
        }
        List<Comment> result = new ArrayList<Comment>();
        for (Comment comment : commentsByPost.get(id)) {
            result.add(comment.copy());
        }
        return Collections.unmodifiableList(result);
    }

    private User requireUser(String userId) {
        String id = requireId(userId, "User ID");
        User user = users.get(id);
        if (user == null) {
            throw new IllegalArgumentException("User does not exist: " + id);
        }
        return user;
    }

    private String requireId(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        return value.trim();
    }
}
