CREATE TABLE currency (
    id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code VARCHAR(3)  NOT NULL UNIQUE,
    name VARCHAR(60) NOT NULL
);

CREATE TABLE receivable_type (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name           VARCHAR(60)   NOT NULL UNIQUE,
    monthly_spread NUMERIC(9, 6) NOT NULL CHECK (monthly_spread >= 0)
);

CREATE TABLE exchange_rate (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    base_currency_id  BIGINT         NOT NULL REFERENCES currency (id),
    quote_currency_id BIGINT         NOT NULL REFERENCES currency (id),
    rate              NUMERIC(18, 8) NOT NULL CHECK (rate > 0),
    valid_at          TIMESTAMP      NOT NULL DEFAULT now()
);

-- índice para buscar a última taxa de câmbio de um par
CREATE INDEX idx_exchange_rate_pair
    ON exchange_rate (base_currency_id, quote_currency_id, valid_at DESC);

CREATE TABLE settlement (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    assignor             VARCHAR(120)   NOT NULL,
    receivable_type_id   BIGINT         NOT NULL REFERENCES receivable_type (id),
    face_value           NUMERIC(18, 2) NOT NULL CHECK (face_value > 0),
    term_months          INT            NOT NULL CHECK (term_months > 0),
    base_rate            NUMERIC(9, 6)  NOT NULL,
    applied_spread       NUMERIC(9, 6)  NOT NULL,
    original_currency_id BIGINT         NOT NULL REFERENCES currency (id),
    payment_currency_id  BIGINT         NOT NULL REFERENCES currency (id),
    exchange_rate_id     BIGINT         REFERENCES exchange_rate (id),
    present_value        NUMERIC(18, 2) NOT NULL,
    net_value_paid       NUMERIC(18, 2) NOT NULL,
    created_at           TIMESTAMP      NOT NULL DEFAULT now()
);

-- índice para relatórios por período
CREATE INDEX idx_settlement_created_at ON settlement (created_at);
