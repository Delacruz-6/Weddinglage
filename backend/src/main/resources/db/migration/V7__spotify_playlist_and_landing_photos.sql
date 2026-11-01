ALTER TABLE wedding_configuration
    ADD COLUMN IF NOT EXISTS spotify_playlist_url VARCHAR(1024) NULL;

-- Atmospheric Christmas / winter wedding placeholders (replace with your photos from admin).
UPDATE wedding_configuration
SET
    hero_image_url = COALESCE(
        NULLIF(hero_image_url, ''),
        'https://images.unsplash.com/photo-1512389142860-9c449e58a543?auto=format&fit=crop&w=1800&q=80'
    ),
    story_image_url = COALESCE(
        NULLIF(story_image_url, ''),
        'https://images.unsplash.com/photo-1519741497674-611481863552?auto=format&fit=crop&w=1400&q=80'
    ),
    show_playlist = TRUE,
    updated_at = NOW()
WHERE id = 1;
