-- ============================================================
-- Seed data: currencies, receivable types and an initial rate.
-- ============================================================

INSERT INTO currency (code, name) VALUES
    ('BRL', 'Real Brasileiro'),
    ('USD', 'Dolar Americano'),
    ('EUR', 'Euro'),
    ('ARS', 'Peso Argentino'),
    ('MXN', 'Peso Mexicano'),
    ('CLP', 'Peso Chileno'),
    ('COP', 'Peso Colombiano');

INSERT INTO receivable_type (name, monthly_spread) VALUES
    ('DUPLICATA_MERCANTIL', 0.015),
    ('CHEQUE_PRE_DATADO',   0.025);

-- Initial USD -> BRL reference rate (mocked)
INSERT INTO exchange_rate (base_currency_id, quote_currency_id, rate, valid_at)
SELECT b.id, q.id, 5.40000000, now()
FROM currency b, currency q
WHERE b.code = 'USD' AND q.code = 'BRL';
