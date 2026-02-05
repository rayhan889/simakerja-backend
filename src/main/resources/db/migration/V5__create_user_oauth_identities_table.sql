CREATE TABLE user_oauth_identities
(
    id               UUID         NOT NULL,
    user_id          UUID         NOT NULL,
    provider         VARCHAR(50)  NOT NULL,
    provider_user_id VARCHAR(255) NOT NULL,
    provider_email   VARCHAR(255) NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_user_oauth_identities PRIMARY KEY (id)
);

ALTER TABLE user_oauth_identities
    ADD CONSTRAINT uc_user_oauth_identities_user UNIQUE (user_id);

ALTER TABLE user_oauth_identities
    ADD CONSTRAINT uk_provider_user UNIQUE (provider, provider_user_id);

CREATE INDEX idx_oauth_provider ON user_oauth_identities (provider);

CREATE INDEX idx_oauth_provider_user_id ON user_oauth_identities (provider_user_id);

ALTER TABLE user_oauth_identities
    ADD CONSTRAINT FK_USER_OAUTH_IDENTITIES_USER FOREIGN KEY (user_id) REFERENCES users (id);