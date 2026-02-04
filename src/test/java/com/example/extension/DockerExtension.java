package com.example.extension;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.Extension;
import org.testcontainers.postgresql.PostgreSQLContainer;

@Slf4j
@SuppressWarnings({
        "PMD.DoNotUseThreads"
})
public class DockerExtension implements Extension {

    private static final PostgreSQLContainer POSTGRES_SQL_CONTAINER = new PostgreSQLContainer("postgres:14");

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("stopping postgres container");
            POSTGRES_SQL_CONTAINER.stop();
        }));
    }

    public static PostgreSQLContainer getPostgres() {
        if (!POSTGRES_SQL_CONTAINER.isRunning()) {
            log.info("starting postgres container");
            POSTGRES_SQL_CONTAINER.start();
        }
        return POSTGRES_SQL_CONTAINER;
    }

}
