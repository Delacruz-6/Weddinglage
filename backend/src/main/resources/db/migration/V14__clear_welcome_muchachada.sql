-- Remove the informal welcome copy from the entry overlay.
UPDATE wedding_configuration
SET welcome_message = NULL
WHERE id = 1
  AND welcome_message ILIKE '%muchachada%';
