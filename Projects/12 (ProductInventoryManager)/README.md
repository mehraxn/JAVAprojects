# Product Inventory Manager

An in-memory Java application for products, stock updates, searching, sorting, and reports.

## Implemented features

- Add products with unique SKUs.
- Change product names and non-negative prices.
- Set an absolute stock quantity or apply a relative stock adjustment.
- Prevent stock from becoming negative or overflowing an integer.
- Remove products and retrieve products by SKU.
- Search case-insensitively by SKU or name.
- Sort by name, price, or quantity in ascending or descending order.
- Report products at or below a configurable low-stock threshold.
- Calculate total inventory value with `BigDecimal`.

The default low-stock threshold is `5` units.

## Structure

- `Product` owns product details, stock validation, and stock value.
- `ProductSortField` lists supported sorting choices.
- `Inventory` manages products and inventory reports.
- `Main` demonstrates stock updates, sorting, warnings, and total value.

Source files are under `src/productinventorymanager` and use only standard Java.

## Run

```powershell
javac -d out src\productinventorymanager\*.java
java -cp out productinventorymanager.Main
```

See `TESTING.md` for manual test cases.
