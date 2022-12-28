package com.example.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bonigarcia.seljup.SeleniumJupiter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtendWith;

@Slf4j
@ExtendWith(SeleniumJupiter.class)
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
