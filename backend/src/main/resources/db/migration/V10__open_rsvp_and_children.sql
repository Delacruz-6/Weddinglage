-- Open RSVP (no invite code): name-based confirmations + named children.

ALTER TABLE rsvp
    ALTER COLUMN invitation_id DROP NOT NULL;

ALTER TABLE rsvp
    ADD COLUMN IF NOT EXISTS full_name VARCHAR(255) NULL;

CREATE TABLE IF NOT EXISTS rsvp_child (
    id              BIGSERIAL PRIMARY KEY,
    rsvp_id         BIGINT NOT NULL REFERENCES rsvp (id) ON DELETE CASCADE,
    full_name       VARCHAR(255) NOT NULL,
    age             INTEGER NULL,
    intolerances    TEXT NULL,
    sort_order      INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_rsvp_child_rsvp ON rsvp_child (rsvp_id);
CREATE INDEX IF NOT EXISTS idx_rsvp_full_name ON rsvp (full_name);
