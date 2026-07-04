package blogapi;

import com.sun.net.httpserver.HttpServer;

public class BlogHttpServer {
    private final BlogService service;
    private HttpServer server;

    public BlogHttpServer(BlogService service) {
        this.service = service;
    }

    public void start(int port) {
        // TODO: Add post/comment contexts and manual JSON-like responses.
        throw new UnsupportedOperationException("TODO: start blog HTTP server");
    }

    public void stop() {
        // TODO: Stop the server safely.
        throw new UnsupportedOperationException("TODO: stop blog HTTP server");
    }
}
