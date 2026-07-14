package productinventorymanager;

import java.math.BigDecimal;

/**
 * Immutable, read-only view of a {@link Product} handed to outside callers.
 *
 * <p>{@link Inventory} returns these instead of live {@code Product} objects, so
 * holding one can never change inventory state. Every field is captured at
 * snapshot time, including the derived {@link #getInventoryValue() inventory
 * value} (unit price times quantity).
 */
public final class ProductSnapshot {
    private final String sku;
    private final String name;
    private final String category;
    private final BigDecimal unitPrice;
    private final int quantity;
    private final int reorderThreshold;
    private final BigDecimal inventoryValue;

    ProductSnapshot(Product product) {
        this.sku = product.getSku();
        this.name = product.getName();
        this.category = product.getCategory();
        this.unitPrice = product.getUnitPrice();
        this.quantity = product.getQuantity();
        this.reorderThreshold = product.getReorderThreshold();
        this.inventoryValue = product.calculateStockValue();
    }

    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }
    public int getReorderThreshold() { return reorderThreshold; }
    public BigDecimal getInventoryValue() { return inventoryValue; }

    public boolean isLowStock() { return quantity <= reorderThreshold; }
    public boolean isOutOfStock() { return quantity == 0; }

    @Override
    public String toString() {
        return sku + " " + name + " (" + category + ") "
                + unitPrice + " x " + quantity + " = " + inventoryValue;
    }
}
