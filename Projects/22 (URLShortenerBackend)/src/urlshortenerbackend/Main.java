package urlshortenerbackend;

public class Main {
    public static void main(String[] args) {
        ShortenerService service = new ShortenerService(new CodeGenerator());

        if (args.length > 0 && "server".equalsIgnoreCase(args[0])) {
            startServer(service, args);
            return;
        }

        UrlEntry generated = service.shorten("https://example.com/articles/java-basics");
        UrlEntry custom = service.shorten("https://openjdk.org/", "openjdk");
        service.resolve(generated.getShortCode());
        service.resolve(generated.getShortCode());

        System.out.println("Stored links:");
        for (UrlEntry entry : service.listEntries()) {
            System.out.println(entry);
        }
        System.out.println("Custom link resolves to: " + service.resolve(custom.getShortCode()));
        System.out.println("Run with 'server [port]' to start the optional HTTP interface.");
    }

    private static void startServer(ShortenerService service, String[] args) {
        int port = 8080;
        try {
            if (args.length > 1) {
                port = Integer.parseInt(args[1]);
            }
            UrlShortenerHttpServer server = new UrlShortenerHttpServer(service);
            server.start(port);
            System.out.println("URL shortener listening on http://localhost:" + port);
            System.out.println("Stop the process with Ctrl+C.");
        } catch (NumberFormatException exception) {
            System.err.println("Port must be a number.");
        } catch (Exception exception) {
            System.err.println("Could not start server: " + exception.getMessage());
        }
    }
}
