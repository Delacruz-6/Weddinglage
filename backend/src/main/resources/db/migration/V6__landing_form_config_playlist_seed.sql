ALTER TABLE wedding_configuration
    ADD COLUMN IF NOT EXISTS hero_image_url VARCHAR(1024) NULL,
    ADD COLUMN IF NOT EXISTS story_image_url VARCHAR(1024) NULL,
    ADD COLUMN IF NOT EXISTS rsvp_show_notes BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS rsvp_show_dietary BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS rsvp_show_transport BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS rsvp_show_songs BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS rsvp_show_children BOOLEAN NOT NULL DEFAULT TRUE;

UPDATE wedding_configuration
SET show_playlist = TRUE,
    updated_at = NOW()
WHERE id = 1;

-- Demo playlist (royalty-free sample hosts). Replace from admin with your licensed tracks.
INSERT INTO playlist_track (title, artist, source_url, license_note, sort_order)
SELECT v.title, v.artist, v.source_url, v.license_note, v.sort_order
FROM (VALUES
    ('SoundHelix Song 1', 'T. Schürger', 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3', 'Sample demo track — replace with licensed music', 1),
    ('SoundHelix Song 2', 'T. Schürger', 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3', 'Sample demo track — replace with licensed music', 2),
    ('SoundHelix Song 3', 'T. Schürger', 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3', 'Sample demo track — replace with licensed music', 3)
) AS v(title, artist, source_url, license_note, sort_order)
WHERE NOT EXISTS (SELECT 1 FROM playlist_track LIMIT 1);
