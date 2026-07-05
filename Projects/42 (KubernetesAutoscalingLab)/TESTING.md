# Testing Kubernetes Autoscaling Lab

## Static checks

- [ ] Confirm Deployment requests exist for every HPA resource metric.
- [ ] Confirm HPA target name, API version, and replica bounds.
- [ ] Confirm VPA remains recommendation-only until reviewed.
- [ ] Confirm load limits cannot target a real endpoint.

## Deferred checks

- [ ] Build and test the Java workload.
- [ ] Validate manifests against a disposable cluster version.
- [ ] Observe baseline, scale-up, stabilization, and scale-down behavior.

No Java, Docker, Kubernetes, autoscaling, or load command was executed.
