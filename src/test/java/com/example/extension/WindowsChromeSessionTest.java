package com.example.extension;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class WindowsChromeSessionTest {

    @Test
    void recognizesWindowsOperatingSystems() {
        assertThat(WindowsChromeSession.isSupported("Windows 11")).isTrue();
        assertThat(WindowsChromeSession.isSupported("Linux")).isFalse();
    }

    @Test
    void buildsQuotedChromeDriverCommandLine() {
        Path chromeDriver = Path.of("Program Files", "ChromeDriver", "chromedriver.exe");
        String commandLine = WindowsChromeSession.buildDriverCommandLine(
                chromeDriver,
                12_345);

        assertThat(commandLine)
                .isEqualTo('"' + chromeDriver.toString() + "\" --port=12345");
    }
}
