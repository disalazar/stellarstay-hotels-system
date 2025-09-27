-- Esquema de la base de datos para pruebas

-- Limpiar tablas si existen
DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS rooms;

-- Crear tabla de habitaciones
CREATE TABLE rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    capacity INTEGER NOT NULL,
    available BOOLEAN NOT NULL DEFAULT TRUE
);

-- Crear tabla de reservas
CREATE TABLE reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT NOT NULL,
    guest_name VARCHAR(100) NOT NULL,
    guests INTEGER NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    breakfast_included BOOLEAN NOT NULL DEFAULT FALSE,
    total_price DOUBLE NOT NULL,
    FOREIGN KEY (room_id) REFERENCES rooms(id)
);
