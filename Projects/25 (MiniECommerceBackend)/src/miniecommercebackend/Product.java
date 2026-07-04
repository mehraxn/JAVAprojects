package miniecommercebackend;

import java.math.BigDecimal;

public class Product {
    private final String id;
    private String name;
    private BigDecimal price;
    private int stockQuantity;

    public Product(String id, String name, BigDecimal price, int stockQuantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }

    public void updateDetails(String name, BigDecimal price) {
        // TODO: Validate and update product details.
        throw new UnsupportedOperationException("TODO: update product details");
    }

    public void adjustStock(int change) {
        // TODO: Apply the change without allowing negative stock or overflow.
        throw new UnsupportedOperationException("TODO: adjust product stock");
    }
}
