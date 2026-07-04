package blogapi;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BlogService {
    private final Map<String, Post> posts = new LinkedHashMap<>();
    private final Map<String, List<Comment>> commentsByPost = new LinkedHashMap<>();

    public Post createPost(String title, String content) {
        // TODO: Validate input, generate an ID, and store a post.
        throw new UnsupportedOperationException("TODO: create a post");
    }

    public Post updatePost(String postId, String title, String content) {
        // TODO: Validate and update an existing post.
        throw new UnsupportedOperationException("TODO: update a post");
    }

    public boolean deletePost(String postId) {
        // TODO: Remove the post and its comments consistently.
        throw new UnsupportedOperationException("TODO: delete a post");
    }

    public List<Post> searchPosts(String searchText) {
        // TODO: Search title and content case-insensitively.
        throw new UnsupportedOperationException("TODO: search posts");
    }

    public Comment addComment(String postId, String author, String body) {
        // TODO: Validate the post and append a comment.
        throw new UnsupportedOperationException("TODO: add a comment");
    }

    public List<Comment> listComments(String postId) {
        // TODO: Return a read-only comment list for the post.
        throw new UnsupportedOperationException("TODO: list comments");
    }
}
