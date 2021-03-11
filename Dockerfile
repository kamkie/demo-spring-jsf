FROM azul/zulu-openjdk-alpine:11

ARG JAR_FILE=build/libs/demo-spring-jsf-*-boot.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
