ALTER TABLE submissions
    ADD lecturer_id UUID;

ALTER TABLE submissions
    ADD lecturer_verified_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE submissions
    ADD CONSTRAINT FK_SUBMISSIONS_ON_LECTURER FOREIGN KEY (lecturer_id) REFERENCES lecturers (id);