CREATE TABLE users (
                       id         BIGSERIAL PRIMARY KEY,
                       name       VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP    NOT NULL
);