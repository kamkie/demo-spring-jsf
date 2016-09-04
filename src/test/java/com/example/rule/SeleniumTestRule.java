package com.example.rule;


import com.example.utils.LazyInitializer;
import lombok.extern.slf4j.Slf4j;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class SeleniumTestRule extends TestWatcher {

    private final LazyInitializer<WebDriver> webDriverLazyInitializer;

    public SeleniumTestRule(LazyInitializer<WebDriver> webDriverLazyInitializer) {
        this.webDriverLazyInitializer = webDriverLazyInitializer;
    }

    @Override
    protected void failed(Throwable e, Description description) {
        if (webDriverLazyInitializer.isInitialized()) {
            File file = ((TakesScreenshot) webDriverLazyInitializer.get()).getScreenshotAs(OutputType.FILE);
            Path target = Paths.get("build")
                    .resolve("screenshots")
                    .resolve(description.getClassName())
                    .resolve(description.getMethodName())
                    .resolve("failed_" + System.currentTimeMillis() + ".png")
                    .toAbsolutePath();
            log.info("taking screenshot on test fail {}", target);
            try {
                target.getParent().toFile().mkdirs();
                Files.copy(file.toPath(), target);
            } catch (IOException e1) {
                log.warn("exception saving screenshot", e1);
            }
        }
    }

    private void cleanWebDriverSession() {
        if (webDriverLazyInitializer.isInitialized()) {
            webDriverLazyInitializer.get().manage().deleteAllCookies();
        }
    }

    @Override
    protected void starting(Description description) {
        cleanWebDriverSession();
    }

    @Override
    protected void finished(Description description) {
        cleanWebDriverSession();
    }

}
