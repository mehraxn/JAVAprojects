# Pipeline Design

The workflow models five requested stages:

1. **Checkout** - obtains the repository source.
2. **Set up Java** - selects Temurin JDK 21.
3. **Compile** - compiles application and test source separately.
4. **Test** - runs the dependency-free `GreetingServiceTest` class.
5. **Package** - creates an executable JAR and uploads it as an artifact.

## Repository placement

GitHub Actions only discovers workflow files under the repository-level `.github/workflows` directory. This file remains inside project 31 to respect project isolation. To enable it later, copy or move it to the repository-level workflow directory after review. Its working-directory paths already target this project in the monorepo.

## Trigger policy

The starter uses `workflow_dispatch` only, so it cannot run automatically on pushes or pull requests. Automatic triggers should be added only after the workflow has been reviewed and intentionally enabled.

## Honest status

The workflow is prepared but has not been executed. No successful pipeline, test, build, or artifact is claimed.
