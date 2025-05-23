package com.example.extension;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.containers.DefaultRecordingFileFactory;
import org.testcontainers.lifecycle.TestDescription;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static java.util.Map.entry;
import static org.testcontainers.containers.BrowserWebDriverContainer.VncRecordingMode.RECORD_ALL;
import static org.testcontainers.containers.VncRecordingContainer.VncRecordingFormat.MP4;

@Slf4j
@SuppressWarnings({
        "PMD.DoNotUseThreads",
        "PMD.TooManyMethods",
        "PMD.AvoidUncheckedExceptionsInSignatures"
})
public class SeleniumExtension implements BeforeAllCallback, BeforeEachCallback, AfterEachCallback, AfterAllCallback, ParameterResolver {

    public static final DateTimeFormatter DATE_TIME_FILE_NAME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH.mm.ss");
    private static final String SCREENSHOT_PATH = "./build/screenshot/";
    private static final BrowserWebDriverContainer<?> WEB_DRIVER_CONTAINER = new BrowserWebDriverContainer<>()
            .withCapabilities(initChromeOptions())
            .withRecordingMode(RECORD_ALL, new File(SCREENSHOT_PATH), MP4)
            .withRecordingFileFactory(new DefaultRecordingFileFactory());

    private static RemoteWebDriver webDriver;

    private static RemoteWebDriver getWebDriver() {
        if (!WEB_DRIVER_CONTAINER.isRunning()) {
            synchronized (WEB_DRIVER_CONTAINER) {
                if (!WEB_DRIVER_CONTAINER.isRunning()) {
                    log.info("starting selenium docker container");
                    WEB_DRIVER_CONTAINER.start();
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        log.info("stopping selenium docker container");
                        WEB_DRIVER_CONTAINER.stop();
                    }));
                    webDriver = new RemoteWebDriver(WEB_DRIVER_CONTAINER.getSeleniumAddress(), initChromeOptions());
                }
            }
        }
        return webDriver;
    }

    private static void createDirForScreenshots() throws IOException {
        Path path = Paths.get(SCREENSHOT_PATH);
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
    }

    private static void makeScreenshot(ExtensionContext context) throws IOException {
        File screenshotAs = getWebDriver().getScreenshotAs(OutputType.FILE);
        String timestamp = DATE_TIME_FILE_NAME_FORMATTER.format(LocalDateTime.now());
        Path target = Paths.get(SCREENSHOT_PATH, context.getDisplayName() + "-" + timestamp + ".png");
        Files.copy(screenshotAs.toPath(), target);
    }

    private static ChromeOptions initChromeOptions() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments(
                "--lang=pl",
                "--window-size=1600,900");
        Map<String, String> props = Map.ofEntries(
                entry("intl.accept_languages", "pl"),
                entry("credentials_enable_service", "false"),
                entry("profile.password_manager_enabled", "false"),
                entry("profile.password_manager_leak_detection", "false")
        );
        chromeOptions.setExperimentalOption("prefs", props);

        return chromeOptions;
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        createDirForScreenshots();
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        getWebDriver().manage().deleteAllCookies();
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        makeScreenshot(context);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        WEB_DRIVER_CONTAINER.afterTest(toTestDescription(context), context.getExecutionException());
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(RemoteWebDriver.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return getWebDriver();
    }

    private static TestDescription toTestDescription(ExtensionContext context) {
        return new TestDescription() {
            @Override
            public String getTestId() {
                return context.getDisplayName();
            }

            @Override
            public String getFilesystemFriendlyName() {
                String timestamp = DATE_TIME_FILE_NAME_FORMATTER.format(LocalDateTime.now());
                return context.getRequiredTestClass().getSimpleName() + "-" + timestamp;
            }
        };
    }
}
