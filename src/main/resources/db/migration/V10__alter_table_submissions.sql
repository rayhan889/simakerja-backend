ALTER TABLE submissions
DROP
COLUMN notes;

ALTER TABLE submissions
DROP
COLUMN status;

ALTER TABLE submissions
DROP
COLUMN submission_type;

ALTER TABLE submissions
    ADD notes TEXT;

ALTER TABLE submissions
    ADD status VARCHAR(50) NOT NULL;

ALTER TABLE submissions
    ADD submission_type VARCHAR(50) NOT NULL;