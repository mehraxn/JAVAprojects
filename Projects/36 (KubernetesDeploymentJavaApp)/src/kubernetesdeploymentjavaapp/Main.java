package kubernetesdeploymentjavaapp;

public class Main {
    public static void main(String[] args) {
        AppConfig config = new AppConfig();
        // TODO: Start a built-in HttpServer with /health and /ready endpoints.
        System.out.println("Kubernetes Java app starter configured for port " + config.port() + ".");
        System.out.println("No HTTP server was started.");
    }
}
