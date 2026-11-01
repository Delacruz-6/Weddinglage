ALTER TABLE wedding_configuration
    ADD COLUMN IF NOT EXISTS ornament_suggestion TEXT NULL;

UPDATE wedding_configuration
SET
    spotify_playlist_url = 'https://open.spotify.com/playlist/6hPK4WkU8eZdOVBhU6dgBB',
    ornament_suggestion = U&'Si quer\00E9is traer un adorno navide\00F1o para el \00E1rbol de la celebraci\00F3n, os sugerimos tonos dorados, verdes y crema, preferiblemente hecho a mano o con un detalle personal. Es totalmente opcional: venid sin adorno si prefer\00EDs.',
    show_playlist = TRUE,
    show_ornaments = TRUE,
    updated_at = NOW()
WHERE id = 1;
