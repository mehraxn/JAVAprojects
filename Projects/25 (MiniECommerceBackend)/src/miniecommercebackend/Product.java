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
}
