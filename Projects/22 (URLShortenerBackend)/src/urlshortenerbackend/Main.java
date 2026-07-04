package urlshortenerbackend;

public class Main {
    public static void main(String[] args) {
        ShortenerService service = new ShortenerService(new CodeGenerator());
        UrlShortenerHttpServer httpServer = new UrlShortenerHttpServer(service);
        // TODO: Demonstrate service logic and optionally start HttpServer.
        System.out.println("URL Shortener Backend skeleton ready.");
        System.out.println("HTTP server was not started.");
    }
}
