# Product Inventory Manager

## Description

Product Inventory Manager is an in-memory Java project for products, stock changes, searching, sorting, and inventory reports.

## Features

- Add products with unique SKU values.
- Update names, prices, and stock quantities.
- Apply relative stock adjustments.
- Prevent negative stock and integer overflow.
- Remove and search products.
- Sort by name, price, or quantity.
- Report products at or below a low-stock threshold.
- Calculate total inventory value with BigDecimal.

## Java concepts practiced

- Encapsulation and mutable domain objects
- Map and List collections
- Enums and Comparator-based sorting
- BigDecimal arithmetic
- Validation, overflow checks, and unmodifiable results

## Main classes

- Product: owns product details, stock validation, and stock value.
- ProductSortField: defines supported sorting choices.
- Inventory: manages products, searching, sorting, and reports.
- Main: demonstrates updates, sorting, warnings, and total value.

## How the program works

Inventory stores Product objects by SKU. Stock can be set directly or adjusted by a signed change. Queries create sorted, read-only result lists without changing the inventory's insertion order.

## Example usage

~~~powershell
javac -d out src\productinventorymanager\*.java
java -cp out productinventorymanager.Main
~~~

The demo updates stock, prints products sorted by price, reports low stock, and calculates total value.

## Possible future improvements

- Add product categories.
- Add stock movement history.
- Add reorder quantities and supplier information.
- Import and export inventory files.
- Add richer reports for zero stock and high-value items.
