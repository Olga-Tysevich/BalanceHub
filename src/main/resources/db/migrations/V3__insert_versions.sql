ALTER TABLE ACCOUNTS
    ADD COLUMN version BIGINT;

UPDATE ACCOUNTS
SET version = 1
WHERE version IS NULL;

SELECT setval('account_id_seq', (SELECT MAX(id) FROM ACCOUNTS), true);