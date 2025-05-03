-- Tabla USERS
CREATE TABLE USERS (
    id UUID PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    passwd VARCHAR(255) NOT NULL,
    creation_date TIMESTAMP,
    update_date TIMESTAMP,
    last_login TIMESTAMP,
    is_active BOOLEAN,
    token VARCHAR(5000)
);

-- Tabla PHONE_LIST
CREATE TABLE PHONE_LIST (
    id SERIAL PRIMARY KEY,
    phone_number VARCHAR(255),
    cod_city VARCHAR(255),
    cod_country VARCHAR(255),
    user_id UUID NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES USERS(id) ON DELETE CASCADE
);

-- Tabla USER_LOG
CREATE TABLE USER_LOG (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL,
    action VARCHAR(255),
    creation_date TIMESTAMP,
    CONSTRAINT fk_usuario FOREIGN KEY (usuario_id) REFERENCES USERS(id) ON DELETE CASCADE
);
