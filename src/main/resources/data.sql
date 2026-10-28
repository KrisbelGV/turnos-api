-- NUEVO (P1-LC3): solo inserta los turnos de ejemplo si la tabla está vacía.
-- Así, aunque se ejecute en cada arranque, no duplica datos.
INSERT INTO turno (cliente, servicio, fecha_hora, estado)
SELECT * FROM (VALUES
    ('Ana Pérez',   'Corte de pelo',        TIMESTAMP '2030-03-04 10:30:00', 'PENDIENTE'),
    ('Bruno Díaz',  'Consulta nutricional', TIMESTAMP '2030-03-04 16:00:00', 'CONFIRMADO'),
    ('Carla Gómez', 'Color',                TIMESTAMP '2030-03-05 11:00:00', 'CANCELADO')
) AS v(cliente, servicio, fecha_hora, estado)
WHERE NOT EXISTS (SELECT 1 FROM turno);
