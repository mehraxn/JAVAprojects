# SBOM (Software Bill of Materials)

Covers stage 8 of [`../ci/github-actions.example.yml`](../ci/github-actions.example.yml).
**No SBOM was generated in this repository** — the commands below are documented
and marked **NOT executed**.

## What an SBOM is

An SBOM is a complete, machine-readable **inventory of everything inside a build
artifact**: every OS package, every Java dependency (direct and transitive),
their exact versions, and often their licenses and hashes. Think of it as the
"ingredients label" for a container image.

Two common standard formats:

- **CycloneDX** — security-focused, from OWASP (used in this template).
- **SPDX** — license/compliance-focused, a Linux Foundation / ISO standard.

## Why it matters

- **Vulnerability response.** When the next Log4Shell-class CVE drops, you answer
  "are we affected?" in seconds by querying SBOMs instead of rebuilding and
  rescanning everything.
- **Transparency & compliance.** Regulations and frameworks (US Executive Order
  14028, NTIA minimum elements) increasingly require an SBOM per release.
- **Provenance.** Combined with signing (see [image-signing.md](image-signing.md)),
  a signed SBOM *attestation* lets consumers trust the inventory, not just read it.

## How it's generated — Syft (NOT executed)

The template uses [Syft](https://github.com/anchore/syft) via
`anchore/sbom-action` to emit a CycloneDX JSON SBOM for the built image.

```bash
# NOT executed — requires Syft installed and the image present locally.
syft ghcr.io/example/secure-cicd-app:<tag> \
  -o cyclonedx-json=sbom.cyclonedx.json

# The image scanner can also consume the SBOM instead of re-analyzing the image:
# NOT executed
trivy sbom sbom.cyclonedx.json --severity HIGH,CRITICAL
```

In the pipeline this runs after the image is built (stage 8) and the resulting
`sbom.cyclonedx.json` is uploaded as a build artifact (stage 10) and, in a real
setup, attached to the image as a signed attestation (stage 9).

## SBOM vs vulnerability scan

They are complementary, not the same:

| | SBOM (Syft) | Vulnerability scan (Trivy) |
| --- | --- | --- |
| Answers | "What is inside?" | "What inside is vulnerable *today*?" |
| Changes over time | no (fixed per build) | yes (new CVEs appear) |
| Output | inventory | list of findings |

You generate the SBOM once at build time; you can re-scan that SBOM against
fresh CVE data any time afterward without rebuilding.

## What was prepared but NOT executed

- No image existed to inventory.
- Syft was not installed or run; no `sbom.cyclonedx.json` was produced.
- No SBOM was uploaded, attested, or signed.
