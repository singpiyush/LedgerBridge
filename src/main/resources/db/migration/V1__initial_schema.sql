-- LedgerBridge Initial Schema

-- API Keys
CREATE TABLE api_keys (
    id VARCHAR(36) PRIMARY KEY,
    key_hash VARCHAR(64) NOT NULL UNIQUE,
    key_prefix VARCHAR(12) NOT NULL,
    name VARCHAR(255) NOT NULL,
    account_id VARCHAR(36) NOT NULL,
    plan VARCHAR(20) NOT NULL DEFAULT 'free',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    last_used_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_api_keys_account ON api_keys(account_id);
CREATE INDEX idx_api_keys_hash ON api_keys(key_hash) WHERE active = TRUE;

-- Connections
CREATE TABLE connections (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    provider VARCHAR(20) NOT NULL,
    type VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    redirect_url VARCHAR(2048),
    access_token_encrypted VARCHAR(2048),
    refresh_token_encrypted VARCHAR(2048),
    credentials_encrypted VARCHAR(2048),
    last_synced_at TIMESTAMPTZ,
    error_code VARCHAR(50),
    error_message VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_connections_user_id ON connections(user_id);
CREATE INDEX idx_connections_status ON connections(status);

-- Assets (Security Master)
CREATE TABLE assets (
    id VARCHAR(36) PRIMARY KEY,
    symbol VARCHAR(20) NOT NULL,
    name VARCHAR(255) NOT NULL,
    exchange VARCHAR(20) NOT NULL,
    isin VARCHAR(12) UNIQUE,
    sector VARCHAR(100),
    industry VARCHAR(100),
    currency VARCHAR(3) NOT NULL DEFAULT 'INR',
    country VARCHAR(2) NOT NULL DEFAULT 'IN',
    asset_type VARCHAR(20) NOT NULL DEFAULT 'EQUITY',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_assets_symbol ON assets(symbol);
CREATE INDEX idx_assets_exchange ON assets(exchange);
CREATE UNIQUE INDEX idx_assets_symbol_exchange ON assets(symbol, exchange);

-- Transactions
CREATE TABLE transactions (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    asset_id VARCHAR(36) NOT NULL REFERENCES assets(id),
    quantity NUMERIC(18,6) NOT NULL,
    price NUMERIC(18,4) NOT NULL,
    total_value NUMERIC(18,4) NOT NULL,
    side VARCHAR(4) NOT NULL,
    date DATE NOT NULL,
    broker VARCHAR(50) NOT NULL,
    exchange VARCHAR(20) NOT NULL,
    ingestion_id VARCHAR(36),
    brokerage NUMERIC(12,4),
    stt NUMERIC(12,4),
    gst NUMERIC(12,4),
    stamp_duty NUMERIC(12,4),
    total_charges NUMERIC(12,4),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_txn_user_id ON transactions(user_id);
CREATE INDEX idx_txn_user_date ON transactions(user_id, date);
CREATE INDEX idx_txn_user_broker ON transactions(user_id, broker);
CREATE INDEX idx_txn_asset_id ON transactions(asset_id);

-- Holdings
CREATE TABLE holdings (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    asset_id VARCHAR(36) NOT NULL REFERENCES assets(id),
    quantity NUMERIC(18,6) NOT NULL,
    avg_price NUMERIC(18,4) NOT NULL,
    invested_value NUMERIC(18,4) NOT NULL,
    broker VARCHAR(50) NOT NULL,
    exchange VARCHAR(20) NOT NULL,
    first_bought_at DATE,
    last_updated TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_holdings_user_id ON holdings(user_id);
CREATE UNIQUE INDEX idx_holdings_user_asset ON holdings(user_id, asset_id, broker);

-- Jobs
CREATE TABLE jobs (
    id VARCHAR(36) PRIMARY KEY,
    type VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'QUEUED',
    progress INT NOT NULL DEFAULT 0,
    result_data TEXT,
    user_id VARCHAR(36),
    completed_at TIMESTAMPTZ,
    error_code VARCHAR(50),
    error_message VARCHAR(1000),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_jobs_status ON jobs(status);

-- Ingestions
CREATE TABLE ingestions (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    source VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'QUEUED',
    broker VARCHAR(50),
    document_type VARCHAR(30),
    transactions_extracted INT,
    transactions_confirmed INT,
    confidence DOUBLE PRECISION,
    job_id VARCHAR(36),
    connection_id VARCHAR(36),
    file_path VARCHAR(500),
    completed_at TIMESTAMPTZ,
    error_code VARCHAR(50),
    error_message VARCHAR(1000),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_ingestions_user_id ON ingestions(user_id);
CREATE INDEX idx_ingestions_status ON ingestions(status);

-- Review Items
CREATE TABLE review_items (
    id VARCHAR(36) PRIMARY KEY,
    ingestion_id VARCHAR(36) NOT NULL,
    extracted_data TEXT NOT NULL,
    alternatives TEXT,
    source_snippet TEXT,
    confidence DOUBLE PRECISION,
    action VARCHAR(10),
    corrected_data TEXT,
    resolved BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_review_items_ingestion ON review_items(ingestion_id);

-- Webhook Subscriptions
CREATE TABLE webhook_subscriptions (
    id VARCHAR(36) PRIMARY KEY,
    account_id VARCHAR(36) NOT NULL,
    url VARCHAR(2048) NOT NULL,
    events VARCHAR(500) NOT NULL,
    secret_hash VARCHAR(64) NOT NULL,
    secret_encrypted VARCHAR(500) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_webhooks_account ON webhook_subscriptions(account_id);

-- Idempotency Records
CREATE TABLE idempotency_records (
    id BIGSERIAL PRIMARY KEY,
    idempotency_key VARCHAR(255) NOT NULL,
    account_id VARCHAR(36) NOT NULL,
    response_status INT NOT NULL,
    response_body TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMPTZ NOT NULL
);

CREATE UNIQUE INDEX idx_idempotency_key ON idempotency_records(idempotency_key, account_id);
CREATE INDEX idx_idempotency_expires ON idempotency_records(expires_at);
