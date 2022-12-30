package com.example.pageobjects;

import com.example.util.JsfUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

@Slf4j
public class TableXhtmlPage {
    private final WebDriver webDriver;

    public TableXhtmlPage(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public WebElement findFilterByLanguage() {
        return webDriver.findElement(By.id("deviceListForm:messagesTable:lang:filter"));
    }

    public WebElement findSortByLanguage() {
        return webDriver.findElement(By.id("deviceListForm:messagesTable:lang"))
                .findElement(By.className("ui-column-title"));
    }

    public WebElement findFirstRow() {
        return webDriver.findElement(By.cssSelector("#deviceListForm\\:messagesTable table tbody tr:first-child"));
    }

    public void waitForTableLoaded() {
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(30));
        wait.until(visibilityOfElementLocated(By.id("deviceListForm:messagesTable")));
    }

    public void waitForAjaxTableLoaded() {
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(30), Duration.ofMillis(1));
        try {
            wait.until(visibilityOfElementLocated(By.cssSelector(".ui-dialog")));
        } catch (Exception e) { // NOPMD
            log.warn("unable to find loading dialog, probably already gone, ignoring");
        }
        wait.until(JsfUtil.waitForJQueryAndPrimeFaces());
        wait.until(invisibilityOfElementLocated(By.cssSelector(".ui-dialog")));
    }

    public int countRowsInTable() {
        return webDriver.findElements(By.cssSelector("#deviceListForm\\:messagesTable table tbody tr")).size();
    }

    public WebElement findPageContent() {
        return webDriver.findElement(By.id("pageContent"));
    }

}
