# AGENTS.md

Guidance for AI coding agents working in this repository.

## Project Overview

This is a Spring Boot application with JSF/PrimeFaces views, a small React/esbuild frontend bundle, PostgreSQL
persistence, Liquibase migrations, and Docker/Testcontainers-based integration testing.

Primary stack:

- Java 25
- Gradle Kotlin DSL via `gradlew` / `gradlew.bat`
- Spring Boot 4
- JoinFaces, JSF, PrimeFaces
- Spring Data JPA and PostgreSQL
- Liquibase
- React 19 bundled by esbuild
- JUnit 5, AssertJ, Spring REST Docs, Selenium, Testcontainers

## Important Paths

- `src/main/java/com/example`: application code
- `src/main/resources/META-INF/resources`: JSF/PrimeFaces XHTML views and reusable view fragments
- `src/main/resources/static/javascript`: React frontend source, entry point `index.jsx`
- `src/main/resources/static/css`: static CSS
- `src/main/resources/db/changelog`: Liquibase changelogs
- `src/test/java/com/example/tests`: tests
- `src/test/java/com/example/pageobjects`: Selenium page objects
- `scripts/build.mjs`: esbuild frontend bundling script
- `docs/tasks.md`: project task notes and roadmap

Do not edit generated outputs under `build/`, dependency folders such as `node_modules/`, or IDE metadata unless
explicitly requested.

## Environment

- Use Java 25. On Windows, a local setup may require setting `JAVA_HOME`, for example:

```powershell
$env:JAVA_HOME = "$env:USERPROFILE\.jdks\azul-25.0.2"
```

- The Gradle Node plugin downloads Node `24.14.1` for Gradle-driven frontend builds.
- Docker is required for PostgreSQL-dependent development flows, Selenium, and Testcontainers tests.

PostgreSQL for local application runs:

```powershell
docker run -d --restart=always --name spring-demo -e POSTGRES_USER=dev -e POSTGRES_PASSWORD=dev -e POSTGRES_DB=spring-demo -p 5432:5432 postgres
```

## Build And Run

Use the Gradle wrapper. On Windows prefer:

```powershell
.\gradlew.bat clean build
```

On Unix-like shells:

```sh
./gradlew clean build
```

The Gradle build runs the frontend bundle step automatically through the `webpack` task.

Useful commands:

```powershell
.\gradlew.bat bootRun
.\gradlew.bat test
.\gradlew.bat test --tests com.example.tests.LongStringUtilsTest
.\gradlew.bat spotlessApply
.\gradlew.bat pmdMain spotbugsMain
npm run build
npm run watch
```

## Testing Guidance

- Use JUnit 5.
- Prefer AssertJ assertions.
- Keep unit tests in `src/test/java/com/example/tests` and use the `*Test.java` suffix.
- For Spring context integration tests, follow existing `BaseIntegrationTest` / `BaseRestIntegrationTest` patterns.
- For Selenium tests, use existing page objects and extension patterns. Set `HOST_FOR_SELENIUM` when
  `host.docker.internal` is unavailable.
- If a change touches application wiring, persistence, security, JSF pages, or shared utilities, run the relevant Gradle
  tests. Run `clean build` for broad or cross-cutting changes when practical.

## Formatting And Static Analysis

- Java formatting is controlled by Spotless using `spotless.eclipseformat.xml`.
- Markdown, Gradle Kotlin DSL, shell scripts, and gitignore files are also covered by the `spotlessMisc` configuration.
- Format Markdown files using IntelliJ IDEA's default Markdown style before committing documentation changes.
- PMD uses `pmd.ruleset.xml`.
- SpotBugs uses `spotbugs-exclude.xml`.
- Keep comments brief and useful. Avoid broad refactors unless required for the task.

## Coding Conventions

- Follow the existing layered structure: JSF/web controllers and views, services/components, repositories, JPA entities,
  and utilities.
- Keep package naming under `com.example`.
- Reuse existing Spring configuration, test extensions, page objects, and utility patterns before introducing new
  abstractions.
- For frontend changes, keep React code in `src/main/resources/static/javascript` and verify the esbuild bundle with
  `npm run build` or the Gradle build.
- For database changes, add Liquibase changesets under `src/main/resources/db/changelog` and wire them through
  `db.changelog-master.xml`.

## Agent Workflow

- Check `git status --short` before editing and avoid overwriting user changes.
- Keep edits scoped to the requested task.
- Prefer `rg` / `rg --files` for repository searches.
- Use the wrapper scripts rather than system Gradle.
- Document any command that could not be run, especially if Docker, Java 25, or network access is missing.
