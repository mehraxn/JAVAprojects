package productinventorymanager;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Inventory {
    private final Map<String, Product> products = new HashMap<>();

    public void addProduct(Product product) {
        // TODO: Validate and add a product with a unique SKU.
        throw new UnsupportedOperationException("TODO: add a product");
    }

    public boolean removeProduct(String sku) {
        // TODO: Remove the product when it exists.
        throw new UnsupportedOperationException("TODO: remove a product");
    }

    public List<Product> findLowStockProducts(int threshold) {
        // TODO: Return products at or below the supplied threshold.
        throw new UnsupportedOperationException("TODO: find low stock");
    }

    public List<Product> searchProducts(String searchText) {
        // TODO: Search by SKU or product name.
        throw new UnsupportedOperationException("TODO: search products");
    }

    public BigDecimal calculateTotalValue() {
        // TODO: Sum the stock value of every product.
        throw new UnsupportedOperationException("TODO: calculate inventory value");
    }
}
