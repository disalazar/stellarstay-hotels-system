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

