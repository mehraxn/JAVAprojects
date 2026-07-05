package bluegreencanarydeployment;

public class Main {
    public static void main(String[] args) {
        String version = System.getenv().getOrDefault("APP_VERSION", "unconfigured");
        // TODO: Expose /version, /health, and /ready over a small HTTP server.
        System.out.println("Deployment version: " + version);
    }
}
