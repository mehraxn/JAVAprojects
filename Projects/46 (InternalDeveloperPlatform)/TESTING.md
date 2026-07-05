# Testing Internal Developer Platform

## Static checks

- [ ] Confirm golden-path inputs map consistently into source, chart, and pipeline.
- [ ] Confirm policies use placeholders and cannot target a real namespace.
- [ ] Confirm templates contain no credential or production endpoint.
- [ ] Confirm escape hatches and ownership are documented.

## Deferred checks

- [ ] Generate one sample service in an isolated workspace.
- [ ] Lint/render generated configuration with approved tools.
- [ ] Measure onboarding steps without claiming deployment.

No template generator, CI, Helm, GitOps, policy, or Kubernetes command was executed.
