package com.example.extension;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.PostgreSQLContainer;

public class DockerExtension implements AfterAllCallback {

    private static final PostgreSQLContainer POSTGRES_SQL_CONTAINER = new PostgreSQLContainer("postgres:14");

    public DockerExtension() {
        POSTGRES_SQL_CONTAINER.start();
    }

    public static PostgreSQLContainer getPostgres() {
        return POSTGRES_SQL_CONTAINER;
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        POSTGRES_SQL_CONTAINER.stop();
    }
}
