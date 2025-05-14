ALTER TABLE transfers
    ADD CONSTRAINT chk_amount_non_negative CHECK (amount >= 0);

CREATE
OR REPLACE FUNCTION check_balance()
RETURNS TRIGGER AS $$
DECLARE
acc RECORD;
BEGIN
SELECT balance, hold
INTO acc
FROM accounts
WHERE id = NEW.from_account_id;

IF
NEW.amount > (acc.balance - acc.hold) THEN
        RAISE EXCEPTION 'Insufficient effective balance (balance - hold) for account %', NEW.from_account_id;
END IF;

RETURN NEW;
END;
$$
LANGUAGE plpgsql;
