# Continuous integration

The repository runs a GitHub Actions pipeline (`.github/workflows/ci.yml`) on every push and pull request that targets `develop` or `master`. You do not need to install anything: GitHub runs it automatically when you open a pull request.

## What the pipeline does

```mermaid
flowchart LR
    A[Push or pull request] --> B[Unit tests and coverage]
    A --> C[Instrumented tests]
    B --> D[JaCoCo XML + HTML reports]
    D --> E[Coverage comment on the pull request]
    D --> F[80% line coverage gate for openpay-sdk]
    C --> G[Emulator test reports]
```

### Unit tests and coverage

This job runs on every push and pull request:

1. Compiles the project with JDK 17 and runs `testDebugUnitTest` for every module.
2. Generates JaCoCo coverage reports (`jacocoTestReport`).
3. Enforces the 80% line coverage minimum on `openpay-sdk` (`jacocoCoverageVerification`). The build fails when coverage drops below that threshold.
4. On pull requests, posts (and keeps updated) a comment with the overall coverage and the coverage of the files changed by the pull request, using [Madrapps/jacoco-report](https://github.com/Madrapps/jacoco-report).
5. Uploads the HTML coverage and test reports as build artifacts, so you can download and browse them from the run page.

### Instrumented tests

This job boots an Android 14 (API 34) emulator on the runner and executes `connectedDebugAndroidTest`. The emulator needs hardware acceleration, which is why the job enables KVM before starting it. Test reports are uploaded as artifacts.

## Reading the coverage comment

Every pull request receives a single comment titled **Unit test coverage** that is refreshed on each push. It shows:

- **Overall coverage** of the project, marked with ✅ when it is at or above 80%.
- **Coverage of changed files**, so reviewers can see whether the new code is tested without opening the full report.

## Running the same checks locally

```bash
./gradlew testDebugUnitTest jacocoTestReport :openpay-sdk:jacocoCoverageVerification
./gradlew connectedDebugAndroidTest   # requires a device or emulator
```

The HTML report is generated at `<module>/build/reports/jacoco/jacocoTestReport/html/index.html`.

## Continuous deployment

A second workflow (`.github/workflows/cd.yml`) releases the library to
Maven Central. It runs when a version tag (`v1.2.3`) is pushed — normally
the tag on the `master` merge commit of a release — and can also be
started manually from the Actions tab.

```mermaid
flowchart LR
    A[Push tag v*] --> B[Unit tests + coverage gate]
    B --> C[Check tag == VERSION_NAME]
    C --> D[Sign and upload to the Central Portal]
    D --> E[Manual Publish in central.sonatype.com]
    D --> F[GitHub release with generated notes]
```

1. **Tests first** — the release is blocked unless the unit tests and the
   80% coverage gate pass.
2. **Version guard** — the workflow fails if the tag does not match
   `VERSION_NAME` in `gradle.properties`, so a mistagged release cannot
   ship.
3. **Signed upload** — artifacts are PGP-signed and uploaded to the
   Sonatype Central Portal staging area. Credentials and the signing key
   come from the repository secrets (`MAVEN_REPOSITORY_USERNAME`,
   `MAVEN_REPOSITORY_PASSWORD`, `SIGNING_KEY`, `SIGNING_PASSWORD`); they
   never live in the repository.
4. **Manual confirmation** — nothing becomes public automatically: a
   maintainer must press **Publish** on the validated deployment at
   [central.sonatype.com](https://central.sonatype.com). The run summary
   links the step.
5. A GitHub release with generated notes is created for the tag.