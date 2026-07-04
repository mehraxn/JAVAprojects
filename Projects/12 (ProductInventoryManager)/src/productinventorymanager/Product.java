package productinventorymanager;

import java.math.BigDecimal;

public class Product {
    private final String sku;
    private String name;
    private BigDecimal unitPrice;
    private int quantity;

    public Product(String sku, String name, BigDecimal unitPrice, int quantity) {
        this.sku = requireText(sku, "SKU");
        setName(name);
        setUnitPrice(unitPrice);
        setQuantity(quantity);
    }

    public String getSku() { return sku; }
    public String getName() { return name; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }

    public void setName(String name) {
        this.name = requireText(name, "Product name");
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Unit price must not be negative");
        }
        this.unitPrice = unitPrice;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must not be negative");
        }
        this.quantity = quantity;
    }

    public void adjustStock(int change) {
        long updatedQuantity = (long) quantity + change;
        if (updatedQuantity < 0 || updatedQuantity > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Stock adjustment produces an invalid quantity");
        }
        quantity = (int) updatedQuantity;
    }

    public boolean isLowStock(int threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException("Low-stock threshold must not be negative");
        }
        return quantity <= threshold;
    }

    public BigDecimal calculateStockValue() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
