# Semantic Versioning and Releases

## Learning goals

- Understand `MAJOR.MINOR.PATCH`.
- Learn snapshot versions and release tags.
- Write basic changelog notes.

## Semantic versioning

```text
MAJOR.MINOR.PATCH
```

Example:

```text
2.4.1
```

- MAJOR: breaking changes.
- MINOR: new backward-compatible features.
- PATCH: backward-compatible fixes.

## Snapshot versions

```xml
<version>1.0.0-SNAPSHOT</version>
```

`SNAPSHOT` means the version is still under development.

## Release tags

Git tags mark release points:

```bash
git tag v1.0.0
git push origin v1.0.0
```

## Changelog basics

Write what changed:

- Added order report.
- Fixed invoice date parsing.
- Changed minimum Java version to 21.

## Common mistakes

- Using random version numbers.
- Releasing without tests.
- Forgetting to update documentation.
- Making breaking changes in a patch release.

## Mini exercises

1. Decide whether a change is major, minor, or patch.
2. Write changelog notes for three changes.
3. Create a release tag name.

## Quick summary

Versioning helps users understand the size and risk of changes.
