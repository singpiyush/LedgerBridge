package io.ledgerbridge.controller;

import io.ledgerbridge.model.dto.AssetResponse;
import io.ledgerbridge.model.dto.ErrorResponse;
import io.ledgerbridge.model.dto.PaginatedResponse;
import io.ledgerbridge.service.AssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/assets")
@Tag(name = "Assets")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @Operation(
            summary = "Search the security master",
            description = """
                    Search for assets by symbol or name, optionally filtered by exchange and sector.

                    The security master contains metadata for all supported financial instruments
                    including equities, ETFs, mutual funds, bonds, and commodities.

                    **Example:** `GET /v1/assets?q=tata&exchange=NSE` returns all NSE-listed assets
                    with "tata" in their name or symbol.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Assets list")
    })
    @GetMapping
    public ResponseEntity<PaginatedResponse<AssetResponse>> listAssets(
            @Parameter(description = "Search by symbol or name (case-insensitive)", example = "TCS")
            @RequestParam(value = "q", required = false) String q,
            @Parameter(description = "Filter by exchange", example = "NSE")
            @RequestParam(value = "exchange", required = false) String exchange,
            @Parameter(description = "Filter by sector", example = "Information Technology")
            @RequestParam(value = "sector", required = false) String sector,
            @Parameter(description = "Pagination cursor from a previous response")
            @RequestParam(value = "cursor", required = false) String cursor,
            @Parameter(description = "Number of items to return (max 100)", example = "25")
            @RequestParam(value = "limit", defaultValue = "25") int limit) {
        return ResponseEntity.ok(assetService.listAssets(q, exchange, sector, cursor, Math.min(limit, 100)));
    }

    @Operation(
            summary = "Get asset details",
            description = "Retrieve full metadata for a specific asset including ISIN, sector, industry, and listing details."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Asset details",
                    content = @Content(schema = @Schema(implementation = AssetResponse.class))),
            @ApiResponse(responseCode = "404", description = "Asset not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{asset_id}")
    public ResponseEntity<AssetResponse> getAsset(
            @Parameter(description = "Asset ID", example = "ast_t1u2v3", required = true)
            @PathVariable("asset_id") String assetId) {
        return ResponseEntity.ok(assetService.getAsset(assetId));
    }
}
