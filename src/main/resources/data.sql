INSERT INTO specialties (name, created_at, updated_at) VALUES
('Cardiology', NOW(), NOW()),
('Dermatology', NOW(), NOW()),
('Pediatrics', NOW(), NOW()),
('Neurology', NOW(), NOW()),
('Orthopedics', NOW(), NOW()),
('Oncology', NOW(), NOW()),
('Psychiatry', NOW(), NOW())
ON CONFLICT (name) DO NOTHING;
