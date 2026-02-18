ALTER TABLE moa_ia_documents
    ADD partner_address VARCHAR(255);

ALTER TABLE moa_ia_documents
    ADD partner_logo_url VARCHAR(255);

ALTER TABLE moa_ia_documents
    ALTER COLUMN partner_address SET NOT NULL;

ALTER TABLE moa_ia_documents
    ALTER COLUMN partner_logo_url SET NOT NULL;