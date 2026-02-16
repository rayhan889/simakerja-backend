ALTER TABLE submissions
    ADD faculty_address VARCHAR(255);

ALTER TABLE submissions
    ALTER COLUMN faculty_address SET NOT NULL;