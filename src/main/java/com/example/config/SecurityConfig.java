package com.example.config;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableMethodSecurity(securedEnabled = true, proxyTargetClass = true)
public class SecurityConfig {

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers("/javax.faces.resource");
    }

    @Bean
    @Order(2)
    public SecurityFilterChain appFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/", "/home", "/welcome", "/favicon.ico").permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().hasRole("USER")
                );
        http.formLogin(flc -> flc.loginPage("/login").permitAll());
        http.logout(LogoutConfigurer::permitAll);
        return http.build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain managementFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/actuator/**");
        http.httpBasic(httpBasic -> httpBasic.realmName("demo-spring-jsf"));
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(EndpointRequest.to("health", "info")).permitAll()
                        .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole("ACTUATOR")
                        .requestMatchers("/error").permitAll()
                        .anyRequest().hasRole("ADMIN")
                );
        http.sessionManagement(smc -> smc.sessionCreationPolicy(STATELESS));
        return http.build();
    }

}
