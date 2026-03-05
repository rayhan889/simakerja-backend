CREATE TABLE verified_partners
(
    id                              UUID         NOT NULL,
    moa_ia_id                       UUID         NOT NULL,
    partner_name                    VARCHAR(255) NOT NULL,
    partner_number                  VARCHAR(50),
    faculty_representative_name     VARCHAR(255) NOT NULL,
    partner_representative_name     VARCHAR(255) NOT NULL,
    partner_representative_position VARCHAR(255) NOT NULL,
    document_activity               VARCHAR(255) NOT NULL,
    partner_logo_key                VARCHAR(255) NOT NULL,
    partner_address                 VARCHAR(255) NOT NULL,
    partner_cooperation_period      INTEGER      NOT NULL,
    verified_at                     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_verified_partners PRIMARY KEY (id)
);

ALTER TABLE verified_partners
    ADD CONSTRAINT uk_verified_partners_moa_ia UNIQUE (moa_ia_id);

ALTER TABLE verified_partners
    ADD CONSTRAINT uk_verified_partners_partner_name UNIQUE (partner_name);

ALTER TABLE verified_partners
    ADD CONSTRAINT uk_verified_partners_partner_number UNIQUE (partner_number);

CREATE INDEX idx_verified_partners_partner_number ON verified_partners (partner_number);

ALTER TABLE verified_partners
    ADD CONSTRAINT FK_VERIFIED_PARTNERS_MOA_IA FOREIGN KEY (moa_ia_id) REFERENCES moa_ia_documents (id);