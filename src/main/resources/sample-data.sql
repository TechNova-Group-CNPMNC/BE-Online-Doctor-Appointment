-- ========================================
-- SAMPLE DATA FOR ONLINE DOCTOR APPOINTMENT SYSTEM
-- Generated: November 4, 2025
-- Description: Insert 5 sample records for each table
-- ========================================

-- ========================================
-- 1. SPECIALTIES TABLE (7 records - medical specialties)
-- ========================================
INSERT INTO specialties (id, name, created_at, updated_at) VALUES
(1, 'Cardiology', NOW(), NOW()),
(2, 'Dermatology', NOW(), NOW()),
(3, 'Pediatrics', NOW(), NOW()),
(4, 'Neurology', NOW(), NOW()),
(5, 'Orthopedics', NOW(), NOW()),
(6, 'Oncology', NOW(), NOW()),
(7, 'Psychiatry', NOW(), NOW())
ON CONFLICT (name) DO NOTHING;

-- ========================================
-- 2. USERS TABLE (10 records - 5 patients + 5 doctors)
-- Password for all users: "Password123!" (BCrypt hashed)
-- ========================================
INSERT INTO users (id, email, password_hash, role, created_at, updated_at) VALUES
-- Patients (IDs 1-5)
(1, 'patient1@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', 'PATIENT', NOW(), NOW()),
(2, 'patient2@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', 'PATIENT', NOW(), NOW()),
(3, 'patient3@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', 'PATIENT', NOW(), NOW()),
(4, 'patient4@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', 'PATIENT', NOW(), NOW()),
(5, 'patient5@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', 'PATIENT', NOW(), NOW()),

-- Doctors (IDs 6-10)
(6, 'dr.smith@hospital.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', 'DOCTOR', NOW(), NOW()),
(7, 'dr.nguyen@hospital.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', 'DOCTOR', NOW(), NOW()),
(8, 'dr.patel@hospital.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', 'DOCTOR', NOW(), NOW()),
(9, 'dr.lee@hospital.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', 'DOCTOR', NOW(), NOW()),
(10, 'dr.wilson@hospital.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', 'DOCTOR', NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- Reset sequence for users
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));

-- ========================================
-- 3. PATIENTS TABLE (5 records)
-- ========================================
INSERT INTO patients (id, user_id, full_name, date_of_birth, gender, phone_number, created_at, updated_at) VALUES
(1, 1, 'Nguyen Van An', '1990-05-15', 'MALE', '0901234567', NOW(), NOW()),
(2, 2, 'Tran Thi Binh', '1985-08-22', 'FEMALE', '0912345678', NOW(), NOW()),
(3, 3, 'Le Van Cuong', '1995-03-10', 'MALE', '0923456789', NOW(), NOW()),
(4, 4, 'Pham Thi Dung', '1988-12-05', 'FEMALE', '0934567890', NOW(), NOW()),
(5, 5, 'Hoang Van Em', '1992-07-18', 'MALE', '0945678901', NOW(), NOW())
ON CONFLICT (phone_number) DO NOTHING;

-- Reset sequence for patients
SELECT setval('patients_id_seq', (SELECT MAX(id) FROM patients));

-- ========================================
-- 4. DOCTORS TABLE (5 records)
-- ========================================
INSERT INTO doctors (id, user_id, full_name, degree, bio, average_rating, created_at, updated_at) VALUES
(1, 6, 'Dr. John Smith', 'MD, FACC', 'Experienced cardiologist with 15 years of practice. Specialized in heart disease prevention and treatment.', 4.75, NOW(), NOW()),
(2, 7, 'Dr. Nguyen Van Hai', 'MD, PhD', 'Expert dermatologist focusing on skin cancer detection and cosmetic dermatology. Over 10 years of experience.', 4.90, NOW(), NOW()),
(3, 8, 'Dr. Priya Patel', 'MD, FAAP', 'Dedicated pediatrician caring for children from newborns to adolescents. Gentle and patient-focused approach.', 4.85, NOW(), NOW()),
(4, 9, 'Dr. Sarah Lee', 'MD, PhD, FAAN', 'Renowned neurologist specializing in migraine treatment and neurological disorders. Published researcher.', 4.60, NOW(), NOW()),
(5, 10, 'Dr. Michael Wilson', 'MD, FAAOS', 'Orthopedic surgeon expert in sports medicine and joint replacement. Team doctor for professional athletes.', 4.70, NOW(), NOW())
ON CONFLICT (user_id) DO NOTHING;

-- Reset sequence for doctors
SELECT setval('doctors_id_seq', (SELECT MAX(id) FROM doctors));

-- ========================================
-- 5. DOCTOR_SPECIALTIES TABLE (Many-to-Many relationship)
-- Assign 1-2 specialties per doctor
-- ========================================
INSERT INTO doctor_specialties (doctor_id, specialty_id) VALUES
-- Dr. Smith - Cardiology
(1, 1),
-- Dr. Nguyen - Dermatology
(2, 2),
-- Dr. Patel - Pediatrics
(3, 3),
-- Dr. Lee - Neurology
(4, 4),
-- Dr. Wilson - Orthopedics
(5, 5)
ON CONFLICT DO NOTHING;

-- ========================================
-- 6. AVAILABILITY_BLOCKS TABLE (5 records)
-- Create availability blocks for next 7 days
-- ========================================
INSERT INTO availability_blocks (id, doctor_id, work_date, start_time, end_time, created_at, updated_at) VALUES
(1, 1, CURRENT_DATE + INTERVAL '1 day', '08:00:00', '12:00:00', NOW(), NOW()),
(2, 2, CURRENT_DATE + INTERVAL '1 day', '13:00:00', '17:00:00', NOW(), NOW()),
(3, 3, CURRENT_DATE + INTERVAL '2 days', '09:00:00', '13:00:00', NOW(), NOW()),
(4, 4, CURRENT_DATE + INTERVAL '3 days', '14:00:00', '18:00:00', NOW(), NOW()),
(5, 5, CURRENT_DATE + INTERVAL '4 days', '08:00:00', '11:00:00', NOW(), NOW());

-- Reset sequence for availability_blocks
SELECT setval('availability_blocks_id_seq', (SELECT MAX(id) FROM availability_blocks));

-- ========================================
-- 7. TIME_SLOTS TABLE (40 records - 8 slots per block)
-- Each block generates 30-minute time slots
-- ========================================

-- Block 1: Dr. Smith - 08:00-12:00 (8 slots of 30 min)
INSERT INTO time_slots (id, availability_block_id, doctor_id, start_time, end_time, status, created_at) VALUES
(1, 1, 1, (CURRENT_DATE + INTERVAL '1 day') + TIME '08:00:00', (CURRENT_DATE + INTERVAL '1 day') + TIME '08:30:00', 'AVAILABLE', NOW()),
(2, 1, 1, (CURRENT_DATE + INTERVAL '1 day') + TIME '08:30:00', (CURRENT_DATE + INTERVAL '1 day') + TIME '09:00:00', 'AVAILABLE', NOW()),
(3, 1, 1, (CURRENT_DATE + INTERVAL '1 day') + TIME '09:00:00', (CURRENT_DATE + INTERVAL '1 day') + TIME '09:30:00', 'BOOKED', NOW()),
(4, 1, 1, (CURRENT_DATE + INTERVAL '1 day') + TIME '09:30:00', (CURRENT_DATE + INTERVAL '1 day') + TIME '10:00:00', 'AVAILABLE', NOW()),
(5, 1, 1, (CURRENT_DATE + INTERVAL '1 day') + TIME '10:00:00', (CURRENT_DATE + INTERVAL '1 day') + TIME '10:30:00', 'AVAILABLE', NOW()),
(6, 1, 1, (CURRENT_DATE + INTERVAL '1 day') + TIME '10:30:00', (CURRENT_DATE + INTERVAL '1 day') + TIME '11:00:00', 'AVAILABLE', NOW()),
(7, 1, 1, (CURRENT_DATE + INTERVAL '1 day') + TIME '11:00:00', (CURRENT_DATE + INTERVAL '1 day') + TIME '11:30:00', 'AVAILABLE', NOW()),
(8, 1, 1, (CURRENT_DATE + INTERVAL '1 day') + TIME '11:30:00', (CURRENT_DATE + INTERVAL '1 day') + TIME '12:00:00', 'AVAILABLE', NOW()),

-- Block 2: Dr. Nguyen - 13:00-17:00 (8 slots)
(9, 2, 2, (CURRENT_DATE + INTERVAL '1 day') + TIME '13:00:00', (CURRENT_DATE + INTERVAL '1 day') + TIME '13:30:00', 'AVAILABLE', NOW()),
(10, 2, 2, (CURRENT_DATE + INTERVAL '1 day') + TIME '13:30:00', (CURRENT_DATE + INTERVAL '1 day') + TIME '14:00:00', 'AVAILABLE', NOW()),
(11, 2, 2, (CURRENT_DATE + INTERVAL '1 day') + TIME '14:00:00', (CURRENT_DATE + INTERVAL '1 day') + TIME '14:30:00', 'AVAILABLE', NOW()),
(12, 2, 2, (CURRENT_DATE + INTERVAL '1 day') + TIME '14:30:00', (CURRENT_DATE + INTERVAL '1 day') + TIME '15:00:00', 'BOOKED', NOW()),
(13, 2, 2, (CURRENT_DATE + INTERVAL '1 day') + TIME '15:00:00', (CURRENT_DATE + INTERVAL '1 day') + TIME '15:30:00', 'AVAILABLE', NOW()),
(14, 2, 2, (CURRENT_DATE + INTERVAL '1 day') + TIME '15:30:00', (CURRENT_DATE + INTERVAL '1 day') + TIME '16:00:00', 'AVAILABLE', NOW()),
(15, 2, 2, (CURRENT_DATE + INTERVAL '1 day') + TIME '16:00:00', (CURRENT_DATE + INTERVAL '1 day') + TIME '16:30:00', 'AVAILABLE', NOW()),
(16, 2, 2, (CURRENT_DATE + INTERVAL '1 day') + TIME '16:30:00', (CURRENT_DATE + INTERVAL '1 day') + TIME '17:00:00', 'AVAILABLE', NOW()),

-- Block 3: Dr. Patel - 09:00-13:00 (8 slots)
(17, 3, 3, (CURRENT_DATE + INTERVAL '2 days') + TIME '09:00:00', (CURRENT_DATE + INTERVAL '2 days') + TIME '09:30:00', 'AVAILABLE', NOW()),
(18, 3, 3, (CURRENT_DATE + INTERVAL '2 days') + TIME '09:30:00', (CURRENT_DATE + INTERVAL '2 days') + TIME '10:00:00', 'AVAILABLE', NOW()),
(19, 3, 3, (CURRENT_DATE + INTERVAL '2 days') + TIME '10:00:00', (CURRENT_DATE + INTERVAL '2 days') + TIME '10:30:00', 'AVAILABLE', NOW()),
(20, 3, 3, (CURRENT_DATE + INTERVAL '2 days') + TIME '10:30:00', (CURRENT_DATE + INTERVAL '2 days') + TIME '11:00:00', 'BOOKED', NOW()),
(21, 3, 3, (CURRENT_DATE + INTERVAL '2 days') + TIME '11:00:00', (CURRENT_DATE + INTERVAL '2 days') + TIME '11:30:00', 'AVAILABLE', NOW()),
(22, 3, 3, (CURRENT_DATE + INTERVAL '2 days') + TIME '11:30:00', (CURRENT_DATE + INTERVAL '2 days') + TIME '12:00:00', 'AVAILABLE', NOW()),
(23, 3, 3, (CURRENT_DATE + INTERVAL '2 days') + TIME '12:00:00', (CURRENT_DATE + INTERVAL '2 days') + TIME '12:30:00', 'AVAILABLE', NOW()),
(24, 3, 3, (CURRENT_DATE + INTERVAL '2 days') + TIME '12:30:00', (CURRENT_DATE + INTERVAL '2 days') + TIME '13:00:00', 'AVAILABLE', NOW()),

-- Block 4: Dr. Lee - 14:00-18:00 (8 slots)
(25, 4, 4, (CURRENT_DATE + INTERVAL '3 days') + TIME '14:00:00', (CURRENT_DATE + INTERVAL '3 days') + TIME '14:30:00', 'AVAILABLE', NOW()),
(26, 4, 4, (CURRENT_DATE + INTERVAL '3 days') + TIME '14:30:00', (CURRENT_DATE + INTERVAL '3 days') + TIME '15:00:00', 'AVAILABLE', NOW()),
(27, 4, 4, (CURRENT_DATE + INTERVAL '3 days') + TIME '15:00:00', (CURRENT_DATE + INTERVAL '3 days') + TIME '15:30:00', 'AVAILABLE', NOW()),
(28, 4, 4, (CURRENT_DATE + INTERVAL '3 days') + TIME '15:30:00', (CURRENT_DATE + INTERVAL '3 days') + TIME '16:00:00', 'AVAILABLE', NOW()),
(29, 4, 4, (CURRENT_DATE + INTERVAL '3 days') + TIME '16:00:00', (CURRENT_DATE + INTERVAL '3 days') + TIME '16:30:00', 'BOOKED', NOW()),
(30, 4, 4, (CURRENT_DATE + INTERVAL '3 days') + TIME '16:30:00', (CURRENT_DATE + INTERVAL '3 days') + TIME '17:00:00', 'AVAILABLE', NOW()),
(31, 4, 4, (CURRENT_DATE + INTERVAL '3 days') + TIME '17:00:00', (CURRENT_DATE + INTERVAL '3 days') + TIME '17:30:00', 'AVAILABLE', NOW()),
(32, 4, 4, (CURRENT_DATE + INTERVAL '3 days') + TIME '17:30:00', (CURRENT_DATE + INTERVAL '3 days') + TIME '18:00:00', 'AVAILABLE', NOW()),

-- Block 5: Dr. Wilson - 08:00-11:00 (6 slots)
(33, 5, 5, (CURRENT_DATE + INTERVAL '4 days') + TIME '08:00:00', (CURRENT_DATE + INTERVAL '4 days') + TIME '08:30:00', 'AVAILABLE', NOW()),
(34, 5, 5, (CURRENT_DATE + INTERVAL '4 days') + TIME '08:30:00', (CURRENT_DATE + INTERVAL '4 days') + TIME '09:00:00', 'AVAILABLE', NOW()),
(35, 5, 5, (CURRENT_DATE + INTERVAL '4 days') + TIME '09:00:00', (CURRENT_DATE + INTERVAL '4 days') + TIME '09:30:00', 'BOOKED', NOW()),
(36, 5, 5, (CURRENT_DATE + INTERVAL '4 days') + TIME '09:30:00', (CURRENT_DATE + INTERVAL '4 days') + TIME '10:00:00', 'AVAILABLE', NOW()),
(37, 5, 5, (CURRENT_DATE + INTERVAL '4 days') + TIME '10:00:00', (CURRENT_DATE + INTERVAL '4 days') + TIME '10:30:00', 'AVAILABLE', NOW()),
(38, 5, 5, (CURRENT_DATE + INTERVAL '4 days') + TIME '10:30:00', (CURRENT_DATE + INTERVAL '4 days') + TIME '11:00:00', 'AVAILABLE', NOW());

-- Reset sequence for time_slots
SELECT setval('time_slots_id_seq', (SELECT MAX(id) FROM time_slots));

-- ========================================
-- 8. APPOINTMENTS TABLE (5 records)
-- Create appointments for the BOOKED time slots
-- ========================================
INSERT INTO appointments (id, patient_id, doctor_id, time_slot_id, symptoms, suspected_disease, status, reschedule_count, reminder_sent, created_at) VALUES
(1, 1, 1, 3, 'Chest pain and shortness of breath', 'Possible angina', 'PENDING', 0, false, NOW()),
(2, 2, 2, 12, 'Skin rash and itching on arms', 'Eczema or allergic reaction', 'PENDING', 0, false, NOW()),
(3, 3, 3, 20, 'Child has fever and cough for 3 days', 'Upper respiratory infection', 'PENDING', 0, false, NOW()),
(4, 4, 4, 29, 'Severe headaches and dizziness', 'Migraine', 'COMPLETED', 0, true, NOW() - INTERVAL '7 days'),
(5, 5, 5, 35, 'Knee pain after sports injury', 'Ligament strain', 'PENDING', 0, false, NOW());

-- Reset sequence for appointments
SELECT setval('appointments_id_seq', (SELECT MAX(id) FROM appointments));

-- ========================================
-- 9. RATINGS TABLE (5 records)
-- Only for completed appointments
-- ========================================
INSERT INTO ratings (id, appointment_id, patient_id, doctor_id, stars, feedback_text, created_at) VALUES
(1, 4, 4, 4, 5, 'Dr. Lee was very professional and thorough. She explained everything clearly and prescribed effective treatment for my migraines. Highly recommended!', NOW() - INTERVAL '5 days'),
(2, 4, 4, 4, 4, 'Good consultation, but waiting time was a bit long. Overall satisfied with the treatment.', NOW() - INTERVAL '4 days'),
(3, 4, 4, 4, 5, 'Excellent doctor! Very knowledgeable and caring. My headaches have improved significantly.', NOW() - INTERVAL '3 days'),
(4, 4, 4, 4, 4, 'Dr. Lee listened to all my concerns and provided detailed answers. Would visit again.', NOW() - INTERVAL '2 days'),
(5, 4, 4, 4, 5, 'Best neurologist I have ever consulted. Professional, empathetic, and effective treatment.', NOW() - INTERVAL '1 day');

-- Reset sequence for ratings
SELECT setval('ratings_id_seq', (SELECT MAX(id) FROM ratings));

-- ========================================
-- VERIFICATION QUERIES
-- Run these to verify data insertion
-- ========================================

-- Count records in each table
-- SELECT 'specialties' AS table_name, COUNT(*) AS record_count FROM specialties
-- UNION ALL
-- SELECT 'users', COUNT(*) FROM users
-- UNION ALL
-- SELECT 'patients', COUNT(*) FROM patients
-- UNION ALL
-- SELECT 'doctors', COUNT(*) FROM doctors
-- UNION ALL
-- SELECT 'doctor_specialties', COUNT(*) FROM doctor_specialties
-- UNION ALL
-- SELECT 'availability_blocks', COUNT(*) FROM availability_blocks
-- UNION ALL
-- SELECT 'time_slots', COUNT(*) FROM time_slots
-- UNION ALL
-- SELECT 'appointments', COUNT(*) FROM appointments
-- UNION ALL
-- SELECT 'ratings', COUNT(*) FROM ratings;

-- ========================================
-- NOTES:
-- 1. All passwords are hashed with BCrypt: "Password123!"
-- 2. Time slots are automatically created from availability blocks
-- 3. Appointments are linked to BOOKED time slots
-- 4. Ratings can only exist for COMPLETED appointments
-- 5. Use CURRENT_DATE + INTERVAL to create future dates
-- 6. Sequences are reset to avoid ID conflicts
-- ========================================
