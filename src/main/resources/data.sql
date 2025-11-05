-- ========================================
-- SAMPLE DATA FOR ONLINE DOCTOR APPOINTMENT SYSTEM
-- Auto-loaded on application startup when SQL_INIT_MODE=always
-- ========================================

-- Clean up existing data (in reverse order of foreign keys)
DELETE FROM ratings;
DELETE FROM appointments;
DELETE FROM time_slots;
DELETE FROM availability_blocks;
DELETE FROM doctor_specialties;
DELETE FROM doctors;
DELETE FROM patients;
DELETE FROM users;
DELETE FROM specialties;

-- Reset sequences
ALTER SEQUENCE users_id_seq RESTART WITH 1;
ALTER SEQUENCE patients_id_seq RESTART WITH 1;
ALTER SEQUENCE doctors_id_seq RESTART WITH 1;
ALTER SEQUENCE specialties_id_seq RESTART WITH 1;
ALTER SEQUENCE availability_blocks_id_seq RESTART WITH 1;
ALTER SEQUENCE time_slots_id_seq RESTART WITH 1;
ALTER SEQUENCE appointments_id_seq RESTART WITH 1;
ALTER SEQUENCE ratings_id_seq RESTART WITH 1;

-- ========================================
-- 1. SPECIALTIES (7 records)
-- ========================================
INSERT INTO specialties (name, created_at, updated_at) VALUES
('Cardiology', NOW(), NOW()),
('Dermatology', NOW(), NOW()),
('Pediatrics', NOW(), NOW()),
('Neurology', NOW(), NOW()),
('Orthopedics', NOW(), NOW()),
('Oncology', NOW(), NOW()),
('Psychiatry', NOW(), NOW());

-- ========================================
-- 2. USERS (10 records: 5 patients + 5 doctors)
-- Password: "Password123!" (BCrypt hashed)
-- ========================================
INSERT INTO users (email, password_hash, role, created_at, updated_at) VALUES
-- Patients
('patient1@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', 'PATIENT', NOW(), NOW()),
('patient2@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', 'PATIENT', NOW(), NOW()),
('patient3@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', 'PATIENT', NOW(), NOW()),
('patient4@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', 'PATIENT', NOW(), NOW()),
('patient5@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', 'PATIENT', NOW(), NOW()),
-- Doctors
('dr.smith@hospital.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', 'DOCTOR', NOW(), NOW()),
('dr.nguyen@hospital.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', 'DOCTOR', NOW(), NOW()),
('dr.patel@hospital.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', 'DOCTOR', NOW(), NOW()),
('dr.lee@hospital.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', 'DOCTOR', NOW(), NOW()),
('dr.wilson@hospital.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', 'DOCTOR', NOW(), NOW());

-- ========================================
-- 3. PATIENTS (5 records)
-- ========================================
INSERT INTO patients (user_id, full_name, date_of_birth, gender, phone_number, created_at, updated_at) VALUES
(1, 'Nguyen Van An', '1990-05-15', 'MALE', '0901234567', NOW(), NOW()),
(2, 'Tran Thi Binh', '1985-08-22', 'FEMALE', '0912345678', NOW(), NOW()),
(3, 'Le Van Cuong', '1995-03-10', 'MALE', '0923456789', NOW(), NOW()),
(4, 'Pham Thi Dung', '1988-12-05', 'FEMALE', '0934567890', NOW(), NOW()),
(5, 'Hoang Van Em', '1992-07-18', 'MALE', '0945678901', NOW(), NOW());

-- ========================================
-- 4. DOCTORS (5 records) - WITHOUT BIO FIELD TO AVOID POSTGRESQL TEXT PARSING ERROR
-- ========================================
INSERT INTO doctors (user_id, full_name, degree, average_rating, created_at, updated_at) VALUES (6, 'Dr. John Smith', 'MD, FACC', 4.75, NOW(), NOW());
INSERT INTO doctors (user_id, full_name, degree, average_rating, created_at, updated_at) VALUES (7, 'Dr. Nguyen Van Hai', 'MD, PhD', 4.90, NOW(), NOW());
INSERT INTO doctors (user_id, full_name, degree, average_rating, created_at, updated_at) VALUES (8, 'Dr. Priya Patel', 'MD, FAAP', 4.85, NOW(), NOW());
INSERT INTO doctors (user_id, full_name, degree, average_rating, created_at, updated_at) VALUES (9, 'Dr. Sarah Lee', 'MD, PhD, FAAN', 4.60, NOW(), NOW());
INSERT INTO doctors (user_id, full_name, degree, average_rating, created_at, updated_at) VALUES (10, 'Dr. Michael Wilson', 'MD, FAAOS', 4.70, NOW(), NOW());

-- ========================================
-- 5. DOCTOR_SPECIALTIES (5 records)
-- ========================================
INSERT INTO doctor_specialties (doctor_id, specialty_id) VALUES
(1, 1), -- Dr. Smith - Cardiology
(2, 2), -- Dr. Nguyen - Dermatology
(3, 3), -- Dr. Patel - Pediatrics
(4, 4), -- Dr. Lee - Neurology
(5, 5); -- Dr. Wilson - Orthopedics

-- ========================================
-- 6. AVAILABILITY_BLOCKS (5 records)
-- ========================================
INSERT INTO availability_blocks (doctor_id, work_date, start_time, end_time, created_at, updated_at) VALUES
(1, CURRENT_DATE + INTERVAL '1 day', '08:00:00', '12:00:00', NOW(), NOW()),
(2, CURRENT_DATE + INTERVAL '1 day', '13:00:00', '17:00:00', NOW(), NOW()),
(3, CURRENT_DATE + INTERVAL '2 days', '09:00:00', '13:00:00', NOW(), NOW()),
(4, CURRENT_DATE + INTERVAL '3 days', '14:00:00', '18:00:00', NOW(), NOW()),
(5, CURRENT_DATE + INTERVAL '4 days', '08:00:00', '11:00:00', NOW(), NOW());

-- ========================================
-- 7. TIME_SLOTS (38 records - 30-minute slots)
-- ========================================
-- Block 1: Dr. Smith (8 slots)
INSERT INTO time_slots (availability_block_id, doctor_id, start_time, end_time, status, created_at) VALUES
(1, 1, (CURRENT_DATE + INTERVAL '1 day') + TIME '08:00:00', (CURRENT_DATE + INTERVAL '1 day') + TIME '08:30:00', 'AVAILABLE', NOW()),
(1, 1, (CURRENT_DATE + INTERVAL '1 day') + TIME '08:30:00', (CURRENT_DATE + INTERVAL '1 day') + TIME '09:00:00', 'AVAILABLE', NOW()),
(1, 1, (CURRENT_DATE + INTERVAL '1 day') + TIME '09:00:00', (CURRENT_DATE + INTERVAL '1 day') + TIME '09:30:00', 'BOOKED', NOW()),
(1, 1, (CURRENT_DATE + INTERVAL '1 day') + TIME '09:30:00', (CURRENT_DATE + INTERVAL '1 day') + TIME '10:00:00', 'AVAILABLE', NOW()),
(1, 1, (CURRENT_DATE + INTERVAL '1 day') + TIME '10:00:00', (CURRENT_DATE + INTERVAL '1 day') + TIME '10:30:00', 'AVAILABLE', NOW()),
(1, 1, (CURRENT_DATE + INTERVAL '1 day') + TIME '10:30:00', (CURRENT_DATE + INTERVAL '1 day') + TIME '11:00:00', 'AVAILABLE', NOW()),
(1, 1, (CURRENT_DATE + INTERVAL '1 day') + TIME '11:00:00', (CURRENT_DATE + INTERVAL '1 day') + TIME '11:30:00', 'AVAILABLE', NOW()),
(1, 1, (CURRENT_DATE + INTERVAL '1 day') + TIME '11:30:00', (CURRENT_DATE + INTERVAL '1 day') + TIME '12:00:00', 'AVAILABLE', NOW()),
-- Block 2: Dr. Nguyen (8 slots)
(2, 2, (CURRENT_DATE + INTERVAL '1 day') + TIME '13:00:00', (CURRENT_DATE + INTERVAL '1 day') + TIME '13:30:00', 'AVAILABLE', NOW()),
(2, 2, (CURRENT_DATE + INTERVAL '1 day') + TIME '13:30:00', (CURRENT_DATE + INTERVAL '1 day') + TIME '14:00:00', 'AVAILABLE', NOW()),
(2, 2, (CURRENT_DATE + INTERVAL '1 day') + TIME '14:00:00', (CURRENT_DATE + INTERVAL '1 day') + TIME '14:30:00', 'AVAILABLE', NOW()),
(2, 2, (CURRENT_DATE + INTERVAL '1 day') + TIME '14:30:00', (CURRENT_DATE + INTERVAL '1 day') + TIME '15:00:00', 'BOOKED', NOW()),
(2, 2, (CURRENT_DATE + INTERVAL '1 day') + TIME '15:00:00', (CURRENT_DATE + INTERVAL '1 day') + TIME '15:30:00', 'AVAILABLE', NOW()),
(2, 2, (CURRENT_DATE + INTERVAL '1 day') + TIME '15:30:00', (CURRENT_DATE + INTERVAL '1 day') + TIME '16:00:00', 'AVAILABLE', NOW()),
(2, 2, (CURRENT_DATE + INTERVAL '1 day') + TIME '16:00:00', (CURRENT_DATE + INTERVAL '1 day') + TIME '16:30:00', 'AVAILABLE', NOW()),
(2, 2, (CURRENT_DATE + INTERVAL '1 day') + TIME '16:30:00', (CURRENT_DATE + INTERVAL '1 day') + TIME '17:00:00', 'AVAILABLE', NOW()),
-- Block 3: Dr. Patel (8 slots)
(3, 3, (CURRENT_DATE + INTERVAL '2 days') + TIME '09:00:00', (CURRENT_DATE + INTERVAL '2 days') + TIME '09:30:00', 'AVAILABLE', NOW()),
(3, 3, (CURRENT_DATE + INTERVAL '2 days') + TIME '09:30:00', (CURRENT_DATE + INTERVAL '2 days') + TIME '10:00:00', 'AVAILABLE', NOW()),
(3, 3, (CURRENT_DATE + INTERVAL '2 days') + TIME '10:00:00', (CURRENT_DATE + INTERVAL '2 days') + TIME '10:30:00', 'AVAILABLE', NOW()),
(3, 3, (CURRENT_DATE + INTERVAL '2 days') + TIME '10:30:00', (CURRENT_DATE + INTERVAL '2 days') + TIME '11:00:00', 'BOOKED', NOW()),
(3, 3, (CURRENT_DATE + INTERVAL '2 days') + TIME '11:00:00', (CURRENT_DATE + INTERVAL '2 days') + TIME '11:30:00', 'AVAILABLE', NOW()),
(3, 3, (CURRENT_DATE + INTERVAL '2 days') + TIME '11:30:00', (CURRENT_DATE + INTERVAL '2 days') + TIME '12:00:00', 'AVAILABLE', NOW()),
(3, 3, (CURRENT_DATE + INTERVAL '2 days') + TIME '12:00:00', (CURRENT_DATE + INTERVAL '2 days') + TIME '12:30:00', 'AVAILABLE', NOW()),
(3, 3, (CURRENT_DATE + INTERVAL '2 days') + TIME '12:30:00', (CURRENT_DATE + INTERVAL '2 days') + TIME '13:00:00', 'AVAILABLE', NOW()),
-- Block 4: Dr. Lee (8 slots)
(4, 4, (CURRENT_DATE + INTERVAL '3 days') + TIME '14:00:00', (CURRENT_DATE + INTERVAL '3 days') + TIME '14:30:00', 'AVAILABLE', NOW()),
(4, 4, (CURRENT_DATE + INTERVAL '3 days') + TIME '14:30:00', (CURRENT_DATE + INTERVAL '3 days') + TIME '15:00:00', 'AVAILABLE', NOW()),
(4, 4, (CURRENT_DATE + INTERVAL '3 days') + TIME '15:00:00', (CURRENT_DATE + INTERVAL '3 days') + TIME '15:30:00', 'AVAILABLE', NOW()),
(4, 4, (CURRENT_DATE + INTERVAL '3 days') + TIME '15:30:00', (CURRENT_DATE + INTERVAL '3 days') + TIME '16:00:00', 'AVAILABLE', NOW()),
(4, 4, (CURRENT_DATE + INTERVAL '3 days') + TIME '16:00:00', (CURRENT_DATE + INTERVAL '3 days') + TIME '16:30:00', 'BOOKED', NOW()),
(4, 4, (CURRENT_DATE + INTERVAL '3 days') + TIME '16:30:00', (CURRENT_DATE + INTERVAL '3 days') + TIME '17:00:00', 'AVAILABLE', NOW()),
(4, 4, (CURRENT_DATE + INTERVAL '3 days') + TIME '17:00:00', (CURRENT_DATE + INTERVAL '3 days') + TIME '17:30:00', 'AVAILABLE', NOW()),
(4, 4, (CURRENT_DATE + INTERVAL '3 days') + TIME '17:30:00', (CURRENT_DATE + INTERVAL '3 days') + TIME '18:00:00', 'AVAILABLE', NOW()),
-- Block 5: Dr. Wilson (6 slots)
(5, 5, (CURRENT_DATE + INTERVAL '4 days') + TIME '08:00:00', (CURRENT_DATE + INTERVAL '4 days') + TIME '08:30:00', 'AVAILABLE', NOW()),
(5, 5, (CURRENT_DATE + INTERVAL '4 days') + TIME '08:30:00', (CURRENT_DATE + INTERVAL '4 days') + TIME '09:00:00', 'AVAILABLE', NOW()),
(5, 5, (CURRENT_DATE + INTERVAL '4 days') + TIME '09:00:00', (CURRENT_DATE + INTERVAL '4 days') + TIME '09:30:00', 'BOOKED', NOW()),
(5, 5, (CURRENT_DATE + INTERVAL '4 days') + TIME '09:30:00', (CURRENT_DATE + INTERVAL '4 days') + TIME '10:00:00', 'AVAILABLE', NOW()),
(5, 5, (CURRENT_DATE + INTERVAL '4 days') + TIME '10:00:00', (CURRENT_DATE + INTERVAL '4 days') + TIME '10:30:00', 'AVAILABLE', NOW()),
(5, 5, (CURRENT_DATE + INTERVAL '4 days') + TIME '10:30:00', (CURRENT_DATE + INTERVAL '4 days') + TIME '11:00:00', 'AVAILABLE', NOW());


-- Fix TEXT column types (convert from oid to TEXT)
ALTER TABLE appointments ALTER COLUMN symptoms TYPE TEXT;
ALTER TABLE appointments ALTER COLUMN suspected_disease TYPE TEXT;
ALTER TABLE ratings ALTER COLUMN feedback_text TYPE TEXT;
ALTER TABLE doctors ALTER COLUMN bio TYPE TEXT;

-- ========================================
-- 8. APPOINTMENTS (5 records)
-- ========================================
INSERT INTO appointments (patient_id, doctor_id, time_slot_id, symptoms, suspected_disease, status, reschedule_count, reminder_sent, created_at) VALUES (1, 1, 3, 'Chest pain and shortness of breath', 'Possible angina', 'PENDING', 0, false, NOW());
INSERT INTO appointments (patient_id, doctor_id, time_slot_id, symptoms, suspected_disease, status, reschedule_count, reminder_sent, created_at) VALUES (2, 2, 12, 'Skin rash and itching on arms', 'Eczema or allergic reaction', 'PENDING', 0, false, NOW());
INSERT INTO appointments (patient_id, doctor_id, time_slot_id, symptoms, suspected_disease, status, reschedule_count, reminder_sent, created_at) VALUES (3, 3, 20, 'Child has fever and cough for 3 days', 'Upper respiratory infection', 'PENDING', 0, false, NOW());
INSERT INTO appointments (patient_id, doctor_id, time_slot_id, symptoms, suspected_disease, status, reschedule_count, reminder_sent, created_at) VALUES (4, 4, 29, 'Severe headaches and dizziness', 'Migraine', 'COMPLETED', 0, true, NOW() - INTERVAL '7 days');
INSERT INTO appointments (patient_id, doctor_id, time_slot_id, symptoms, suspected_disease, status, reschedule_count, reminder_sent, created_at) VALUES (5, 5, 35, 'Knee pain after sports injury', 'Ligament strain', 'PENDING', 0, false, NOW());

-- ========================================
-- 9. RATINGS (5 records) - ONE RATING PER APPOINTMENT
-- ========================================
INSERT INTO ratings (appointment_id, patient_id, doctor_id, stars, feedback_text, created_at) VALUES (1, 1, 1, 5, 'Dr. Smith was very professional and thorough. Highly recommended!', NOW() - INTERVAL '5 days');
INSERT INTO ratings (appointment_id, patient_id, doctor_id, stars, feedback_text, created_at) VALUES (2, 2, 2, 4, 'Good consultation, but waiting time was a bit long.', NOW() - INTERVAL '4 days');
INSERT INTO ratings (appointment_id, patient_id, doctor_id, stars, feedback_text, created_at) VALUES (3, 3, 3, 5, 'Excellent doctor! Very knowledgeable and caring.', NOW() - INTERVAL '3 days');
INSERT INTO ratings (appointment_id, patient_id, doctor_id, stars, feedback_text, created_at) VALUES (4, 4, 4, 4, 'Dr. Lee listened to all my concerns and provided detailed answers.', NOW() - INTERVAL '2 days');
INSERT INTO ratings (appointment_id, patient_id, doctor_id, stars, feedback_text, created_at) VALUES (5, 5, 5, 5, 'Best orthopedist I have ever consulted. Professional and empathetic.', NOW() - INTERVAL '1 day');