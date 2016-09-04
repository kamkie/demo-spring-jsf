package com.example.tests;

import com.example.rule.SeleniumClassRule;
import com.example.rule.SeleniumTestRule;
import com.example.utils.LazyInitializer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
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

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DemoApplicationTests {

    private static LazyInitializer<WebDriver> webDriverLazyInitializer = new LazyInitializer<>(() -> createWebDriver());

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
        return new FirefoxDriver();
    }

    @Test
    public void contextLoads() {
        assertThat(port).isNotZero();
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
    public void adminLogin() throws Exception {
        webDriverLazyInitializer.get().get("http://localhost:" + port + "/admin");
        loginAsAdmin();
    }

    private void loginAsAdmin() {
        WebElement loginForm = webDriverLazyInitializer.get().findElement(By.tagName("form"));
        loginForm.findElement(By.name("username")).sendKeys("admin");
        loginForm.findElement(By.name("password")).sendKeys("password");
        loginForm.submit();
    }

}
