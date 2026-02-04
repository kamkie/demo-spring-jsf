package com.example.tests;

import com.example.pageobjects.LoginPage;
import com.example.pageobjects.SessionMessagesPanel;
import com.example.pageobjects.TableXhtmlPage;
import com.example.pageobjects.ToolbarPanel;
import io.github.artsok.RepeatedIfExceptionsTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

@Slf4j
@Tag("selenium")
@SuppressWarnings({
        "PMD.AvoidDuplicateLiterals",
        "PMD.TestClassWithoutTestCases"
})
class SeleniumIntegrationTest extends BaseSeleniumIntegrationTest {

    @Autowired
    SeleniumIntegrationTest(
            @LocalServerPort int localServerPort,
            ObjectMapper objectMapper) {
        super(localServerPort, objectMapper);
    }

    @RepeatedIfExceptionsTest(repeats = 3, suspend = 50)
    void adminLoginFailPassword(RemoteWebDriver webDriver) {
        webDriver.get(seleniumBaseUrl + "/admin");
        new LoginPage(webDriver).login("admin", "wrong");

        String content = webDriver.findElement(By.id("login-error")).getText();
        assertThat(content).contains("Invalid username and password.");
    }

    @RepeatedIfExceptionsTest(repeats = 3, suspend = 50)
    void adminLoginFailUserName(RemoteWebDriver webDriver) {
        webDriver.get(seleniumBaseUrl + "/admin");
        new LoginPage(webDriver).login("wrong", "password");

        String content = webDriver.findElement(By.id("login-error")).getText();
        assertThat(content).contains("Invalid username and password.");
    }

    @RepeatedIfExceptionsTest(repeats = 3, suspend = 50)
    void hello(RemoteWebDriver webDriver) {
        webDriver.get(seleniumBaseUrl + "/hello");
        new LoginPage(webDriver).login("user", "password");

        assertThat(webDriver.findElement(By.id("text")).getText()).contains("witaj świecie");

        webDriver.get(seleniumBaseUrl + "/hello?lang=en");
        assertThat(webDriver.findElement(By.id("text")).getText()).contains("hello word");

        webDriver.get(seleniumBaseUrl + "/hello");
        assertThat(webDriver.findElement(By.id("text")).getText()).contains("hello word");

        webDriver.get(seleniumBaseUrl + "/hello?lang=pl");
        assertThat(webDriver.findElement(By.id("text")).getText()).contains("witaj świecie");
    }

    @RepeatedIfExceptionsTest(repeats = 3, suspend = 50)
    void adminLogin(RemoteWebDriver webDriver) throws Exception {
        webDriver.get(seleniumBaseUrl + "/admin");
        new LoginPage(webDriver).login("admin", "password");

        By preElementSelector = By.tagName("pre");
        String content = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(visibilityOfElementLocated(preElementSelector)).getText();
        Map<String, Object> payload = objectMapper.readValue(content, MAP_TYPE_REFERENCE);
        assertThat(payload)
                .containsKey("principal")
                .containsKey("userList")
                .containsKey("user")
                .containsKey("message")
                .containsKey("buildProperties");
    }

    @RepeatedIfExceptionsTest(repeats = 3, suspend = 50)
    void tableXhtml(RemoteWebDriver webDriver) throws Exception {
        webDriver.get(seleniumBaseUrl + "/table.xhtml");
        new LoginPage(webDriver).login("user", "password");

        TableXhtmlPage tableXhtmlPage = new TableXhtmlPage(webDriver);
        tableXhtmlPage.waitForTableLoaded();
        assertThat(tableXhtmlPage.countRowsInTable()).isEqualTo(2);
        assertThat(tableXhtmlPage.findFirstRow().getText()).contains("hello word");

        // try to filter
        tableXhtmlPage.findFilterByLanguage().sendKeys("pol");
        tableXhtmlPage.waitForAjaxTableLoaded();
        assertThat(tableXhtmlPage.countRowsInTable()).isEqualTo(1);

        // try to sort
        tableXhtmlPage.findFilterByLanguage().sendKeys(Keys.chord(Keys.CONTROL, "c"), Keys.DELETE);
        tableXhtmlPage.findFilterByLanguage().clear();
        tableXhtmlPage.waitForAjaxTableLoaded();
        tableXhtmlPage.findSortByLanguage().click();
        tableXhtmlPage.waitForAjaxTableLoaded();
        assertThat(tableXhtmlPage.findFirstRow().getText()).contains("hello word");
        tableXhtmlPage.findSortByLanguage().click();
        tableXhtmlPage.waitForAjaxTableLoaded();
        assertThat(tableXhtmlPage.countRowsInTable()).isEqualTo(2);
        assertThat(tableXhtmlPage.findFirstRow().getText()).contains("witaj świecie");
    }

    @RepeatedIfExceptionsTest(repeats = 3, suspend = 50)
    void changeLanguageInJsf(RemoteWebDriver webDriver) {
        webDriver.get(seleniumBaseUrl + "/table.xhtml");
        new LoginPage(webDriver).login("user", "password");
        assertThat(new TableXhtmlPage(webDriver).findPageContent().getText()).contains("locale pl");

        new ToolbarPanel(webDriver).changeLanguage(Locale.ENGLISH);
        assertThat(new TableXhtmlPage(webDriver).findPageContent().getText()).contains("locale en");
    }

    @RepeatedIfExceptionsTest(repeats = 3, suspend = 50)
    void changeLanguageInJsfAndMvc(RemoteWebDriver webDriver) {
        webDriver.get(seleniumBaseUrl + "/hello");
        new LoginPage(webDriver).login("user", "password");
        TableXhtmlPage tableXhtmlPage = new TableXhtmlPage(webDriver);

        // locale is pl
        assertThat(webDriver.findElement(By.id("text")).getText()).contains("witaj świecie");
        webDriver.get(seleniumBaseUrl + "/table.xhtml");
        assertThat(tableXhtmlPage.findPageContent().getText()).contains("locale pl");

        // change locale to en in jsf
        new ToolbarPanel(webDriver).changeLanguage(Locale.ENGLISH);
        assertThat(tableXhtmlPage.findPageContent().getText()).contains("locale en");
        webDriver.get(seleniumBaseUrl + "/hello");
        assertThat(webDriver.findElement(By.id("text")).getText()).contains("hello word");

        // change locale to pl in mvc
        webDriver.get(seleniumBaseUrl + "/hello?lang=pl");
        assertThat(webDriver.findElement(By.id("text")).getText()).contains("witaj świecie");
        webDriver.get(seleniumBaseUrl + "/table.xhtml");
        assertThat(tableXhtmlPage.findPageContent().getText()).contains("locale pl");
    }

    @RepeatedIfExceptionsTest(repeats = 3, suspend = 50)
    void jsfSessionMessages(RemoteWebDriver webDriver) {
        webDriver.get(seleniumBaseUrl + "/index.xhtml");
        new LoginPage(webDriver).login("user", "password");

        SessionMessagesPanel sessionMessagesPanel = new SessionMessagesPanel(webDriver);
        sessionMessagesPanel.showInfo();
        sessionMessagesPanel.waitForAjax();
        assertThat(sessionMessagesPanel.findSessionMessage().getText()).contains("PrimeFaces Rocks.");

        sessionMessagesPanel.showWarn();
        sessionMessagesPanel.waitForAjax();
        assertThat(sessionMessagesPanel.findSessionMessage().getText()).contains("Watch out for PrimeFaces.");

        sessionMessagesPanel.showError();
        sessionMessagesPanel.waitForAjax();
        assertThat(sessionMessagesPanel.findSessionMessage().getText()).contains("Contact admin.");

        sessionMessagesPanel.showFatal();
        sessionMessagesPanel.waitForAjax();
        assertThat(sessionMessagesPanel.findSessionMessage().getText()).contains("System Error");
    }

}
