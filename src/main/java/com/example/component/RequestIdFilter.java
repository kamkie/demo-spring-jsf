package com.example.component;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

import static javax.servlet.DispatcherType.*;

@Slf4j
@ConditionalOnProperty(name = "logging.custom.request.enable")
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
@WebFilter(urlPatterns = {"/", "/*"}, asyncSupported = true, dispatcherTypes = {REQUEST, ASYNC, ERROR, FORWARD, INCLUDE})
public class RequestIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            UUID uid = UUID.randomUUID();
            long shortenedId = uid.getMostSignificantBits();
            String id = Long.toString(shortenedId).substring(1, 7);
            MDC.put("rid", id);
            log.info("add request id to logging context {}", id);

            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("rid");
            MDC.remove("sid");
            MDC.remove("userName");
        }
    }

}
