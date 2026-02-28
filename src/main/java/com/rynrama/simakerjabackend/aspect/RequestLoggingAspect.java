package com.rynrama.simakerjabackend.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class RequestLoggingAspect {

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object logRequest(ProceedingJoinPoint pjp) throws Throwable {
        String method = pjp.getSignature().toShortString();
        long start = System.currentTimeMillis();

        try {
            Object result = pjp.proceed();
            long elapsed = System.currentTimeMillis() - start;
            log.info("Request completed. method={}, durationMs={}", method, elapsed);
            return result;

        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - start;
            log.error("Request failed. method={}, durationMs={}", method, elapsed, e);
            throw e;
        }
    }
}
