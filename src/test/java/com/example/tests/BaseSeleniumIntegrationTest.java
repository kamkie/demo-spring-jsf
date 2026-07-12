package com.example.tests;

import com.example.extension.SeleniumExtension;
import com.example.extension.SeleniumMode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.Testcontainers;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@ExtendWith(SeleniumExtension.class)
@SuppressWarnings("PMD.AvoidUsingHardCodedIP")
public abstract class BaseSeleniumIntegrationTest extends BaseIntegrationTest {

    protected final String seleniumBaseUrl;

    public BaseSeleniumIntegrationTest(int localServerPort, ObjectMapper objectMapper) {
        super(objectMapper);
        SeleniumMode seleniumMode = SeleniumMode.current();
        if (seleniumMode == SeleniumMode.CONTAINER) {
            Testcontainers.exposeHostPorts(localServerPort);
        }
        String hostForSelenium = seleniumMode == SeleniumMode.HOST
                ? "127.0.0.1"
                : System.getenv("HOST_FOR_SELENIUM") == null
                        ? "host.testcontainers.internal"
                        : System.getenv("HOST_FOR_SELENIUM");
        // noinspection HttpUrlsUsage local url for tests
        this.seleniumBaseUrl = "http://" + hostForSelenium + ":" + localServerPort;
    }
}
