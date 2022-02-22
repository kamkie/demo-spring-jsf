package com.example.pageobjects;

import com.example.util.JsfUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Locale;

public class ToolbarPanel {

    private final WebDriver webDriver;

    public ToolbarPanel(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public void changeLanguage(Locale locale) {
        Select languageSelectBox = new Select(webDriver.findElement(By.id("localeForm:language-select-box")));
        languageSelectBox.selectByValue(locale.getLanguage());
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(30));
        wait.until(JsfUtil.waitForJQueryAndPrimeFaces());
    }
}
