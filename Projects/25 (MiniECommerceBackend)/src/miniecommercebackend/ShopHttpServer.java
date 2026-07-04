package miniecommercebackend;

import com.sun.net.httpserver.HttpServer;

public class ShopHttpServer {
    private final ShopService service;
    private HttpServer server;

    public ShopHttpServer(ShopService service) {
        this.service = service;
    }

    public void start(int port) {
        // TODO: Add product, cart, and checkout HTTP contexts.
        throw new UnsupportedOperationException("TODO: start shop HTTP server");
    }

    public void stop() {
        // TODO: Stop a previously started server.
        throw new UnsupportedOperationException("TODO: stop shop HTTP server");
    }
}
