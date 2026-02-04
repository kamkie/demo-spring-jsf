FROM azul/zulu-openjdk-alpine:21 AS base
RUN addgroup -S spring && adduser -S spring -G spring
RUN mkdir /app
WORKDIR /app

FROM base AS builder
ARG JAR_FILE=build/libs/demo-spring-jsf-*-boot.jar
COPY ${JAR_FILE} app.jar
RUN java -Djarmode=tools -jar app.jar extract --layers --destination extracted/

FROM base AS runnable

COPY --from=builder /app/extracted/spring-boot-loader/ ./
COPY --from=builder /app/extracted/dependencies/ ./
COPY --from=builder /app/extracted/application/ ./
USER spring:spring

RUN ls -lha

ENTRYPOINT ["java", "-jar", "app.jar"]
