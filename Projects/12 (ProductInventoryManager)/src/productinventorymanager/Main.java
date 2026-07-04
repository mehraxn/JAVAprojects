package productinventorymanager;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        Inventory inventory = new Inventory();
        inventory.addProduct(new Product("P100", "Keyboard", new BigDecimal("49.90"), 8));
        inventory.addProduct(new Product("P200", "Mouse", new BigDecimal("24.50"), 4));
        inventory.addProduct(new Product("P300", "Monitor", new BigDecimal("189.00"), 2));

        inventory.adjustStock("P100", -3);
        inventory.setStock("P200", 10);

        System.out.println("Products sorted by price:");
        for (Product product : inventory.sortProducts(ProductSortField.PRICE)) {
            System.out.println(product.getName() + ": " + product.getUnitPrice()
                    + ", quantity " + product.getQuantity());
        }

        System.out.println("Low-stock warnings:");
        for (Product product : inventory.findLowStockProducts()) {
            System.out.println("- " + product.getName() + " has " + product.getQuantity() + " left");
        }
        System.out.println("Total inventory value: " + inventory.calculateTotalValue());
    }
}
