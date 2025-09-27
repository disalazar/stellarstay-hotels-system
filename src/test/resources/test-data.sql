-- Datos de ejemplo para las pruebas

-- Insertar habitaciones
INSERT INTO rooms (id, type, capacity, available) VALUES
(1, 'JUNIOR_SUITE', 2, true),
(2, 'KING_SUITE', 3, true),
(3, 'PRESIDENTIAL_SUITE', 4, true);

-- Insertar algunas reservas
INSERT INTO reservations (room_id, guest_name, guests, check_in_date, check_out_date, breakfast_included, total_price) VALUES
(1, 'Test User', 2, DATEADD('DAY', 1, CURRENT_DATE()), DATEADD('DAY', 3, CURRENT_DATE()), true, 350.0);
