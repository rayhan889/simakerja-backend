ALTER TABLE users
    ADD failed_login_attempts INTEGER;

ALTER TABLE users
    ADD locked_until TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE users
    ALTER COLUMN failed_login_attempts SET NOT NULL;