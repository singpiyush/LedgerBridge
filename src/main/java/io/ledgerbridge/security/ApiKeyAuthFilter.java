package io.ledgerbridge.security;

import io.jsonwebtoken.Claims;
import io.ledgerbridge.model.entity.ApiKey;
import io.ledgerbridge.repository.ApiKeyRepository;
import io.ledgerbridge.util.RequestContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private final ApiKeyRepository apiKeyRepository;
    private final JwtService jwtService;

    public ApiKeyAuthFilter(ApiKeyRepository apiKeyRepository, JwtService jwtService) {
        this.apiKeyRepository = apiKeyRepository;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        String requestId = "req_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        RequestContext.setRequestId(requestId);
        response.setHeader("X-Request-Id", requestId);

        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                authenticateToken(token);
            }
            filterChain.doFilter(request, response);
        } finally {
            RequestContext.clear();
        }
    }

    private void authenticateToken(String token) {
        // Try JWT first
        try {
            Claims claims = jwtService.parseToken(token);
            String accountId = claims.getSubject();
            RequestContext.setAccountId(accountId);
            setAuthentication(accountId);
            return;
        } catch (Exception ignored) {
            // Not a JWT, try API key
        }

        // Try API key
        if (token.startsWith("lb_live_") || token.startsWith("lb_sandbox_")) {
            String hash = sha256(token);
            apiKeyRepository.findByKeyHashAndActiveTrue(hash).ifPresent(apiKey -> {
                apiKey.setLastUsedAt(Instant.now());
                apiKeyRepository.save(apiKey);
                RequestContext.setAccountId(apiKey.getAccountId());
                setAuthentication(apiKey.getAccountId());
            });
        }
    }

    private void setAuthentication(String accountId) {
        var auth = new UsernamePasswordAuthenticationToken(
                accountId, null, List.of(new SimpleGrantedAuthority("ROLE_API")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
