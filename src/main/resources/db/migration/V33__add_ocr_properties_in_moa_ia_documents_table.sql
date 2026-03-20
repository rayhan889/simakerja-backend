ALTER TABLE moa_ia_documents
    ADD scanned_document_key VARCHAR(255);

ALTER TABLE moa_ia_documents
    ADD scanned_ocr_confident_score DOUBLE PRECISION;

ALTER TABLE moa_ia_documents
    ADD send_scanned_at TIMESTAMP WITHOUT TIME ZONE;