package com.example.config;

import javax.servlet.Filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import com.example.annotation.Timed;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Timed
@Configuration
public class WebConfiguration {

	@Bean
	public Filter logFilter() {
		CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
		filter.setIncludeQueryString(true);
		filter.setIncludePayload(true);
		filter.setMaxPayloadLength(5120);
		return filter;
	}

}
