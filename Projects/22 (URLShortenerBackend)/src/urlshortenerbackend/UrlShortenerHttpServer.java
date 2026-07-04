package urlshortenerbackend;

import com.sun.net.httpserver.HttpServer;

public class UrlShortenerHttpServer {
    private final ShortenerService service;
    private HttpServer server;

    public UrlShortenerHttpServer(ShortenerService service) {
        this.service = service;
    }

    public void start(int port) {
        // TODO: Create HttpServer contexts for shortening and resolving URLs.
        throw new UnsupportedOperationException("TODO: start the URL shortener server");
    }

    public void stop() {
        // TODO: Stop the server safely when it has been started.
        throw new UnsupportedOperationException("TODO: stop the URL shortener server");
    }
}
