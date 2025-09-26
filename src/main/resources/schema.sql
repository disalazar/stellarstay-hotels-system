CREATE TABLE IF NOT EXISTS rooms (
    id SERIAL PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    capacity INT NOT NULL,
    available BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS reservations (
    id SERIAL PRIMARY KEY,
    room_id INT NOT NULL REFERENCES rooms(id),
    guest_name VARCHAR(255) NOT NULL,
    guests INT NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    breakfast_included BOOLEAN NOT NULL,
    total_price DOUBLE PRECISION NOT NULL
);

-- Composite index for quick searches by type, capacity, and availability
CREATE INDEX IF NOT EXISTS idx_rooms_type_capacity_available
    ON rooms (type, capacity, available);

-- Composite index for efficient searches of overlapping reservations
CREATE INDEX IF NOT EXISTS idx_reservations_room_dates
    ON reservations (room_id, check_in_date, check_out_date);
