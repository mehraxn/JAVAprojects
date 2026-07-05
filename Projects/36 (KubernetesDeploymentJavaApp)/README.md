# Kubernetes Deployment Java App

## Description

A small dependency-free Java HTTP application accompanied by plain Kubernetes manifests for learning Deployments, Services, ConfigMaps, Secrets, probes, security contexts, resource settings, and optional Ingress routing.

## Goal

The goal is to connect application behavior to Kubernetes configuration: the Java process exposes health/config endpoints, the Deployment manages Pods, the Service provides stable routing, and configuration remains outside the image.

## Technologies and concepts used

- Java 21 built-in `HttpServer`
- Multi-stage Docker image definition
- Kubernetes Deployment and rolling-update concepts
- ClusterIP Service and label selectors
- ConfigMap and optional Secret references
- Readiness and liveness probes
- Resource requests/limits and non-root security contexts
- Optional networking.k8s.io/v1 Ingress

## Project structure

```text
app/
  Dockerfile
  src/kubernetesdeploymentjavaapp/
k8s/
  deployment.yaml
  service.yaml
  configmap.yaml
  secret.example.yaml
  ingress.yaml
docs/kubernetes-explanation.md
.gitignore
README.md
TESTING.md
```

## Important files explained

- `AppConfig.java` validates port, environment, and message variables.
- `Main.java` exposes `/health`, `/ready`, and `/config` through standard Java.
- `app/Dockerfile` builds the source and runs with numeric non-root identity `10001`.
- `deployment.yaml` defines replicas, selectors, probes, resources, configuration, security controls, and writable temporary storage.
- `service.yaml` maps stable Service port 80 to named container port 8080.
- `configmap.yaml` supplies non-sensitive environment variables.
- `secret.example.yaml` shows shape only and contains no real value.
- `ingress.yaml` uses the documentation-only host `java-app.example.invalid`.

## Intended real-environment workflow

In an approved disposable cluster, a developer would first compile/test the Java app, build and scan `my-java-app:latest`, make that image available to the cluster, review the active context and namespace, validate every manifest, and inspect a diff. ConfigMap, Deployment, and Service should be introduced before optional Ingress. The example Secret must not be applied unchanged.

Service traffic would reach Ready Pods on container port 8080. Ingress would require a separately installed compatible controller and suitable lab DNS.

## Prepared but not executed

- Java source, Dockerfile, Deployment, Service, ConfigMap, example Secret, Ingress, probes, resources, and security fields were prepared.
- Java, Docker, kubectl, and Kubernetes were not executed.
- No image was built/pushed and no manifest was validated by kubectl or submitted to an API server.
- No Pod, Service, ConfigMap, Secret, Ingress, rollout, or probe result exists.

## Manual validation checklist

- [ ] Confirm Deployment selector and Pod labels match exactly.
- [ ] Confirm Service selector and named target port match the Pod.
- [ ] Confirm `/health` and `/ready` exist in Java source.
- [ ] Confirm ConfigMap/Secret names match references.
- [ ] Confirm the Secret reference remains optional unless a real strategy is approved.
- [ ] Review security context, resources, and `/tmp` emptyDir mount.
- [ ] Verify context/namespace and image availability before any future apply.

## Common mistakes avoided

- No real secret, registry, cluster, or public host is embedded.
- Application configuration is not baked into the image.
- Service selectors and Deployment labels use the same stable key/value.
- Readiness and liveness have separate meanings and endpoints.
- A read-only root filesystem still provides bounded writable JVM temporary space.
- Ingress is not described as functional without a controller.

## Possible future improvements

- Add automated Java and manifest-schema tests.
- Pin the image by immutable tag or digest.
- Add namespace, service-account, and NetworkPolicy examples.
- Add TLS only with an approved certificate and DNS workflow.
- Add rollout/rollback exercises in a disposable cluster.
- Replace the example Secret with an approved external secret mechanism.
