package authenticationsystem;

import com.sun.net.httpserver.HttpServer;

public class AuthHttpServer {
    private final AuthService authService;
    private HttpServer server;

    public AuthHttpServer(AuthService authService) {
        this.authService = authService;
    }

    public void start(int port) {
        // TODO: Add local register, login, authenticate, and logout contexts.
        throw new UnsupportedOperationException("TODO: start authentication server");
    }

    public void stop() {
        // TODO: Stop a previously started server safely.
        throw new UnsupportedOperationException("TODO: stop authentication server");
    }
}
