package com.example.tests;

import com.example.component.RequestIdFilter;
import com.example.component.SessionIdFilter;
import com.example.component.TimeLoggingFilter;
import com.example.component.UserNameFilter;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Tag("integration")
@ActiveProfiles(profiles = "profiling", inheritProfiles = false)
@TestPropertySource(properties = "spring.jmx.enabled=true")
class ProfilingProfileIntegrationTest extends BaseIntegrationTest {

    private final ApplicationContext applicationContext;
    private final Environment environment;
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    ProfilingProfileIntegrationTest(
            ObjectMapper objectMapper,
            ApplicationContext applicationContext,
            Environment environment,
            EntityManagerFactory entityManagerFactory) {
        super(objectMapper);
        this.applicationContext = applicationContext;
        this.environment = environment;
        this.entityManagerFactory = entityManagerFactory;
    }

    @Test
    void profilingProfileInstantiatesRetainedDiagnostics() {
        assertAll(
                () -> assertThat(environment.getActiveProfiles()).containsExactly("profiling"),
                () -> assertThat(applicationContext.getBeansOfType(MBeanExporter.class)).hasSize(1),
                () -> assertThat(entityManagerFactory.unwrap(SessionFactory.class).getStatistics().isStatisticsEnabled())
                        .isTrue(),
                () -> assertThat(applicationContext.getBeansOfType(RequestIdFilter.class)).hasSize(1),
                () -> assertThat(applicationContext.getBeansOfType(SessionIdFilter.class)).hasSize(1),
                () -> assertThat(applicationContext.getBeansOfType(UserNameFilter.class)).hasSize(1),
                () -> assertThat(applicationContext.getBeansOfType(TimeLoggingFilter.class)).hasSize(1)
        );
    }
}
