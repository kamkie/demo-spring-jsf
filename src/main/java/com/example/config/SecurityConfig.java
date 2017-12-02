package com.example.config;

import org.springframework.boot.actuate.autoconfigure.security.EndpointRequest;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@Order(SecurityProperties.DEFAULT_FILTER_ORDER - 20)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/", "/home", "/welcome").permitAll()
                .anyRequest().hasRole("USER")
                .and().formLogin().loginPage("/login").permitAll()
                .and().logout().permitAll();
    }

    @Configuration
    @Order(SecurityProperties.DEFAULT_FILTER_ORDER - 25)
    public static class ManagementSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/actuator/**")
                    .csrf().ignoringAntMatchers("/actuator", "/actuator/**")
                    .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and().httpBasic()
                    .and().authorizeRequests()
                    .requestMatchers(EndpointRequest.to("health", "info")).permitAll()
                    .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole("ACTUATOR")
                    .anyRequest().hasRole("ADMIN");
        }
    }
}
