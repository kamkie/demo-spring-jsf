package com.example.extension;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.PostgreSQLContainer;

public class DockerExtension implements AfterAllCallback {

    private static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:14");

    public DockerExtension() {
        postgres.start();
    }

    public static PostgreSQLContainer getPostgres() {
        return postgres;
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        postgres.stop();
    }
}
