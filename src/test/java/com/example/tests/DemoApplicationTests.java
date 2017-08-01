package com.example.tests;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.example.pageobjects.LoginPage;
import com.example.pageobjects.SessionMessagesPanel;
import com.example.pageobjects.TableXhtmlPage;
import com.example.pageobjects.ToolbarPanel;
import com.example.rule.SeleniumClassRule;
import com.example.rule.SeleniumTestRule;
import com.example.utils.LazyInitializer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.web.client.LocalHostUriTemplateHandler;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DemoApplicationTests {

    private static LazyInitializer<WebDriver> webDriverLazyInitializer = new LazyInitializer<>(DemoApplicationTests::createWebDriver);

    @ClassRule
    public static final SeleniumClassRule seleniumClassRule = new SeleniumClassRule(webDriverLazyInitializer);
    @Rule
    public final SeleniumTestRule seleniumTestRule = new SeleniumTestRule(webDriverLazyInitializer);

    @LocalServerPort
    private int port;
    private ObjectMapper objectMapper;
    private TestRestTemplate restTemplate;
    private TestRestTemplate restUserAuthTemplate;
    private TestRestTemplate restAdminAuthTemplate;

    @Autowired
    public void initRestTemplate(RestTemplateBuilder restTemplateBuilder, Environment environment) {
        LocalHostUriTemplateHandler handler = new LocalHostUriTemplateHandler(environment);
        this.restTemplate = new TestRestTemplate(restTemplateBuilder.build());
        this.restTemplate.setUriTemplateHandler(handler);
        this.restUserAuthTemplate = new TestRestTemplate(restTemplateBuilder.basicAuthorization("user", "password").build());
        this.restUserAuthTemplate.setUriTemplateHandler(handler);
        this.restAdminAuthTemplate = new TestRestTemplate(restTemplateBuilder.basicAuthorization("admin", "password").build());
        this.restAdminAuthTemplate.setUriTemplateHandler(handler);
    }

    @Autowired
    public void initJacksonTester(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        JacksonTester.initFields(this, objectMapper);
    }

    private static WebDriver createWebDriver() {
        log.info("creating webDriver");
        ChromeDriverManager.getInstance().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--lang=pl");
        return new ChromeDriver(options);
    }

    @Test
    public void contextLoads() {
        assertThat(port).isPositive();
    }

    @Test
    public void home() throws Exception {
        ResponseEntity<String> responseEntity = this.restTemplate.getForEntity("/", String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.hasBody()).isTrue();
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON_UTF8);
        assertThat(responseEntity.getBody()).contains("principal");
    }

    @Test
    public void homeLogging() throws Exception {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.OFF);

        ResponseEntity<String> responseEntity = this.restTemplate.getForEntity("/", String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        root.setLevel(Level.DEBUG);
        responseEntity = this.restTemplate.getForEntity("/", String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        root.setLevel(Level.INFO);
    }

    @Test
    public void adminRedirectToLogin() throws Exception {
        ResponseEntity<String> responseEntity = this.restTemplate.getForEntity("/admin", String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(responseEntity.hasBody()).isFalse();
        assertThat(responseEntity.getHeaders().getLocation()).hasPath("/login");
    }

    @Test
    public void managementUnauthorized() throws Exception {
        ResponseEntity<String> responseEntity = this.restTemplate.getForEntity("/management/info", String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(responseEntity.hasBody()).isTrue();
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON_UTF8);
        assertThat(responseEntity.getHeaders().get(HttpHeaders.WWW_AUTHENTICATE)).contains("Basic realm=\"Realm\"");
        assertThat(responseEntity.getHeaders().getCacheControl()).isEqualTo("no-cache, no-store, max-age=0, must-revalidate");
    }

    @Test
    public void managementForbidden() throws Exception {
        ResponseEntity<byte[]> responseEntity = this.restUserAuthTemplate.getForEntity("/management/info", byte[].class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(responseEntity.hasBody()).isTrue();
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON_UTF8);

        Map<String, Object> payload = objectMapper.readValue(responseEntity.getBody(), new TypeReference<Map<String, Object>>() {
        });
        assertThat(payload)
                .containsKey("message")
                .containsKey("status")
                .containsKey("timestamp")
                .containsKey("path");
    }

    @Test
    public void managementAuthorized() throws Exception {
        ResponseEntity<byte[]> responseEntity = this.restAdminAuthTemplate.getForEntity("/management/info", byte[].class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.hasBody()).isTrue();
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON_UTF8);

        Map<String, Object> payload = objectMapper.readValue(responseEntity.getBody(), new TypeReference<Map<String, Object>>() {
        });
        assertThat(payload)
                .containsKey("git")
                .containsKey("build");
    }

    @Test
    public void adminLoginFailPassword() throws Exception {
        WebDriver webDriver = webDriverLazyInitializer.get();
        webDriver.get("http://localhost:" + port + "/admin");
        new LoginPage(webDriver).login("admin", "wrong");

        String content = webDriver.findElement(By.id("login-error")).getText();
        assertThat(content).contains("Invalid username and password.");
    }

    @Test
    public void adminLoginFailUserName() throws Exception {
        WebDriver webDriver = webDriverLazyInitializer.get();
        webDriver.get("http://localhost:" + port + "/admin");
        new LoginPage(webDriver).login("wrong", "password");

        String content = webDriver.findElement(By.id("login-error")).getText();
        assertThat(content).contains("Invalid username and password.");
    }

    @Test
    public void hello() throws Exception {
        WebDriver webDriver = webDriverLazyInitializer.get();
        webDriver.get("http://localhost:" + port + "/hello");
        new LoginPage(webDriver).login("user", "password");

        assertThat(webDriver.findElement(By.id("text")).getText()).contains("witaj świecie");

        webDriver.get("http://localhost:" + port + "/hello?lang=en");
        assertThat(webDriver.findElement(By.id("text")).getText()).contains("hello word");

        webDriver.get("http://localhost:" + port + "/hello");
        assertThat(webDriver.findElement(By.id("text")).getText()).contains("hello word");

        webDriver.get("http://localhost:" + port + "/hello?lang=pl");
        assertThat(webDriver.findElement(By.id("text")).getText()).contains("witaj świecie");
    }

    @Test
    public void adminLogin() throws Exception {
        WebDriver webDriver = webDriverLazyInitializer.get();
        webDriver.get("http://localhost:" + port + "/admin");
        new LoginPage(webDriver).login("admin", "password");

        String content = webDriver.findElement(By.tagName("pre")).getText();
        Map<String, Object> payload = objectMapper.readValue(content, new TypeReference<Map<String, Object>>() {
        });
        assertThat(payload)
                .containsKey("principal")
                .containsKey("userList")
                .containsKey("user")
                .containsKey("message")
                .containsKey("buildProperties");
    }

    @Test
    public void tableXhtml() throws Exception {
        WebDriver webDriver = webDriverLazyInitializer.get();
        webDriver.get("http://localhost:" + port + "/table.xhtml");
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
        assertThat(tableXhtmlPage.findFirstRow().getText()).contains("hello word");
        tableXhtmlPage.waitForAjaxTableLoaded();
        tableXhtmlPage.findSortByLanguage().click();
        tableXhtmlPage.waitForAjaxTableLoaded();
        assertThat(tableXhtmlPage.countRowsInTable()).isEqualTo(2);
        assertThat(tableXhtmlPage.findFirstRow().getText()).contains("witaj świecie");
    }

    @Test
    public void changeLanguageInJsf() throws Exception {
        WebDriver webDriver = webDriverLazyInitializer.get();
        webDriver.get("http://localhost:" + port + "/table.xhtml");
        new LoginPage(webDriver).login("user", "password");
        assertThat(new TableXhtmlPage(webDriver).findPageContent().getText()).contains("locale pl");

        new ToolbarPanel(webDriver).changeLanguage(Locale.ENGLISH);
        assertThat(new TableXhtmlPage(webDriver).findPageContent().getText()).contains("locale en");
    }

    @Test
    public void changeLanguageInJsfAndMvc() throws Exception {
        WebDriver webDriver = webDriverLazyInitializer.get();
        webDriver.get("http://localhost:" + port + "/hello");
        new LoginPage(webDriver).login("user", "password");
        TableXhtmlPage tableXhtmlPage = new TableXhtmlPage(webDriver);

        // locale is pl
        assertThat(webDriver.findElement(By.id("text")).getText()).contains("witaj świecie");
        webDriver.get("http://localhost:" + port + "/table.xhtml");
        assertThat(tableXhtmlPage.findPageContent().getText()).contains("locale pl");

        // change locale to en in jsf
        new ToolbarPanel(webDriver).changeLanguage(Locale.ENGLISH);
        assertThat(tableXhtmlPage.findPageContent().getText()).contains("locale en");
        webDriver.get("http://localhost:" + port + "/hello");
        assertThat(webDriver.findElement(By.id("text")).getText()).contains("hello word");

        // change locale to pl in mvc
        webDriver.get("http://localhost:" + port + "/hello?lang=pl");
        assertThat(webDriver.findElement(By.id("text")).getText()).contains("witaj świecie");
        webDriver.get("http://localhost:" + port + "/table.xhtml");
        assertThat(tableXhtmlPage.findPageContent().getText()).contains("locale pl");
    }

    @Test
    public void jsfSessionMessages() throws Exception {
        WebDriver webDriver = webDriverLazyInitializer.get();
        webDriver.get("http://localhost:" + port + "/index.xhtml");
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
