-- Richer Christmas / winter wedding atmosphere for landing placeholders.
UPDATE wedding_configuration
SET
    hero_image_url = 'https://images.unsplash.com/photo-1576919228236-a097c32a5cd4?auto=format&fit=crop&w=2000&q=80',
    story_image_url = 'https://images.unsplash.com/photo-1511285560929-80b456fe9e0?auto=format&fit=crop&w=1400&q=80',
    show_playlist = TRUE,
    updated_at = NOW()
WHERE id = 1;
