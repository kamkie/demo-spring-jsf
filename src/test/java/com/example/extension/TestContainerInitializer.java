package com.example.extension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.springframework.boot.test.util.TestPropertyValues.Type.SYSTEM_ENVIRONMENT;

public class TestContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestContainerInitializer.class);

    @Override
    public void initialize(@NonNull ConfigurableApplicationContext configurableApplicationContext) {
        @SuppressWarnings("PMD.CloseResource")
        PostgreSQLContainer postgreSQLContainer = DockerExtension.getPostgres();
        LOGGER.info("------- initializing postgres config for test containers with port {} -------", postgreSQLContainer.getPortBindings());
        TestPropertyValues
                .of(
                        "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                        "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                        "spring.datasource.password=" + postgreSQLContainer.getPassword()
                )
                .applyTo(configurableApplicationContext.getEnvironment(), SYSTEM_ENVIRONMENT, "testPropertySource");
    }
}
