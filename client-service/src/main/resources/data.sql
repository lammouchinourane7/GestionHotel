INSERT INTO clients (
    id, first_name, last_name, email, phone, address, national_id, created_at, updated_at,
    stay_preferred_room_type, stay_preferred_bed_type, stay_preferred_floor,
    stay_quiet_room, stay_non_smoking, stay_high_floor, stay_needs_baby_bed, stay_special_requests
)
VALUES (
    1, 'Nour', 'Haddad', 'nour.haddad@example.com', '+21650111222', 'Tunis Centre', 'CIN10001',
    '2026-04-01T10:00:00', '2026-04-10T16:45:00',
    'DELUXE', 'KING', 4, true, true, true, false, 'Vue degagee et chambre calme'
);

INSERT INTO clients (
    id, first_name, last_name, email, phone, address, national_id, created_at, updated_at,
    stay_preferred_room_type, stay_preferred_bed_type, stay_preferred_floor,
    stay_quiet_room, stay_non_smoking, stay_high_floor, stay_needs_baby_bed, stay_special_requests
)
VALUES (
    2, 'Amine', 'Ben Salem', 'amine.bensalem@example.com', '+21655123456', 'Sousse', 'CIN10002',
    '2026-04-02T11:15:00', '2026-04-12T13:10:00',
    'SUITE', 'KING', 6, false, true, true, true, 'Lit bebe si disponible'
);

INSERT INTO clients (
    id, first_name, last_name, email, phone, address, national_id, created_at, updated_at,
    stay_preferred_room_type, stay_preferred_bed_type, stay_preferred_floor,
    stay_quiet_room, stay_non_smoking, stay_high_floor, stay_needs_baby_bed, stay_special_requests
)
VALUES (
    3, 'Leila', 'Trabelsi', 'leila.trabelsi@example.com', '+21699111222', 'Sfax', 'CIN10003',
    '2026-04-03T09:30:00', '2026-04-11T09:30:00',
    'STANDARD', 'TWIN', 2, true, false, false, false, 'Proche de l ascenseur'
);

INSERT INTO client_change_history (id, client_id, action, details, changed_by, changed_at)
VALUES (1, 1, 'CREATED', 'Client profile created from initial demo data.', 'seed', '2026-04-01T10:00:00');

INSERT INTO client_change_history (id, client_id, action, details, changed_by, changed_at)
VALUES (2, 1, 'PREFERENCES_UPDATED', 'Initial stay preferences loaded for demo purposes.', 'seed', '2026-04-10T16:45:00');

INSERT INTO client_change_history (id, client_id, action, details, changed_by, changed_at)
VALUES (3, 2, 'CREATED', 'Client profile created from initial demo data.', 'seed', '2026-04-02T11:15:00');

INSERT INTO client_change_history (id, client_id, action, details, changed_by, changed_at)
VALUES (4, 3, 'CREATED', 'Client profile created from initial demo data.', 'seed', '2026-04-03T09:30:00');

ALTER TABLE clients ALTER COLUMN id RESTART WITH 4;
ALTER TABLE client_change_history ALTER COLUMN id RESTART WITH 5;
