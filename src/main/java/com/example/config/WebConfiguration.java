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

import javax.servlet.DispatcherType;
import java.util.Arrays;

@Slf4j
@Timed
@Configuration
public class WebConfiguration {

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

}
