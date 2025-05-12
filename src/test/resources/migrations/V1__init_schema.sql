CREATE SEQUENCE user_id_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE USERS
(
    ID            BIGINT PRIMARY KEY,
    NAME          VARCHAR(500),
    DATE_OF_BIRTH DATE,
    PASSWORD      VARCHAR(500)
);

CREATE SEQUENCE account_id_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE ACCOUNTS
(
    ID      BIGINT PRIMARY KEY,
    USER_ID BIGINT NOT NULL,
    BALANCE DECIMAL(19, 2) NOT NULL CHECK (BALANCE >= 0),
    HOLD DECIMAL(19, 2) NOT NULL CHECK (HOLD >= 0),
    CONSTRAINT FK_ACCOUNT_USER FOREIGN KEY (USER_ID) REFERENCES USERS (ID)
);

CREATE SEQUENCE email_data_id_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE EMAIL_DATA
(
    ID      BIGINT PRIMARY KEY,
    USER_ID BIGINT NOT NULL,
    EMAIL   VARCHAR(200) UNIQUE,
    CONSTRAINT FK_EMAIL_USER FOREIGN KEY (USER_ID) REFERENCES USERS (ID)
);

CREATE SEQUENCE phone_data_id_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE PHONE_DATA
(
    ID      BIGINT PRIMARY KEY,
    USER_ID BIGINT NOT NULL,
    PHONE_NUMBER   VARCHAR(13) UNIQUE,
    CONSTRAINT FK_PHONE_USER FOREIGN KEY (USER_ID) REFERENCES USERS (ID)
);

CREATE SEQUENCE transfer_id_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE transfers
(
    id             BIGINT PRIMARY KEY DEFAULT nextval('transfer_id_seq'),
    from_account_id BIGINT NOT NULL,
    to_account_id   BIGINT NOT NULL,
    amount         DECIMAL(19, 2) NOT NULL,
    status         VARCHAR(50) NOT NULL,
    created_at     TIMESTAMP NOT NULL,
    confirmed_at   TIMESTAMP,
    version        BIGINT,
    CONSTRAINT fk_transfer_from_account FOREIGN KEY (from_account_id) REFERENCES accounts (id),
    CONSTRAINT fk_transfer_to_account   FOREIGN KEY (to_account_id) REFERENCES accounts (id)
);

CREATE INDEX idx_transfers_from_account ON transfers(from_account_id);
CREATE INDEX idx_transfers_to_account ON transfers(to_account_id);
CREATE INDEX idx_transfers_status ON transfers(status);
