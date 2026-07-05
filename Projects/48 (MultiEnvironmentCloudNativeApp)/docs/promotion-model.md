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
2. **dev**: overlay tag → that digest; merge to `main`; Argo CD auto-syncs.
   Run unit/smoke checks against dev.
3. **staging**: a promotion PR sets `k8s/overlays/staging` image tag to dev's
   digest. Merge → Argo CD auto-syncs. Run integration/smoke tests; bake.
4. **prod**: a promotion PR sets `k8s/overlays/prod` image tag to staging's
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

Just the image tag/digest in the target overlay:

```yaml
# k8s/overlays/staging/kustomization.yaml
images:
  - name: my-cloud-native-java-app
    newTag: 1.4.0        # ← set to the digest verified in dev
```

Config and scale for each environment are already defined in that environment's
overlay, so a promotion never accidentally changes them.

## What was NOT done

- No image was built or pushed; no digest exists.
- No promotion PR was opened; no environment was synced or deployed.
