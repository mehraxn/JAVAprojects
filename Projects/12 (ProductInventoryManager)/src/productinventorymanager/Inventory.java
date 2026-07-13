package productinventorymanager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory service layer that owns products and all inventory operations:
 * storage, stock changes, search, sorting, and reports.
 *
 * <p>This is the only class outside callers use to change state. Every query
 * returns immutable {@link ProductSnapshot} views in unmodifiable lists, so live
 * {@link Product} objects are never leaked and cannot be mutated from outside.
 *
 * <h2>Behaviour notes</h2>
 * <ul>
 *   <li>SKU uniqueness and lookups are <strong>case-insensitive</strong>
 *       ({@code sku-1} and {@code SKU-1} are the same product).</li>
 *   <li>Low stock means {@code quantity <= reorderThreshold}.</li>
 *   <li>Out of stock means {@code quantity == 0}.</li>
 *   <li>Failed operations leave inventory state unchanged.</li>
 * </ul>
 */
public final class Inventory {

    // Keyed by normalised (lower-case) SKU; preserves insertion order.
    private final Map<String, Product> products = new LinkedHashMap<>();

    // ------------------------------------------------------- product management

    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product must not be null");
        }
        if (products.containsKey(product.skuKey())) {
            throw new IllegalArgumentException("SKU already exists: " + product.getSku());
        }
        products.put(product.skuKey(), product);
    }

    /** Returns a snapshot of the product, or {@link Optional#empty()} if unknown. */
    public Optional<ProductSnapshot> findProductBySku(String sku) {
        Product product = products.get(requireSku(sku).toLowerCase(Locale.ROOT));
        return product == null ? Optional.empty() : Optional.of(new ProductSnapshot(product));
    }

    /** Removes a product by SKU; returns {@code true} if one was removed. */
    public boolean removeProduct(String sku) {
        return products.remove(requireSku(sku).toLowerCase(Locale.ROOT)) != null;
    }

    public List<ProductSnapshot> listProducts() {
        return snapshots(products.values());
    }

    public int size() {
        return products.size();
    }

    // ----------------------------------------------------------- stock changes

    /** Applies a signed stock change to an existing product. */
    public void adjustStock(String sku, int change) {
        requireProduct(sku).adjustStock(change);
    }

    /** Sets an absolute stock quantity on an existing product. */
    public void setStock(String sku, int quantity) {
        requireProduct(sku).setQuantity(quantity);
    }

    // ------------------------------------------------------------------ search

    /** Case-insensitive substring search over SKU only. */
    public List<ProductSnapshot> searchBySku(String query) {
        String needle = requireQuery(query);
        List<Product> matches = new ArrayList<>();
        for (Product product : products.values()) {
            if (product.getSku().toLowerCase(Locale.ROOT).contains(needle)) {
                matches.add(product);
            }
        }
        return snapshotsSortedByName(matches);
    }

    /** Case-insensitive substring search over name only. */
    public List<ProductSnapshot> searchByName(String query) {
        String needle = requireQuery(query);
        List<Product> matches = new ArrayList<>();
        for (Product product : products.values()) {
            if (product.getName().toLowerCase(Locale.ROOT).contains(needle)) {
                matches.add(product);
            }
        }
        return snapshotsSortedByName(matches);
    }

    /** Case-insensitive substring search over both SKU and name. */
    public List<ProductSnapshot> searchProducts(String query) {
        String needle = requireQuery(query);
        List<Product> matches = new ArrayList<>();
        for (Product product : products.values()) {
            if (product.getSku().toLowerCase(Locale.ROOT).contains(needle)
                    || product.getName().toLowerCase(Locale.ROOT).contains(needle)) {
                matches.add(product);
            }
        }
        return snapshotsSortedByName(matches);
    }

    // ------------------------------------------------------------------- sorting

    public List<ProductSnapshot> sortProducts(ProductSortField sortField) {
        return sortProducts(sortField, true);
    }

    public List<ProductSnapshot> sortProducts(ProductSortField sortField, boolean ascending) {
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
        // Deterministic tie-break by SKU (always ascending).
        comparator = comparator.thenComparing(Product::getSku, String.CASE_INSENSITIVE_ORDER);

        List<Product> sorted = new ArrayList<>(products.values());
        sorted.sort(comparator);
        return snapshots(sorted);
    }

    // ------------------------------------------------------------------ reports

    /** Products at or below their reorder threshold, sorted by name. */
    public List<ProductSnapshot> findLowStockProducts() {
        List<Product> matches = new ArrayList<>();
        for (Product product : products.values()) {
            if (product.isLowStock()) {
                matches.add(product);
            }
        }
        return snapshotsSortedByName(matches);
    }

    /** Products with zero quantity, sorted by name. */
    public List<ProductSnapshot> findOutOfStockProducts() {
        List<Product> matches = new ArrayList<>();
        for (Product product : products.values()) {
            if (product.isOutOfStock()) {
                matches.add(product);
            }
        }
        return snapshotsSortedByName(matches);
    }

    /** Total value of all stock: sum of unit price times quantity. */
    public BigDecimal calculateTotalInventoryValue() {
        BigDecimal total = BigDecimal.ZERO;
        for (Product product : products.values()) {
            total = total.add(product.calculateStockValue());
        }
        return total;
    }

    /** Inventory value grouped by category, in first-seen category order. */
    public Map<String, BigDecimal> calculateInventoryValueByCategory() {
        Map<String, BigDecimal> totals = new LinkedHashMap<>();
        for (Product product : products.values()) {
            BigDecimal current = totals.getOrDefault(product.getCategory(), BigDecimal.ZERO);
            totals.put(product.getCategory(), current.add(product.calculateStockValue()));
        }
        return Collections.unmodifiableMap(totals);
    }

    /** Top {@code limit} products by inventory value, descending (tie-break by SKU). */
    public List<ProductSnapshot> findHighestValueProducts(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than zero");
        }
        List<Product> sorted = new ArrayList<>(products.values());
        sorted.sort(Comparator.comparing(Product::calculateStockValue).reversed()
                .thenComparing(Product::getSku, String.CASE_INSENSITIVE_ORDER));
        if (sorted.size() > limit) {
            sorted = sorted.subList(0, limit);
        }
        return snapshots(sorted);
    }

    // ------------------------------------------------------------------ helpers

    private Product requireProduct(String sku) {
        Product product = products.get(requireSku(sku).toLowerCase(Locale.ROOT));
        if (product == null) {
            throw new IllegalArgumentException("Unknown SKU: " + sku.trim());
        }
        return product;
    }

    private static List<ProductSnapshot> snapshotsSortedByName(List<Product> source) {
        source.sort(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Product::getSku, String.CASE_INSENSITIVE_ORDER));
        return snapshots(source);
    }

    private static List<ProductSnapshot> snapshots(Iterable<Product> source) {
        List<ProductSnapshot> views = new ArrayList<>();
        for (Product product : source) {
            views.add(new ProductSnapshot(product));
        }
        return Collections.unmodifiableList(views);
    }

    private static String requireSku(String sku) {
        if (sku == null || sku.trim().isEmpty()) {
            throw new IllegalArgumentException("SKU must not be blank");
        }
        return sku.trim();
    }

    private static String requireQuery(String query) {
        if (query == null) {
            throw new IllegalArgumentException("Search text must not be null");
        }
        return query.trim().toLowerCase(Locale.ROOT);
    }
}
