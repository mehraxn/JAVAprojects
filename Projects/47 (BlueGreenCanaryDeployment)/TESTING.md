# Testing Blue-Green and Canary Deployment

## Static checks

- [ ] Confirm blue/green labels and active/preview Service selectors.
- [ ] Confirm canary annotations match the chosen controller only.
- [ ] Confirm images and hosts are placeholders.
- [ ] Review rollback steps and schema compatibility assumptions.

## Deferred checks

- [ ] Build two versioned app images in an isolated lab.
- [ ] Validate health/readiness and Service switching.
- [ ] Observe canary metrics before promotion or rollback.

No Java, Docker, Kubernetes, traffic, monitoring, or rollback command was executed.
