CREATE TABLE admin_user (
    id              BIGSERIAL PRIMARY KEY,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    roles           VARCHAR(255) NOT NULL DEFAULT 'ADMIN',
    failed_logins   INTEGER NOT NULL DEFAULT 0,
    locked_until    TIMESTAMPTZ NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE wedding_configuration (
    id                  SMALLINT PRIMARY KEY CHECK (id = 1),
    partner_names       VARCHAR(255) NULL,
    wedding_at          TIMESTAMPTZ NOT NULL DEFAULT TIMESTAMPTZ '2027-12-18 12:00:00+00',
    welcome_message     TEXT NULL,
    story_html          TEXT NULL,
    gift_iban           VARCHAR(64) NULL,
    gift_holder         VARCHAR(255) NULL,
    show_story          BOOLEAN NOT NULL DEFAULT TRUE,
    show_venue          BOOLEAN NOT NULL DEFAULT TRUE,
    show_timeline       BOOLEAN NOT NULL DEFAULT TRUE,
    show_playlist       BOOLEAN NOT NULL DEFAULT TRUE,
    show_gifts          BOOLEAN NOT NULL DEFAULT TRUE,
    show_rsvp           BOOLEAN NOT NULL DEFAULT TRUE,
    show_photos         BOOLEAN NOT NULL DEFAULT TRUE,
    show_ornaments      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

INSERT INTO wedding_configuration (id, partner_names, welcome_message)
VALUES (1, 'Nombres pendientes', 'Bienvenidos a nuestra boda');

CREATE TABLE admin_audit_log (
    id          BIGSERIAL PRIMARY KEY,
    actor       VARCHAR(255) NOT NULL,
    action      VARCHAR(128) NOT NULL,
    entity_type VARCHAR(128) NULL,
    entity_id   VARCHAR(128) NULL,
    details     TEXT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_admin_audit_log_created_at ON admin_audit_log (created_at DESC);
