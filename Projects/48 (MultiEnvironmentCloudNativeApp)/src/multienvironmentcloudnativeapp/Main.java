package multienvironmentcloudnativeapp;

public class Main {
    public static void main(String[] args) {
        String environment = System.getenv().getOrDefault("APP_ENVIRONMENT", "unconfigured");
        // TODO: Add validated configuration plus /health, /ready, and /config endpoints.
        System.out.println("Cloud-native app environment: " + environment);
    }
}
