CREATE TABLE moa_ia_documents
(
    id                              UUID         NOT NULL,
    submission_id                   VARCHAR(255) NOT NULL,
    document_type                   SMALLINT     NOT NULL,
    partner_name                    VARCHAR(255) NOT NULL,
    partner_number                  VARCHAR(50)  NOT NULL,
    faculty_representative_name     VARCHAR(255) NOT NULL,
    partner_representative_name     VARCHAR(255) NOT NULL,
    partner_representative_position VARCHAR(255) NOT NULL,
    document_activity               SMALLINT     NOT NULL,
    student_snapshot                JSONB        NOT NULL,
    CONSTRAINT pk_moa_ia_documents PRIMARY KEY (id)
);

ALTER TABLE moa_ia_documents
    ADD CONSTRAINT uc_moa_ia_documents_submission UNIQUE (submission_id);

ALTER TABLE moa_ia_documents
    ADD CONSTRAINT FK_MOA_IA_DOCUMENTS_SUBMISSION FOREIGN KEY (submission_id) REFERENCES submissions (id);