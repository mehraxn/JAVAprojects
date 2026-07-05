# Rollback

## Git-first rollback

1. Identify the last reviewed healthy Git revision and image tag.
2. Revert the faulty desired-state commit through normal review.
3. Let Argo CD compare the reverted Git state with live state.
4. Review the diff and synchronize according to the environment policy.
5. Observe rollout health and preserve incident evidence.

Direct cluster edits are not durable GitOps rollback: self-healing may overwrite them, and Git would still describe the faulty state. Any emergency live change must be followed by an equivalent reviewed Git correction.

## Boundaries

Rollback also depends on database/schema compatibility, configuration compatibility, image availability, and controller health. These are not implemented in this starter.

No rollback or recovery operation was executed.
