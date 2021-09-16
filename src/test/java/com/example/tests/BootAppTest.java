package com.example.tests;

import com.example.DemoApplication;
import com.example.extension.DockerExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.PostgreSQLContainer;

@ExtendWith({DockerExtension.class})
class BootAppTest {

    @Test
    void main() {
        System.setProperty("spring.devtools.restart.enabled", "false");
        @SuppressWarnings("PMD.CloseResource")
        PostgreSQLContainer postgres = DockerExtension.getPostgres();
        DemoApplication.main(new String[]{
                "--server.port=-1",
                "--spring.profiles.active=test,swagger",
                "--spring.datasource.url=" + postgres.getJdbcUrl(),
                "--spring.datasource.username=" + postgres.getUsername(),
                "--spring.datasource.password=" + postgres.getPassword()
        });
    }

    @AfterEach
    void closeContext() {
        DemoApplication.getApplicationContext().close();
    }

}
