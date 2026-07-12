package com.example.tests;

import com.example.extension.SeleniumExtension;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.Testcontainers;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@ExtendWith(SeleniumExtension.class)
public abstract class BaseSeleniumIntegrationTest extends BaseIntegrationTest {

    protected final String seleniumBaseUrl;

    public BaseSeleniumIntegrationTest(int localServerPort, ObjectMapper objectMapper) {
        super(objectMapper);
        boolean useHostChrome = "host".equalsIgnoreCase(System.getenv("SELENIUM_EXECUTION"));
        if (!useHostChrome) {
            Testcontainers.exposeHostPorts(localServerPort);
        }
        String hostForSelenium = useHostChrome
                ? "localhost"
                : System.getenv("HOST_FOR_SELENIUM") == null
                        ? "host.testcontainers.internal"
                        : System.getenv("HOST_FOR_SELENIUM");
        // noinspection HttpUrlsUsage local url for tests
        this.seleniumBaseUrl = "http://" + hostForSelenium + ":" + localServerPort;
    }
}
