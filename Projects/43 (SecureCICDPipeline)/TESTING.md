# Testing Secure CI/CD Pipeline

## Static checks

- [ ] Confirm the workflow remains disabled and uses minimal permissions.
- [ ] Confirm no secret, token, registry, or production endpoint is embedded.
- [ ] Review pinned action/tool versions before enabling anything.
- [ ] Confirm build evidence cannot be confused with deployment approval.

## Deferred checks

- [ ] Compile and test the Java example locally.
- [ ] Validate each approved scanner in an isolated branch.
- [ ] Verify failures block later stages and secrets are masked.

No CI, build, scan, signing, publication, or deployment command was executed.
