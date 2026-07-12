package com.example.extension;

import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

final class ChromeStartupOptions {

    private static final List<String> COMMON_ARGUMENTS = List.of(
            "--guest",
            "--disable-infobars",
            "--lang=pl");
    private static final Map<String, String> PREFERENCES = Map.ofEntries(
            entry("intl.accept_languages", "pl"),
            entry("credentials_enable_service", "false"),
            entry("profile.password_manager_enabled", "false"),
            entry("profile.password_manager_leak_detection", "false"));

    private ChromeStartupOptions() {
    }

    static ChromeOptions maximized() {
        return create("--start-maximized");
    }

    private static ChromeOptions create(String windowArgument) {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments(COMMON_ARGUMENTS);
        chromeOptions.addArguments(windowArgument);
        chromeOptions.setExperimentalOption("prefs", PREFERENCES);
        return chromeOptions;
    }
}
