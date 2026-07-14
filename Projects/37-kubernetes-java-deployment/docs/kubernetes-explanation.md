# Kubernetes Object Explanation

## Deployment

A Deployment describes the desired Pod template and replica count. Its controller would maintain two Pods, replace failed Pods, and perform rolling updates when the Pod template changes. The selector is intentionally identical to the Pod label and must not be changed independently.

The container has readiness and liveness probes:

- Readiness answers whether a Pod should receive Service traffic.
- Liveness answers whether Kubernetes should restart an unhealthy container.

The example endpoints always return success once the Java server starts. Real readiness often checks required dependencies, while liveness should avoid fragile external checks that cause unnecessary restarts.

## Service

Pod IP addresses are temporary. The ClusterIP Service selects Pods by label and provides a stable in-cluster name and virtual IP. Clients use Service port 80; Kubernetes forwards traffic to the container's named `http` port, which is 8080.

## ConfigMap

A ConfigMap stores non-sensitive values independently from the image and Deployment source. `envFrom` exposes each data key as a container environment variable. ConfigMaps are not encrypted secret stores.

## Secret

The example Secret demonstrates the object shape only. `stringData` is convenient input syntax, but Kubernetes converts it to base64 data; base64 is encoding, not encryption. The manifest contains no real value and should not be applied unchanged. The Deployment reference is optional so the example app does not depend on creating this placeholder object.

## Ingress

An Ingress describes HTTP routing from a host/path to a Service. It does nothing without a compatible Ingress controller. `java-app.example.invalid` is reserved for documentation and cannot be a real public endpoint. TLS and DNS ownership are deliberately omitted.

## Configuration flow

```text
ConfigMap data ---------------------> Pod environment
approved Secret (optional) --------> Pod environment
Deployment Pod template -----------> Java container
Service selector ------------------> Ready Pods
Ingress rule (optional) -----------> Service
```

## Security and resource fields

The Pod runs with numeric non-root identity `10001`, a runtime-default seccomp profile, dropped capabilities, disabled privilege escalation, and a read-only root filesystem. Resource requests help scheduling; limits cap container consumption. The values are educational starting points, not measured production settings.

## Safe review order

For any future disposable lab: inspect the cluster context and namespace, validate files, make the placeholder image available locally, review diffs, create non-sensitive configuration, deploy without Ingress, verify probes and Service routing, and only then consider external routing.

## Verification status

The manifests were validated with `kubectl` and deployed to a disposable `kind` cluster during testing, as recorded in `TEST_RESULTS.md`. The lab remains a local learning deployment, not a production Kubernetes environment.
