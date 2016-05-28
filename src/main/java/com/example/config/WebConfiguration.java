package com.example.config;

import com.example.annotation.Timed;
import com.example.component.RequestIdFilter;
import com.example.component.SessionIdFilter;
import com.example.component.TimeLoggingFilter;
import com.example.component.UserNameFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.servlet.DispatcherType;
import java.util.Arrays;

@Slf4j
@Timed
@Configuration
public class WebConfiguration extends WebMvcConfigurerAdapter {

    @Bean
    public FilterRegistrationBean requestIdFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean(new RequestIdFilter());
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC, DispatcherType.ERROR,
                DispatcherType.FORWARD, DispatcherType.INCLUDE);
        registration.setEnabled(true);
        registration.setUrlPatterns(Arrays.asList("/*"));

        return registration;
    }

    @Bean
    public FilterRegistrationBean timeLoggingFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean(new TimeLoggingFilter(50));
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC, DispatcherType.ERROR,
                DispatcherType.FORWARD, DispatcherType.INCLUDE);
        registration.setEnabled(true);
        registration.setUrlPatterns(Arrays.asList("/*"));

        return registration;
    }

    @Bean
    public FilterRegistrationBean sessionIdFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean(new SessionIdFilter());
        registration.setOrder(SecurityProperties.DEFAULT_FILTER_ORDER);
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC, DispatcherType.ERROR,
                DispatcherType.FORWARD, DispatcherType.INCLUDE);
        registration.setEnabled(true);
        registration.setUrlPatterns(Arrays.asList("/*"));

        return registration;
    }

    @Bean
    public FilterRegistrationBean userNameFilterRegistration(SecurityProperties securityProperties) {
        FilterRegistrationBean registration = new FilterRegistrationBean(new UserNameFilter());
        registration.setOrder(securityProperties.getFilterOrder() + 1);
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC, DispatcherType.ERROR,
                DispatcherType.FORWARD, DispatcherType.INCLUDE);
        registration.setEnabled(true);
        registration.setUrlPatterns(Arrays.asList("/*"));

        return registration;
    }

    @Bean
    public LocaleResolver localeResolver() {
        return new SessionLocaleResolver();
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
