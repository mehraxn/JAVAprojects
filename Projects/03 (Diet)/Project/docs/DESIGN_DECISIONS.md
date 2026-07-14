# Design Decisions

## Java 21

The project targets Java 21 because it is a current LTS version and is widely available for local development and CI. The previous Java 25 release target did not compile on the local Java 21 environment.

## Maven

Maven remains the build tool because the original project already used it and the lab tests are configured through Maven. The existing `src` and `test` folders are preserved through Maven configuration instead of moving the project to a different layout.

## In-memory model

The project intentionally stores data in memory. This keeps the focus on Java OOP, collections, validation, deterministic sorting, and domain behavior.

## Nutritional model

Raw materials use values per 100 grams. Products use values for one unit. Recipes normalize values to 100 grams based on ingredient weights. Menus calculate totals for the full menu.

## Duplicate behavior

Raw material and product definitions replace previous values with the same name. Recipes and menus return the existing object when requested again by name, which avoids accidentally losing previously added ingredients or menu items.

## Opening-hour intervals

Opening intervals are interpreted as `[start, end)`. Start is included and end is excluded. Cross-midnight intervals are supported.

## Delivery-time adjustment

If an order is requested while the restaurant is closed, the delivery time is adjusted to the next opening start. If the requested time is after all opening intervals, it uses the first opening start of the day.

## Sorting rules

The implementation uses sorted maps and explicit comparators for deterministic output:

- raw materials by name
- products by name
- recipes by name
- menus by name
- restaurants by name
- customers by last name, first name, then email
- order menu lines by menu name
- status query output by restaurant, customer text, then delivery time

## Validation strategy

Invalid input throws `IllegalArgumentException`. Lookup methods from the original API still return `null` for missing values where the lab API expects that style.

Validation covers blank names, negative nutrition values, invalid quantities, missing menu items, invalid opening hours, invalid order times, and unknown restaurant names.

## Trade-offs

This is an educational local project. It does not include persistence, an HTTP API, authentication, a user interface, deployment configuration, or payment processing.
