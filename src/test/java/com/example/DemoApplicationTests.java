package com.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.WebClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
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

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DemoApplicationTests {

    @LocalServerPort
    private int port;

    private ObjectMapper objectMapper;
    private TestRestTemplate restTemplate;
    private TestRestTemplate restAuthTemplate;
    private WebDriver webDriver;
    private WebDriver webDriverAuth;

    @Autowired
    public void initRestTemplate(RestTemplateBuilder restTemplateBuilder, Environment environment) {
        this.restTemplate = new TestRestTemplate(restTemplateBuilder.build());
        LocalHostUriTemplateHandler handler = new LocalHostUriTemplateHandler(environment);
        this.restTemplate.setUriTemplateHandler(handler);
        this.restAuthTemplate = new TestRestTemplate(restTemplateBuilder.basicAuthorization("kamkie", "password").build());
        this.restAuthTemplate.setUriTemplateHandler(handler);
    }

    @Autowired
    public void initJacksonTester(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        JacksonTester.initFields(this, objectMapper);
    }

    @Before
    public void setup() {
        webDriver = new HtmlUnitDriver(BrowserVersion.CHROME, true);
        webDriverAuth = new HtmlUnitDriver() {
            @Override
            protected WebClient modifyWebClient(WebClient client) {
                DefaultCredentialsProvider creds = new DefaultCredentialsProvider();
                creds.addCredentials("kamkie", "password");
                client.setCredentialsProvider(creds);
                return client;
            }
        };
    }

    @Test
    public void contextLoads() {
        assertThat(port).isNotZero();
    }

    @Test
    public void home() throws Exception {
        ResponseEntity<String> responseEntity = this.restTemplate.getForEntity("/", String.class);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.hasBody()).isTrue();
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON_UTF8);
        assertThat(responseEntity.getBody()).contains("principal");
    }

    @Test
    public void adminRedirectToLogin() throws Exception {
        ResponseEntity<String> responseEntity = this.restTemplate.getForEntity("/admin", String.class);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(302);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(responseEntity.hasBody()).isFalse();
        assertThat(responseEntity.getHeaders().getLocation()).hasPath("/login");
    }

    @Test
    public void managementUnauthorized() throws Exception {
        ResponseEntity<String> responseEntity = this.restTemplate.getForEntity("/management/info", String.class);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(401);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(responseEntity.hasBody()).isTrue();
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON_UTF8);
        assertThat(responseEntity.getHeaders().get(HttpHeaders.WWW_AUTHENTICATE)).contains("Basic realm=\"Realm\"");
        assertThat(responseEntity.getHeaders().getCacheControl()).isEqualTo("no-cache, no-store, max-age=0, must-revalidate");
    }

    @Test
    public void managementAuthorized() throws Exception {
        ResponseEntity<byte[]> responseEntity = this.restAuthTemplate.getForEntity("/management/info", byte[].class);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.hasBody()).isTrue();
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON_UTF8);

        Map<String, Object> payload = objectMapper.readValue(responseEntity.getBody(), new TypeReference<Map<String, Object>>() {
        });
        assertThat(payload)
                .containsKey("git")
                .containsKey("build");
    }

}
