# Architecture

Planned service ownership:

- Order service owns order lifecycle state.
- Inventory service owns stock and reservations.
- Payment service provides mock authorization outcomes only.

TODO: define orchestration, compensation, timeouts, retry budgets, correlation IDs, and persistence boundaries before coding cross-service calls.
