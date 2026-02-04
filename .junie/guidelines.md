# Project Guidelines - demo-spring-jsf

This document provides project-specific guidelines and instructions for developers working on the `demo-spring-jsf`
project.

## Project Overview

This is a Spring Boot application using JSF (JavaServer Faces) for the frontend, running with Docker and a PostgreSQL
database.

## Key Technologies

- **Backend:** Spring Boot, Spring Data JPA, Java
- **Frontend:** JSF (PrimeFaces), Webpack for asset bundling
- **Database:** PostgreSQL
- **Build Tool:** Gradle (Kotlin DSL)
- **Containerization:** Docker, Docker Compose

## 1. Build and Configuration Instructions

### Prerequisites

- **Java 25**: The project is configured to use Java 25 (`JavaVersion.VERSION_25` in `build.gradle.kts`).
- **Docker**: Required for running the PostgreSQL database and Selenium tests.
- **Node.js**: Version 20.9.0 is managed via the Gradle Node plugin.

### Build Commands

- **Full Build**: Run `set JAVA_HOME=%USERPROFILE%\.jdks\azul-25.0.2&& gradlew clean build`.
    - *Note*: You might need to set `JAVA_HOME` to point to a Java 25 installation.
- **Frontend Assets**: Managed via npm and Webpack. While Gradle handles this during the build, you can use
  `npm install` and `npm run build` manually if needed.

### Database Setup

A PostgreSQL instance is required for the application to run. Use the following Docker command to start a compatible
container:

```bash
docker run -d --restart=always --name spring-demo \
        -e POSTGRES_USER=dev \
        -e POSTGRES_PASSWORD=dev \
        -e POSTGRES_DB=spring-demo \
        -p 5432:5432 postgres
```

### Liquibase

The project uses Liquibase for database migrations. Configuration is found in `build.gradle.kts` under the `liquibase`
block, and changelogs are located in `src/main/resources/db/changelog/`.

---

## 2. Testing Information

### Running Tests

- **All Tests**: `./gradlew test`
- **Specific Test**: `./gradlew test --tests com.example.tests.LongStringUtilsTest`

### Test Configuration

- The project uses **JUnit 5** and **AssertJ** for assertions.
- **Integration Tests**: Use `BaseIntegrationTest` or `BaseRestIntegrationTest` for Spring Boot context-aware tests.
- **Selenium Tests**: Requires a running Docker environment. Use `HOST_FOR_SELENIUM` environment variable if
  `host.docker.internal` is not available.

### Guidelines for New Tests

1. **Unit Tests**: Place in `src/test/java/com/example/tests/` and use JUnit 5.
2. **Naming**: Follow the `*Test.java` suffix convention.
3. **Assertions**: Prefer AssertJ's `assertThat()` for better readability.

### Demonstration Test

Here is a simple example of a test case using JUnit 5 and AssertJ:

```java
package com.example.tests;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleDemoTest {

    @Test
    void testConcatenation() {
        String first = "Spring";
        String second = "JSF";
        String result = first + "-" + second;

        assertThat(result)
                .isEqualTo("Spring-JSF")
                .contains("JSF");
    }
}
```

---

## 3. Additional Development Information

### Code Style and Formatting

- **Spotless**: The project uses Spotless to enforce code formatting. Run `./gradlew spotlessApply` to format your code.
- **Eclipse Formatter**: The formatting rules are defined in `spotless.eclipseformat.xml`.
- **PMD**: Static analysis is performed using PMD with rules defined in `pmd.ruleset.xml`.

### Architecture

- The project follows a layered architecture: **Web (JSF/PrimeFaces) -> Services -> Repositories -> JPA Entities**.
- Frontend resources are located in `src/main/webapp`.

### Task Management

- Check `docs/tasks.md` for the current roadmap and mark tasks as completed when finished.
