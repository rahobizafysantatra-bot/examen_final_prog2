INSERT INTO accounts (id, account_type)
VALUES
    ('account-standard-001', 'STANDARD'),
    ('account-premium-001', 'PREMIUM'),
    ('account-gold-001', 'GOLD');

INSERT INTO transactions (
    id,
    created_at,
    transaction_type,
    amount,
    reason,
    account_id
)
VALUES
    (
        'transaction-001',
        '2026-08-01T09:00:00Z',
        'IN',
        1500.00,
        'Monthly salary',
        'account-standard-001'
    ),
    (
        'transaction-002',
        '2026-08-02T14:30:00Z',
        'OUT',
        120.50,
        'Groceries',
        'account-standard-001'
    ),
    (
        'transaction-003',
        '2026-08-03T10:15:00Z',
        'IN',
        2500.00,
        'Project payment',
        'account-premium-001'
    ),
    (
        'transaction-004',
        '2026-08-04T18:45:00Z',
        'OUT',
        350.00,
        'Hotel booking',
        'account-premium-001'
    ),
    (
        'transaction-005',
        '2026-08-05T08:20:00Z',
        'IN',
        5000.00,
        'Investment return',
        'account-gold-001'
    ),
    (
        'transaction-006',
        '2026-08-06T12:00:00Z',
        'OUT',
        900.00,
        'Office equipment',
        'account-gold-001'
    );
