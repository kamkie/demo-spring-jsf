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
### selenium tests
to run selenium test where `host.docker.internal` is not set env variable `HOST_FOR_SELENIUM` can be used

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
