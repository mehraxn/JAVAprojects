# Testing Product Inventory Manager

The project has no external test dependencies. Compile and run `Main`, or call the classes from a small Java driver.

## Manual test cases

1. Add several products with unique SKUs and verify `listProducts()` contains them.
2. Add a duplicate SKU; expect `IllegalArgumentException`.
3. Increase and decrease stock while keeping the result non-negative.
4. Set stock to zero and verify it is accepted.
5. Try a negative absolute quantity or an adjustment below zero; expect `IllegalArgumentException` and no stock change.
6. Try an adjustment that exceeds `Integer.MAX_VALUE`; expect `IllegalArgumentException`.
7. Search by partial SKU, partial name, and different letter case.
8. Sort by name, price, and quantity and verify ascending order.
9. Repeat each sort in descending order.
10. Verify products at or below the threshold appear in the low-stock report.
11. Try a negative low-stock threshold; expect `IllegalArgumentException`.
12. Verify total inventory value equals the sum of price multiplied by quantity.
13. Remove an existing product, then remove it again; expect `true` followed by `false`.
14. Try blank product data, a negative price, or an unknown SKU; expect `IllegalArgumentException`.
15. Try modifying a returned product list; expect `UnsupportedOperationException`.
