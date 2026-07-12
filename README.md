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
