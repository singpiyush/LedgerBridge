package io.ledgerbridge.controller;

import io.ledgerbridge.model.dto.TokenRequest;
import io.ledgerbridge.model.dto.TokenResponse;
import io.ledgerbridge.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/token")
    public ResponseEntity<TokenResponse> createToken(@Valid @RequestBody TokenRequest request) {
        TokenResponse response = authService.createToken(request);
        return ResponseEntity.ok(response);
    }
}
