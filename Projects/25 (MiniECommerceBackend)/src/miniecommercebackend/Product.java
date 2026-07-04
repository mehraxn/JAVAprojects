package miniecommercebackend;

import java.math.BigDecimal;

public class Product {
    private final String id;
    private String name;
    private BigDecimal price;
    private int stockQuantity;

    public Product(String id, String name, BigDecimal price, int stockQuantity) {
        if (id == null || !id.matches("[A-Za-z0-9_-]{1,40}")) {
            throw new IllegalArgumentException(
                    "Product ID must contain 1-40 letters, numbers, underscores, or hyphens.");
        }
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative.");
        }
        this.id = id;
        this.stockQuantity = stockQuantity;
        updateDetails(name, price);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void updateDetails(String name, BigDecimal price) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be greater than zero.");
        }
        this.name = name.trim();
        this.price = price;
    }

    public void adjustStock(int change) {
        long result = (long) stockQuantity + change;
        if (result < 0) {
            throw new IllegalArgumentException("Stock cannot become negative.");
        }
        if (result > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Stock quantity is too large.");
        }
        stockQuantity = (int) result;
    }

    public Product copy() {
        return new Product(id, name, price, stockQuantity);
    }

    @Override
    public String toString() {
        return id + ": " + name + " | " + price.toPlainString() + " | stock " + stockQuantity;
    }
}
