package io.ledgerbridge.service;

import io.ledgerbridge.exception.ApiException;
import io.ledgerbridge.model.dto.TokenRequest;
import io.ledgerbridge.model.dto.TokenResponse;
import io.ledgerbridge.model.entity.ApiKey;
import io.ledgerbridge.repository.ApiKeyRepository;
import io.ledgerbridge.security.JwtService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class AuthService {

    private final ApiKeyRepository apiKeyRepository;
    private final JwtService jwtService;

    public AuthService(ApiKeyRepository apiKeyRepository, JwtService jwtService) {
        this.apiKeyRepository = apiKeyRepository;
        this.jwtService = jwtService;
    }

    public TokenResponse createToken(TokenRequest request) {
        if ("api_key".equals(request.grantType())) {
            if (request.apiKey() == null || request.apiKey().isBlank()) {
                throw ApiException.validation("api_key is required for grant_type=api_key", "api_key");
            }
            String hash = sha256(request.apiKey());
            ApiKey apiKey = apiKeyRepository.findByKeyHashAndActiveTrue(hash)
                    .orElseThrow(() -> ApiException.unauthorized("Invalid API key"));

            String token = jwtService.generateToken(apiKey.getAccountId(), "read write");
            return new TokenResponse(token, "Bearer", jwtService.getExpirationSeconds(), null, "read write");
        }
        throw ApiException.validation("Unsupported grant_type: " + request.grantType(), "grant_type");
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
