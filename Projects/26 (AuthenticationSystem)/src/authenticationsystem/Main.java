package authenticationsystem;

public class Main {
    public static void main(String[] args) {
        AuthService authService = new AuthService(new PasswordHasher());
        AuthHttpServer httpServer = new AuthHttpServer(authService);
        // TODO: Add a local demonstration without printing passwords or hashes.
        System.out.println("Authentication System skeleton ready.");
        System.out.println("HTTP server was not started.");
    }
}
