package com.example.extension;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ChromeStartupOptionsTest {

    @Test
    void createsHeadedMaximizedChromeOptions() {
        assertThat(arguments(ChromeStartupOptions.maximized()))
                .contains("--start-maximized")
                .doesNotContain("--headless", "--start-minimized");
    }

    @SuppressWarnings("unchecked")
    private static List<String> arguments(ChromeOptions chromeOptions) {
        Map<String, Object> options = (Map<String, Object>) chromeOptions.asMap().get("goog:chromeOptions");
        return (List<String>) options.get("args");
    }
}
