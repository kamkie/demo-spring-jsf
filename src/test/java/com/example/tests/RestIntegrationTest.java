package com.example.tests;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SuppressWarnings({
        "PMD.TooManyStaticImports"
})
class RestIntegrationTest extends BaseRestIntegrationTest {

    @Autowired
    public RestIntegrationTest(
            @LocalServerPort int localServerPort,
            ObjectMapper objectMapper,
            RestTemplateBuilder restTemplateBuilder) {
        super(localServerPort, objectMapper, restTemplateBuilder);
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

        assertAll(
                () -> assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED),
                () -> assertThat(responseEntity.hasBody()).isTrue(),
                () -> assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_JSON),
                () -> assertThat(headers.get(HttpHeaders.WWW_AUTHENTICATE)).contains("Basic realm=\"demo-spring-jsf\""),
                () -> assertThat(headers.getCacheControl()).isEqualTo("no-cache, no-store, max-age=0, must-revalidate")
        );
    }

    @Test
    void managementForbidden() throws IOException {
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
    void managementAuthorized() throws IOException {
        ResponseEntity<byte[]> responseEntity = this.restAdminAuthTemplate.getForEntity("/actuator/info", byte[].class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.hasBody()).isTrue();
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = objectMapper.readValue(responseEntity.getBody(), MAP_TYPE_REFERENCE);
        assertThat(payload)
                .containsKey("git")
                .containsKey("build");
    }
}
