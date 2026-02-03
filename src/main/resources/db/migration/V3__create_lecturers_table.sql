CREATE TABLE lecturers
(
    id            UUID         NOT NULL,
    user_id       UUID         NOT NULL,
    nidn          VARCHAR(20),
    nip           VARCHAR(20),
    study_program VARCHAR(255) NOT NULL,
    is_adhoc      BOOLEAN,
    CONSTRAINT pk_lecturers PRIMARY KEY (id)
);

ALTER TABLE lecturers
    ADD CONSTRAINT uk_lecturers_nidn UNIQUE (nidn);

ALTER TABLE lecturers
    ADD CONSTRAINT uk_lecturers_nip UNIQUE (nip);

ALTER TABLE lecturers
    ADD CONSTRAINT uk_lecturers_user UNIQUE (user_id);

CREATE INDEX idx_nidn ON lecturers (nidn);

CREATE INDEX idx_nip ON lecturers (nip);

ALTER TABLE lecturers
    ADD CONSTRAINT FK_LECTURERS_USER FOREIGN KEY (user_id) REFERENCES users (id);