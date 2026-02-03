CREATE TABLE students
(
    id            UUID         NOT NULL,
    user_id       UUID         NOT NULL,
    nim           VARCHAR(12)  NOT NULL,
    study_program VARCHAR(255) NOT NULL,
    CONSTRAINT pk_students PRIMARY KEY (id)
);

ALTER TABLE students
    ADD CONSTRAINT uk_students_nim UNIQUE (nim);

ALTER TABLE students
    ADD CONSTRAINT uk_students_user UNIQUE (user_id);

CREATE INDEX idx_nim ON students (nim);

ALTER TABLE students
    ADD CONSTRAINT FK_STUDENTS_USER FOREIGN KEY (user_id) REFERENCES users (id);