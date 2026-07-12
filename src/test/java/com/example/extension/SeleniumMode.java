package com.example.extension;

import java.util.Locale;

public enum SeleniumMode {
    HOST,
    CONTAINER;

    private static final String PROPERTY_NAME = "selenium.mode";
    private static final String ENVIRONMENT_NAME = "SELENIUM_MODE";

    public static SeleniumMode current() {
        return resolve(System.getProperty(PROPERTY_NAME), System.getenv(ENVIRONMENT_NAME));
    }

    static SeleniumMode resolve(String propertyValue, String environmentValue) {
        String configuredValue = propertyValue == null || propertyValue.isBlank() ? environmentValue : propertyValue;
        if (configuredValue == null || configuredValue.isBlank()) {
            return HOST;
        }
        try {
            return valueOf(configuredValue.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    PROPERTY_NAME + " must be 'host' or 'container', but was '" + configuredValue + "'",
                    exception);
        }
    }
}
