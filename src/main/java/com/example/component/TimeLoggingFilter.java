package com.example.component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.component.ExecutionTimeLogger.formatDuration;
import static jakarta.servlet.DispatcherType.ASYNC;
import static jakarta.servlet.DispatcherType.ERROR;
import static jakarta.servlet.DispatcherType.FORWARD;
import static jakarta.servlet.DispatcherType.INCLUDE;
import static jakarta.servlet.DispatcherType.REQUEST;

@Slf4j
@ConditionalOnProperty(name = "logging.custom.time.enable")
@Order(Ordered.HIGHEST_PRECEDENCE + 105)
@Component
@WebFilter(urlPatterns = {"/", "/*"}, asyncSupported = true, dispatcherTypes = {REQUEST, ASYNC, ERROR, FORWARD, INCLUDE})
public class TimeLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long start = System.nanoTime();
        filterChain.doFilter(request, response);

        if (log.isInfoEnabled()) {
            logTime(request, start);
        }
    }

    @SuppressWarnings("PMD.GuardLogStatement")
    private void logTime(HttpServletRequest request, long start) {
        long nanos = System.nanoTime() - start;
        String duration = formatDuration(nanos);
        String headers = getHeadersAsString(request);

        if (log.isDebugEnabled()) {
            log.debug("request: url: {}, time: {} ({}), params: {}, headers: {}", request.getRequestURL(), duration,
                    nanos, createMessage(request, "", ""), headers);
        } else {
            log.info("request: url: {}, time: {} ({}), params: {}", request.getRequestURL(), duration,
                    nanos, createMessage(request, "", ""));
        }
    }

    private String getHeadersAsString(HttpServletRequest request) {
        Function<String, Pair<String, Enumeration<String>>> mapHeaderNameToHeaders = s -> Pair
                .of(s, request.getHeaders(s));
        return Collections.list(request.getHeaderNames()).stream()
                .map(mapHeaderNameToHeaders)
                .flatMap(TimeLoggingFilter::mapPairToStrings)
                .collect(Collectors.joining("\n"));
    }

    private String createMessage(HttpServletRequest request, String prefix, String suffix) {
        StringBuilder msg = new StringBuilder();
        msg.append(prefix).append("uri=").append(request.getRequestURI());

        String queryString = request.getQueryString();
        if (queryString != null) {
            msg.append('?').append(queryString);
        }

        msg.append(";client=").append(request.getRemoteAddr()).append(suffix);
        return msg.toString();
    }

    private static Stream<? extends String> mapPairToStrings(Pair<String, Enumeration<String>> header) {
        return Collections.list(header.getSecond()).stream().map(s -> header.getFirst() + "= " + s);
    }

}
