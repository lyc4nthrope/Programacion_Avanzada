-- ================================================
-- ESQUEMA DE BASE DE DATOS - ACCOMMODATION PLATFORM
-- ================================================
-- Este archivo es solo de referencia.
-- Hibernate creará automáticamente las tablas con spring.jpa.hibernate.ddl-auto=update
-- ================================================

-- Tabla de Usuarios
CREATE TABLE IF NOT EXISTS user (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(200) NOT NULL,
    photo_url VARCHAR(200),
    date_birth DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_status (status)
);

-- Tabla de Perfiles de Anfitrión
CREATE TABLE IF NOT EXISTS host_profile (
    id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL UNIQUE,
    about_me LONGTEXT NOT NULL,
    legal_document VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,

    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- Tabla de Alojamientos
CREATE TABLE IF NOT EXISTS accommodation (
    id VARCHAR(255) PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    city VARCHAR(100) NOT NULL,
    address VARCHAR(150) NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    price_per_night DOUBLE NOT NULL,
    max_capacity INT NOT NULL,
    amenities VARCHAR(500),
    average_rating DOUBLE DEFAULT 0.0,
    rating_count INT DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    host_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,

    INDEX idx_city (city),
    INDEX idx_price (price_per_night),
    INDEX idx_rating (average_rating),
    INDEX idx_status (status),
    FOREIGN KEY (host_id) REFERENCES user(id) ON DELETE CASCADE
);

-- Tabla de Fotos de Alojamiento
CREATE TABLE IF NOT EXISTS accommodation_photo (
    id VARCHAR(255) PRIMARY KEY,
    accommodation_id VARCHAR(255) NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    FOREIGN KEY (accommodation_id) REFERENCES accommodation(id) ON DELETE CASCADE
);

-- Tabla de Calendario de Disponibilidad
CREATE TABLE IF NOT EXISTS availability_calendar (
    id VARCHAR(255) PRIMARY KEY,
    accommodation_id VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    available BOOLEAN NOT NULL DEFAULT TRUE,
    reason VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    UNIQUE KEY unique_accommodation_date (accommodation_id, date),
    FOREIGN KEY (accommodation_id) REFERENCES accommodation(id) ON DELETE CASCADE
);

-- Tabla de Reservas
CREATE TABLE IF NOT EXISTS reservation (
    id VARCHAR(255) PRIMARY KEY,
    accommodation_id VARCHAR(255) NOT NULL,
    guest_id VARCHAR(255) NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    number_of_guests INT NOT NULL,
    total_price DOUBLE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,

    INDEX idx_guest (guest_id),
    INDEX idx_accommodation (accommodation_id),
    INDEX idx_dates (check_in_date, check_out_date),
    INDEX idx_status (status),
    FOREIGN KEY (accommodation_id) REFERENCES accommodation(id) ON DELETE CASCADE,
    FOREIGN KEY (guest_id) REFERENCES user(id) ON DELETE CASCADE
);

-- Tabla de Pagos
CREATE TABLE IF NOT EXISTS payment (
    id VARCHAR(255) PRIMARY KEY,
    reservation_id VARCHAR(255) NOT NULL UNIQUE,
    amount DOUBLE NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    transaction_reference VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,

    INDEX idx_reservation (reservation_id),
    INDEX idx_status (status),
    FOREIGN KEY (reservation_id) REFERENCES reservation(id) ON DELETE CASCADE
);

-- Tabla de Reseñas
CREATE TABLE IF NOT EXISTS review (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    accommodation_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    reservation_id VARCHAR(255),
    comment TEXT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,

    -- Campos del embeddable Answer
    text TEXT,
    response_date TIMESTAMP,

    INDEX idx_accommodation (accommodation_id),
    INDEX idx_user (user_id),
    INDEX idx_reservation (reservation_id),
    FOREIGN KEY (accommodation_id) REFERENCES accommodation(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (reservation_id) REFERENCES reservation(id) ON DELETE SET NULL
);

-- Tabla de Favoritos
CREATE TABLE IF NOT EXISTS favorite (
    id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    accommodation_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    UNIQUE KEY unique_user_accommodation (user_id, accommodation_id),
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (accommodation_id) REFERENCES accommodation(id) ON DELETE CASCADE
);

-- Tabla de Chats
CREATE TABLE IF NOT EXISTS chat (
    id VARCHAR(255) PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Tabla intermedia Chat-Usuario
CREATE TABLE IF NOT EXISTS chat_users (
    chat_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,

    PRIMARY KEY (chat_id, user_id),
    FOREIGN KEY (chat_id) REFERENCES chat(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- Tabla de Mensajes
CREATE TABLE IF NOT EXISTS message (
    id VARCHAR(255) PRIMARY KEY,
    chat_id VARCHAR(255) NOT NULL,
    sender_id VARCHAR(255) NOT NULL,
    recipient_id VARCHAR(255) NOT NULL,
    content VARCHAR(1000) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    INDEX idx_chat (chat_id),
    INDEX idx_sender (sender_id),
    INDEX idx_read (is_read),
    FOREIGN KEY (chat_id) REFERENCES chat(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (recipient_id) REFERENCES user(id) ON DELETE CASCADE
);

-- Tabla de Códigos de Recuperación de Contraseña
CREATE TABLE IF NOT EXISTS password_reset_code (
    id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    code VARCHAR(10) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,

    INDEX idx_user (user_id),
    INDEX idx_code (code),
    INDEX idx_expires (expires_at),
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- ================================================
-- DATOS DE PRUEBA (OPCIONAL)
-- ================================================

-- Insertar usuarios de prueba
INSERT INTO user (id, name, email, password, date_birth, role, status) VALUES
('user-1', 'Juan Pérez', 'juan@email.com', '$2a$10$encrypted_password', '1990-01-15', 'GUEST', 'ACTIVE'),
('user-2', 'María López', 'maria@email.com', '$2a$10$encrypted_password', '1985-05-20', 'HOST', 'ACTIVE'),
('user-3', 'Carlos Ruiz', 'carlos@email.com', '$2a$10$encrypted_password', '1992-08-10', 'ADMIN', 'ACTIVE');

-- Insertar perfil de anfitrión
INSERT INTO host_profile (id, user_id, about_me, created_at, updated_at) VALUES
('hp-1', 'user-2', 'Soy anfitrión profesional con más de 5 años de experiencia.', NOW(), NOW());

-- Insertar alojamiento de prueba
INSERT INTO accommodation (id, title, description, city, address, latitude, longitude, price_per_night, max_capacity, host_id, status) VALUES
('acc-1', 'Casa en Bogotá', 'Hermosa casa con vista a la ciudad', 'Bogota', 'Calle 50 #10-20', 4.7110, -74.0721, 150.00, 5, 'user-2', 'ACTIVE');

-- ================================================
-- NOTAS IMPORTANTES
-- ================================================
-- 1. Este archivo es SOLO de referencia
-- 2. Hibernate creará las tablas automáticamente
-- 3. No ejecutes este script manualmente a menos que:
--    - Desees crear la base de datos desde cero
--    - Estés en producción y prefieras control manual
-- 4. Si usas este script, cambia en application.properties:
--    spring.jpa.hibernate.ddl-auto=none