package dockercomposefullstack;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class HealthCheck {
    public static void main(String[] args) {
        try {
            String port = System.getenv().getOrDefault("APP_PORT", "8080");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://127.0.0.1:" + port + "/health"))
                    .timeout(Duration.ofSeconds(2))
                    .GET()
                    .build();
            HttpResponse<Void> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() != 200) {
                System.exit(1);
            }
        } catch (Exception exception) {
            System.exit(1);
        }
    }
}
