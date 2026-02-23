CREATE TABLE student_snapshot_students
(
    id          UUID         NOT NULL,
    snapshot_id UUID         NOT NULL,
    full_name   VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL,
    nim         VARCHAR(12)  NOT NULL,
    CONSTRAINT pk_student_snapshot_students PRIMARY KEY (id)
);

CREATE TABLE student_snapshots
(
    id                 UUID         NOT NULL,
    moa_ia_document_id UUID         NOT NULL,
    study_program      VARCHAR(255) NOT NULL,
    unit               VARCHAR(255) NOT NULL,
    total              INTEGER      NOT NULL,
    CONSTRAINT pk_student_snapshots PRIMARY KEY (id)
);

CREATE INDEX idx_snapshot_student_nim ON student_snapshot_students (nim);

ALTER TABLE student_snapshot_students
    ADD CONSTRAINT FK_SNAPSHOT_STUDENTS_SNAPSHOT FOREIGN KEY (snapshot_id) REFERENCES student_snapshots (id);

ALTER TABLE student_snapshots
    ADD CONSTRAINT FK_STUDENT_SNAPSHOTS_MOA_IA_DOCUMENT FOREIGN KEY (moa_ia_document_id) REFERENCES moa_ia_documents (id);