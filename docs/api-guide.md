# LedgerBridge API Guide

Base URL: `https://api.ledgerbridge.io/v1`
Sandbox: `https://sandbox.ledgerbridge.io/v1`

## Authentication

Every request must include your API key in the `Authorization` header:

```
Authorization: Bearer lb_live_xxxxxxxxxxxxxx
```

Sandbox keys start with `lb_sandbox_`.

## Quick Start

### 1. Create a connection (link a user's email)

```bash
curl -X POST https://api.ledgerbridge.io/v1/connections \
  -H "Authorization: Bearer lb_live_xxxxx" \
  -H "Content-Type: application/json" \
  -d '{
    "user_id": "usr_k8m2n4",
    "provider": "gmail",
    "type": "email",
    "redirect_uri": "https://yourapp.com/callback"
  }'
```

Response:
```json
{
  "id": "conn_a1b2c3",
  "user_id": "usr_k8m2n4",
  "provider": "gmail",
  "type": "email",
  "status": "pending",
  "redirect_url": "https://accounts.google.com/o/oauth2/auth?...",
  "created_at": "2026-03-15T10:00:00Z"
}
```

Redirect the user to `redirect_url` to complete OAuth.

### 2. Trigger an email scan

```bash
curl -X POST https://api.ledgerbridge.io/v1/ingestion/email-scan \
  -H "Authorization: Bearer lb_live_xxxxx" \
  -H "Content-Type: application/json" \
  -d '{
    "connection_id": "conn_a1b2c3",
    "since": "2025-01-01"
  }'
```

Response:
```json
{
  "id": "job_x1y2z3",
  "type": "email_scan",
  "status": "queued",
  "progress": 0,
  "created_at": "2026-03-15T10:01:00Z"
}
```

### 3. Upload a document directly

```bash
curl -X POST https://api.ledgerbridge.io/v1/ingestion/upload \
  -H "Authorization: Bearer lb_live_xxxxx" \
  -F "user_id=usr_k8m2n4" \
  -F "file=@contract_note.pdf" \
  -F "broker=zerodha" \
  -F "document_type=contract_note"
```

### 4. Poll job status

```bash
curl https://api.ledgerbridge.io/v1/jobs/job_x1y2z3 \
  -H "Authorization: Bearer lb_live_xxxxx"
```

### 5. Get transactions

```bash
curl "https://api.ledgerbridge.io/v1/users/usr_k8m2n4/transactions?broker=zerodha&start_date=2026-01-01&limit=50" \
  -H "Authorization: Bearer lb_live_xxxxx"
```

Response:
```json
{
  "data": [
    {
      "id": "txn_m3n4o5",
      "user_id": "usr_k8m2n4",
      "asset": {
        "id": "ast_t1u2v3",
        "symbol": "TCS",
        "name": "Tata Consultancy Services Ltd",
        "exchange": "NSE"
      },
      "quantity": 5,
      "price": 3400.00,
      "total_value": 17000.00,
      "side": "buy",
      "date": "2026-03-15",
      "broker": "zerodha",
      "exchange": "NSE",
      "created_at": "2026-03-15T10:05:00Z"
    }
  ],
  "pagination": {
    "has_more": true,
    "next_cursor": "txn_a2b3c4",
    "total_count": 142
  }
}
```

### 6. Get holdings

```bash
curl "https://api.ledgerbridge.io/v1/users/usr_k8m2n4/holdings" \
  -H "Authorization: Bearer lb_live_xxxxx"
```

### 7. Get portfolio summary

```bash
curl "https://api.ledgerbridge.io/v1/users/usr_k8m2n4/portfolio" \
  -H "Authorization: Bearer lb_live_xxxxx"
```

Response:
```json
{
  "user_id": "usr_k8m2n4",
  "total_invested": 500000.00,
  "current_value": 575000.00,
  "total_gain": 75000.00,
  "total_gain_pct": 15.0,
  "realized_gain": 20000.00,
  "unrealized_gain": 55000.00,
  "holdings_count": 12,
  "brokers": ["zerodha", "upstox"],
  "sector_allocation": [
    {"name": "Information Technology", "value": 200000.00, "percentage": 34.78},
    {"name": "Banking", "value": 150000.00, "percentage": 26.09}
  ],
  "as_of": "2026-03-15T10:05:00Z"
}
```

### 8. Get capital gains report

```bash
curl "https://api.ledgerbridge.io/v1/users/usr_k8m2n4/portfolio/gains?financial_year=2025-26" \
  -H "Authorization: Bearer lb_live_xxxxx"
```

## Handling Reviews

When parser confidence is low, the ingestion status will be `review_required`.
Fetch the pending items and let the user confirm:

```bash
# Get review items
curl https://api.ledgerbridge.io/v1/ingestion/ing_a1b2c3d4/review \
  -H "Authorization: Bearer lb_live_xxxxx"

# Submit review
curl -X POST https://api.ledgerbridge.io/v1/ingestion/ing_a1b2c3d4/review \
  -H "Authorization: Bearer lb_live_xxxxx" \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      {"id": "rev_item_01", "action": "accept"},
      {"id": "rev_item_02", "action": "correct", "corrected_transaction": {
        "symbol": "INFY",
        "exchange": "NSE",
        "quantity": 10,
        "price": 1450.00,
        "side": "buy",
        "date": "2026-03-10",
        "broker": "zerodha"
      }}
    ]
  }'
```

## Webhooks

Subscribe to events instead of polling:

```bash
curl -X POST https://api.ledgerbridge.io/v1/webhooks \
  -H "Authorization: Bearer lb_live_xxxxx" \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://yourapp.com/webhooks/ledgerbridge",
    "events": [
      "ingestion.completed",
      "ingestion.review_required",
      "holdings.updated"
    ]
  }'
```

### Webhook payload example

```json
{
  "id": "evt_a1b2c3",
  "type": "ingestion.completed",
  "created_at": "2026-03-15T10:05:00Z",
  "data": {
    "ingestion_id": "ing_a1b2c3d4",
    "user_id": "usr_k8m2n4",
    "transactions_extracted": 8,
    "status": "completed"
  }
}
```

### Verifying signatures

```python
import hmac
import hashlib

def verify_signature(payload_body, signature, secret):
    expected = hmac.new(
        secret.encode(),
        payload_body,
        hashlib.sha256
    ).hexdigest()
    return hmac.compare_digest(f"sha256={expected}", signature)
```

## Pagination

All list endpoints use cursor-based pagination:

```bash
# First page
curl "https://api.ledgerbridge.io/v1/users/usr_k8m2n4/transactions?limit=25"

# Next page (use next_cursor from previous response)
curl "https://api.ledgerbridge.io/v1/users/usr_k8m2n4/transactions?limit=25&cursor=txn_abc123"
```

## Error Handling

All errors follow a consistent format:

```json
{
  "error": {
    "type": "validation_error",
    "message": "broker: must be one of [zerodha, upstox, groww, auto]",
    "param": "broker",
    "request_id": "req_abc123def456"
  }
}
```

Error types: `authentication_error`, `authorization_error`, `validation_error`,
`not_found`, `rate_limit_error`, `conflict`, `server_error`.

## Rate Limits

When rate limited, you'll receive a `429` response with a `Retry-After` header:

```
HTTP/1.1 429 Too Many Requests
Retry-After: 30
X-RateLimit-Limit: 60
X-RateLimit-Remaining: 0
X-RateLimit-Reset: 1710500430
```

## Idempotency

For `POST` and `PUT` requests, include an `Idempotency-Key` header to safely retry:

```bash
curl -X POST https://api.ledgerbridge.io/v1/ingestion/upload \
  -H "Authorization: Bearer lb_live_xxxxx" \
  -H "Idempotency-Key: upload-user123-2026-03-15-001" \
  -F "user_id=usr_k8m2n4" \
  -F "file=@contract_note.pdf" \
  -F "broker=zerodha"
```

Keys are valid for 24 hours. Replaying a request with the same key returns the original response.
