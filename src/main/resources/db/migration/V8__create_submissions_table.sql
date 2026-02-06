CREATE TABLE submissions
(
    id                    VARCHAR(255) NOT NULL,
    submission_code       VARCHAR(50)  NOT NULL,
    user_id               UUID         NOT NULL,
    submission_type       SMALLINT     NOT NULL,
    status                SMALLINT     NOT NULL,
    notes                 OID,
    faculty_letter_number VARCHAR(255),
    faculty               VARCHAR(50)  NOT NULL,
    submission_date       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_at            TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at            TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_submissions PRIMARY KEY (id)
);

ALTER TABLE submissions
    ADD CONSTRAINT uk_submission_code UNIQUE (submission_code);

CREATE INDEX idx_status ON submissions (status);

CREATE INDEX idx_submission_code ON submissions (submission_code);

CREATE INDEX idx_submission_type ON submissions (submission_type);

ALTER TABLE submissions
    ADD CONSTRAINT FK_SUBMISSIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_user_id ON submissions (user_id);