package com.example.extension;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.springframework.boot.test.util.TestPropertyValues.Type.SYSTEM_ENVIRONMENT;

@Slf4j
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class TestContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    @SuppressWarnings("PMD.GuardLogStatement")
    public void initialize(@NonNull ConfigurableApplicationContext configurableApplicationContext) {
        @SuppressWarnings("PMD.CloseResource")
        PostgreSQLContainer postgreSQLContainer = DockerExtension.getPostgres();
        log.info("------- initializing postgres config for test containers with port {} -------", postgreSQLContainer.getPortBindings());
        TestPropertyValues
                .of(
                        "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                        "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                        "spring.datasource.password=" + postgreSQLContainer.getPassword()
                )
                .applyTo(configurableApplicationContext.getEnvironment(), SYSTEM_ENVIRONMENT, "testPropertySource");
    }
}
