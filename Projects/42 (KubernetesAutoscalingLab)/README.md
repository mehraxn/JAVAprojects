# Kubernetes Autoscaling Lab

Starter structure for studying resource requests, Horizontal Pod Autoscaling, optional Vertical Pod Autoscaling recommendations, and controlled load generation for a Java workload.

## Structure

```text
src/kubernetesautoscalinglab/
docker/Dockerfile
k8s/deployment.yaml
k8s/service.yaml
k8s/hpa.yaml
k8s/vpa.example.yaml
docs/autoscaling-plan.md
docs/load-test-plan.md
README.md
TESTING.md
```

## Status

Skeleton only. The Java workload, metrics endpoint, image, metrics provider, autoscalers, load test, and cluster behavior are not implemented or executed.

## Required confirmations

- Metrics Server or another HPA metrics source
- Disposable cluster capacity and namespace
- Safe load limits and stop conditions
- HPA target, replica bounds, stabilization, and measurement method
