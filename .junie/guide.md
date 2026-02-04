# Junie Guide - demo-spring-jsf

Welcome to the `demo-spring-jsf` project! This guide provides essential information for Junie to work effectively on
this codebase.

## Project Overview

This is a Spring Boot application using JSF (JavaServer Faces) for the frontend, running with Docker and a PostgreSQL
database.

## Key Technologies

- **Backend:** Spring Boot, Spring Data JPA, Java
- **Frontend:** JSF (PrimeFaces), Webpack for asset bundling
- **Database:** PostgreSQL
- **Build Tool:** Gradle (Kotlin DSL)
- **Containerization:** Docker, Docker Compose

## Development Setup

- **Build:** `JAVA_HOME=/C/Users/kamki/.jdks/azul-21.0.10 ./gradlew clean build`
- **Database:** A PostgreSQL container is required. See `README.md` for the `docker run` command.
- **Frontend Assets:** Managed via npm/webpack. See `package.json` for scripts.

## Project Structure

- `src/main/java`: Backend source code.
- `src/main/webapp`: JSF pages and web resources.
- `docs/tasks.md`: A list of planned improvements and tasks.

## Guidelines for Junie

1. **Consistency:** Follow the existing coding style. Refer to `spotless.eclipseformat.xml` and `pmd.ruleset.xml`.
2. **Architecture:** Aim for a layered architecture (Controllers -> Services -> Repositories).
3. **Testing:**
    - Add unit tests for new logic.
    - Update integration tests for API changes.
    - Check `src/test` for existing patterns.
4. **Task Management:** When completing tasks listed in `docs/tasks.md`, update the file by checking off the relevant
   item.
5. **Database:** Be mindful of database migrations if changing entities.
6. **Documentation:** Keep `README.md` and `docs/` updated with significant changes.
