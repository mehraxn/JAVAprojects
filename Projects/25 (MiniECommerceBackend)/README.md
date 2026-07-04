# Mini E-Commerce Backend

## Status

Product, cart, order, service, and built-in HttpServer skeleton created.

## Planned features

- Manage an in-memory product catalog.
- Create carts and update quantities.
- Validate stock before checkout.
- Calculate totals with BigDecimal.
- Create orders and maintain order history.
- Expose optional local HTTP endpoints.

## Current classes

- Product: catalog model.
- Cart: product quantities for one shopping session.
- Order: immutable checkout summary and status.
- ShopService: catalog, cart, stock, and checkout logic.
- ShopHttpServer: optional HTTP adapter.
- Main: safe runner that does not bind a port.

## Scope

The initial implementation remains in memory so stock and checkout rules can be developed before adding persistence.

## Source layout

Source files are under src/miniecommercebackend.
