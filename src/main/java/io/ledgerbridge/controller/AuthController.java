package io.ledgerbridge.controller;

import io.ledgerbridge.model.dto.ErrorResponse;
import io.ledgerbridge.model.dto.TokenRequest;
import io.ledgerbridge.model.dto.TokenResponse;
import io.ledgerbridge.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@Tag(name = "Authentication")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "Exchange credentials for an access token",
            description = """
                    Returns a short-lived access token (JWT) in exchange for an API key or authorization code.
                    Use this when you need a scoped, time-limited token for a specific user session.

                    **Grant types:**
                    - `api_key` — Exchange your API key for a JWT. Provide the `api_key` field.
                    - `authorization_code` — Exchange an OAuth authorization code. Provide `code` and `redirect_uri`.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token issued successfully",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirements // No auth required for token exchange
    @PostMapping("/token")
    public ResponseEntity<TokenResponse> createToken(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Token exchange request",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "grant_type": "api_key",
                              "api_key": "lb_live_xxxxxxxxxxxxxx"
                            }
                            """)))
            @Valid @RequestBody TokenRequest request) {
        TokenResponse response = authService.createToken(request);
        return ResponseEntity.ok(response);
    }
}
