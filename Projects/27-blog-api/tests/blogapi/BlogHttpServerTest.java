package blogapi;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
final class BlogHttpServerTest {
    private BlogHttpServerTest() { }
    static void run() {
        BlogHttpServer server = new BlogHttpServer(new BlogService());
        try {
            server.start(0); Assertions.assertTrue(server.getPort() > 0, "server starts on ephemeral port");
            Client client = new Client(server.getPort());
            HttpResponse<String> user = client.send("POST", "/users", "name=Alice");
            json(user, 201); Assertions.assertContains(user.body(), "\"id\":\"U-1\"", "created user JSON");
            HttpResponse<String> duplicate = client.send("POST", "/users", "name=Alice"); json(duplicate, 400);
            HttpResponse<String> post = client.send("POST", "/posts", "authorId=U-1&title=Hello&content=First+post");
            json(post, 201); Assertions.assertEquals("/posts/P-1", post.headers().firstValue("Location").orElse(""), "post Location");
            HttpResponse<String> posts = client.send("GET", "/posts", null); json(posts, 200); Assertions.assertContains(posts.body(), "First post", "list posts");
            HttpResponse<String> comment = client.send("POST", "/posts/P-1/comments", "authorId=U-1&body=Nice");
            json(comment, 201); Assertions.assertContains(comment.body(), "\"id\":\"C-1\"", "create comment");
            HttpResponse<String> comments = client.send("GET", "/posts/P-1/comments", null); json(comments, 200); Assertions.assertContains(comments.body(), "Nice", "list comments");
            HttpResponse<String> missingComments = client.send("GET", "/posts/P-999/comments", null); json(missingComments, 404); Assertions.assertContains(missingComments.body(), "Post not found", "missing post JSON");
            HttpResponse<String> unknown = client.send("GET", "/unknown", null); json(unknown, 404);
            Assertions.assertEquals("{\"error\":\"Endpoint not found.\"}", unknown.body(), "fallback error JSON"); Assertions.assertFalse(unknown.body().toLowerCase().contains("html"), "fallback is not HTML");
            json(client.send("GET", "/posts-extra", null), 404); json(client.send("GET", "/users-extra", null), 404);
            json(client.send("GET", "/posts/P-1/comments-extra", null), 404);
            HttpResponse<String> method = client.send("PATCH", "/users", "name=X"); json(method, 405);
            Assertions.assertContains(method.body(), "Method not allowed", "method JSON"); Assertions.assertContains(method.headers().firstValue("Allow").orElse(""), "POST", "Allow header");
            json(client.send("POST", "/users", "name=A&name=B"), 400);
            HttpResponse<String> large = client.send("POST", "/users", "name=" + "x".repeat(131_073)); json(large, 413);
            Assertions.assertContains(large.body(), "too large", "payload error JSON");
            HttpResponse<String> deleted = client.send("DELETE", "/posts/P-1", null); Assertions.assertEquals(204, deleted.statusCode(), "delete post");
            json(client.send("GET", "/posts/P-1/comments", null), 404);
        } catch (IOException | InterruptedException exception) {
            Thread.currentThread().interrupt(); throw new IllegalStateException("HTTP smoke test failed", exception);
        } finally { server.stop(); }
        Assertions.assertThrows(IllegalStateException.class, server::getPort, "server stops");
    }
    private static void json(HttpResponse<String> response, int status) {
        Assertions.assertEquals(status, response.statusCode(), "HTTP status for " + response.uri());
        Assertions.assertContains(response.headers().firstValue("Content-Type").orElse(""), "application/json", "JSON content type");
    }
    private static final class Client {
        private final String base; private final HttpClient client = HttpClient.newHttpClient();
        Client(int port) { base = "http://127.0.0.1:" + port; }
        HttpResponse<String> send(String method, String path, String body) throws IOException, InterruptedException {
            HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(base + path));
            if (body == null) builder.method(method, HttpRequest.BodyPublishers.noBody());
            else builder.header("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
                    .method(method, HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8));
            return client.send(builder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        }
    }
}
