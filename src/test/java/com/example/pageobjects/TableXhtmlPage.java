package com.example.pageobjects;

import com.example.util.JsfUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.support.ui.ExpectedConditions.not;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

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
        WebDriverWait wait = new WebDriverWait(webDriver, 30);
        wait.until(visibilityOfElementLocated(By.id("deviceListForm:messagesTable")));
    }

    public void waitForAjaxTableLoaded() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(webDriver, 30);
        TimeUnit.MILLISECONDS.sleep(200);
        wait.until(JsfUtil.waitForJQueryAndPrimeFaces());
        wait.until(not(visibilityOfElementLocated(By.cssSelector(".ui-dialog"))));
    }

    public int countRowsInTable() {
        return webDriver.findElements(By.cssSelector("#deviceListForm\\:messagesTable table tbody tr")).size();
    }

    public WebElement findPageContent() {
        return webDriver.findElement(By.id("pageContent"));
    }

}
