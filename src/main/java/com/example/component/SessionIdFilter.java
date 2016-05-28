package com.example.component;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

@Slf4j
public class SessionIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String sessionId = Optional.ofNullable(request.getSession()).map(HttpSession::getId).orElse("{no session}");
        MDC.put("sid", sessionId);
        log.info("add sessionId: {}, to logging context {}", sessionId);

        filterChain.doFilter(request, response);
    }

}
