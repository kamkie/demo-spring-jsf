package com.example.config;

import java.util.Arrays;

import javax.servlet.DispatcherType;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import com.example.annotation.Timed;
import com.example.component.TimeLoggingFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Timed
@Configuration
public class WebConfiguration {

	@Bean
	public FilterRegistrationBean timeLoggingFilterRegistration() {
		FilterRegistrationBean registration = new FilterRegistrationBean(new TimeLoggingFilter());
		registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
		registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC, DispatcherType.ERROR,
				DispatcherType.FORWARD, DispatcherType.INCLUDE);
		registration.setEnabled(true);
		registration.setUrlPatterns(Arrays.asList("/*"));

		return registration;
	}

}
