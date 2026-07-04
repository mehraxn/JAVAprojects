package blogapi;

import java.util.List;

public final class BlogJson {
    private BlogJson() {
    }

    public static String user(User user) {
        return "{\"id\":\"" + escape(user.getId())
                + "\",\"name\":\"" + escape(user.getName()) + "\"}";
    }

    public static String post(Post post) {
        return "{\"id\":\"" + escape(post.getId())
                + "\",\"author\":" + user(post.getAuthor())
                + ",\"title\":\"" + escape(post.getTitle())
                + "\",\"content\":\"" + escape(post.getContent())
                + "\",\"createdAt\":\"" + post.getCreatedAt()
                + "\",\"updatedAt\":\"" + post.getUpdatedAt() + "\"}";
    }

    public static String comment(Comment comment) {
        return "{\"id\":\"" + escape(comment.getId())
                + "\",\"postId\":\"" + escape(comment.getPostId())
                + "\",\"author\":" + user(comment.getAuthor())
                + ",\"body\":\"" + escape(comment.getBody())
                + "\",\"createdAt\":\"" + comment.getCreatedAt() + "\"}";
    }

    public static String users(List<User> users) {
        StringBuilder json = new StringBuilder("[");
        for (int index = 0; index < users.size(); index++) {
            if (index > 0) {
                json.append(',');
            }
            json.append(user(users.get(index)));
        }
        return json.append(']').toString();
    }

    public static String posts(List<Post> posts) {
        StringBuilder json = new StringBuilder("[");
        for (int index = 0; index < posts.size(); index++) {
            if (index > 0) {
                json.append(',');
            }
            json.append(post(posts.get(index)));
        }
        return json.append(']').toString();
    }

    public static String comments(List<Comment> comments) {
        StringBuilder json = new StringBuilder("[");
        for (int index = 0; index < comments.size(); index++) {
            if (index > 0) {
                json.append(',');
            }
            json.append(comment(comments.get(index)));
        }
        return json.append(']').toString();
    }

    public static String error(String message) {
        return "{\"error\":\"" + escape(message == null ? "Request failed." : message) + "\"}";
    }

    private static String escape(String value) {
        StringBuilder result = new StringBuilder();
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            if (character == '"') {
                result.append("\\\"");
            } else if (character == '\\') {
                result.append("\\\\");
            } else if (character == '\n') {
                result.append("\\n");
            } else if (character == '\r') {
                result.append("\\r");
            } else if (character == '\t') {
                result.append("\\t");
            } else if (character < 0x20) {
                result.append(String.format("\\u%04x", (int) character));
            } else {
                result.append(character);
            }
        }
        return result.toString();
    }
}
