package com.example.tests;

import com.example.extension.SeleniumExtension;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtendWith;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@ExtendWith(SeleniumExtension.class)
public abstract class BaseSeleniumIntegrationTest extends BaseIntegrationTest {

    protected final String seleniumBaseUrl;

    public BaseSeleniumIntegrationTest(int localServerPort, ObjectMapper objectMapper) {
        super(localServerPort, objectMapper);
        String hostForSelenium = System.getenv("HOST_FOR_SELENIUM") == null
                ? "host.docker.internal"
                : System.getenv("HOST_FOR_SELENIUM");
        this.seleniumBaseUrl = "http://" + hostForSelenium + ":" + localServerPort;
    }
}
