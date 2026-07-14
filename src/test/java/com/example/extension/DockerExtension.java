package com.example.extension;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.Extension;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@SuppressWarnings("PMD.DoNotUseThreads")
public class DockerExtension implements Extension {

    private static final String REUSE_PROPERTY = "testcontainers.reuse";
    private static final String REUSE_LABEL = "com.example.demo-spring-jsf.reusable-postgres";
    private static final long REUSE_LOCK_ID = 1_137L;
    private static final boolean REUSE_ENABLED = reuseEnabled();
    private static final PostgreSQLContainer POSTGRES_SQL_CONTAINER = postgresContainer();
    private static final Lock LOCK = new ReentrantLock();
    private static Connection reuseLockConnection;

    public static PostgreSQLContainer getPostgres() {
        if (POSTGRES_SQL_CONTAINER.isRunning()) {
            return POSTGRES_SQL_CONTAINER;
        }
        LOCK.lock();
        try {
            if (POSTGRES_SQL_CONTAINER.isRunning()) {
                return POSTGRES_SQL_CONTAINER;
            }
            log.info("starting postgres container");
            POSTGRES_SQL_CONTAINER.start();
            if (REUSE_ENABLED) {
                resetReusableDatabase();
            } else {
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    log.info("stopping postgres container");
                    POSTGRES_SQL_CONTAINER.stop();
                }));
            }
        } finally {
            LOCK.unlock();
        }
        return POSTGRES_SQL_CONTAINER;
    }

    private static boolean reuseEnabled() {
        if (!Boolean.getBoolean(REUSE_PROPERTY) || isCi()) {
            return false;
        }
        if (!TestcontainersConfiguration.getInstance().environmentSupportsReuse()) {
            throw new IllegalStateException(
                    "Reusable PostgreSQL requires TESTCONTAINERS_REUSE_ENABLE=true in addition to -D"
                            + REUSE_PROPERTY + "=true"
            );
        }
        return true;
    }

    private static PostgreSQLContainer postgresContainer() {
        PostgreSQLContainer container = new PostgreSQLContainer("postgres:17.10");
        if (REUSE_ENABLED) {
            container.withLabel(REUSE_LABEL, "true").withReuse(true);
        }
        return container;
    }

    private static boolean isCi() {
        return System.getenv("CI") != null
                || System.getenv("GITHUB_ACTIONS") != null
                || System.getenv("JENKINS_URL") != null
                || System.getenv("BUILD_NUMBER") != null;
    }

    private static void resetReusableDatabase() {
        try {
            reuseLockConnection = openResetConnection();
            log.info("reset reusable postgres schema and retained advisory lock {} for this test JVM", REUSE_LOCK_ID);
        } catch (SQLException e) {
            throw new IllegalStateException("Could not reset reusable PostgreSQL before Spring startup", e);
        }
    }

    private static Connection openResetConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(
                POSTGRES_SQL_CONTAINER.getJdbcUrl(),
                POSTGRES_SQL_CONTAINER.getUsername(),
                POSTGRES_SQL_CONTAINER.getPassword()
        );
        try {
            try (Statement statement = connection.createStatement()) {
                statement.execute("SET lock_timeout = '120s'");
                statement.execute("SELECT pg_advisory_lock(" + REUSE_LOCK_ID + ")");
                statement.execute("SELECT pg_terminate_backend(pid) FROM pg_stat_activity "
                        + "WHERE datname = current_database() AND pid <> pg_backend_pid()");
                statement.execute("DROP SCHEMA IF EXISTS public CASCADE");
                statement.execute("CREATE SCHEMA public");
            }
            return connection;
        } catch (SQLException e) {
            connection.close();
            throw e;
        }
    }

}
