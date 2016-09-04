package com.example.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginPage {

    private final WebDriver webDriver;

    public LoginPage(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public void login(String userName, String password) {
        WebElement loginForm = webDriver.findElement(By.tagName("form"));
        loginForm.findElement(By.name("username")).sendKeys(userName);
        loginForm.findElement(By.name("password")).sendKeys(password);
        loginForm.submit();
    }
}
