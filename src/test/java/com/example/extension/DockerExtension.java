package com.example.extension;

import org.junit.jupiter.api.extension.Extension;
import org.testcontainers.containers.PostgreSQLContainer;

public class DockerExtension implements Extension {

    private static final PostgreSQLContainer POSTGRES_SQL_CONTAINER = new PostgreSQLContainer("postgres:14");

    public DockerExtension() {
        Runtime.getRuntime().addShutdownHook(new Thread(POSTGRES_SQL_CONTAINER::stop));
    }

    public static PostgreSQLContainer getPostgres() {
        if (!POSTGRES_SQL_CONTAINER.isRunning()) {
            POSTGRES_SQL_CONTAINER.start();
        }
        return POSTGRES_SQL_CONTAINER;
    }

}
