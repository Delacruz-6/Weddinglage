CREATE TABLE venue (
    id              SMALLINT PRIMARY KEY CHECK (id = 1),
    name            VARCHAR(255) NULL,
    address_line    VARCHAR(512) NULL,
    city            VARCHAR(128) NULL,
    postal_code     VARCHAR(32) NULL,
    country         VARCHAR(128) NULL,
    latitude        DOUBLE PRECISION NULL,
    longitude       DOUBLE PRECISION NULL,
    how_to_arrive   TEXT NULL,
    parking_notes   TEXT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

INSERT INTO venue (id, name, address_line, city, how_to_arrive)
VALUES (1, 'Lugar pendiente', NULL, NULL, 'Indicaciones pendientes de confirmar.');

CREATE TABLE venue_image (
    id          BIGSERIAL PRIMARY KEY,
    venue_id    SMALLINT NOT NULL REFERENCES venue (id) ON DELETE CASCADE,
    image_url   VARCHAR(1024) NOT NULL,
    alt_text    VARCHAR(255) NULL,
    sort_order  INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE timeline_event (
    id          BIGSERIAL PRIMARY KEY,
    event_time  TIME NOT NULL,
    title       VARCHAR(255) NOT NULL,
    description TEXT NULL,
    icon        VARCHAR(64) NULL,
    sort_order  INTEGER NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Use Unicode escapes so seeds stay correct regardless of JDBC client encoding.
INSERT INTO timeline_event (event_time, title, description, icon, sort_order) VALUES
('13:00', 'Ceremonia', U&'Inicio de la ceremonia. Os esperamos con ilusi\00F3n.', 'ceremony', 1),
('14:00', U&'C\00F3ctel', U&'Brindis y c\00F3ctel de bienvenida tras la ceremonia.', 'cocktail', 2),
('15:30', 'Almuerzo', U&'Compartimos mesa y celebraci\00F3n juntos.', 'meal', 3),
('17:00', 'Barra libre', U&'M\00FAsica, baile y barra libre para seguir celebrando.', 'party', 4),
('18:00', U&'Primer autob\00FAs', U&'Salida del primer autob\00FAs de regreso.', 'bus', 5),
('22:00', U&'Segundo autob\00FAs', U&'Salida del segundo autob\00FAs de regreso.', 'bus', 6);

CREATE TABLE playlist_track (
    id            BIGSERIAL PRIMARY KEY,
    title         VARCHAR(255) NOT NULL,
    artist        VARCHAR(255) NULL,
    source_url    VARCHAR(1024) NULL,
    license_note  VARCHAR(512) NULL,
    sort_order    INTEGER NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
