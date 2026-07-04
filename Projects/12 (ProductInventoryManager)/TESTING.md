# Testing Product Inventory Manager

## Testing approach

Create products with known prices and quantities. Verify both Product state and Inventory query results after each operation.

## Normal test cases

| Test | Action | Expected result |
|---|---|---|
| Add products | Add several unique SKUs | Products appear in listProducts |
| Adjust stock | Add and subtract valid quantities | Quantity changes correctly |
| Set stock | Set an absolute quantity | Product stores that value |
| Search | Search partial SKU or name | Case-insensitive matches are returned |
| Sort | Sort by each supported field | Results follow selected order |
| Total value | Sum known products | Total equals price times quantity sum |

## Edge-case test cases

| Test | Action | Expected result |
|---|---|---|
| Zero values | Use zero stock or zero price | Both are accepted |
| Empty inventory | Run lists, search, sort, and report | Empty lists and total zero |
| Low-stock boundary | Quantity equals threshold | Product is included |
| Shared name | Use different SKUs with same name | Both products are accepted |
| Remove twice | Remove the same SKU twice | True then false |
| Descending sort | Sort each field descending | Highest value appears first |

## Invalid input test cases

| Test | Action | Expected result |
|---|---|---|
| Duplicate SKU | Add an existing SKU | IllegalArgumentException |
| Negative data | Use negative price, stock, or threshold | IllegalArgumentException |
| Stock underflow | Subtract below zero | IllegalArgumentException and quantity unchanged |
| Stock overflow | Exceed Integer.MAX_VALUE | IllegalArgumentException and quantity unchanged |
| Invalid sort | Use null ProductSortField | IllegalArgumentException |
| Unknown/blank SKU | Update missing or blank SKU | IllegalArgumentException |

## Manual testing checklist

- [ ] Compile and run Main.
- [ ] Test absolute and relative stock updates.
- [ ] Verify failed updates preserve quantity.
- [ ] Verify all three sort fields in both directions.
- [ ] Test low-stock values below, equal to, and above threshold.
- [ ] Recalculate total after stock changes and removal.
- [ ] Verify returned lists cannot be modified.
