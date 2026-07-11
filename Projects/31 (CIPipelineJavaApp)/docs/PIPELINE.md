# Pipeline Design

The workflow models the classic CI stages explicitly:

1. **Checkout** - obtains the repository source.
2. **Set up Java** - selects Temurin JDK 21.
3. **Compile** - compiles application and test source separately (`out` vs `test-out`).
4. **Test** - runs the dependency-free `GreetingServiceTest` class; a non-zero exit stops the pipeline.
5. **Package** - creates an executable JAR from application classes only.
6. **Smoke test** - runs the packaged JAR once to prove it starts.
7. **Upload** - stores the JAR as a build artifact.

## Repository placement

GitHub Actions only discovers workflow files under the repository-level `.github/workflows` directory. This file remains inside project 31 to respect project isolation. To enable it later, copy or move it to the repository-level workflow directory after review. Its working-directory paths already target this project in the monorepo.

## Trigger policy

The template declares `push`, `pull_request`, and `workflow_dispatch` triggers. Because the file sits inside the project folder, none of them fire until the file is moved to repository scope — the triggers document how the pipeline is meant to run once activated.

## Honest status

The pipeline stages were executed locally on 2026-07-11 with a JDK 21: compile, tests (7 checks), JAR packaging, and the JAR smoke test all passed; see `TEST_RESULTS.md`. GitHub Actions itself has not been run, so no CI pipeline run or artifact upload is claimed.
