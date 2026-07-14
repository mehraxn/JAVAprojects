# Rollback

## Git-first rollback

In GitOps, rollback is a Git operation, not a cluster operation:

1. Identify the last reviewed healthy Git revision and the image reference it
   pinned.
2. `git revert` the faulty desired-state commit (or restore the prior image
   reference in the overlay) through normal review.
3. Argo CD compares the reverted Git state with the live state.
4. Review the diff and synchronize according to the environment policy —
   automatically in dev, via the manual gate in prod.
5. Observe rollout health and preserve incident evidence.

Direct cluster edits are not durable GitOps rollback: dev's self-heal would
overwrite them, and Git would still describe the faulty state. Any emergency
live change must be followed by an equivalent reviewed Git correction.

## Why digests make rollback stronger

Reverting a commit restores yesterday's **reference**. If that reference is a
tag, the registry may have repointed it since; if it is a **digest**, the
revert provably restores the exact prior bytes (as long as the registry
retains the image). That is why production GitOps should promote immutable
image digests — in this lab, references are versioned tags for readability.

## Boundaries

- Rollback also depends on database/schema compatibility, configuration
  compatibility, image availability, and controller health — none of which
  this lab implements.
- **No rollback or recovery operation was executed here**: Argo CD was not
  run, so the workflow above is a documented runbook validated only by
  reviewing the manifests it operates on. This is a local portfolio lab, not
  a production platform.
