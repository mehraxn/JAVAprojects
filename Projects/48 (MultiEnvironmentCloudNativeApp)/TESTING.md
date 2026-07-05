# Testing Multi-Environment Cloud-Native App

## Static checks

- [ ] Confirm every overlay references the same base.
- [ ] Confirm environment labels, ConfigMap values, and image tags are distinct placeholders.
- [ ] Confirm no real secret, endpoint, account, or production identifier exists.
- [ ] Review promotion and rollback gates.

## Deferred checks

- [ ] Compile and test configuration behavior.
- [ ] Render and compare all overlays with approved tooling.
- [ ] Promote one immutable image through disposable environments only.

No Java, Terraform, CI, Kubernetes, promotion, or deployment command was executed.
