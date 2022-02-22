package com.example.pageobjects;

import com.example.util.JsfUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class SessionMessagesPanel {

    private final WebDriver webDriver;

    public SessionMessagesPanel(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public void showInfo() {
        webDriver.findElement(By.id("messages-form:show-info")).click();
    }

    public void showWarn() {
        webDriver.findElement(By.id("messages-form:show-warn")).click();
    }

    public void showError() {
        webDriver.findElement(By.id("messages-form:show-error")).click();
    }

    public void showFatal() {
        webDriver.findElement(By.id("messages-form:show-fatal")).click();
    }

    public WebElement findSessionMessage() {
        return webDriver.findElement(By.cssSelector(".ui-messages.ui-widget"));
    }

    public void waitForAjax() {
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(30));
        wait.until(JsfUtil.waitForJQueryAndPrimeFaces());
    }
}
