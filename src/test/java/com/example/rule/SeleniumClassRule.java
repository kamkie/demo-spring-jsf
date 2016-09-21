package com.example.rule;

import com.example.utils.LazyInitializer;
import lombok.extern.slf4j.Slf4j;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.openqa.selenium.WebDriver;

@Slf4j
public class SeleniumClassRule implements TestRule {

    private final LazyInitializer<WebDriver> webDriverLazyInitializer;

    public SeleniumClassRule(LazyInitializer<WebDriver> webDriverLazyInitializer) {
        this.webDriverLazyInitializer = webDriverLazyInitializer;
    }

    private void before() {
    }

    private void after() {
        if (webDriverLazyInitializer.isInitialized()) {
            log.info("closing webDriver");
            webDriverLazyInitializer.get().quit();
        }
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    before();
                    base.evaluate();
                } finally {
                    after();
                }
            }
        };
    }
}
