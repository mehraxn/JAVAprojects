# Restaurant Ordering System

## Description

Restaurant Ordering System is an in-memory Java project for menu management, customer orders, discounts, summaries, and order status.

## Features

- Add menu items with unique IDs and names.
- Remove menu items.
- Create named or walk-in customer orders.
- Add items or increase an existing quantity.
- Remove items while an order is editable.
- Calculate item subtotals, order subtotal, discount, and total.
- Apply a 10% discount from a 50.00 subtotal.
- Produce readable order summaries.
- Enforce simple order-status transitions.

## Java concepts practiced

- BigDecimal monetary arithmetic
- Map and List collections
- Enums and state transitions
- Composition between menu, order, and order-item objects
- Validation, overflow checks, and generated IDs

## Main classes

- MenuItem: stores menu identity and price.
- OrderItem: combines a menu item with quantity.
- Order: owns order items, discount calculation, summary, and status.
- OrderStatus: defines the order workflow.
- Restaurant: manages menu items and orders.
- Main: demonstrates ordering, discount, and status changes.

## How the program works

Restaurant stores menu items and generates orders. Orders can be edited only in CREATED status and cannot move to PREPARING while empty. Valid transitions are CREATED to PREPARING, PREPARING to READY, and READY to SERVED; cancellation is allowed early in the workflow.

## Example usage

~~~powershell
javac -d out src\restaurantorderingsystem\*.java
java -cp out restaurantorderingsystem.Main
~~~

The demo builds an order, prints its summary and discount, then advances it to READY.

## Possible future improvements

- Add tax and service-charge rules.
- Add table numbers and servers.
- Add configurable discounts.
- Add payment tracking.
- Save completed order summaries to files.
