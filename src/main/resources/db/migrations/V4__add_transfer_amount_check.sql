ALTER TABLE transfers
    ADD CONSTRAINT chk_amount_non_negative CHECK (amount >= 0);

CREATE OR REPLACE FUNCTION check_balance()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.amount > (SELECT balance FROM accounts WHERE id = NEW.from_account_id) THEN
        RAISE EXCEPTION 'Insufficient balance for account %', NEW.from_account_id;
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_balance
    BEFORE INSERT OR UPDATE ON transfers
                         FOR EACH ROW EXECUTE FUNCTION check_balance();