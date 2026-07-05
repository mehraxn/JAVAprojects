# Testing Kubernetes Deployment Java App

No Java, Docker, kubectl, Kubernetes, cluster, image, Pod, probe, or network command was executed while preparing this project.

## Static validation checklist

- [ ] Review Java input validation and endpoint responses.
- [ ] Confirm Dockerfile source paths, module flags, non-root user, and entry point.
- [ ] Confirm API versions and Kubernetes object kinds.
- [ ] Confirm selectors, labels, names, and named ports agree.
- [ ] Confirm probe paths and application endpoints agree.
- [ ] Review resources and security contexts at correct YAML levels.

## File existence checks

- [ ] Java source and `app/Dockerfile` exist.
- [ ] All five requested `k8s/*.yaml` manifests exist.
- [ ] `docs/kubernetes-explanation.md` exists.
- [ ] `.gitignore`, `README.md`, and `TESTING.md` exist.

## Configuration review checklist

- [ ] ConfigMap keys match application environment names.
- [ ] Container port, Service target port, and app port agree.
- [ ] Secret name/key and optional behavior agree.
- [ ] Ingress routes to the Service name and named port.
- [ ] Placeholder image policy is understood before cluster use.
- [ ] EmptyDir size and mount support the read-only root filesystem.

## Security checks

- [ ] No real secret, credential, certificate, or image-pull token is present.
- [ ] No production endpoint, cluster, registry, namespace, or hostname is present.
- [ ] Container runs non-root with dropped capabilities and no privilege escalation.
- [ ] Example Secret is clearly marked and not suitable for direct use.

## Commands normally used - NOT executed

```text
docker build -t my-java-app:latest app
kubectl config current-context
kubectl apply --dry-run=client -f k8s/
kubectl diff -f k8s/
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

These commands require installed tooling, reviewed manifests, an available image, and an approved disposable cluster. None were executed.

## Expected results in a proper environment

- Java endpoints respond on port 8080.
- Two Pods become Ready and remain live.
- The Service selects both Ready Pods and forwards port 80 to the named HTTP port.
- ConfigMap values appear through `/config`.
- The workload starts without the optional example Secret.
- Invalid configuration prevents a healthy rollout rather than silently using unsafe values.
