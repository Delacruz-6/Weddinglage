CREATE TABLE invitation (
    id              BIGSERIAL PRIMARY KEY,
    invite_code     VARCHAR(64) NOT NULL UNIQUE,
    label           VARCHAR(255) NULL,
    max_attendees   INTEGER NOT NULL DEFAULT 2,
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE guest (
    id              BIGSERIAL PRIMARY KEY,
    invitation_id   BIGINT NOT NULL REFERENCES invitation (id) ON DELETE CASCADE,
    first_name      VARCHAR(128) NOT NULL,
    last_name       VARCHAR(128) NULL,
    email           VARCHAR(255) NULL,
    is_child        BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_guest_invitation ON guest (invitation_id);

CREATE TABLE rsvp (
    id                  BIGSERIAL PRIMARY KEY,
    invitation_id       BIGINT NOT NULL UNIQUE REFERENCES invitation (id) ON DELETE CASCADE,
    edit_token_hash     VARCHAR(128) NOT NULL,
    attending           BOOLEAN NOT NULL,
    adults              INTEGER NOT NULL DEFAULT 0,
    children            INTEGER NOT NULL DEFAULT 0,
    notes               TEXT NULL,
    submitted_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_rsvp_edit_token_hash ON rsvp (edit_token_hash);

CREATE TABLE dietary_requirement (
    id              BIGSERIAL PRIMARY KEY,
    rsvp_id         BIGINT NOT NULL REFERENCES rsvp (id) ON DELETE CASCADE,
    requirement_type VARCHAR(64) NOT NULL,
    detail          TEXT NULL
);

CREATE TABLE bus_capacity (
    id                  BIGSERIAL PRIMARY KEY,
    bus_slot            VARCHAR(8) NOT NULL UNIQUE,
    capacity            INTEGER NOT NULL,
    waitlist_enabled    BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE transport_reservation (
    id              BIGSERIAL PRIMARY KEY,
    rsvp_id         BIGINT NOT NULL REFERENCES rsvp (id) ON DELETE CASCADE,
    bus_slot        VARCHAR(8) NOT NULL,
    stop_id         VARCHAR(64) NULL,
    status          VARCHAR(32) NOT NULL,
    seats           INTEGER NOT NULL DEFAULT 1,
    CONSTRAINT chk_bus_slot CHECK (bus_slot IN ('18:00', '22:00')),
    CONSTRAINT chk_transport_status CHECK (status IN ('CONFIRMED', 'WAITLIST'))
);

CREATE INDEX idx_transport_bus_slot ON transport_reservation (bus_slot);

CREATE TABLE song_suggestion (
    id              BIGSERIAL PRIMARY KEY,
    rsvp_id         BIGINT NOT NULL REFERENCES rsvp (id) ON DELETE CASCADE,
    title           VARCHAR(255) NOT NULL,
    artist          VARCHAR(255) NULL
);

CREATE TABLE ornament_type (
    id              BIGSERIAL PRIMARY KEY,
    code            VARCHAR(64) NOT NULL UNIQUE,
    name            VARCHAR(128) NOT NULL,
    image_key       VARCHAR(255) NULL,
    active          BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE guest_ornament (
    id                  BIGSERIAL PRIMARY KEY,
    invitation_id       BIGINT NOT NULL UNIQUE REFERENCES invitation (id) ON DELETE CASCADE,
    ornament_type_id    BIGINT NOT NULL REFERENCES ornament_type (id),
    assigned_at         TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

INSERT INTO ornament_type (code, name, image_key) VALUES
('STAR', 'Estrella', 'ornaments/star.svg'),
('BELL', 'Campana', 'ornaments/bell.svg'),
('TREE', 'Arbolito', 'ornaments/tree.svg'),
('SNOWFLAKE', 'Copo de nieve', 'ornaments/snowflake.svg'),
('CANDY', U&'Bast\00F3n de caramelo', 'ornaments/candy.svg');

INSERT INTO invitation (invite_code, label, max_attendees) VALUES
('INVITE001', 'Familia Demo Uno', 4),
('INVITE002', 'Familia Demo Dos', 3),
('INVITE003', 'Invitado Demo Tres', 2),
('INVITE004', 'Familia Demo Cuatro', 5),
('INVITE005', 'Invitado Demo Cinco', 2),
('INVITE006', 'Familia Demo Seis', 4),
('INVITE007', 'Invitado Demo Siete', 2),
('INVITE008', 'Familia Demo Ocho', 3);
