package kubernetesdeploymentjavaapp;

public class AppConfig {
    public int port() {
        String value = System.getenv("APP_PORT");
        if (value == null || value.trim().isEmpty()) {
            return 8080;
        }
        // TODO: Add range validation and a clear configuration exception.
        return Integer.parseInt(value);
    }
}
