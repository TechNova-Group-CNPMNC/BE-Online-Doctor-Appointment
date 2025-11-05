-- ========================================
-- CLEAR EXISTING DATA AND RESET SEQUENCES
-- ========================================
TRUNCATE TABLE ratings CASCADE;
TRUNCATE TABLE appointments CASCADE;
TRUNCATE TABLE time_slots CASCADE;
TRUNCATE TABLE availability_blocks CASCADE;
TRUNCATE TABLE doctor_specialties CASCADE;
TRUNCATE TABLE doctors CASCADE;
TRUNCATE TABLE patients CASCADE;
TRUNCATE TABLE specialties CASCADE;
TRUNCATE TABLE users CASCADE;

-- Reset sequences to start from 1
ALTER SEQUENCE IF EXISTS users_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS doctors_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS patients_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS specialties_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS availability_blocks_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS time_slots_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS appointments_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS ratings_id_seq RESTART WITH 1;

-- ========================================
-- 1. INSERT SPECIALTIES
-- ========================================
INSERT INTO specialties (name, created_at, updated_at) VALUES
('Cardiology', NOW(), NOW()),
('Dermatology', NOW(), NOW()),
('Pediatrics', NOW(), NOW()),
('Neurology', NOW(), NOW()),
('Orthopedics', NOW(), NOW()),
('Oncology', NOW(), NOW()),
('Psychiatry', NOW(), NOW()),
('Ophthalmology', NOW(), NOW()),
('ENT', NOW(), NOW()),
('General Medicine', NOW(), NOW())
ON CONFLICT (name) DO NOTHING;

-- ========================================
-- 2. INSERT USERS
-- ========================================
-- Password: "password123"
-- Hash: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

-- PATIENTS
INSERT INTO users (email, password_hash, role, created_at, updated_at) VALUES
('patient1@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'PATIENT', NOW(), NOW()),
('patient2@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'PATIENT', NOW(), NOW()),
('patient3@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'PATIENT', NOW(), NOW()),
('patient4@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'PATIENT', NOW(), NOW()),
('patient5@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'PATIENT', NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- DOCTORS
INSERT INTO users (email, password_hash, role, created_at, updated_at) VALUES
('doctor1@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'DOCTOR', NOW(), NOW()),
('doctor2@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'DOCTOR', NOW(), NOW()),
('doctor3@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'DOCTOR', NOW(), NOW()),
('doctor4@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'DOCTOR', NOW(), NOW()),
('doctor5@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'DOCTOR', NOW(), NOW()),
('doctor6@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'DOCTOR', NOW(), NOW()),
('doctor7@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'DOCTOR', NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- ========================================
-- 3. INSERT PATIENTS
-- ========================================
INSERT INTO patients (user_id, full_name, date_of_birth, gender, phone_number, created_at, updated_at)
SELECT
    u.id,
    data.full_name,
    data.date_of_birth::date,
    data.gender::varchar,
    data.phone_number,
    NOW(),
    NOW()
FROM (VALUES
    ('patient1@example.com', 'John Smith', '1990-05-15', 'MALE', '+84901234567'),
    ('patient2@example.com', 'Jane Doe', '1992-08-20', 'FEMALE', '+84901234568'),
    ('patient3@example.com', 'Michael Johnson', '1988-03-10', 'MALE', '+84901234569'),
    ('patient4@example.com', 'Emily Wilson', '1995-11-25', 'FEMALE', '+84901234570'),
    ('patient5@example.com', 'David Brown', '1985-07-30', 'MALE', '+84901234571')
) AS data(email, full_name, date_of_birth, gender, phone_number)
JOIN users u ON u.email = data.email
ON CONFLICT (user_id) DO NOTHING;

-- ========================================
-- 4. INSERT DOCTORS
-- ========================================
INSERT INTO doctors (user_id, full_name, degree, bio, average_rating, created_at, updated_at)
SELECT
    u.id,
    data.full_name,
    data.degree,
    lo_from_bytea(0, data.bio::bytea),
    data.rating::numeric,
    NOW(),
    NOW()
FROM (VALUES
    ('doctor1@example.com', 'Dr. Robert Brown', 'MD, FACC', 'Experienced cardiologist with 15+ years of practice. Specialized in heart disease prevention and treatment.', '4.8'),
    ('doctor2@example.com', 'Dr. Sarah Davis', 'MD, Dermatology', 'Expert in skin care and cosmetic dermatology. Focus on acne treatment and anti-aging solutions.', '4.6'),
    ('doctor3@example.com', 'Dr. David Miller', 'MD, Pediatrics', 'Caring pediatrician dedicated to children health. Specialized in child development and vaccinations.', '4.9'),
    ('doctor4@example.com', 'Dr. Lisa Anderson', 'MD, Neurology', 'Neurologist specializing in brain and nervous system disorders. Expert in migraine and epilepsy treatment.', '4.7'),
    ('doctor5@example.com', 'Dr. James Wilson', 'MD, Orthopedics', 'Orthopedic surgeon with expertise in joint replacement and sports injuries.', '4.5'),
    ('doctor6@example.com', 'Dr. Maria Garcia', 'MD, Oncology', 'Oncologist specializing in cancer treatment and chemotherapy. Compassionate care for cancer patients.', '4.8'),
    ('doctor7@example.com', 'Dr. Thomas Lee', 'MD, Psychiatry', 'Psychiatrist with expertise in mental health and behavioral disorders. Focus on anxiety and depression treatment.', '4.7')
) AS data(email, full_name, degree, bio, rating)
JOIN users u ON u.email = data.email
ON CONFLICT (user_id) DO NOTHING;

-- ========================================
-- 5. INSERT DOCTOR_SPECIALTIES (Many-to-Many)
-- ========================================
INSERT INTO doctor_specialties (doctor_id, specialty_id)
SELECT d.id, s.id
FROM (VALUES
    ('doctor1@example.com', 'Cardiology'),
    ('doctor2@example.com', 'Dermatology'),
    ('doctor3@example.com', 'Pediatrics'),
    ('doctor4@example.com', 'Neurology'),
    ('doctor5@example.com', 'Orthopedics'),
    ('doctor6@example.com', 'Oncology'),
    ('doctor7@example.com', 'Psychiatry'),
    ('doctor1@example.com', 'General Medicine'),
    ('doctor3@example.com', 'General Medicine')
) AS data(doctor_email, specialty_name)
JOIN users u ON u.email = data.doctor_email
JOIN doctors d ON d.user_id = u.id
JOIN specialties s ON s.name = data.specialty_name
ON CONFLICT (doctor_id, specialty_id) DO NOTHING;

-- ========================================
-- 6. INSERT AVAILABILITY_BLOCKS
-- ========================================
INSERT INTO availability_blocks (doctor_id, work_date, start_time, end_time, created_at, updated_at)
SELECT
    d.id,
    work_date,
    start_time,
    end_time,
    NOW(),
    NOW()
FROM doctors d
CROSS JOIN LATERAL (
    SELECT
        (CURRENT_DATE + day_offset)::date AS work_date,
        '08:00:00'::time AS start_time,
        '12:00:00'::time AS end_time
    FROM generate_series(0, 13) AS day_offset
    WHERE EXTRACT(DOW FROM CURRENT_DATE + day_offset) BETWEEN 1 AND 5

    UNION ALL

    SELECT
        (CURRENT_DATE + day_offset)::date AS work_date,
        '13:00:00'::time AS start_time,
        '17:00:00'::time AS end_time
    FROM generate_series(0, 13) AS day_offset
    WHERE EXTRACT(DOW FROM CURRENT_DATE + day_offset) BETWEEN 1 AND 5
) AS schedule
ON CONFLICT DO NOTHING;

-- ========================================
-- 7. INSERT TIME_SLOTS
-- ========================================
INSERT INTO time_slots (availability_block_id, doctor_id, start_time, end_time, status, created_at)
SELECT
    ab.id,
    ab.doctor_id,
    (ab.work_date + slot_time.start_offset)::timestamp,
    (ab.work_date + slot_time.end_offset)::timestamp,
    'AVAILABLE',
    NOW()
FROM availability_blocks ab
CROSS JOIN LATERAL (
    SELECT
        (ab.start_time + (slot * interval '30 minutes')) AS start_offset,
        (ab.start_time + ((slot + 1) * interval '30 minutes')) AS end_offset
    FROM generate_series(0,
        EXTRACT(EPOCH FROM (ab.end_time - ab.start_time))::integer / 1800 - 1
    ) AS slot
) AS slot_time
ON CONFLICT DO NOTHING;

-- -- ========================================
-- -- 8. INSERT APPOINTMENTS (Sample)
-- -- ========================================
-- INSERT INTO appointments (patient_id, doctor_id, time_slot_id, symptoms, suspected_disease, status, reschedule_count, reminder_sent, created_at)
-- SELECT
--     p.id,
--     d.id,
--     ts.id,
--     lo_from_bytea(0, data.symptoms::bytea),  -- Cast symptoms to oid
--     data.suspected_disease,
--     data.status::varchar,
--     0,
--     CASE WHEN data.status = 'COMPLETED' THEN true ELSE false END,
--     NOW()
-- FROM (VALUES
--     ('patient1@example.com', 'doctor1@example.com', 'Chest pain, shortness of breath', 'Possible heart condition', 'COMPLETED'),
--     ('patient2@example.com', 'doctor2@example.com', 'Skin rash on arms', 'Eczema', 'PENDING'),
--     ('patient3@example.com', 'doctor3@example.com', 'Child fever and cough', 'Common cold', 'CONFIRMED'),
--     ('patient4@example.com', 'doctor4@example.com', 'Severe headache', 'Migraine', 'COMPLETED'),
--     ('patient5@example.com', 'doctor5@example.com', 'Knee pain after exercise', 'Sports injury', 'PENDING')
-- ) AS data(patient_email, doctor_email, symptoms, suspected_disease, status)
-- JOIN users pu ON pu.email = data.patient_email
-- JOIN patients p ON p.user_id = pu.id
-- JOIN users du ON du.email = data.doctor_email
-- JOIN doctors d ON d.user_id = du.id
-- CROSS JOIN LATERAL (
--     SELECT id
--     FROM time_slots
--     WHERE doctor_id = d.id
--     AND status = 'AVAILABLE'
--     AND start_time > NOW()
--     ORDER BY start_time
--     LIMIT 1
-- ) AS ts
-- ON CONFLICT DO NOTHING;

-- -- Update booked time slots
-- UPDATE time_slots
-- SET status = 'BOOKED'
-- WHERE id IN (SELECT time_slot_id FROM appointments);

-- -- ========================================
-- -- 9. INSERT RATINGS
-- -- ========================================
-- INSERT INTO ratings (appointment_id, patient_id, doctor_id, stars, feedback_text, created_at)
-- SELECT
--     a.id,
--     a.patient_id,
--     a.doctor_id,
--     data.stars::integer,
--     data.feedback,
--     NOW()
-- FROM appointments a
-- JOIN (VALUES
--     ('Chest pain, shortness of breath', '5', 'Excellent doctor! Very thorough examination and clear explanation.'),
--     ('Severe headache', '4', 'Good consultation. Helped me understand my condition better.')
-- ) AS data(symptoms, stars, feedback) ON lo_get(a.symptoms) = data.symptoms::bytea
-- WHERE a.status = 'COMPLETED'
-- ON CONFLICT (appointment_id) DO NOTHING;

-- -- ========================================
-- -- VERIFY DATA
-- -- ========================================
-- SELECT 'Specialties' as table_name, COUNT(*) as count FROM specialties
-- UNION ALL
-- SELECT 'Users', COUNT(*) FROM users
-- UNION ALL
-- SELECT 'Patients', COUNT(*) FROM patients
-- UNION ALL
-- SELECT 'Doctors', COUNT(*) FROM doctors
-- UNION ALL
-- SELECT 'Doctor Specialties', COUNT(*) FROM doctor_specialties
-- UNION ALL
-- SELECT 'Availability Blocks', COUNT(*) FROM availability_blocks
-- UNION ALL
-- SELECT 'Time Slots', COUNT(*) FROM time_slots
-- UNION ALL
-- SELECT 'Appointments', COUNT(*) FROM appointments
-- UNION ALL
-- SELECT 'Ratings', COUNT(*) FROM ratings;