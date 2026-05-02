CREATE TABLE market_quotes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ticker VARCHAR(20) NOT NULL,
    name VARCHAR(255),
    price NUMERIC(19, 2) NOT NULL,
    change_percent NUMERIC(8, 4),
    market_cap NUMERIC(24, 2),
    quote_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_market_quotes_ticker ON market_quotes(ticker);
