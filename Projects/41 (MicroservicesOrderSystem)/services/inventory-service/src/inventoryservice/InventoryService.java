package inventoryservice;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryService {
    private final Map<String, InventoryItem> items = new ConcurrentHashMap<>();
    private final Map<String, Reservation> reservations = new ConcurrentHashMap<>();

    public InventoryService() {
        items.put("JAVA-BOOK", new InventoryItem("JAVA-BOOK", 10));
        items.put("DEVOPS-KIT", new InventoryItem("DEVOPS-KIT", 5));
    }

    public Optional<InventoryItem> find(String sku) {
        return Optional.ofNullable(items.get(normalize(sku, "SKU")));
    }

    public synchronized boolean reserve(String orderId, String sku, int quantity) {
        String cleanOrderId = normalize(orderId, "Order ID");
        String cleanSku = normalize(sku, "SKU");
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        Reservation existing = reservations.get(cleanOrderId);
        if (existing != null) {
            return existing.sku.equals(cleanSku) && existing.quantity == quantity;
        }
        InventoryItem item = items.get(cleanSku);
        if (item == null || !item.reserve(quantity)) {
            return false;
        }
        reservations.put(cleanOrderId, new Reservation(cleanSku, quantity));
        return true;
    }

    public synchronized boolean release(String orderId) {
        String cleanOrderId = normalize(orderId, "Order ID");
        Reservation reservation = reservations.remove(cleanOrderId);
        if (reservation == null) {
            return false;
        }
        items.get(reservation.sku).release(reservation.quantity);
        return true;
    }

    private static String normalize(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required.");
        }
        return value.trim().toUpperCase();
    }

    private static class Reservation {
        private final String sku;
        private final int quantity;

        private Reservation(String sku, int quantity) {
            this.sku = sku;
            this.quantity = quantity;
        }
    }
}
