CREATE SEQUENCE user_id_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE USERS
(
    ID               BIGINT PRIMARY KEY,
    NAME             VARCHAR(500),
    DATE_OF_BIRTHDAY DATE,
    PASSWORD         VARCHAR(500)
);

CREATE SEQUENCE account_id_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE ACCOUNTS
(
    ID      BIGINT PRIMARY KEY,
    USER_ID BIGINT NOT NULL,
    BALANCE DECIMAL,
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
    ID           BIGINT PRIMARY KEY,
    USER_ID      BIGINT NOT NULL,
    PHONE_NUMBER VARCHAR(13) UNIQUE,
    CONSTRAINT FK_PHONE_USER FOREIGN KEY (USER_ID) REFERENCES USERS (ID)
);

INSERT INTO "users" (id, name, date_of_birthday, password)
VALUES (1, 'Alice Johnson', '1985-02-10', '$2a$10$aC0AZKlIDDsj0wq9KHGeleIvCsxorMCvs1QzdvWEI2VjqvaAnjIxK'),
       (2, 'Bob Smith', '1990-07-15', '$2a$10$md8WKR8A0hDR.pJ37OUoyOmRxoXqjVeQS8PPuzhRr7zuX0WKcKbEy'),
       (3, 'Charlie Brown', '1982-12-03', '$2a$10$6ZdnvnFxR8h7a8XjeWYuLu/OIEhqcr6vBmuAmIyBzQOItuYvhgT52'),
       (4, 'Diana Prince', '1995-05-21', '$2a$10$eGFGLR.w6B0cKcG3JODDeOb7F78Fvkph3tBBu3a4UKxPoIOwmEB66');

INSERT INTO phone_data (id, user_id, phone_number)
VALUES (1, 1, '79201111101'),
       (2, 1, '79201111102'),
       (3, 2, '79202222201'),
       (4, 2, '79202222202'),
       (5, 3, '79203333301'),
       (6, 3, '79203333302'),
       (7, 4, '79204444401'),
       (8, 4, '79204444402');

INSERT INTO email_data (id, user_id, email)
VALUES (1, 1, 'alice1@test.com'),
       (2, 1, 'alice2@test.com'),
       (3, 2, 'bob1@test.com'),
       (4, 2, 'bob2@test.com'),
       (5, 3, 'charlie1@test.com'),
       (6, 3, 'charlie2@test.com'),
       (7, 4, 'diana1@test.com'),
       (8, 4, 'diana2@test.com');

INSERT INTO accounts (id, user_id, balance)
VALUES (1, 1, 1000.00),
       (2, 2, 1500.50),
       (3, 3, 900.00),
       (4, 4, 1100.25);

SELECT setval('user_id_seq', 10, true);
SELECT setval('phone_data_id_seq', 20, true);
SELECT setval('email_data_id_seq', 20, true);
SELECT setval('account_id_seq', 10, true);
