INSERT INTO `user` (
  `id`, `created_at`, `date_birth`, `email`, `is_host`,
  `name`, `password`, `phone`, `photo_url`, `role`, `status`
) VALUES
('u001', '2025-10-05 14:30:00', '1990-06-15', 'maria.gomez@example.com', b'0',
 'María Gómez', '$2b$10$A1bCdEfGhIjKlMnOpQrStUvWxYz1234567890abcdEfGhIj',
 '+573001112233', 'https://example.com/photos/maria.jpg', 0, 'ACTIVE'),

('u002', '2025-09-28 09:45:00', '1985-12-02', 'juan.perez@example.com', b'1',
 'Juan Pérez', '$2b$10$ZyXwVuTsRqPoNmLkJiHgFeDcBa9876543210ZYXWVUTSRQPON',
 '+573224445566', 'https://example.com/photos/juan.jpg', 1, 'ACTIVE'),

('u003', '2025-10-01 17:20:00', '1998-03-22', 'laura.mendoza@example.com', b'0',
 'Laura Mendoza', '$2b$10$MnOpQrStUvWxYz1234567890abcdefGhIjKlMnOpQrStUvWxY',
 NULL, NULL, 2, 'INACTIVE');


INSERT INTO `place` (
  `id`, `address`, `city`, `latitude`, `longitude`, `avg_rating`,
  `created_at`, `description`, `max_guests`, `num_ratings`,
  `price_per_night`, `status`, `title`, `host_id`
) VALUES
(
  1, 'Carrera 45 #23-10', 'Medellín', 6.2518, -75.5636, 4.8,
  '2025-09-15 10:20:00',
  'Apartamento moderno en El Poblado con vista a la ciudad y acceso a piscina y gimnasio.',
  4, 56, 320000, 'ACTIVE', 'Apartamento moderno con vista panorámica', 'u002'
),
(
  2, 'Calle 10 #5-22', 'Cartagena', 10.3910, -75.4794, 4.6,
  '2025-08-30 18:45:00',
  'Casa colonial en el centro histórico, ideal para grupos grandes, con terraza y jacuzzi.',
  8, 89, 580000, 'ACTIVE', 'Casa colonial en el centro histórico', 'u002'
),
(
  3, 'Carrera 7 #72-50', 'Bogotá', 4.6486, -74.0990, 4.2,
  '2025-10-02 09:10:00',
  'Estudio acogedor en Chapinero, cerca de restaurantes, transporte y zonas comerciales.',
  2, 34, 190000, 'INACTIVE', 'Estudio céntrico en Chapinero', 'u002'
);
