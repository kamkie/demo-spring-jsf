Spring Boot Aplication with Docker
==================================
[`Spring Boot`](https://projects.spring.io/spring-boot/) application with [`Docker`](https://www.docker.com/) on which database is running.

[![Java CI with Gradle](https://github.com/kamkie/demo-spring-jsf/actions/workflows/gradle.yml/badge.svg)](https://github.com/kamkie/demo-spring-jsf/actions/workflows/gradle.yml)
[![codecov](https://codecov.io/gh/kamkie/demo-spring-jsf/branch/master/graph/badge.svg)](https://codecov.io/gh/kamkie/demo-spring-jsf)

## Build and Run
### gradle Build
```
  ./gradlew clean build
```
The Gradle build runs the frontend bundle step automatically.

### Runtime profiles

Runtime intent is explicit and does not use profile groups:

- `./gradlew bootRun` activates `local-development`: Hibernate statistics, DEBUG web/SQL/timing logs, and the four
  request context filters are enabled; JSF uses development stage.
- Integration tests retain `@ActiveProfiles("test")`: statistics, JMX, verbose logging, and custom request filters are
  disabled; JSF uses production stage.
- The Docker image activates `deployed`: the same lean runtime defaults are explicit for deployed containers.
- `--spring.profiles.active=profiling` opts into Hibernate statistics, JMX, Mojarra TRACE, verbose web/SQL logging, and
  all four custom request filters.
- Starting the executable JAR without a profile uses the lean defaults and is equivalent to the diagnostic settings of
  `deployed`; select `deployed` explicitly for deployment automation outside the checked-in Docker image.

Boot and plain archives precompute JoinFaces scan metadata with the fixed `deployed` profile. Tests do not consume this
metadata; their `test` profile and test runtime classpath continue to be scanned independently.

All modes instantiate and expose only the required read-only Actuator endpoints: health, info, metrics, and Prometheus.
Existing security rules remain responsible for HTTP authorization.

Run the fast unit-only test loop without integration or Selenium tests:

```
./gradlew unitTest
```

### frontend assets
```
npm run build
npm run watch
```

### selenium tests
Selenium tests default to locally installed Chrome (resolved by Selenium Manager), which is convenient for Gradle and
IDE runs. Select the browser explicitly with `selenium.mode=host|container`; a JVM/Gradle property takes precedence over
the `SELENIUM_MODE` environment variable. CI sets `SELENIUM_MODE=container` explicitly.

Gradle examples:

```
./gradlew test -Pselenium.mode=host
./gradlew test -Pselenium.mode=container
```

For IntelliJ/JUnit, add `-Dselenium.mode=host` or `-Dselenium.mode=container` to the run configuration's VM options, or
set `SELENIUM_MODE`. Host mode creates screenshots but no MP4 recording. Container mode keeps Testcontainers, screenshots,
and VNC MP4 recording. When `host.testcontainers.internal` is unavailable in container mode, set `HOST_FOR_SELENIUM`.

### Reusing the test database locally

Repeated local test invocations can retain the project Testcontainers PostgreSQL instance. This is an experimental,
developer-only opt-in and is always disabled when a CI environment variable is present. Enable both required flags in
PowerShell:

```powershell
$env:TESTCONTAINERS_REUSE_ENABLE = 'true'
.\gradlew.bat test -Ptestcontainers.reuse=true
```

On POSIX shells:

```sh
export TESTCONTAINERS_REUSE_ENABLE=true
./gradlew test -Ptestcontainers.reuse=true
```

Each invocation takes an advisory lock for its full test JVM lifetime, removes and recreates the `public` schema, and
then lets Liquibase rebuild schema and seed data. Do not use the retained database for application data. Disable reuse
with `Remove-Item Env:TESTCONTAINERS_REUSE_ENABLE` (PowerShell) or `unset TESTCONTAINERS_REUSE_ENABLE` (POSIX), and omit
the Gradle property. Remove only this project's retained containers with:

```powershell
docker ps -aq --filter "label=com.example.demo-spring-jsf.reusable-postgres=true" |
    ForEach-Object { docker rm -f $_ }
```

```sh
for container in $(docker ps -aq --filter "label=com.example.demo-spring-jsf.reusable-postgres=true"); do
    docker rm -f "$container"
done
```

### Docker
Install Docker.
Run the Docker image, by executing the
[`docker run`](https://docs.docker.com/engine/reference/run/) command from the terminal:
```
docker run -d --restart=always --name spring-demo -e POSTGRES_USER=dev -e POSTGRES_PASSWORD=dev -e POSTGRES_DB=spring-demo -p 5432:5432 postgres
```
##### Options
* `--restart=always` always restart docker when we turn on computer
* `--name`name of the container, where will be PostgreSQL
* `-e` system variables by which we create database, user and password
* [`-p 5432:5432`](https://docs.docker.com/engine/reference/run/#expose-incoming-ports) option publishes all
                exposed ports to the host interfaces. In our example, it is port `5432` is both `hostPort` and `containerPort`
