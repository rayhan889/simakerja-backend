ALTER TABLE submissions
    ADD period date;

ALTER TABLE submissions
    ADD staff_id UUID;

ALTER TABLE submissions
    ADD staff_verified_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE submissions
    ALTER COLUMN period SET NOT NULL;

ALTER TABLE submissions
    ADD CONSTRAINT FK_SUBMISSIONS_ON_STAFF FOREIGN KEY (staff_id) REFERENCES staffs (id);

CREATE INDEX idx_staff_id ON submissions (staff_id);