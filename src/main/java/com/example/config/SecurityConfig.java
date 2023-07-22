package com.example.config;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableMethodSecurity(securedEnabled = true, proxyTargetClass = true)
public class SecurityConfig {

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(antMatcher("/javax.faces.resource"));
    }

    @Bean
    @Order(2)
    public SecurityFilterChain appFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(antMatcher("/favicon.ico"),
                                antMatcher("/error"))
                        .permitAll()
                        .requestMatchers(mvc.pattern("/"),
                                mvc.pattern("/home"),
                                mvc.pattern("/welcome"))
                        .permitAll()
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
                        .requestMatchers(antMatcher("/error")).permitAll()
                        .anyRequest().hasRole("ADMIN")
                );
        http.sessionManagement(smc -> smc.sessionCreationPolicy(STATELESS));
        return http.build();
    }

    @Scope("prototype")
    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

}
