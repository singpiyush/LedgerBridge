package io.ledgerbridge.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ledgerBridgeOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LedgerBridge API")
                        .version("1.0.0")
                        .description("""
                                LedgerBridge is a financial data ingestion and normalization platform.
                                It converts unstructured financial communications (emails, PDFs, broker statements)
                                into structured portfolio data.

                                ## Authentication
                                All API requests require a Bearer token. Use your API key directly or exchange it
                                for a short-lived JWT via `POST /v1/auth/token`.

                                ```
                                Authorization: Bearer lb_live_xxxxxxxxxxxxxx
                                ```

                                ## Environments
                                | Environment | Base URL |
                                |-------------|----------|
                                | Production  | `https://api.ledgerbridge.io/v1` |
                                | Sandbox     | `https://sandbox.ledgerbridge.io/v1` |

                                ## Rate Limits
                                | Plan       | Requests/min | Burst |
                                |------------|-------------|-------|
                                | Free       | 60          | 10    |
                                | Growth     | 600         | 50    |
                                | Enterprise | 6000        | 200   |

                                Rate limit headers (`X-RateLimit-Limit`, `X-RateLimit-Remaining`, `X-RateLimit-Reset`) are included in every response.

                                ## Pagination
                                List endpoints use cursor-based pagination for stable, high-performance results:
                                ```
                                GET /v1/transactions?limit=50&cursor=txn_abc123
                                ```

                                ## Idempotency
                                Pass an `Idempotency-Key` header on POST/PUT requests to safely retry:
                                ```
                                Idempotency-Key: unique-request-id
                                ```
                                Keys are valid for 24 hours.

                                ## Webhooks
                                Subscribe to events via the API. All payloads are signed with HMAC-SHA256
                                in the `X-LedgerBridge-Signature` header.
                                """)
                        .contact(new Contact()
                                .name("LedgerBridge API Support")
                                .email("support@ledgerbridge.io")
                                .url("https://docs.ledgerbridge.io"))
                        .license(new License()
                                .name("Proprietary")))
                .externalDocs(new ExternalDocumentation()
                        .description("LedgerBridge API Documentation")
                        .url("https://docs.ledgerbridge.io"))
                .servers(List.of(
                        new Server().url("https://api.ledgerbridge.io/v1").description("Production"),
                        new Server().url("https://sandbox.ledgerbridge.io/v1").description("Sandbox"),
                        new Server().url("http://localhost:8080/v1").description("Local Development")
                ))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Use your API key (`lb_live_xxx`) directly as a bearer token, " +
                                        "or exchange it for a short-lived JWT via `POST /v1/auth/token`.")))
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .tags(List.of(
                        new Tag().name("Authentication").description("API key management and OAuth token exchange"),
                        new Tag().name("Connections").description("Link user email accounts and broker logins for data ingestion"),
                        new Tag().name("Ingestion").description("Upload documents and trigger email scans for financial data extraction"),
                        new Tag().name("Transactions").description("Normalized financial transactions across all connected brokers"),
                        new Tag().name("Holdings").description("Current portfolio holdings with cost basis and broker attribution"),
                        new Tag().name("Portfolios").description("Portfolio summaries, performance analytics, and capital gains reports"),
                        new Tag().name("Assets").description("Security master data - search and retrieve asset metadata"),
                        new Tag().name("Jobs").description("Track the status of async operations (email scans, document parsing, portfolio recomputation)"),
                        new Tag().name("Webhooks").description("Subscribe to real-time event notifications with HMAC-SHA256 signed payloads")
                ));
    }
}
