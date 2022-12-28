package com.example.tests;

import com.example.DemoApplication;
import com.example.extension.DockerExtension;
import com.example.extension.TestContainerInitializer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Map;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.boot.test.context.SpringBootTest.UseMainMethod.ALWAYS;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@TestInstance(PER_CLASS)
@ExtendWith(DockerExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT, useMainMethod = ALWAYS)
@ContextConfiguration(classes = DemoApplication.class, initializers = TestContainerInitializer.class)
public abstract class BaseIntegrationTest {

    protected static final TypeReference<Map<String, Object>> MAP_TYPE_REFERENCE = new TypeReference<>() {
    };

    protected final int localServerPort;
    protected final ObjectMapper objectMapper;

    public BaseIntegrationTest(int localServerPort, ObjectMapper objectMapper) {
        this.localServerPort = localServerPort;
        this.objectMapper = objectMapper;
    }

}
