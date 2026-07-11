package miniecommercebackend;

import java.math.BigDecimal;

public final class Product {
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
        this.name = requireName(name);
        this.price = requirePrice(price);
        this.stockQuantity = stockQuantity;
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
        String checkedName = requireName(name);
        BigDecimal checkedPrice = requirePrice(price);
        this.name = checkedName;
        this.price = checkedPrice;
    }

    public void increaseStock(int quantity) {
        requirePositiveQuantity(quantity);
        long result = (long) stockQuantity + quantity;
        if (result > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Stock quantity is too large.");
        }
        stockQuantity = (int) result;
    }

    public void decreaseStock(int quantity) {
        requirePositiveQuantity(quantity);
        if (quantity > stockQuantity) {
            throw new IllegalArgumentException("Stock cannot become negative.");
        }
        stockQuantity -= quantity;
    }

    private static String requireName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }
        return name.trim();
    }

    private static BigDecimal requirePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be greater than zero.");
        }
        return price;
    }

    private static void requirePositiveQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Stock quantity change must be positive.");
        }
    }

    public Product copy() {
        return new Product(id, name, price, stockQuantity);
    }

    @Override
    public String toString() {
        return id + ": " + name + " | " + price.toPlainString() + " | stock " + stockQuantity;
    }
}
