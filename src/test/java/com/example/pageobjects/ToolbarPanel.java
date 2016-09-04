package com.example.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

import java.util.Locale;

public class ToolbarPanel {

    private final WebDriver webDriver;

    public ToolbarPanel(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public void changeLanguage(Locale locale) {
        Select languageSelectBox = new Select(webDriver.findElement(By.id("toolbarForm:localeForm:language-select-box")));
        languageSelectBox.selectByValue(locale.getLanguage());
    }
}
