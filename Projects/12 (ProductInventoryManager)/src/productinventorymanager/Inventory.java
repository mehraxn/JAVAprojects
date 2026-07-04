package productinventorymanager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Inventory {
    public static final int DEFAULT_LOW_STOCK_THRESHOLD = 5;

    private final Map<String, Product> products = new LinkedHashMap<>();

    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product must not be null");
        }
        if (products.containsKey(product.getSku())) {
            throw new IllegalArgumentException("SKU already exists: " + product.getSku());
        }
        products.put(product.getSku(), product);
    }

    public Product getProduct(String sku) {
        String validSku = requireSku(sku);
        Product product = products.get(validSku);
        if (product == null) {
            throw new IllegalArgumentException("Unknown SKU: " + validSku);
        }
        return product;
    }

    public void adjustStock(String sku, int change) {
        getProduct(sku).adjustStock(change);
    }

    public void setStock(String sku, int quantity) {
        getProduct(sku).setQuantity(quantity);
    }

    public boolean removeProduct(String sku) {
        return products.remove(requireSku(sku)) != null;
    }

    public List<Product> findLowStockProducts() {
        return findLowStockProducts(DEFAULT_LOW_STOCK_THRESHOLD);
    }

    public List<Product> findLowStockProducts(int threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException("Low-stock threshold must not be negative");
        }
        List<Product> lowStockProducts = new ArrayList<>();
        for (Product product : products.values()) {
            if (product.isLowStock(threshold)) {
                lowStockProducts.add(product);
            }
        }
        lowStockProducts.sort(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER));
        return Collections.unmodifiableList(lowStockProducts);
    }

    public List<Product> searchProducts(String searchText) {
        if (searchText == null) {
            throw new IllegalArgumentException("Search text must not be null");
        }
        String query = searchText.trim().toLowerCase(Locale.ROOT);
        List<Product> matches = new ArrayList<>();
        for (Product product : products.values()) {
            if (product.getSku().toLowerCase(Locale.ROOT).contains(query)
                    || product.getName().toLowerCase(Locale.ROOT).contains(query)) {
                matches.add(product);
            }
        }
        matches.sort(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER));
        return Collections.unmodifiableList(matches);
    }

    public List<Product> sortProducts(ProductSortField sortField) {
        return sortProducts(sortField, true);
    }

    public List<Product> sortProducts(ProductSortField sortField, boolean ascending) {
        if (sortField == null) {
            throw new IllegalArgumentException("Sort field must not be null");
        }
        Comparator<Product> comparator;
        switch (sortField) {
            case NAME:
                comparator = Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER);
                break;
            case PRICE:
                comparator = Comparator.comparing(Product::getUnitPrice);
                break;
            case QUANTITY:
                comparator = Comparator.comparingInt(Product::getQuantity);
                break;
            default:
                throw new IllegalArgumentException("Unsupported sort field: " + sortField);
        }
        if (!ascending) {
            comparator = comparator.reversed();
        }
        comparator = comparator.thenComparing(Product::getSku);

        List<Product> sortedProducts = new ArrayList<>(products.values());
        sortedProducts.sort(comparator);
        return Collections.unmodifiableList(sortedProducts);
    }

    public List<Product> listProducts() {
        return Collections.unmodifiableList(new ArrayList<>(products.values()));
    }

    public BigDecimal calculateTotalValue() {
        BigDecimal total = BigDecimal.ZERO;
        for (Product product : products.values()) {
            total = total.add(product.calculateStockValue());
        }
        return total;
    }

    private static String requireSku(String sku) {
        if (sku == null || sku.trim().isEmpty()) {
            throw new IllegalArgumentException("SKU must not be blank");
        }
        return sku.trim();
    }
}
