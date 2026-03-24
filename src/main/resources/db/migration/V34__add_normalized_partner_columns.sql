CREATE EXTENSION IF NOT EXISTS pg_trgm;

ALTER TABLE moa_ia_documents
    ADD COLUMN IF NOT EXISTS partner_name_normalized VARCHAR(255),
    ADD COLUMN IF NOT EXISTS partner_name_acronym VARCHAR(100);

ALTER TABLE verified_partners
    ADD COLUMN IF NOT EXISTS partner_name_normalized VARCHAR(255),
    ADD COLUMN IF NOT EXISTS partner_name_acronym VARCHAR(100);

CREATE INDEX IF NOT EXISTS idx_moa_ia_partner_name_trgm
    ON moa_ia_documents USING gin (partner_name_normalized gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_verified_partner_name_trgm
    ON verified_partners USING gin (partner_name_normalized gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_moa_ia_partner_acronym
    ON moa_ia_documents (partner_name_acronym);
CREATE INDEX IF NOT EXISTS idx_verified_partner_acronym
    ON verified_partners (partner_name_acronym);