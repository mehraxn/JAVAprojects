package orderservice;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class HttpDownstreamGateway implements DownstreamGateway {
    private final HttpClient client;
    private final String inventoryUrl;
    private final String paymentUrl;
    private final String notificationUrl;

    public HttpDownstreamGateway(String inventoryUrl, String paymentUrl, String notificationUrl) {
        client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();
        this.inventoryUrl = requireUrl(inventoryUrl, "Inventory URL");
        this.paymentUrl = requireUrl(paymentUrl, "Payment URL");
        this.notificationUrl = requireUrl(notificationUrl, "Notification URL");
    }

    @Override
    public boolean reserveInventory(Order order) {
        return post(inventoryUrl + "/inventory/reserve?orderId=" + encode(order.getId())
                + "&sku=" + encode(order.getSku()) + "&quantity=" + order.getQuantity());
    }

    @Override
    public boolean releaseInventory(Order order) {
        return post(inventoryUrl + "/inventory/release?orderId=" + encode(order.getId()));
    }

    @Override
    public boolean authorizePayment(Order order) {
        return post(paymentUrl + "/payments/authorize?orderId=" + encode(order.getId())
                + "&amount=" + encode(order.getTotal().toPlainString()));
    }

    @Override
    public boolean sendNotification(Order order) {
        String message = "Order " + order.getId() + " is confirmed";
        return post(notificationUrl + "/notifications?orderId=" + encode(order.getId())
                + "&message=" + encode(message));
    }

    private boolean post(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(3))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            int statusCode = client.send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
            return statusCode >= 200 && statusCode < 300;
        } catch (IllegalArgumentException exception) {
            return false;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return false;
        } catch (Exception exception) {
            return false;
        }
    }

    private static String requireUrl(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required.");
        }
        URI uri = URI.create(value.trim());
        if (!("http".equals(uri.getScheme()) || "https".equals(uri.getScheme())) || uri.getHost() == null) {
            throw new IllegalArgumentException(field + " must be an HTTP URL.");
        }
        return value.trim().replaceAll("/+$", "");
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
