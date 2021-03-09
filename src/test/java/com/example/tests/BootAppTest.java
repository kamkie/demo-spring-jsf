package com.example.tests;

import com.example.DemoApplication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class BootAppTest {

    @Container
    private static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:12");

    @Test
    void main() {
        System.setProperty("spring.devtools.restart.enabled", "false");
        DemoApplication.main(new String[]{
                "--server.port=-1",
                "--spring.profiles.active=test,swagger",
                "--spring.datasource.url=" + POSTGRES.getJdbcUrl(),
                "--spring.datasource.username=" + POSTGRES.getUsername(),
                "--spring.datasource.password=" + POSTGRES.getPassword()
        });
    }

    @AfterEach
    void closeContext() {
        DemoApplication.getApplicationContext().close();
    }

}
