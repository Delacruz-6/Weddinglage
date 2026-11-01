CREATE TABLE photo (
    id              BIGSERIAL PRIMARY KEY,
    storage_key     VARCHAR(512) NOT NULL,
    thumb_key       VARCHAR(512) NULL,
    original_name   VARCHAR(255) NULL,
    mime_type       VARCHAR(128) NOT NULL,
    status          VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    uploaded_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    reviewed_at     TIMESTAMPTZ NULL,
    reviewed_by     VARCHAR(255) NULL,
    CONSTRAINT chk_photo_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'))
);

CREATE INDEX idx_photo_status ON photo (status);

CREATE TABLE photo_upload_token (
    id              BIGSERIAL PRIMARY KEY,
    token_hash      VARCHAR(128) NOT NULL UNIQUE,
    label           VARCHAR(255) NULL,
    expires_at      TIMESTAMPTZ NULL,
    revoked         BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE photo_publication_config (
    id                  SMALLINT PRIMARY KEY CHECK (id = 1),
    publish_at          TIMESTAMPTZ NULL,
    admin_authorized    BOOLEAN NOT NULL DEFAULT FALSE,
    authorized_by       VARCHAR(255) NULL,
    authorized_at       TIMESTAMPTZ NULL,
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

INSERT INTO photo_publication_config (id, publish_at, admin_authorized)
VALUES (1, NULL, FALSE);
