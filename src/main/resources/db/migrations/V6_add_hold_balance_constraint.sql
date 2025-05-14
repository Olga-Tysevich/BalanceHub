ALTER TABLE accounts ADD CONSTRAINT chk_balance_hold CHECK (balance >= hold);
ALTER TABLE accounts ADD CONSTRAINT chk_bonus_balance_hold CHECK (bonus_balance >= bonus_hold);
ALTER TABLE accounts ADD CONSTRAINT chk_total_effective_balance CHECK (
    (balance - hold) + (bonus_balance - bonus_hold) >= 0
    );
