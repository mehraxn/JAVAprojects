package inventoryservice;

public class InventoryItem {
    private final String sku;
    private int availableQuantity;

    public InventoryItem(String sku, int availableQuantity) {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("SKU is required.");
        }
        if (availableQuantity < 0) {
            throw new IllegalArgumentException("Available quantity cannot be negative.");
        }
        this.sku = sku.trim();
        this.availableQuantity = availableQuantity;
    }

    public synchronized boolean reserve(int quantity) {
        if (quantity <= 0 || quantity > availableQuantity) {
            return false;
        }
        availableQuantity -= quantity;
        return true;
    }

    public synchronized void release(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Release quantity must be positive.");
        }
        availableQuantity += quantity;
    }

    public synchronized String toJson() {
        return "{\"sku\":\"" + escape(sku) + "\",\"availableQuantity\":"
                + availableQuantity + "}";
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
