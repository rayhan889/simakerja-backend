ALTER TABLE moa_ia_documents
    ADD partner_logo_key VARCHAR(255);

ALTER TABLE moa_ia_documents
    ALTER COLUMN partner_logo_key SET NOT NULL;