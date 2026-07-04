package productinventorymanager;

import java.math.BigDecimal;

public class Product {
    private final String sku;
    private String name;
    private BigDecimal unitPrice;
    private int quantity;

    public Product(String sku, String name, BigDecimal unitPrice, int quantity) {
        this.sku = sku;
        this.name = name;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public String getSku() { return sku; }
    public int getQuantity() { return quantity; }

    public void adjustStock(int change) {
        // TODO: Prevent negative stock before applying the change.
        throw new UnsupportedOperationException("TODO: adjust stock");
    }

    public BigDecimal calculateStockValue() {
        // TODO: Multiply unit price by quantity.
        throw new UnsupportedOperationException("TODO: calculate stock value");
    }
}
