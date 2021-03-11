FROM azul/zulu-openjdk-alpine:11 as builder
WORKDIR application
ARG JAR_FILE=build/libs/demo-spring-jsf-*-boot.jar
COPY ${JAR_FILE} app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM azul/zulu-openjdk-alpine:11

WORKDIR application
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=builder application/dependencies/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
