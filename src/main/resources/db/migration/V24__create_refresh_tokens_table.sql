CREATE TABLE refresh_tokens
(
    id         UUID         NOT NULL,
    user_id    UUID         NOT NULL,
    token      VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_refresh_tokens PRIMARY KEY (id)
);

ALTER TABLE refresh_tokens
    ADD CONSTRAINT uk_refresh_tokens_token UNIQUE (token);

CREATE INDEX idx_refresh_tokens_token ON refresh_tokens (token);

ALTER TABLE refresh_tokens
    ADD CONSTRAINT FK_REFRESH_TOKENS_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);