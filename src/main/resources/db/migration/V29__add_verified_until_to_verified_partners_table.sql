ALTER TABLE verified_partners
    ADD COLUMN verified_until TIMESTAMP WITHOUT TIME ZONE;

UPDATE verified_partners
SET verified_until = verified_at + (partner_cooperation_period * INTERVAL '1 year');

ALTER TABLE verified_partners
    ALTER COLUMN verified_until SET NOT NULL;