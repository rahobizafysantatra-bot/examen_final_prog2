CREATE TABLE IF NOT EXISTS accounts (
    id VARCHAR(255) PRIMARY KEY,
    account_type VARCHAR(20) NOT NULL,
    CONSTRAINT chk_accounts_account_type
        CHECK (account_type IN ('STANDARD', 'PREMIUM', 'GOLD'))
);

CREATE TABLE IF NOT EXISTS transactions (
    id VARCHAR(255) PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL,
    transaction_type VARCHAR(3) NOT NULL,
    amount NUMERIC NOT NULL,
    reason VARCHAR(255) NOT NULL,
    account_id VARCHAR(255) NOT NULL,
    CONSTRAINT chk_transactions_transaction_type
        CHECK (transaction_type IN ('IN', 'OUT')),
    CONSTRAINT chk_transactions_amount
        CHECK (amount > 0),
    CONSTRAINT fk_transactions_account
        FOREIGN KEY (account_id)
        REFERENCES accounts (id)
        ON DELETE RESTRICT
);

INSERT INTO accounts (id, account_type)
VALUES
    ('account-standard-001', 'STANDARD'),
    ('account-premium-001', 'PREMIUM'),
    ('account-gold-001', 'GOLD')
ON CONFLICT (id) DO NOTHING;

INSERT INTO transactions
    (id, created_at, transaction_type, amount, reason, account_id)
VALUES
    (
        'transaction-seed-001',
        '2026-01-10T09:00:00Z',
        'IN',
        2500.00,
        'Monthly salary',
        'account-standard-001'
    ),
    (
        'transaction-seed-002',
        '2026-01-11T12:30:00Z',
        'OUT',
        145.75,
        'Grocery shopping',
        'account-standard-001'
    ),
    (
        'transaction-seed-003',
        '2026-01-12T15:45:00Z',
        'IN',
        600.00,
        'Freelance payment',
        'account-premium-001'
    ),
    (
        'transaction-seed-004',
        '2026-01-13T08:20:00Z',
        'OUT',
        80.00,
        'Public transport pass',
        'account-premium-001'
    )
ON CONFLICT (id) DO NOTHING;
