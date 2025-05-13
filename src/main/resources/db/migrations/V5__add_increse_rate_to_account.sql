ALTER TABLE ACCOUNTS
    ADD COLUMN initial_balance DECIMAL(19, 2) NOT NULL DEFAULT 0.00;

UPDATE accounts
SET initial_balance = balance;

ALTER TABLE ACCOUNTS
    ADD COLUMN bonus_balance DECIMAL(19, 2) NOT NULL DEFAULT 0.00;

SELECT setval('account_id_seq', (SELECT MAX(id) FROM ACCOUNTS), true);