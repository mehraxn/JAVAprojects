# Deployment Strategy

This project compares two progressive delivery patterns for the same Java service.

## Blue-green

Blue-green keeps two full tracks running: blue for the current version and green
for the new version. The user-facing Service selects exactly one track. Promotion
is a selector change from `track: blue` to `track: green`; rollback is changing it
back.

Best when you want an instant cutover and instant rollback, and you can afford
temporary double capacity.

## Canary

Canary keeps the stable version serving most traffic while the new version gets a
small share. In this project, canary is shown in two ways:

1. Replica ratio: 9 stable pods + 1 canary pod behind one Service.
2. NGINX ingress weight: exact percentage using canary annotations.

Best when you want to reduce blast radius and learn from real traffic before full
promotion.

## Promotion rules

- Promote only when readiness is healthy and version checks show the expected
  version.
- For canary, compare error rate and latency against stable during a bake period.
- Keep rollback commands simple and known before promotion starts.
- Use backward-compatible database changes while two versions may run together.
- Avoid timer-only promotion; metrics and human approval should decide.

## Ownership

A real team should define who can pause, promote, and roll back. For a portfolio
project, those actions are documented but not connected to a production pipeline.
