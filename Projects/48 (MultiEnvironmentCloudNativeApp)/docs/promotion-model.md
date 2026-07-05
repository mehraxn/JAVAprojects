# Promotion Model

How a change moves dev → staging → prod. **No promotion was performed and
nothing was deployed.**

## Principle: promote the artifact, not the source

A promotion moves the **exact image** (by digest) that was verified in the lower
environment — it does **not** rebuild. Rebuilding could pull newer base layers or
dependencies, producing an artifact you never tested. So CI builds once
([../ci/build.example.yml](../ci/build.example.yml)) and promotion only re-points
environments at that digest ([../ci/promotion.example.yml](../ci/promotion.example.yml)).

## The flow

```
 build once ──► dev ──(checks pass)──► staging ──(checks + approval)──► prod
   digest        auto                   auto                            manual sync
     └──────────── same image digest the whole way ───────────────────────┘
```

1. **Build** the image once; record its digest.
2. **dev**: replace `REPLACE_WITH_DEV_DIGEST` with that digest; merge to `main`;
   Argo CD auto-syncs.
   Run unit/smoke checks against dev.
3. **staging**: a promotion PR sets `k8s/overlays/staging` image digest to dev's
   digest. Merge → Argo CD auto-syncs. Run integration/smoke tests; bake.
4. **prod**: a promotion PR sets `k8s/overlays/prod` image digest to staging's
   digest. Merge requires **manual approval + a change record**, and prod's Argo
   CD Application is **manual-sync**, so a human performs the release.

Each promotion is an ordinary Git change — reviewable, auditable, revertible.

## Gates

| To | Requires |
| --- | --- |
| dev | unit tests |
| staging | integration + smoke tests |
| prod | manual approval + change record; manual Argo CD sync |

## What a promotion changes in Git

Only the image digest in the target overlay:

```yaml
# k8s/overlays/staging/kustomization.yaml
images:
  - name: registry.example.invalid/cloud-native-app
    digest: sha256:REPLACE_WITH_STAGING_DIGEST
```

The committed `REPLACE_WITH_*_DIGEST` strings are obvious placeholders, not real
digests. A promotion copies the exact `sha256:<real digest>` verified in the
source environment into the target environment field.

Config and scale for each environment are already defined in that environment's
overlay, so a promotion never accidentally changes them.

## What was NOT done

- No image was pushed to a registry and no real registry digest is recorded.
- No promotion PR was opened; no environment was synced or deployed.
