package com.example.tests;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.example.DemoApplication;
import com.example.extension.TestContainerInitializer;
import com.example.pageobjects.LoginPage;
import com.example.pageobjects.SessionMessagesPanel;
import com.example.pageobjects.TableXhtmlPage;
import com.example.pageobjects.ToolbarPanel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.LocalHostUriTemplateHandler;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.containers.BrowserWebDriverContainer.VncRecordingMode.RECORD_ALL;
import static org.testcontainers.containers.VncRecordingContainer.VncRecordingFormat.MP4;

@Slf4j
@SuppressWarnings({
        "PMD.AvoidDuplicateLiterals",
        "PMD.SignatureDeclareThrowsException",
        "PMD.UseConcurrentHashMap",
        "PMD.NcssCount",
        "PMD.TooManyMethods",
        "PMD.ExcessiveImports",
        "PMD.TooManyStaticImports",
})
@TestInstance(PER_CLASS)
@Testcontainers
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = DemoApplication.class, initializers = TestContainerInitializer.class)
class DemoApplicationTest {

    private static final String SCREENSHOT_PATH = "./build/screenshot/";
    @Container
    private static final BrowserWebDriverContainer<?> WEB_DRIVER_CONTAINER = new BrowserWebDriverContainer<>()
            .withCapabilities(initChromeOptions())
            .withRecordingMode(RECORD_ALL, new File(SCREENSHOT_PATH), MP4);

    private static final String HOST_FOR_SELENIUM = System.getenv("HOST_FOR_SELENIUM") == null ? "host.docker.internal" : System.getenv("HOST_FOR_SELENIUM");

    private final ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private TestRestTemplate restAnonymousTemplate;
    private TestRestTemplate restUserAuthTemplate;
    private TestRestTemplate restAdminAuthTemplate;
    @LocalServerPort
    private int port;

    @Autowired
    DemoApplicationTest(
            Environment environment,
            RestTemplateBuilder restTemplateBuilder,
            ObjectMapper objectMapper) throws IOException {
        this.objectMapper = objectMapper;
        initRestTemplate(restTemplateBuilder.uriTemplateHandler(new LocalHostUriTemplateHandler(environment)));
        createDirForScreenshots();
    }

    private static void createDirForScreenshots() throws IOException {
        Path path = Paths.get(SCREENSHOT_PATH);
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
    }

    private static ChromeOptions initChromeOptions() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments(
                "--lang=pl",
                "--window-size=1600,900");
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("intl.accept_languages", "pl");
        chromeOptions.setExperimentalOption("prefs", prefs);

        return chromeOptions;
    }

    @BeforeEach
    void beforeEach(WebApplicationContext webApplicationContext,
                    RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
        WEB_DRIVER_CONTAINER.getWebDriver().manage().deleteAllCookies();
    }

    @AfterEach
    void makeScreenshot(TestInfo testInfo) throws IOException {
        File screenshotAs = WEB_DRIVER_CONTAINER.getWebDriver().getScreenshotAs(OutputType.FILE);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH.mm.ss");
        Path target = Paths.get("./build/screenshot/", testInfo.getDisplayName() + "-" + dateTimeFormatter.format(LocalDateTime.now()) + ".png");
        Files.copy(screenshotAs.toPath(), target);
    }

    private void initRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        OkHttpClient client = new OkHttpClient.Builder().followRedirects(false).build();
        this.restAnonymousTemplate = new TestRestTemplate(restTemplateBuilder.requestFactory(() -> new OkHttp3ClientHttpRequestFactory(client)));
        this.restUserAuthTemplate = new TestRestTemplate(restTemplateBuilder, "user", "password");
        this.restAdminAuthTemplate = new TestRestTemplate(restTemplateBuilder, "admin", "password");
    }

    @Test
    void contextLoads() {
        assertThat(port).isPositive();
    }

    @Test
    void home() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(containsString("principal")))
                .andDo(document("index"));
    }

    @Test
    void homeLogging() {
        Logger appLoggers = (Logger) LoggerFactory.getLogger("com.example");
        appLoggers.setLevel(Level.OFF);

        ResponseEntity<String> responseEntity = this.restAnonymousTemplate.getForEntity("/", String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        appLoggers.setLevel(Level.DEBUG);
        responseEntity = this.restAnonymousTemplate.getForEntity("/", String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        appLoggers.setLevel(Level.INFO);
    }

    @Test
    void adminRedirectToLogin() {
        ResponseEntity<String> responseEntity = this.restAnonymousTemplate.getForEntity("/admin", String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(responseEntity.hasBody()).isFalse();
        assertThat(responseEntity.getHeaders().getLocation()).hasPath("/login");
    }

    @Test
    void managementUnauthorized() {
        ResponseEntity<String> responseEntity = this.restAnonymousTemplate.getForEntity("/actuator/metrics", String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(responseEntity.hasBody()).isTrue();
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(responseEntity.getHeaders().get(HttpHeaders.WWW_AUTHENTICATE)).contains("Basic realm=\"Realm\"");
        assertThat(responseEntity.getHeaders().getCacheControl()).isEqualTo("no-cache, no-store, max-age=0, must-revalidate");
    }

    @Test
    void managementForbidden() throws Exception {
        ResponseEntity<byte[]> responseEntity = this.restUserAuthTemplate.getForEntity("/actuator/metrics", byte[].class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(responseEntity.hasBody()).isTrue();
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = objectMapper.readValue(responseEntity.getBody(), new TypeReference<>() {
        });
        assertThat(payload)
                .containsKey("message")
                .containsKey("status")
                .containsKey("timestamp")
                .containsKey("path");
    }

    @Test
    void managementAuthorized() throws Exception {
        ResponseEntity<byte[]> responseEntity = this.restAdminAuthTemplate.getForEntity("/actuator/info", byte[].class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.hasBody()).isTrue();
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = objectMapper.readValue(responseEntity.getBody(), new TypeReference<Map<String, Object>>() {
        });
        assertThat(payload)
                .containsKey("git")
                .containsKey("build");
    }

    @Test
    void adminLoginFailPassword() {
        RemoteWebDriver webDriver = WEB_DRIVER_CONTAINER.getWebDriver();
        webDriver.get("http://" + HOST_FOR_SELENIUM + ":" + port + "/admin");
        new LoginPage(webDriver).login("admin", "wrong");

        String content = webDriver.findElement(By.id("login-error")).getText();
        assertThat(content).contains("Invalid username and password.");
    }

    @Test
    void adminLoginFailUserName() {
        RemoteWebDriver webDriver = WEB_DRIVER_CONTAINER.getWebDriver();
        webDriver.get("http://" + HOST_FOR_SELENIUM + ":" + port + "/admin");
        new LoginPage(webDriver).login("wrong", "password");

        String content = webDriver.findElement(By.id("login-error")).getText();
        assertThat(content).contains("Invalid username and password.");
    }

    @Test
    void hello() {
        RemoteWebDriver webDriver = WEB_DRIVER_CONTAINER.getWebDriver();
        webDriver.get("http://" + HOST_FOR_SELENIUM + ":" + port + "/hello");
        new LoginPage(webDriver).login("user", "password");

        assertThat(webDriver.findElement(By.id("text")).getText()).contains("witaj świecie");

        webDriver.get("http://" + HOST_FOR_SELENIUM + ":" + port + "/hello?lang=en");
        assertThat(webDriver.findElement(By.id("text")).getText()).contains("hello word");

        webDriver.get("http://" + HOST_FOR_SELENIUM + ":" + port + "/hello");
        assertThat(webDriver.findElement(By.id("text")).getText()).contains("hello word");

        webDriver.get("http://" + HOST_FOR_SELENIUM + ":" + port + "/hello?lang=pl");
        assertThat(webDriver.findElement(By.id("text")).getText()).contains("witaj świecie");
    }

    @Test
    void adminLogin() throws Exception {
        RemoteWebDriver webDriver = WEB_DRIVER_CONTAINER.getWebDriver();
        webDriver.get("http://" + HOST_FOR_SELENIUM + ":" + port + "/admin");
        new LoginPage(webDriver).login("admin", "password");

        String content = webDriver.findElement(By.tagName("pre")).getText();
        Map<String, Object> payload = objectMapper.readValue(content, new TypeReference<>() {
        });
        assertThat(payload)
                .containsKey("principal")
                .containsKey("userList")
                .containsKey("user")
                .containsKey("message")
                .containsKey("buildProperties");
    }

    @Test
    void tableXhtml() throws Exception {
        RemoteWebDriver webDriver = WEB_DRIVER_CONTAINER.getWebDriver();
        webDriver.get("http://" + HOST_FOR_SELENIUM + ":" + port + "/table.xhtml");
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

    @Test
    void changeLanguageInJsf() {
        RemoteWebDriver webDriver = WEB_DRIVER_CONTAINER.getWebDriver();
        webDriver.get("http://" + HOST_FOR_SELENIUM + ":" + port + "/table.xhtml");
        new LoginPage(webDriver).login("user", "password");
        assertThat(new TableXhtmlPage(webDriver).findPageContent().getText()).contains("locale pl");

        new ToolbarPanel(webDriver).changeLanguage(Locale.ENGLISH);
        assertThat(new TableXhtmlPage(webDriver).findPageContent().getText()).contains("locale en");
    }

    @Test
    void changeLanguageInJsfAndMvc() {
        RemoteWebDriver webDriver = WEB_DRIVER_CONTAINER.getWebDriver();
        webDriver.get("http://" + HOST_FOR_SELENIUM + ":" + port + "/hello");
        new LoginPage(webDriver).login("user", "password");
        TableXhtmlPage tableXhtmlPage = new TableXhtmlPage(webDriver);

        // locale is pl
        assertThat(webDriver.findElement(By.id("text")).getText()).contains("witaj świecie");
        webDriver.get("http://" + HOST_FOR_SELENIUM + ":" + port + "/table.xhtml");
        assertThat(tableXhtmlPage.findPageContent().getText()).contains("locale pl");

        // change locale to en in jsf
        new ToolbarPanel(webDriver).changeLanguage(Locale.ENGLISH);
        assertThat(tableXhtmlPage.findPageContent().getText()).contains("locale en");
        webDriver.get("http://" + HOST_FOR_SELENIUM + ":" + port + "/hello");
        assertThat(webDriver.findElement(By.id("text")).getText()).contains("hello word");

        // change locale to pl in mvc
        webDriver.get("http://" + HOST_FOR_SELENIUM + ":" + port + "/hello?lang=pl");
        assertThat(webDriver.findElement(By.id("text")).getText()).contains("witaj świecie");
        webDriver.get("http://" + HOST_FOR_SELENIUM + ":" + port + "/table.xhtml");
        assertThat(tableXhtmlPage.findPageContent().getText()).contains("locale pl");
    }

    @Test
    void jsfSessionMessages() {
        RemoteWebDriver webDriver = WEB_DRIVER_CONTAINER.getWebDriver();
        webDriver.get("http://" + HOST_FOR_SELENIUM + ":" + port + "/index.xhtml");
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
