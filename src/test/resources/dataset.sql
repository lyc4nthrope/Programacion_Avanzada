-- ================================================
-- DATOS DE PRUEBA PARA TESTING
-- ================================================

-- Limpiar datos previos (opcional, solo si necesitas empezar limpio)
-- DELETE FROM review;
-- DELETE FROM reservation;
-- DELETE FROM favorite;
-- DELETE FROM payment;
-- DELETE FROM accommodation_photo;
-- DELETE FROM availability_calendar;
-- DELETE FROM accommodation;
-- DELETE FROM host_profile;
-- DELETE FROM password_reset_code;
-- DELETE FROM message;
-- DELETE FROM chat_users;
-- DELETE FROM chat;
-- DELETE FROM user;

-- ================================================
-- INSERTAR USUARIOS DE PRUEBA
-- ================================================

INSERT INTO `user` (
  `id`, `created_at`, `date_birth`, `email`, 
  `name`, `password`, `phone`, `photo_url`, `role`, `status`
) VALUES
-- Usuario GUEST activo
('u001', '2025-10-05 14:30:00', '1990-06-15', 'maria.gomez@example.com',
 'María Gómez', '$2a$10$N9qo8uLOickgx2ZrVzY6qe6HlRVPZ9AHXEfv5zzH0I9Lb5C5gDqGG', 
 '+573001112233', 'https://example.com/photos/maria.jpg', 'GUEST', 'ACTIVE'),

-- Usuario HOST activo
('u002', '2025-09-28 09:45:00', '1985-12-02', 'juan.perez@example.com',
 'Juan Pérez', '$2a$10$N9qo8uLOickgx2ZrVzY6qe6HlRVPZ9AHXEfv5zzH0I9Lb5C5gDqGG',
 '+573224445566', 'https://example.com/photos/juan.jpg', 'HOST', 'ACTIVE'),

-- Usuario ADMIN activo
('u003', '2025-10-01 17:20:00', '1998-03-22', 'admin@example.com',
 'Admin Usuario', '$2a$10$N9qo8uLOickgx2ZrVzY6qe6HlRVPZ9AHXEfv5zzH0I9Lb5C5gDqGG',
 '+573334445566', NULL, 'ADMIN', 'ACTIVE'),

-- Usuario INACTIVE para pruebas
('u004', '2025-10-10 12:00:00', '1992-08-15', 'inactive@example.com',
 'Usuario Inactivo', '$2a$10$N9qo8uLOickgx2ZrVzY6qe6HlRVPZ9AHXEfv5zzH0I9Lb5C5gDqGG',
 NULL, NULL, 'GUEST', 'INACTIVE');

-- NOTA: La contraseña para TODOS es: "Password123"
-- Hash generado con BCrypt

-- ================================================
-- INSERTAR PERFILES DE ANFITRIÓN
-- ================================================

INSERT INTO `host_profile` (
  `id`, `user_id`, `about_me`, `legal_document`, 
  `created_at`, `updated_at`
) VALUES
('hp001', 'u002', 
 'Soy un anfitrión experimentado con más de 5 años en la plataforma. Me encanta recibir huéspedes de todo el mundo.',
 'DOC-12345-ABC',
 '2025-09-28 10:00:00', '2025-09-28 10:00:00');

-- ================================================
-- INSERTAR ALOJAMIENTOS
-- ================================================

INSERT INTO `accommodation` (
  `id`, `title`, `description`, `city`, `address`, 
  `latitude`, `longitude`, `price_per_night`, `max_capacity`, 
  `amenities`, `average_rating`, `rating_count`, 
  `status`, `host_id`, `created_at`, `updated_at`
) VALUES
-- Alojamiento ACTIVE
('acc001', 
 'Apartamento moderno con vista panorámica',
 'Apartamento moderno en El Poblado con vista a la ciudad y acceso a piscina y gimnasio.',
 'Medellin', 'Carrera 45 #23-10',
 6.2518, -75.5636, 320.00, 4,
 'WiFi, Cocina, Piscina, Gimnasio',
 4.8, 15,
 'ACTIVE', 'u002',
 '2025-09-15 10:20:00', '2025-09-15 10:20:00'),

-- Alojamiento ACTIVE
('acc002',
 'Casa colonial en el centro histórico',
 'Casa colonial en el centro histórico, ideal para grupos grandes, con terraza y jacuzzi.',
 'Cartagena', 'Calle 10 #5-22',
 10.3910, -75.4794, 580.00, 8,
 'WiFi, Cocina, Terraza, Jacuzzi',
 4.6, 25,
 'ACTIVE', 'u002',
 '2025-08-30 18:45:00', '2025-08-30 18:45:00'),

-- Alojamiento INACTIVE para pruebas
('acc003',
 'Estudio céntrico en Chapinero',
 'Estudio acogedor en Chapinero, cerca de restaurantes, transporte y zonas comerciales.',
 'Bogota', 'Carrera 7 #72-50',
 4.6486, -74.0990, 190.00, 2,
 'WiFi, Cocina',
 4.2, 8,
 'INACTIVE', 'u002',
 '2025-10-02 09:10:00', '2025-10-02 09:10:00');

-- ================================================
-- INSERTAR RESERVAS
-- ================================================

INSERT INTO `reservation` (
  `id`, `accommodation_id`, `guest_id`, 
  `check_in_date`, `check_out_date`, `number_of_guests`,
  `total_price`, `status`, `created_at`, `updated_at`
) VALUES
-- Reserva CONFIRMED
('res001', 'acc001', 'u001',
 '2025-12-20', '2025-12-25', 2,
 1600.00, 'CONFIRMED',
 '2025-11-15 10:00:00', '2025-11-15 10:00:00'),

-- Reserva PENDING
('res002', 'acc002', 'u001',
 '2025-12-28', '2025-12-30', 4,
 1160.00, 'PENDING',
 '2025-11-18 14:30:00', '2025-11-18 14:30:00');

-- ================================================
-- INSERTAR RESEÑAS
-- ================================================

INSERT INTO `review` (
  `accommodation_id`, `user_id`, `reservation_id`,
  `comment`, `rating`, `created_at`, `updated_at`
) VALUES
('acc001', 'u001', 'res001',
 'Excelente alojamiento, muy limpio y acogedor. La ubicación es perfecta.',
 5, '2025-11-20 10:00:00', '2025-11-20 10:00:00'),

('acc002', 'u001', NULL,
 'Muy buena experiencia, aunque el servicio fue un poco lento.',
 4, '2025-11-21 15:30:00', '2025-11-21 15:30:00');

-- ================================================
-- INSERTAR FAVORITOS
-- ================================================

INSERT INTO `favorite` (
  `id`, `user_id`, `accommodation_id`, `created_at`
) VALUES
('fav001', 'u001', 'acc001', '2025-11-10 08:00:00'),
('fav002', 'u001', 'acc002', '2025-11-12 10:30:00');

-- ================================================
-- NOTAS IMPORTANTES
-- ================================================
-- 1. Contraseña para TODOS los usuarios: "Password123"
--    Hash BCrypt: $2a$10$N9qo8uLOickgx2ZrVzY6qe6HlRVPZ9AHXEfv5zzH0I9Lb5C5gDqGG
--
-- 2. IDs de usuarios:
--    - u001: María (GUEST, ACTIVE)
--    - u002: Juan (HOST, ACTIVE)
--    - u003: Admin (ADMIN, ACTIVE)
--    - u004: Usuario Inactivo (GUEST, INACTIVE)
--
-- 3. IDs de alojamientos:
--    - acc001: Apartamento en Medellín (ACTIVE)
--    - acc002: Casa en Cartagena (ACTIVE)
--    - acc003: Estudio en Bogotá (INACTIVE)
--
-- 4. IDs de reservas:
--    - res001: Reserva confirmada de María en acc001
--    - res002: Reserva pendiente de María en acc002