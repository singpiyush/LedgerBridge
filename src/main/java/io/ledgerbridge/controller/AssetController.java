package io.ledgerbridge.controller;

import io.ledgerbridge.model.dto.AssetResponse;
import io.ledgerbridge.model.dto.PaginatedResponse;
import io.ledgerbridge.service.AssetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/assets")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<AssetResponse>> listAssets(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "exchange", required = false) String exchange,
            @RequestParam(value = "sector", required = false) String sector,
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "limit", defaultValue = "25") int limit) {
        return ResponseEntity.ok(assetService.listAssets(q, exchange, sector, cursor, Math.min(limit, 100)));
    }

    @GetMapping("/{asset_id}")
    public ResponseEntity<AssetResponse> getAsset(
            @PathVariable("asset_id") String assetId) {
        return ResponseEntity.ok(assetService.getAsset(assetId));
    }
}
