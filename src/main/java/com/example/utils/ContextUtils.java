package com.example.utils;

import jakarta.servlet.ServletContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

public interface ContextUtils {

    static WebApplicationContext getApplicationContext() {
        ServletContext servletContext = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getServletContext();
        return WebApplicationContextUtils.findWebApplicationContext(servletContext);
    }
}
