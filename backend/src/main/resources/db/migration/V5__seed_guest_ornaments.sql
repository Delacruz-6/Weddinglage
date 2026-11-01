-- Assign demo ornaments round-robin so public ornament lookup works locally.
INSERT INTO guest_ornament (invitation_id, ornament_type_id)
SELECT i.id,
       (
           SELECT ot.id
           FROM ornament_type ot
           ORDER BY ot.id
           OFFSET ((i.id - 1) % (SELECT COUNT(*) FROM ornament_type))
           LIMIT 1
       )
FROM invitation i
WHERE NOT EXISTS (
    SELECT 1 FROM guest_ornament go WHERE go.invitation_id = i.id
);
