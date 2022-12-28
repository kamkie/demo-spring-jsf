package com.example.extension;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.PostgreSQLContainer;

public class DockerExtension implements AfterAllCallback {

    private static final PostgreSQLContainer POSTGRES_SQL_CONTAINER = new PostgreSQLContainer("postgres:14");

    public DockerExtension() {
        if (!POSTGRES_SQL_CONTAINER.isRunning()) {
            POSTGRES_SQL_CONTAINER.start();
        }
        Runtime.getRuntime().addShutdownHook(new Thread(POSTGRES_SQL_CONTAINER::stop));
    }

    public static PostgreSQLContainer getPostgres() {
        if (!POSTGRES_SQL_CONTAINER.isRunning()) {
            POSTGRES_SQL_CONTAINER.start();
        }
        return POSTGRES_SQL_CONTAINER;
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        POSTGRES_SQL_CONTAINER.stop();
    }
}
