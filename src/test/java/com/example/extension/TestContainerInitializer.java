package com.example.extension;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.springframework.boot.test.util.TestPropertyValues.Type.SYSTEM_ENVIRONMENT;

@Slf4j
public class TestContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final PostgreSQLContainer POSTGRE_SQL_CONTAINER = new PostgreSQLContainer("postgres:12");

    @Override
    public void initialize(@NonNull ConfigurableApplicationContext configurableApplicationContext) {
        POSTGRE_SQL_CONTAINER.start();
        log.info("------- initializing postgres config for test containers with port {} -------", POSTGRE_SQL_CONTAINER.getPortBindings());
        TestPropertyValues.of(
                "spring.datasource.url=" + POSTGRE_SQL_CONTAINER.getJdbcUrl(),
                "spring.datasource.username=" + POSTGRE_SQL_CONTAINER.getUsername(),
                "spring.datasource.password=" + POSTGRE_SQL_CONTAINER.getPassword())
                .applyTo(configurableApplicationContext.getEnvironment(), SYSTEM_ENVIRONMENT, "testPropertySource");
    }
}
