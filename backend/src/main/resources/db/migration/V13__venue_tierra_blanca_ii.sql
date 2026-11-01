UPDATE venue
SET name = 'Hacienda Tierra Blanca II',
    address_line = 'Avenida de Sevilla, 32',
    city = 'Salteras',
    postal_code = '41909',
    country = U&'Sevilla, Espa\00F1a',
    latitude = 37.414588,
    longitude = -6.102037,
    how_to_arrive = U&'Hacienda Tierra Blanca II, en Salteras (Sevilla)',
    parking_notes = U&'Hay aparcamiento en las instalaciones',
    updated_at = NOW()
WHERE id = 1;
