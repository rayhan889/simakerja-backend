package com.rynrama.simakerjabackend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;
import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MdcRequestFilter extends OncePerRequestFilter {

    private static final String TRACE_ID = "traceId";
    private static final String USER_ID  = "userId";
    private static final String REQUEST_PATH = "path";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String traceId = Optional
                    .ofNullable(request.getHeader("X-Trace-Id"))
                    .orElse(UUID.randomUUID().toString().replace("-", "").substring(0, 12));

            MDC.put(TRACE_ID, traceId);
            MDC.put(REQUEST_PATH, request.getRequestURI());

            Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                    .map(auth -> auth.getName())
                    .ifPresent(user -> MDC.put(USER_ID, user));

            response.setHeader("X-Trace-Id", traceId);

            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
