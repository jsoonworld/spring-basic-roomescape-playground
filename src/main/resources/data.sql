INSERT INTO member (name, email, password, role)
VALUES ('어드민', 'admin@email.com', 'password', 'ADMIN'),
       ('브라운', 'brown@email.com', 'password', 'USER'),
       ('워니', 'woni@email.com', 'password', 'USER');

INSERT INTO theme (name, description)
VALUES ('테마1', '테마1입니다.'),
       ('테마2', '테마2입니다.'),
       ('테마3', '테마3입니다.');

INSERT INTO time (time_value)
VALUES ('10:00'), ('12:00'), ('14:00'), ('15:40'), ('16:00'), ('18:00'), ('20:00');

INSERT INTO reservation (member_id, date, time_id, theme_id, status)
VALUES (1, '2025-08-02', 1, 1, 'CONFIRMED'),
       (1, '2025-08-02', 2, 2, 'CONFIRMED'),
       (1, '2025-08-03', 3, 3, 'CONFIRMED');
