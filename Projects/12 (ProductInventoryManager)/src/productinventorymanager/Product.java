package productinventorymanager;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * A single product tracked by the inventory.
 *
 * <p>Identity, name, category, unit price, and reorder threshold are fixed at
 * construction. Only the stock {@code quantity} changes, and only through
 * package-private methods driven by {@link Inventory}. Outside callers never
 * receive a live {@code Product}; {@link Inventory} hands out immutable
 * {@link ProductSnapshot} views instead, so internal state cannot be corrupted.
 *
 * <p>Money uses {@link BigDecimal} to avoid floating-point rounding errors; unit
 * price must be strictly greater than zero.
 */
public final class Product {
    /** Default reorder threshold used by the simpler constructor. */
    public static final int DEFAULT_REORDER_THRESHOLD = 5;
    /** Default category used by the simpler constructor. */
    public static final String DEFAULT_CATEGORY = "General";

    private final String sku;
    private final String name;
    private final String category;
    private final BigDecimal unitPrice;
    private final int reorderThreshold;
    private int quantity;

    /** Creates a product with an explicit category and reorder threshold. */
    public Product(String sku, String name, String category,
                   BigDecimal unitPrice, int quantity, int reorderThreshold) {
        this.sku = requireText(sku, "SKU");
        this.name = requireText(name, "Product name");
        this.category = requireText(category, "Category");
        if (unitPrice == null) {
            throw new IllegalArgumentException("Unit price must not be null");
        }
        if (unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Unit price must be greater than zero");
        }
        this.unitPrice = unitPrice;
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must not be negative");
        }
        this.quantity = quantity;
        if (reorderThreshold < 0) {
            throw new IllegalArgumentException("Reorder threshold must not be negative");
        }
        this.reorderThreshold = reorderThreshold;
    }

    /** Creates a product with the default category and reorder threshold. */
    public Product(String sku, String name, BigDecimal unitPrice, int quantity) {
        this(sku, name, DEFAULT_CATEGORY, unitPrice, quantity, DEFAULT_REORDER_THRESHOLD);
    }

    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }
    public int getReorderThreshold() { return reorderThreshold; }

    /** Normalised SKU key used for case-insensitive lookups. */
    String skuKey() {
        return sku.toLowerCase(Locale.ROOT);
    }

    /** Applies a signed stock change; rejects underflow and integer overflow. */
    void adjustStock(int change) {
        long updatedQuantity = (long) quantity + change;
        if (updatedQuantity < 0) {
            throw new IllegalArgumentException(
                    "Stock adjustment would make quantity negative for SKU " + sku);
        }
        if (updatedQuantity > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(
                    "Stock adjustment would overflow quantity for SKU " + sku);
        }
        quantity = (int) updatedQuantity;
    }

    /** Sets an absolute stock quantity; rejects negatives. */
    void setQuantity(int newQuantity) {
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Quantity must not be negative");
        }
        quantity = newQuantity;
    }

    public boolean isLowStock() {
        return quantity <= reorderThreshold;
    }

    public boolean isOutOfStock() {
        return quantity == 0;
    }

    /** Inventory value for this product: unit price times quantity. */
    public BigDecimal calculateStockValue() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public String toString() {
        return sku + " " + name + " (" + category + ") "
                + unitPrice + " x " + quantity;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
