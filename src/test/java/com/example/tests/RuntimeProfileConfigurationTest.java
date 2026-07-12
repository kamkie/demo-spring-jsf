package com.example.tests;

import org.junit.jupiter.api.Test;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.PropertySourcesPropertyResolver;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RuntimeProfileConfigurationTest {

    private static final List<String> REQUIRED_ENDPOINTS = List.of("health", "info", "metrics", "prometheus");

    @Test
    void defaultProfileIsLean() throws IOException {
        assertLean(resolver());
    }

    @Test
    void deployedProfileIsLean() throws IOException {
        assertLean(resolver("application-deployed.yaml"));
    }

    @Test
    void testProfileIsLean() throws IOException {
        assertLean(resolver("application-test.yaml"));
    }

    @Test
    void localDevelopmentProfileRetainsDevelopmentDiagnostics() throws IOException {
        PropertySourcesPropertyResolver properties = resolver("application-local-development.yaml");

        assertThat(properties.getProperty("spring.jpa.properties.hibernate.generate_statistics", Boolean.class))
                .isTrue();
        assertThat(properties.getProperty("spring.jmx.enabled", Boolean.class)).isFalse();
        assertThat(properties.getProperty("joinfaces.faces.project-stage")).isEqualTo("development");
        assertThat(properties.getProperty("logging.level.web")).isEqualTo("DEBUG");
        assertCustomFilters(properties, true);
        assertRequiredEndpoints(properties);
    }

    @Test
    void profilingProfileRetainsAllProfilingDiagnostics() throws IOException {
        PropertySourcesPropertyResolver properties = resolver("application-profiling.yaml");

        assertThat(properties.getProperty("spring.jpa.properties.hibernate.generate_statistics", Boolean.class))
                .isTrue();
        assertThat(properties.getProperty("spring.jmx.enabled", Boolean.class)).isTrue();
        assertThat(properties.getProperty("joinfaces.faces.project-stage")).isEqualTo("development");
        assertThat(properties.getProperty("logging.level.web")).isEqualTo("DEBUG");
        assertThat(properties.getProperty("logging.level.com.sun.faces")).isEqualTo("trace");
        assertCustomFilters(properties, true);
        assertRequiredEndpoints(properties);
    }

    private void assertLean(PropertySourcesPropertyResolver properties) {
        assertThat(properties.getProperty("spring.jpa.properties.hibernate.generate_statistics", Boolean.class))
                .isFalse();
        assertThat(properties.getProperty("spring.jmx.enabled", Boolean.class)).isFalse();
        assertThat(properties.getProperty("joinfaces.faces.project-stage")).isEqualTo("production");
        assertThat(properties.getProperty("logging.level.web")).isEqualTo("INFO");
        assertCustomFilters(properties, false);
        assertRequiredEndpoints(properties);
    }

    private void assertCustomFilters(PropertySourcesPropertyResolver properties, boolean enabled) {
        for (String filter : List.of("time", "user", "session", "request")) {
            assertThat(properties.getProperty("logging.custom." + filter + ".enable", Boolean.class))
                    .as("logging.custom.%s.enable", filter)
                    .isEqualTo(enabled);
        }
    }

    private void assertRequiredEndpoints(PropertySourcesPropertyResolver properties) {
        assertThat(properties.getProperty("management.endpoints.access.default")).isEqualTo("none");
        assertThat(properties.getProperty("management.endpoints.web.exposure.include"))
                .isEqualTo(String.join(",", REQUIRED_ENDPOINTS));
        for (String endpoint : REQUIRED_ENDPOINTS) {
            assertThat(properties.getProperty("management.endpoint." + endpoint + ".access"))
                    .as("management.endpoint.%s.access", endpoint)
                    .isEqualTo("read-only");
        }
    }

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private PropertySourcesPropertyResolver resolver(String... overlays) throws IOException {
        MutablePropertySources propertySources = new MutablePropertySources();
        YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
        for (String overlay : overlays) {
            List<PropertySource<?>> sources = loader.load(overlay, new ClassPathResource(overlay));
            sources.forEach(propertySources::addLast);
        }
        loader.load("application.yaml", new ClassPathResource("application.yaml"))
                .forEach(propertySources::addLast);
        return new PropertySourcesPropertyResolver(propertySources);
    }
}
