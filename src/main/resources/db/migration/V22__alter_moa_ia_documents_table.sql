ALTER TABLE moa_ia_documents
    ADD partner_cooperation_period INTEGER;

ALTER TABLE moa_ia_documents
    ALTER COLUMN partner_cooperation_period SET NOT NULL;