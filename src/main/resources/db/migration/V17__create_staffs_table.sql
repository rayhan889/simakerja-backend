CREATE TABLE staffs
(
    id        UUID NOT NULL,
    user_id   UUID NOT NULL,
    full_name VARCHAR(255),
    nip       VARCHAR(20),
    CONSTRAINT pk_staffs PRIMARY KEY (id)
);

ALTER TABLE staffs
    ADD CONSTRAINT uk_staffs_nip UNIQUE (nip);

ALTER TABLE staffs
    ADD CONSTRAINT uk_staffs_user UNIQUE (user_id);

ALTER TABLE staffs
    ADD CONSTRAINT FK_STAFFS_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_staffs_nip ON staffs (nip);