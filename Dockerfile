FROM azul/zulu-openjdk-alpine:11 as base
RUN addgroup -S spring && adduser -S spring -G spring
RUN mkdir /application
WORKDIR /application

FROM base as builder
ARG JAR_FILE=build/libs/demo-spring-jsf-*-boot.jar
COPY ${JAR_FILE} app.jar
RUN java -Djarmode=layertools -jar app.jar extract && ls -lah

FROM base

COPY --from=builder /application/spring-boot-loader/ ./
COPY --from=builder /application/dependencies/ ./
COPY --from=builder /application/snapshot-dependencies/ ./
COPY --from=builder /application/application/ ./
USER spring:spring

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
