package com.example.component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

import static jakarta.servlet.DispatcherType.ASYNC;
import static jakarta.servlet.DispatcherType.ERROR;
import static jakarta.servlet.DispatcherType.FORWARD;
import static jakarta.servlet.DispatcherType.INCLUDE;
import static jakarta.servlet.DispatcherType.REQUEST;

@Slf4j
@ConditionalOnProperty(name = "logging.custom.session.enable")
@Order(SecurityProperties.DEFAULT_FILTER_ORDER + 102)
@Component
@WebFilter(urlPatterns = {"/", "/*"}, asyncSupported = true, dispatcherTypes = {REQUEST, ASYNC, ERROR, FORWARD, INCLUDE})
public class SessionIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String sessionId = Optional.ofNullable(request.getSession()).map(HttpSession::getId).orElse("{no session}");
        MDC.put("sid", sessionId);
        log.info("add sessionId: {}, to logging context", sessionId);

        filterChain.doFilter(request, response);
    }

}
