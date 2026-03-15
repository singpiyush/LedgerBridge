package io.ledgerbridge.util;

import io.ledgerbridge.model.dto.*;
import io.ledgerbridge.model.entity.*;

public final class EntityMapper {

    private EntityMapper() {}

    public static ConnectionResponse toConnectionResponse(Connection c) {
        ConnectionResponse.ConnectionErrorDto errorDto = null;
        if (c.getErrorCode() != null) {
            errorDto = new ConnectionResponse.ConnectionErrorDto(c.getErrorCode(), c.getErrorMessage());
        }
        return new ConnectionResponse(
                c.getId(), c.getUserId(), c.getProvider().getValue(), c.getType().getValue(),
                c.getStatus().getValue(), c.getRedirectUrl(), c.getLastSyncedAt(),
                c.getCreatedAt(), errorDto);
    }

    public static AssetRef toAssetRef(Asset a) {
        return new AssetRef(a.getId(), a.getSymbol(), a.getName(), a.getExchange());
    }

    public static AssetResponse toAssetResponse(Asset a) {
        return new AssetResponse(
                a.getId(), a.getSymbol(), a.getName(), a.getExchange(), a.getIsin(),
                a.getSector(), a.getIndustry(), a.getCurrency(), a.getCountry(),
                a.getAssetType().getValue());
    }

    public static TransactionResponse toTransactionResponse(Transaction t) {
        TransactionResponse.ChargesDto charges = null;
        if (t.getBrokerage() != null) {
            charges = new TransactionResponse.ChargesDto(
                    t.getBrokerage(), t.getStt(), t.getGst(), t.getStampDuty(), t.getTotalCharges());
        }
        return new TransactionResponse(
                t.getId(), t.getUserId(), toAssetRef(t.getAsset()),
                t.getQuantity(), t.getPrice(), t.getTotalValue(),
                t.getSide().getValue(), t.getDate(), t.getBroker(), t.getExchange(),
                t.getIngestionId(), charges, t.getCreatedAt());
    }

    public static HoldingResponse toHoldingResponse(Holding h) {
        return new HoldingResponse(
                h.getId(), h.getUserId(), toAssetRef(h.getAsset()),
                h.getQuantity(), h.getAvgPrice(), h.getInvestedValue(),
                h.getBroker(), h.getExchange(), h.getFirstBoughtAt(), h.getLastUpdated());
    }

    public static IngestionResponse toIngestionResponse(Ingestion i) {
        IngestionResponse.IngestionErrorDto errorDto = null;
        if (i.getErrorCode() != null) {
            errorDto = new IngestionResponse.IngestionErrorDto(i.getErrorCode(), i.getErrorMessage());
        }
        String docType = i.getDocumentType() != null ? i.getDocumentType().getValue() : null;
        return new IngestionResponse(
                i.getId(), i.getUserId(), i.getSource().getValue(), i.getStatus().getValue(),
                i.getBroker(), docType, i.getTransactionsExtracted(),
                i.getTransactionsConfirmed(), i.getConfidence(), i.getJobId(),
                i.getCreatedAt(), i.getCompletedAt(), errorDto);
    }

    public static JobResponse toJobResponse(Job j) {
        JobResponse.JobResult result = null;
        JobResponse.JobError error = null;
        if (j.getErrorCode() != null) {
            error = new JobResponse.JobError(j.getErrorCode(), j.getErrorMessage());
        }
        return new JobResponse(
                j.getId(), j.getType().getValue(), j.getStatus().getValue(),
                j.getProgress(), result, j.getCreatedAt(), j.getCompletedAt(), error);
    }
}
