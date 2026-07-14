# SBOM (Software Bill of Materials)

Covers the SBOM stage (stage 10) of
[`../ci/github-actions.example.yml`](../ci/github-actions.example.yml) and the
runnable workflow. An SBOM for the locally built image was generated during
local validation (see [../TEST_RESULTS.md](../TEST_RESULTS.md)); generated
SBOMs live in `artifacts/` and are gitignored, so regenerate one with the
commands in [../TESTING.md](../TESTING.md).

## What an SBOM is

An SBOM is a complete, machine-readable **inventory of everything inside a build
artifact**: every OS package, every Java dependency (direct and transitive),
their exact versions, and often their licenses and hashes. Think of it as the
"ingredients label" for a container image.

Two common standard formats:

- **CycloneDX** — security-focused, from OWASP (used in this project).
- **SPDX** — license/compliance-focused, a Linux Foundation / ISO standard.

## Why it matters

- **Vulnerability response.** When the next Log4Shell-class CVE drops, you answer
  "are we affected?" in seconds by querying SBOMs instead of rebuilding and
  rescanning everything.
- **Transparency & compliance.** Regulations and frameworks (US Executive Order
  14028, NTIA minimum elements) increasingly require an SBOM per release.
- **Provenance.** Combined with signing (see [image-signing.md](image-signing.md)),
  a signed SBOM *attestation* lets consumers trust the inventory, not just read it.

## How it's generated — Syft

The pipeline uses [Syft](https://github.com/anchore/syft) via
`anchore/sbom-action` to emit a CycloneDX JSON SBOM for the built image:

```bash
syft secure-cicd-java-app:0.1.0 \
  -o cyclonedx-json=artifacts/sbom.cyclonedx.json

# The scanner can also consume the SBOM instead of re-analyzing the image:
trivy sbom artifacts/sbom.cyclonedx.json --severity HIGH,CRITICAL
```

In the pipeline this runs after the image is built and scanned, and the
resulting `sbom.cyclonedx.json` is uploaded as a build artifact. In a real
release setup it would additionally be attached to the pushed image as a
signed attestation (see [image-signing.md](image-signing.md)) — that step is
documented only, since this project never pushes an image.

## SBOM vs vulnerability scan

They are complementary, not the same:

| | SBOM (Syft) | Vulnerability scan (Trivy) |
| --- | --- | --- |
| Answers | "What is inside?" | "What inside is vulnerable *today*?" |
| Changes over time | no (fixed per build) | yes (new CVEs appear) |
| Output | inventory | list of findings |

You generate the SBOM once at build time; you can re-scan that SBOM against
fresh CVE data any time afterward without rebuilding.

## Lab boundary

- SBOMs are generated for the **local** image only; none is published.
- No SBOM attestation was signed — that requires a pushed image digest and an
  OIDC identity, which this lab intentionally does not use.
