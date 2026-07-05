# Testing Helm Chart Java App

## Static checks

- Review template names, indentation, selectors, and values references.
- Confirm default image and secret values are placeholders.
- Confirm no cluster-specific namespace or host is embedded.

## Deferred checks

- Lint and render the chart when Helm is available.
- Compare rendered resources with project 36's intended contract.
- Install only in an approved disposable cluster.

No Helm or Kubernetes command was executed.
