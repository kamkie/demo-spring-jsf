package com.example.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

@Slf4j
@ExtendWith(RestDocumentationExtension.class)
public abstract class BaseRestIntegrationTest extends BaseIntegrationTest {

    protected final TestRestTemplate restAnonymousTemplate;
    protected final TestRestTemplate restUserAuthTemplate;
    protected final TestRestTemplate restAdminAuthTemplate;
    protected MockMvc mockMvc;

    public BaseRestIntegrationTest(int localServerPort, ObjectMapper objectMapper, RestTemplateBuilder restTemplateBuilder) {
        super(localServerPort, objectMapper);
        OkHttpClient client = new OkHttpClient.Builder().followRedirects(false).build();
        RestTemplateBuilder testRestTemplateBuilder1 = restTemplateBuilder.rootUri("http://localhost:" + localServerPort);
        this.restAnonymousTemplate = new TestRestTemplate(testRestTemplateBuilder1.requestFactory(() -> new OkHttp3ClientHttpRequestFactory(client)));
        this.restUserAuthTemplate = new TestRestTemplate(testRestTemplateBuilder1, "user", "password");
        this.restAdminAuthTemplate = new TestRestTemplate(testRestTemplateBuilder1, "admin", "password");
    }

    @BeforeEach
    void beforeEach(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

}
