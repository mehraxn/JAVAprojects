package contactsrestapi;

import com.sun.net.httpserver.HttpServer;

public class ContactHttpServer {
    private final ContactService service;
    private HttpServer server;

    public ContactHttpServer(ContactService service) {
        this.service = service;
    }

    public void start(int port) {
        // TODO: Register CRUD contexts, method checks, and JSON responses.
        throw new UnsupportedOperationException("TODO: start contacts HTTP server");
    }

    public void stop() {
        // TODO: Stop a previously started server safely.
        throw new UnsupportedOperationException("TODO: stop contacts HTTP server");
    }
}
