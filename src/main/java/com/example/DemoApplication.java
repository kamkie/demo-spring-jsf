package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

@EnableCaching
@EnableJdbcHttpSession
@EnableAspectJAutoProxy
@EnableGlobalMethodSecurity(securedEnabled = true, proxyTargetClass = true)
@SpringBootApplication
public class DemoApplication {// NOSONAR

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args); // NOSONAR
    }
}
