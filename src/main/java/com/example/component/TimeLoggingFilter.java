package com.example.component;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.data.util.Pair;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import lombok.extern.slf4j.Slf4j;

import static com.example.component.ExecutionTimeLogger.formatDuration;

@Slf4j
public class TimeLoggingFilter extends AbstractRequestLoggingFilter implements Filter {

	public TimeLoggingFilter() {
		setIncludeClientInfo(true);
		setIncludeQueryString(true);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			addRequestIdToContext();
			addSessionIdToContext(request);

			long start = System.nanoTime();
			super.doFilterInternal(request, response, filterChain);

			if (log.isInfoEnabled()) {
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
		} finally {
			MDC.remove("sid");
			MDC.remove("rid");
			MDC.remove("userName");
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

	@Override
	protected void beforeRequest(HttpServletRequest request, String message) {

	}

	@Override
	protected void afterRequest(HttpServletRequest request, String message) {

	}

	private void addSessionIdToContext(HttpServletRequest request) {
		String sid = request.getSession().getId();
		MDC.put("sid", sid);
	}

	private void addRequestIdToContext() {
		UUID uid = UUID.randomUUID();
		long shortenedId = uid.getMostSignificantBits();
		String id = Long.toString(shortenedId).substring(1, 7);
		MDC.put("rid", id);
	}

}
