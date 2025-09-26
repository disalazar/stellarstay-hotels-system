INSERT INTO rooms (id, type, capacity, available)
VALUES (1, 'JUNIOR_SUITE', 2, true)
ON CONFLICT (id) DO NOTHING;

INSERT INTO rooms (id, type, capacity, available)
VALUES (2, 'KING_SUITE', 3, true)
ON CONFLICT (id) DO NOTHING;

INSERT INTO rooms (id, type, capacity, available)
VALUES (3, 'PRESIDENTIAL_SUITE', 4, true)
ON CONFLICT (id) DO NOTHING;

INSERT INTO reservations (id, room_id, guest_name, guests, check_in_date, check_out_date, breakfast_included, total_price)
VALUES (1, 1, 'Juan Pérez', 2, '2025-09-26', '2025-09-28', true, 500.0)
ON CONFLICT (id) DO NOTHING;
