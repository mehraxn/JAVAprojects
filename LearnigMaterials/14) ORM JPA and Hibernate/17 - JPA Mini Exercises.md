# JPA Mini Exercises

## Learning goals

- Practice entity mapping.
- Practice relationship ownership.
- Identify cascade, fetch, and equality issues.

## Exercise 1: Create entities

Create `Student` and `Course` entities with generated IDs and required names.

## Exercise 2: Map one-to-many

Create `Order` and `OrderItem`.

Requirements:

- `OrderItem` owns the relationship.
- `Order` has helper methods `addItem` and `removeItem`.
- Items are removed when removed from the order.

## Exercise 3: Map many-to-many

Create `Author` and `Book` with a join table. Then explain when a separate relationship entity would be better.

## Exercise 4: Fix owning side

Given a bidirectional relationship where only the inverse side is updated, explain why the database relationship may not change.

## Exercise 5: Cascade behavior

Decide whether cascade is safe for:

- order and order item;
- student and course;
- user account and profile.

## Exercise 6: N+1 issue

Find the N+1 problem in a loop over orders and order items. Write a fetch join query.

## Exercise 7: Index design

Choose indexes for invoice date, customer ID, and product SKU.

## Quick summary

JPA practice should include mappings, ownership, cascade, fetch behavior, and database constraints.
