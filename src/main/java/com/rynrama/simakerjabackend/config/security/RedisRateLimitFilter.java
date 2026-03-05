package com.rynrama.simakerjabackend.config.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
@Slf4j
public class RedisRateLimitFilter implements Filter {
    private final StringRedisTemplate stringRedisTemplate;
    private static final int LIMIT = 100;
    private static final long WINDOW_DURATION = 60; // in seconds

    public RedisRateLimitFilter(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException
    {
        HttpServletRequest httpRequest = (HttpServletRequest) req;
        HttpServletResponse httpResponse = (HttpServletResponse) res;
        String clientIp = httpRequest.getRemoteAddr();
        String key = "rate_limit_" + clientIp;
        Long requests = stringRedisTemplate.opsForValue().increment(key, 1);

        if (requests == 1) {
            stringRedisTemplate.expire(key, Duration.ofSeconds(WINDOW_DURATION));
        }

        if (requests > LIMIT) {
            log.warn("Request exceeds limit of " + LIMIT + " requests");
            httpResponse.setStatus(429);
            res.getWriter().flush();
            return;
        }

        chain.doFilter(req, res);
    }
}
