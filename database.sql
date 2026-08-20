DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS accounts;

DROP TYPE IF EXISTS transaction_type;
DROP TYPE IF EXISTS account_type;

CREATE TYPE account_type AS ENUM (
    'STANDARD',
    'PREMIUM',
    'GOLD'
);

CREATE TYPE transaction_type AS ENUM (
    'IN',
    'OUT'
);

CREATE TABLE accounts (
    id VARCHAR PRIMARY KEY,
    account_type account_type NOT NULL
);

CREATE TABLE transactions (
    id VARCHAR PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL,
    transaction_type transaction_type NOT NULL,
    amount NUMERIC NOT NULL,
    reason VARCHAR NOT NULL,
    account_id VARCHAR NOT NULL,
    CONSTRAINT fk_transactions_account
        FOREIGN KEY (account_id)
        REFERENCES accounts(id)
);
