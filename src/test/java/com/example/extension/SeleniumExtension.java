package com.example.extension;

import lombok.SneakyThrows;
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
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.lifecycle.TestDescription;
import software.xdev.testcontainers.selenium.containers.browser.BrowserWebDriverContainer;
import software.xdev.testcontainers.selenium.containers.browser.CapabilitiesBrowserWebDriverContainer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.ReentrantLock;

import static com.example.util.LockUtils.withLock;
import static software.xdev.testcontainers.selenium.containers.browser.BrowserWebDriverContainer.RecordingMode.RECORD_ALL;

@Slf4j
@SuppressWarnings({
        "PMD.DoNotUseThreads",
        "PMD.TooManyMethods",
        "PMD.AvoidUncheckedExceptionsInSignatures",
        "PMD.AvoidUsingVolatile",
        "PMD.NullAssignment"
})
public class SeleniumExtension implements BeforeAllCallback, BeforeEachCallback, AfterEachCallback, AfterAllCallback, ParameterResolver {

    public static final DateTimeFormatter DATE_TIME_FILE_NAME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH.mm.ss");
    private static final String SCREENSHOT_PATH = "./build/screenshot/";
    private static final SeleniumMode SELENIUM_MODE = SeleniumMode.current();
    private static final BrowserWebDriverContainer<?> WEB_DRIVER_CONTAINER = new CapabilitiesBrowserWebDriverContainer<>(ChromeStartupOptions.maximized())
            .withRecordingMode(RECORD_ALL)
            .withAccessToHost(true)
            .withRecordingDirectory(Path.of(SCREENSHOT_PATH));
    private static final ReentrantLock LOCK = new ReentrantLock();
    private static volatile RemoteWebDriver webDriver;
    private static WindowsChromeSession windowsChromeSession;

    private static RemoteWebDriver getWebDriver() {
        RemoteWebDriver currentWebDriver = webDriver;
        if (currentWebDriver == null) {
            currentWebDriver = withLock(LOCK, () -> {
                if (webDriver == null) {
                    webDriver = startWebDriver();
                }
                return webDriver;
            });
        }
        return currentWebDriver;
    }

    @SneakyThrows
    private static RemoteWebDriver startWebDriver() {
        if (SELENIUM_MODE == SeleniumMode.HOST) {
            if (WindowsChromeSession.isSupported()) {
                log.info("starting host Chrome on a dedicated Windows desktop");
                windowsChromeSession = WindowsChromeSession.start(ChromeStartupOptions.maximized());
                return windowsChromeSession.driver();
            }
            log.info("starting host Chrome through Selenium Manager");
            return new ChromeDriver(ChromeStartupOptions.maximized());
        }
        log.info("starting selenium docker container");
        WEB_DRIVER_CONTAINER.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("stopping selenium docker container");
            WEB_DRIVER_CONTAINER.stop();
        }));
        return new RemoteWebDriver(WEB_DRIVER_CONTAINER.getSeleniumAddressURI().toURL(), ChromeStartupOptions.maximized());
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
        if (SELENIUM_MODE == SeleniumMode.CONTAINER) {
            WEB_DRIVER_CONTAINER.afterTest(toTestDescription(context), context.getExecutionException());
        } else {
            quitHostWebDriver();
        }
    }

    private static void quitHostWebDriver() {
        withLock(LOCK, () -> {
            if (webDriver != null) {
                try {
                    if (windowsChromeSession == null) {
                        webDriver.quit();
                    } else {
                        windowsChromeSession.close();
                    }
                } finally {
                    webDriver = null;
                    windowsChromeSession = null;
                }
            }
        });
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
