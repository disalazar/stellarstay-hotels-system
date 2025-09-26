INSERT INTO rooms (type, capacity, available)
VALUES ('JUNIOR_SUITE', 2, true)
ON CONFLICT (id) DO NOTHING;

INSERT INTO rooms (type, capacity, available)
VALUES ('KING_SUITE', 3, true)
ON CONFLICT (id) DO NOTHING;

INSERT INTO rooms (type, capacity, available)
VALUES ('PRESIDENTIAL_SUITE', 4, true)
ON CONFLICT (id) DO NOTHING;
