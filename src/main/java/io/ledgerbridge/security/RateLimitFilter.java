package io.ledgerbridge.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.ledgerbridge.model.dto.ErrorResponse;
import io.ledgerbridge.util.RequestContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Map<String, Long> PLAN_LIMITS = Map.of(
            "free", 60L,
            "growth", 600L,
            "enterprise", 6000L
    );

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public RateLimitFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        String accountId = RequestContext.getAccountId();
        if (accountId == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Bucket bucket = buckets.computeIfAbsent(accountId, this::createBucket);
        var probe = bucket.tryConsumeAndReturnRemaining(1);

        long limit = PLAN_LIMITS.getOrDefault("free", 60L);
        response.setHeader("X-RateLimit-Limit", String.valueOf(limit));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(probe.getRemainingTokens()));
        response.setHeader("X-RateLimit-Reset",
                String.valueOf(System.currentTimeMillis() / 1000 + 60));

        if (probe.isConsumed()) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429);
            response.setHeader("Retry-After", "60");
            response.setContentType("application/json");
            ErrorResponse error = ErrorResponse.of("rate_limit_error",
                    "Rate limit exceeded. Please retry after the period indicated in the Retry-After header.",
                    RequestContext.getRequestId());
            objectMapper.writeValue(response.getWriter(), error);
        }
    }

    private Bucket createBucket(String accountId) {
        long limit = PLAN_LIMITS.getOrDefault("free", 60L);
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(limit)
                        .refillGreedy(limit, Duration.ofMinutes(1))
                        .build())
                .build();
    }
}
