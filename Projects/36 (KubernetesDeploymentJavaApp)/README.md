# Kubernetes Deployment Java App

Starter Java application plus Kubernetes Deployment, Service, ConfigMap, and example Secret manifests.

## Structure

```text
src/kubernetesdeploymentjavaapp/
Dockerfile
k8s/deployment.yml
k8s/service.yml
k8s/configmap.yml
k8s/secret.example.yml
docs/DEPLOYMENT.md
TESTING.md
```

## Safety

The image repository is a non-routable placeholder and the Secret contains only `CHANGE_ME`. No image was built, no manifest was applied, and no cluster was contacted.

## Next implementation steps

- Implement health and readiness endpoints.
- Confirm image name, port, namespace, and resource limits.
- Replace the example Secret with an external secret-delivery strategy.
- Review security context and rollout behavior.
