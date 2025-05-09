INSERT INTO "user" (id, name, date_of_birth, password) VALUES
    (1, 'Alice Johnson', '10.02.1985', 'passAlice1'),
    (2, 'Bob Smith', '15.07.1990', 'passBob12'),
    (3, 'Charlie Brown', '03.12.1982', 'charlieP3'),
    (4, 'Diana Prince', '21.05.1995', 'dianaPa44'),
    (5, 'Ethan Hunt', '09.09.1988', 'ethanHH55'),
    (6, 'Fiona Glenanne', '17.03.1992', 'fionaGG66'),
    (7, 'George Costanza', '25.11.1980', 'georgeC7'),
    (8, 'Hannah Abbott', '30.01.1991', 'hannahA8'),
    (9, 'Ian Malcolm', '14.08.1983', 'ianMalc9'),
    (10, 'Julia Roberts', '05.06.1987', 'juliaRo10');

INSERT INTO phone_data (id, user_id, phone) VALUES
    (1, 1, '79201111101'), (2, 1, '79201111102'),
    (3, 2, '79202222201'), (4, 2, '79202222202'),
    (5, 3, '79203333301'), (6, 3, '79203333302'),
    (7, 4, '79204444401'), (8, 4, '79204444402'),
    (9, 5, '79205555501'), (10, 5, '79205555502'),
    (11, 6, '79206666601'), (12, 6, '79206666602'),
    (13, 7, '79207777701'), (14, 7, '79207777702'),
    (15, 8, '79208888801'), (16, 8, '79208888802'),
    (17, 9, '79209999901'), (18, 9, '79209999902'),
    (19, 10, '79200000001'), (20, 10, '79200000002');

INSERT INTO email_data (id, user_id, email) VALUES
    (1, 1, 'alice1@test.com'), (2, 1, 'alice2@test.com'),
    (3, 2, 'bob1@test.com'), (4, 2, 'bob2@test.com'),
    (5, 3, 'charlie1@test.com'), (6, 3, 'charlie2@test.com'),
    (7, 4, 'diana1@test.com'), (8, 4, 'diana2@test.com'),
    (9, 5, 'ethan1@test.com'), (10, 5, 'ethan2@test.com'),
    (11, 6, 'fiona1@test.com'), (12, 6, 'fiona2@test.com'),
    (13, 7, 'george1@test.com'), (14, 7, 'george2@test.com'),
    (15, 8, 'hannah1@test.com'), (16, 8, 'hannah2@test.com'),
    (17, 9, 'ian1@test.com'), (18, 9, 'ian2@test.com'),
    (19, 10, 'julia1@test.com'), (20, 10, 'julia2@test.com');

INSERT INTO account (id, user_id, balance) VALUES
    (1, 1, 1000.00),
    (2, 2, 1500.50),
    (3, 3, 900.00),
    (4, 4, 1100.25),
    (5, 5, 2000.00),
    (6, 6, 1200.75),
    (7, 7, 950.60),
    (8, 8, 880.00),
    (9, 9, 1340.40),
    (10, 10, 3000.00);

SELECT setval('user_id_seq', 10, true);
SELECT setval('phone_data_id_seq', 20, true);
SELECT setval('email_data_id_seq', 20, true);
SELECT setval('account_id_seq', 10, true);
