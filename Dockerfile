FROM azul/zulu-openjdk-alpine:25@sha256:a772cce2bb079795bb02a3637275fbfa885893ca5014eb8c84e2e1c027aa48da AS base
RUN addgroup -S spring && adduser -S spring -G spring
RUN mkdir /app
WORKDIR /app

FROM base AS builder
ARG JAR_FILE=build/libs/demo-spring-jsf-*-boot.jar
COPY ${JAR_FILE} app.jar
RUN java -Djarmode=tools -jar app.jar extract --layers --destination extracted/

FROM base AS trainer

RUN apk add --no-cache postgresql18=18.4-r0

COPY --from=builder /app/extracted/spring-boot-loader/ ./
COPY --from=builder /app/extracted/dependencies/ ./
COPY --from=builder /app/extracted/application/ ./
COPY scripts/aot-train.sh /usr/local/bin/aot-train

ENV SPRING_PROFILES_ACTIVE=deployed

RUN /usr/local/bin/aot-train

FROM base AS runnable

COPY --from=trainer /app/ ./
USER spring:spring

ENV SPRING_PROFILES_ACTIVE=deployed

RUN ls -lha

ENTRYPOINT ["java", "-XX:AOTMode=on", "-XX:AOTCache=/app/application.aot", "-Xlog:aot=info", "-jar", "app.jar"]
