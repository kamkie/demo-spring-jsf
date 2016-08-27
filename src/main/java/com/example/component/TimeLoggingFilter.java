package com.example.component;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.component.ExecutionTimeLogger.formatDuration;
import static javax.servlet.DispatcherType.*;

@Slf4j
@ConditionalOnProperty(name = "logging.custom.time.enable")
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@Component
@WebFilter(urlPatterns = {"/", "/*"}, asyncSupported = true, dispatcherTypes = {REQUEST, ASYNC, ERROR, FORWARD, INCLUDE})
public class TimeLoggingFilter extends OncePerRequestFilter {

    private final int maxPayloadLength;

    public TimeLoggingFilter(@Value("${logging.custom.time.max-payload-size:50}") int maxPayloadLength) {
        this.maxPayloadLength = Math.max(10, maxPayloadLength);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            long start = System.nanoTime();
            filterChain.doFilter(request, response);

            if (log.isInfoEnabled()) {
                logTime(request, start);
            }
        } finally {
            MDC.remove("userName");
        }
    }

    private void logTime(HttpServletRequest request, long start) {
        long nanos = System.nanoTime() - start;
        String duration = formatDuration(nanos);
        String headers = getHeadersAsString(request);

        if (log.isDebugEnabled()) {
            log.debug("request: url: {}, time {}, params: {}, headers: {}", request.getRequestURL(), duration,
                    createMessage(request, "", ""), headers);
        } else {
            log.info("request: url: {}, time {}, params: {}", request.getRequestURL(), duration,
                    createMessage(request, "", ""));
        }
    }

    private String getHeadersAsString(HttpServletRequest request) {
        Function<String, Pair<String, Enumeration<String>>> mapHeaderNameToHeaders = s -> Pair
                .of(s, request.getHeaders(s));
        Function<Pair<String, Enumeration<String>>, Stream<? extends String>> mapPairToStrings = header -> Collections
                .list(header.getSecond()).stream().map(s -> header.getFirst() + "= " + s);
        return Collections.list(request.getHeaderNames()).stream().map(mapHeaderNameToHeaders).flatMap(mapPairToStrings)
                .collect(Collectors.joining("\n"));
    }


    private String createMessage(HttpServletRequest request, String prefix, String suffix) {
        StringBuilder msg = new StringBuilder();
        msg.append(prefix);
        msg.append("uri=").append(request.getRequestURI());

        String queryString = request.getQueryString();
        if (queryString != null) {
            msg.append('?').append(queryString);
        }

        String client = request.getRemoteAddr();
        if (StringUtils.hasLength(client)) {
            msg.append(";client=").append(client);
        }

        if (log.isDebugEnabled()) {
            ContentCachingRequestWrapper wrapper =
                    WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
            if (wrapper != null) {
                appendResponseBody(msg, wrapper);
            }
        }
        msg.append(suffix);
        return msg.toString();
    }

    private void appendResponseBody(StringBuilder msg, ContentCachingRequestWrapper wrapper) {
        byte[] buf = wrapper.getContentAsByteArray();
        if (buf.length > 0) {
            int length = Math.min(buf.length, maxPayloadLength);
            String payload;
            try {
                payload = new String(buf, 0, length, wrapper.getCharacterEncoding());
            } catch (UnsupportedEncodingException ex) {
                payload = "[unknown]";
                log.warn("request had unsupported encoding", ex);
            }
            msg.append(";payload=").append(payload);
        }
    }

}
