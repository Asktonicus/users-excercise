-- Insertar usuario en la tabla USERS
INSERT INTO USERS (id, name, email, passwd, creation_date, update_date, last_login, is_active, token)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'Juan Pérez', 'juan.perez1@example.com', 'hashed_password_1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMTExMTExMS0xMTExLTExMTEtMTExMS0xMTExMTExMTExMTEiLCJlbWFpbCI6Imp1YW4ucGVyZXoxQGV4YW1wbGUuY29tIiwibmFtZSI6Ikp1YW4gUMOpcmV6IiwiaWF0IjoxNzQ1MTAzMDY3LCJleHAiOjE3NjA5MTA2Njd9.vKJuXVLO6O2A5NU0hZxqtkCWyQ30FfH2UxSUmYmEuIg');

-- Insertar teléfonos en la tabla PHONE_LIST con códigos de ciudad y país numéricos como strings
INSERT INTO PHONE_LIST (phone_number, cod_city, cod_country, user_id)
VALUES
    ('54321012', '9', '56', '11111111-1111-1111-1111-111111111111');

-- Insertar log de usuario en la tabla USER_LOG
INSERT INTO USER_LOG (id, usuario_id, action, creation_date)
VALUES
    ('22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', 'User created', CURRENT_TIMESTAMP);
