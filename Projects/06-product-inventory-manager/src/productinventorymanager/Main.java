package productinventorymanager;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * Command-line entry point and demo driver.
 *
 * <p>All work happens in {@link #run(String[], PrintStream, PrintStream)}, which
 * returns an exit code and never calls {@link System#exit}. Only
 * {@link #main(String[])} exits the JVM, so the CLI can be tested in-process.
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        int exitCode = run(args, System.out, System.err);
        System.exit(exitCode);
    }

    /**
     * Runs one CLI command.
     *
     * @return {@code 0} for a recognised command (including {@code validation-demo},
     *         whose failures are intentional), non-zero for an unknown command.
     */
    public static int run(String[] args, PrintStream out, PrintStream err) {
        String command = (args == null || args.length == 0) ? "help" : args[0];
        switch (command) {
            case "help":
            case "--help":
            case "-h":
                printHelp(out);
                return 0;
            case "demo":
                runDemo(out);
                return 0;
            case "stock-demo":
                runStockDemo(out);
                return 0;
            case "search-demo":
                runSearchDemo(out);
                return 0;
            case "sort-demo":
                runSortDemo(out);
                return 0;
            case "report-demo":
                runReportDemo(out);
                return 0;
            case "validation-demo":
                runValidationDemo(out);
                return 0;
            default:
                err.println("Unknown command: " + command);
                err.println("Run 'help' to see available commands.");
                return 2;
        }
    }

    private static void printHelp(PrintStream out) {
        out.println("Product Inventory Manager");
        out.println();
        out.println("Usage: java -cp out productinventorymanager.Main <command>");
        out.println();
        out.println("Commands:");
        out.println("  help             Show this help text.");
        out.println("  demo             End-to-end walkthrough of the main features.");
        out.println("  stock-demo       Stock increase/decrease and underflow/overflow safeguards.");
        out.println("  search-demo      Case-insensitive search by SKU and name.");
        out.println("  sort-demo        Sort by name/price/quantity, ascending and descending.");
        out.println("  report-demo      Low-stock, out-of-stock, valuation, and category reports.");
        out.println("  validation-demo  Intentional validation failures, handled cleanly.");
    }

    // ------------------------------------------------------------------- demos

    private static Inventory sampleInventory() {
        Inventory inventory = new Inventory();
        inventory.addProduct(new Product("P100", "Keyboard", "Electronics", money("49.90"), 8, 5));
        inventory.addProduct(new Product("P200", "Mouse", "Electronics", money("24.50"), 4, 5));
        inventory.addProduct(new Product("P300", "Monitor", "Electronics", money("189.00"), 2, 3));
        inventory.addProduct(new Product("P400", "Desk Lamp", "Home", money("15.75"), 0, 2));
        inventory.addProduct(new Product("P500", "Notebook", "Stationery", money("3.20"), 40, 10));
        return inventory;
    }

    private static void runDemo(PrintStream out) {
        out.println("== Product Inventory Demo ==");
        Inventory inventory = sampleInventory();

        out.println();
        out.println("All products (" + inventory.size() + "):");
        printProducts(out, inventory.listProducts());

        out.println();
        out.println("Search 'mo': " + skus(inventory.searchProducts("mo")));

        out.println();
        out.println("Adjusting stock: Keyboard -3, Mouse set to 10");
        inventory.adjustStock("P100", -3);
        inventory.setStock("P200", 10);

        out.println();
        out.println("Products sorted by price (ascending):");
        printProducts(out, inventory.sortProducts(ProductSortField.PRICE));

        out.println();
        out.println("Low-stock products (quantity <= reorder threshold):");
        printProducts(out, inventory.findLowStockProducts());

        out.println();
        out.println("Total inventory value: " + formatMoney(inventory.calculateTotalInventoryValue()));
    }

    private static void runStockDemo(PrintStream out) {
        out.println("== Stock Demo ==");
        Inventory inventory = sampleInventory();
        out.println("Mouse (P200) starting quantity: " + quantityOf(inventory, "P200"));

        inventory.adjustStock("P200", 6);
        out.println("After +6: " + quantityOf(inventory, "P200"));

        inventory.adjustStock("P200", -4);
        out.println("After -4: " + quantityOf(inventory, "P200"));

        out.println();
        out.println("Attempting underflow (-100)...");
        try {
            inventory.adjustStock("P200", -100);
            out.println("  ERROR: underflow was NOT rejected");
        } catch (IllegalArgumentException expected) {
            out.println("  Rejected as expected: " + expected.getMessage());
        }
        out.println("Quantity unchanged: " + quantityOf(inventory, "P200"));

        out.println();
        out.println("Attempting overflow (+Integer.MAX_VALUE)...");
        try {
            inventory.adjustStock("P200", Integer.MAX_VALUE);
            out.println("  ERROR: overflow was NOT rejected");
        } catch (IllegalArgumentException expected) {
            out.println("  Rejected as expected: " + expected.getMessage());
        }
        out.println("Quantity unchanged: " + quantityOf(inventory, "P200"));
    }

    private static void runSearchDemo(PrintStream out) {
        out.println("== Search Demo ==");
        Inventory inventory = sampleInventory();

        out.println("Search by SKU 'p3': " + skus(inventory.searchBySku("p3")));
        out.println("Search by name 'MOUSE' (case-insensitive): "
                + skus(inventory.searchByName("MOUSE")));
        out.println("Search by name 'o': " + skus(inventory.searchByName("o")));
        out.println("Search by SKU 'zzz' (no match): " + skus(inventory.searchBySku("zzz")));

        out.println();
        out.println("Find by exact SKU 'p100': "
                + inventory.findProductBySku("p100").map(ProductSnapshot::getName).orElse("<none>"));
        out.println("Find by unknown SKU 'nope': "
                + inventory.findProductBySku("nope").map(ProductSnapshot::getName).orElse("<none>"));
    }

    private static void runSortDemo(PrintStream out) {
        out.println("== Sort Demo ==");
        Inventory inventory = sampleInventory();

        out.println("By name (ascending): " + skus(inventory.sortProducts(ProductSortField.NAME)));
        out.println("By name (descending): "
                + skus(inventory.sortProducts(ProductSortField.NAME, false)));
        out.println("By price (ascending): " + skus(inventory.sortProducts(ProductSortField.PRICE)));
        out.println("By price (descending): "
                + skus(inventory.sortProducts(ProductSortField.PRICE, false)));
        out.println("By quantity (ascending): "
                + skus(inventory.sortProducts(ProductSortField.QUANTITY)));
        out.println("By quantity (descending): "
                + skus(inventory.sortProducts(ProductSortField.QUANTITY, false)));
    }

    private static void runReportDemo(PrintStream out) {
        out.println("== Report Demo ==");
        Inventory inventory = sampleInventory();

        out.println("Low-stock products:");
        printProducts(out, inventory.findLowStockProducts());

        out.println();
        out.println("Out-of-stock products:");
        printProducts(out, inventory.findOutOfStockProducts());

        out.println();
        out.println("Total inventory value: " + formatMoney(inventory.calculateTotalInventoryValue()));

        out.println();
        out.println("Inventory value by category:");
        for (Map.Entry<String, BigDecimal> entry : inventory.calculateInventoryValueByCategory().entrySet()) {
            out.println("  " + entry.getKey() + ": " + formatMoney(entry.getValue()));
        }

        out.println();
        out.println("Top 3 products by inventory value:");
        for (ProductSnapshot snapshot : inventory.findHighestValueProducts(3)) {
            out.println("  " + snapshot.getName() + ": " + formatMoney(snapshot.getInventoryValue()));
        }
    }

    private static void runValidationDemo(PrintStream out) {
        out.println("== Validation Demo (failures below are intentional) ==");
        Inventory inventory = sampleInventory();

        expectFailure(out, "blank SKU", () -> new Product("  ", "Cable", money("5.00"), 3));
        expectFailure(out, "blank name", () -> new Product("P900", "  ", money("5.00"), 3));
        expectFailure(out, "null price", () -> new Product("P900", "Cable", null, 3));
        expectFailure(out, "zero price", () -> new Product("P900", "Cable", money("0.00"), 3));
        expectFailure(out, "negative price", () -> new Product("P900", "Cable", money("-1.00"), 3));
        expectFailure(out, "negative quantity", () -> new Product("P900", "Cable", money("5.00"), -1));
        expectFailure(out, "duplicate SKU (case-insensitive)",
                () -> inventory.addProduct(new Product("p100", "Clone", money("5.00"), 1)));
        expectFailure(out, "stock underflow",
                () -> inventory.adjustStock("P300", -1000));
        expectFailure(out, "missing SKU stock adjust",
                () -> inventory.adjustStock("NOPE", 1));

        out.println();
        out.println("All validation failures were handled cleanly.");
    }

    // ----------------------------------------------------------------- helpers

    private static BigDecimal money(String value) {
        return new BigDecimal(value);
    }

    private static String formatMoney(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private static int quantityOf(Inventory inventory, String sku) {
        return inventory.findProductBySku(sku).map(ProductSnapshot::getQuantity).orElse(-1);
    }

    private static void printProducts(PrintStream out, List<ProductSnapshot> products) {
        if (products.isEmpty()) {
            out.println("  (none)");
            return;
        }
        for (ProductSnapshot product : products) {
            out.println("  " + product.getSku() + "  " + pad(product.getName(), 12)
                    + " " + pad(product.getCategory(), 12)
                    + " price " + pad(formatMoney(product.getUnitPrice()), 8)
                    + " qty " + product.getQuantity()
                    + " value " + formatMoney(product.getInventoryValue()));
        }
    }

    private static String pad(String text, int width) {
        if (text.length() >= width) {
            return text;
        }
        StringBuilder builder = new StringBuilder(text);
        while (builder.length() < width) {
            builder.append(' ');
        }
        return builder.toString();
    }

    private static String skus(List<ProductSnapshot> products) {
        if (products.isEmpty()) {
            return "(no matches)";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < products.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(products.get(i).getSku());
        }
        return builder.toString();
    }

    private static void expectFailure(PrintStream out, String label, Runnable action) {
        try {
            action.run();
            out.println("  [" + label + "] ERROR: expected a failure but none occurred");
        } catch (IllegalArgumentException | IllegalStateException expected) {
            out.println("  [" + label + "] rejected: " + expected.getMessage());
        }
    }
}
