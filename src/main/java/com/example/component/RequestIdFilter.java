package com.example.component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

import static jakarta.servlet.DispatcherType.ASYNC;
import static jakarta.servlet.DispatcherType.ERROR;
import static jakarta.servlet.DispatcherType.FORWARD;
import static jakarta.servlet.DispatcherType.INCLUDE;
import static jakarta.servlet.DispatcherType.REQUEST;

@Slf4j
@ConditionalOnProperty(name = "logging.custom.request.enable")
@Order(Ordered.HIGHEST_PRECEDENCE + 101)
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
