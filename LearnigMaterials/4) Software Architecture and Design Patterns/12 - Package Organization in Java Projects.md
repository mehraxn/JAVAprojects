# Package Organization in Java Projects

## Learning goals

- Organize Java packages by layer or by feature.
- Understand trade-offs of each style.
- Keep packages readable as code grows.

## Why package organization matters

Packages are not just folders. They communicate how the code is structured. Good package names help readers find domain objects, services, repositories, and entry points.

## Organizing by layer

```text
com.example.app.domain
com.example.app.service
com.example.app.repository
com.example.app.cli
```

This style groups classes by technical role.

### Benefits

- Easy for beginners to understand.
- Clear separation of layers.
- Good for small and medium learning apps.

### Trade-offs

- A feature can be spread across many packages.
- Large applications may require jumping between packages often.

## Organizing by feature

```text
com.example.app.orders
com.example.app.products
com.example.app.customers
```

Each feature package can contain its own domain, service, repository, and DTO classes.

### Benefits

- Everything for one feature is close together.
- Scales well when features grow independently.
- Easier to remove or replace a feature.

### Trade-offs

- Requires more discipline inside each feature.
- Beginners may mix layers if package boundaries are unclear.

## Hybrid approach

Small learning apps often start by layer. As features grow, a feature-based structure can become cleaner.

```text
com.example.app.orders.domain
com.example.app.orders.service
com.example.app.orders.repository
```

## Common mistakes

- Putting every class directly in one package.
- Naming packages after vague words like `stuff` or `helpers`.
- Letting package structure contradict the architecture.
- Creating too many packages for a five-class program.

## Mini exercises

1. Design a layer-based package structure for a bookstore.
2. Design a feature-based package structure for order and payment workflows.
3. Decide which structure is better for a 10-class CLI app and explain why.

## Quick summary

Package organization should make responsibilities easy to find. Choose by layer for simplicity and by feature when features become large.
