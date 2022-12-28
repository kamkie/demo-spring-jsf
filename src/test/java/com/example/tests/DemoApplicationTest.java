package com.example.tests;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.example.DemoApplication;
import com.example.extension.DockerExtension;
import com.example.extension.TestContainerInitializer;
import com.example.pageobjects.LoginPage;
import com.example.pageobjects.SessionMessagesPanel;
import com.example.pageobjects.TableXhtmlPage;
import com.example.pageobjects.ToolbarPanel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bonigarcia.seljup.DockerBrowser;
import io.github.bonigarcia.seljup.SeleniumJupiter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
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

import java.util.Locale;
import java.util.Map;

import static io.github.bonigarcia.seljup.BrowserType.CHROME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.boot.test.context.SpringBootTest.UseMainMethod.ALWAYS;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
@ExtendWith(DockerExtension.class)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@ExtendWith(SeleniumJupiter.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT, useMainMethod = ALWAYS)
@ContextConfiguration(classes = DemoApplication.class, initializers = TestContainerInitializer.class)
class DemoApplicationTest {

    private static final TypeReference<Map<String, Object>> MAP_TYPE_REFERENCE = new TypeReference<>() {
    };
    private final ObjectMapper objectMapper;
    private final String seleniumBaseUrl;
    private MockMvc mockMvc;
    private TestRestTemplate restAnonymousTemplate;
    private TestRestTemplate restUserAuthTemplate;
    private TestRestTemplate restAdminAuthTemplate;

    @Autowired
    DemoApplicationTest(
            @LocalServerPort int port,
            RestTemplateBuilder restTemplateBuilder,
            ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        String hostForSelenium = System.getenv("HOST_FOR_SELENIUM") == null
                ? "host.docker.internal"
                : System.getenv("HOST_FOR_SELENIUM");
        this.seleniumBaseUrl = "http://" + hostForSelenium + ":" + port;
        initRestTemplate(restTemplateBuilder.rootUri("http://localhost:" + port));
    }

    @BeforeEach
    void beforeEach(WebApplicationContext webApplicationContext,
                    RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    private void initRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        OkHttpClient client = new OkHttpClient.Builder().followRedirects(false).build();
        this.restAnonymousTemplate = new TestRestTemplate(restTemplateBuilder.requestFactory(() -> new OkHttp3ClientHttpRequestFactory(client)));
        this.restUserAuthTemplate = new TestRestTemplate(restTemplateBuilder, "user", "password");
        this.restAdminAuthTemplate = new TestRestTemplate(restTemplateBuilder, "admin", "password");
    }

    @Test
    void home() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(containsString("principal")))
                .andDo(document("index", preprocessResponse(prettyPrint())));
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
        HttpHeaders headers = responseEntity.getHeaders();

        Assertions.assertAll(
                () -> assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED),
                () -> assertThat(responseEntity.hasBody()).isTrue(),
                () -> assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_JSON),
                () -> assertThat(headers.get(HttpHeaders.WWW_AUTHENTICATE)).contains("Basic realm=\"Realm\""),
                () -> assertThat(headers.getCacheControl()).isEqualTo("no-cache, no-store, max-age=0, must-revalidate")
        );
    }

    @Test
    void managementForbidden() throws Exception {
        ResponseEntity<byte[]> responseEntity = this.restUserAuthTemplate.getForEntity("/actuator/metrics", byte[].class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(responseEntity.hasBody()).isTrue();
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = objectMapper.readValue(responseEntity.getBody(), MAP_TYPE_REFERENCE);
        assertThat(payload)
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

        Map<String, Object> payload = objectMapper.readValue(responseEntity.getBody(), MAP_TYPE_REFERENCE);
        assertThat(payload)
                .containsKey("git")
                .containsKey("build");
    }

    @Test
    void adminLoginFailPassword(@DockerBrowser(type = CHROME, lang = "PL", version = "latest") RemoteWebDriver webDriver) {
        webDriver.get(seleniumBaseUrl + "/admin");
        new LoginPage(webDriver).login("admin", "wrong");

        String content = webDriver.findElement(By.id("login-error")).getText();
        assertThat(content).contains("Invalid username and password.");
    }

    @Test
    void adminLoginFailUserName(@DockerBrowser(type = CHROME, lang = "PL", version = "latest") RemoteWebDriver webDriver) {
        webDriver.get(seleniumBaseUrl + "/admin");
        new LoginPage(webDriver).login("wrong", "password");

        String content = webDriver.findElement(By.id("login-error")).getText();
        assertThat(content).contains("Invalid username and password.");
    }

    @Test
    void hello(@DockerBrowser(type = CHROME, lang = "PL", version = "latest") RemoteWebDriver webDriver) {
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

    @Test
    void adminLogin(@DockerBrowser(type = CHROME, lang = "PL", version = "latest") RemoteWebDriver webDriver) throws Exception {
        webDriver.get(seleniumBaseUrl + "/admin");
        new LoginPage(webDriver).login("admin", "password");

        String content = webDriver.findElement(By.tagName("pre")).getText();
        Map<String, Object> payload = objectMapper.readValue(content, MAP_TYPE_REFERENCE);
        assertThat(payload)
                .containsKey("principal")
                .containsKey("userList")
                .containsKey("user")
                .containsKey("message")
                .containsKey("buildProperties");
    }

    @Test
    void tableXhtml(@DockerBrowser(type = CHROME, lang = "PL", version = "latest") RemoteWebDriver webDriver) throws Exception {
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

    @Test
    void changeLanguageInJsf(@DockerBrowser(type = CHROME, lang = "PL", version = "latest") RemoteWebDriver webDriver) {
        webDriver.get(seleniumBaseUrl + "/table.xhtml");
        new LoginPage(webDriver).login("user", "password");
        assertThat(new TableXhtmlPage(webDriver).findPageContent().getText()).contains("locale pl");

        new ToolbarPanel(webDriver).changeLanguage(Locale.ENGLISH);
        assertThat(new TableXhtmlPage(webDriver).findPageContent().getText()).contains("locale en");
    }

    @Test
    void changeLanguageInJsfAndMvc(@DockerBrowser(type = CHROME, lang = "PL", version = "latest") RemoteWebDriver webDriver) {
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

    @Test
    void jsfSessionMessages(@DockerBrowser(type = CHROME, lang = "PL", version = "latest") RemoteWebDriver webDriver) {
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
