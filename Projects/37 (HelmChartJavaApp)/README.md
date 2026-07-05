# Helm Chart Java App

Starter Helm chart for parameterizing the Kubernetes resources of a Java application without duplicating application source.

## Structure

```text
helm/java-app/Chart.yaml
helm/java-app/values.yaml
helm/java-app/templates/_helpers.tpl
helm/java-app/templates/deployment.yaml
helm/java-app/templates/service.yaml
helm/java-app/templates/configmap.yaml
helm/java-app/templates/secret.example.yaml
docs/VALUES.md
TESTING.md
```

## Safety

Image, host, and secret values are placeholders. The chart was not rendered, installed, upgraded, or connected to a cluster.

## Next implementation steps

- Confirm the Java image and health endpoints.
- Define supported values and validation rules.
- Decide whether secrets belong in this chart or an external secret system.
- Add environment-specific values only after policy review.
