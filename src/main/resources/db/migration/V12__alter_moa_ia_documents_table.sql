ALTER TABLE moa_ia_documents
    ADD student_snapshots JSONB;

ALTER TABLE moa_ia_documents
    ALTER COLUMN student_snapshots SET NOT NULL;

ALTER TABLE moa_ia_documents
DROP
COLUMN student_snapshot;