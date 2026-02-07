ALTER TABLE moa_ia_documents
DROP
COLUMN document_activity;

ALTER TABLE moa_ia_documents
DROP
COLUMN document_type;

ALTER TABLE moa_ia_documents
    ADD document_activity VARCHAR(255) NOT NULL;

ALTER TABLE moa_ia_documents
    ADD document_type VARCHAR(255) NOT NULL;