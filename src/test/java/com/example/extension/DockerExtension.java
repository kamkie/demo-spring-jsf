package com.example.extension;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.Extension;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@SuppressWarnings("PMD.DoNotUseThreads")
public class DockerExtension implements Extension {

    private static final PostgreSQLContainer POSTGRES_SQL_CONTAINER = new PostgreSQLContainer("postgres:14");
    private static final Lock LOCK = new ReentrantLock();

    public static PostgreSQLContainer getPostgres() {
        if (!POSTGRES_SQL_CONTAINER.isRunning()) {
            LOCK.lock();
            try {
                if (!POSTGRES_SQL_CONTAINER.isRunning()) {
                    log.info("starting postgres container");
                    POSTGRES_SQL_CONTAINER.start();
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        log.info("stopping postgres container");
                        POSTGRES_SQL_CONTAINER.stop();
                    }));
                }
            } finally {
                LOCK.unlock();
            }
        }
        return POSTGRES_SQL_CONTAINER;
    }

}
