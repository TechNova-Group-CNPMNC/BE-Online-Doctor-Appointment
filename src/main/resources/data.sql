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
-- 1. SPECIALTIES (15 records)
-- ========================================
INSERT INTO specialties (name, created_at, updated_at) VALUES
('Cardiology', NOW(), NOW()),
('Dermatology', NOW(), NOW()),
('Pediatrics', NOW(), NOW()),
('Neurology', NOW(), NOW()),
('Orthopedics', NOW(), NOW()),
('Oncology', NOW(), NOW()),
('Psychiatry', NOW(), NOW()),
('Gastroenterology', NOW(), NOW()),
('Endocrinology', NOW(), NOW()),
('Pulmonology', NOW(), NOW()),
('Nephrology', NOW(), NOW()),
('Rheumatology', NOW(), NOW()),
('Ophthalmology', NOW(), NOW()),
('ENT (Ear, Nose & Throat)', NOW(), NOW()),
('Urology', NOW(), NOW());

-- ========================================
-- 2. USERS (50 records: 30 patients + 20 doctors)
-- Password: "Password123!" (BCrypt hashed)
-- ========================================
INSERT INTO users (email, password_hash, role, created_at, updated_at) VALUES
-- Patients (30 users)
('nguyen.van.a@gmail.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'PATIENT', NOW(), NOW()),
('tran.thi.b@gmail.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'PATIENT', NOW(), NOW()),
('le.van.c@gmail.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'PATIENT', NOW(), NOW()),
('pham.thi.d@gmail.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'PATIENT', NOW(), NOW()),
('hoang.van.e@gmail.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'PATIENT', NOW(), NOW()),
('vu.thi.f@gmail.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'PATIENT', NOW(), NOW()),
('dang.van.g@gmail.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'PATIENT', NOW(), NOW()),
('bui.thi.h@gmail.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'PATIENT', NOW(), NOW()),
('do.van.i@gmail.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'PATIENT', NOW(), NOW()),
('ngo.thi.k@gmail.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'PATIENT', NOW(), NOW()),
('duong.van.l@gmail.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'PATIENT', NOW(), NOW()),
('ly.thi.m@gmail.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'PATIENT', NOW(), NOW()),
('truong.van.n@gmail.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'PATIENT', NOW(), NOW()),
('phan.thi.o@gmail.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'PATIENT', NOW(), NOW()),
('vo.van.p@gmail.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'PATIENT', NOW(), NOW()),
('ta.thi.q@gmail.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'PATIENT', NOW(), NOW()),
('mai.van.r@gmail.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'PATIENT', NOW(), NOW()),
('cao.thi.s@gmail.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'PATIENT', NOW(), NOW()),
('ha.van.t@gmail.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'PATIENT', NOW(), NOW()),
('dinh.thi.u@gmail.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'PATIENT', NOW(), NOW()),
('tong.van.v@gmail.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'PATIENT', NOW(), NOW()),
('tang.thi.w@gmail.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'PATIENT', NOW(), NOW()),
('lu.van.x@gmail.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'PATIENT', NOW(), NOW()),
('khong.thi.y@gmail.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'PATIENT', NOW(), NOW()),
('mac.van.z@gmail.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'PATIENT', NOW(), NOW()),
('kieu.thi.aa@gmail.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'PATIENT', NOW(), NOW()),
('quan.van.bb@gmail.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'PATIENT', NOW(), NOW()),
('bach.thi.cc@gmail.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'PATIENT', NOW(), NOW()),
('lam.van.dd@gmail.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'PATIENT', NOW(), NOW()),
('phi.thi.ee@gmail.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'PATIENT', NOW(), NOW()),
-- Doctors (20 users)
('dr.john.smith@hospital.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'DOCTOR', NOW(), NOW()),
('dr.nguyen.hai@hospital.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'DOCTOR', NOW(), NOW()),
('dr.priya.patel@hospital.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'DOCTOR', NOW(), NOW()),
('dr.sarah.lee@hospital.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'DOCTOR', NOW(), NOW()),
('dr.michael.wilson@hospital.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'DOCTOR', NOW(), NOW()),
('dr.emily.chen@hospital.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'DOCTOR', NOW(), NOW()),
('dr.david.garcia@hospital.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'DOCTOR', NOW(), NOW()),
('dr.lisa.anderson@hospital.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'DOCTOR', NOW(), NOW()),
('dr.james.martinez@hospital.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'DOCTOR', NOW(), NOW()),
('dr.maria.rodriguez@hospital.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'DOCTOR', NOW(), NOW()),
('dr.robert.johnson@hospital.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'DOCTOR', NOW(), NOW()),
('dr.jennifer.brown@hospital.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'DOCTOR', NOW(), NOW()),
('dr.william.davis@hospital.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'DOCTOR', NOW(), NOW()),
('dr.susan.miller@hospital.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'DOCTOR', NOW(), NOW()),
('dr.thomas.moore@hospital.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'DOCTOR', NOW(), NOW()),
('dr.patricia.taylor@hospital.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'DOCTOR', NOW(), NOW()),
('dr.charles.jackson@hospital.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'DOCTOR', NOW(), NOW()),
('dr.linda.white@hospital.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'DOCTOR', NOW(), NOW()),
('dr.daniel.harris@hospital.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'DOCTOR', NOW(), NOW()),
('dr.nancy.martin@hospital.com', '$2a$10$EG5iQPqNBpeOG4Yg1.QQO.1NHeNcqKMYZINaJqTgJNH.RnT1PWMHu', 'DOCTOR', NOW(), NOW());

-- ========================================
-- 3. PATIENTS (30 records)
-- ========================================
INSERT INTO patients (user_id, full_name, date_of_birth, gender, phone_number, created_at, updated_at) VALUES
(1, 'Nguyen Van An', '1990-05-15', 'MALE', '0901234567', NOW(), NOW()),
(2, 'Tran Thi Binh', '1985-08-22', 'FEMALE', '0912345678', NOW(), NOW()),
(3, 'Le Van Cuong', '1995-03-10', 'MALE', '0923456789', NOW(), NOW()),
(4, 'Pham Thi Dung', '1988-12-05', 'FEMALE', '0934567890', NOW(), NOW()),
(5, 'Hoang Van Em', '1992-07-18', 'MALE', '0945678901', NOW(), NOW()),
(6, 'Vu Thi Phuong', '1993-09-25', 'FEMALE', '0956789012', NOW(), NOW()),
(7, 'Dang Van Giang', '1987-11-30', 'MALE', '0967890123', NOW(), NOW()),
(8, 'Bui Thi Huong', '1991-04-12', 'FEMALE', '0978901234', NOW(), NOW()),
(9, 'Do Van Khanh', '1989-06-08', 'MALE', '0989012345', NOW(), NOW()),
(10, 'Ngo Thi Lan', '1994-02-20', 'FEMALE', '0990123456', NOW(), NOW()),
(11, 'Duong Van Minh', '1986-10-15', 'MALE', '0901234568', NOW(), NOW()),
(12, 'Ly Thi Nga', '1992-12-28', 'FEMALE', '0912345679', NOW(), NOW()),
(13, 'Truong Van Phuc', '1988-05-03', 'MALE', '0923456780', NOW(), NOW()),
(14, 'Phan Thi Quynh', '1990-08-17', 'FEMALE', '0934567891', NOW(), NOW()),
(15, 'Vo Van Son', '1993-01-22', 'MALE', '0945678902', NOW(), NOW()),
(16, 'Ta Thi Thao', '1987-03-14', 'FEMALE', '0956789013', NOW(), NOW()),
(17, 'Mai Van Tuan', '1991-07-09', 'MALE', '0967890124', NOW(), NOW()),
(18, 'Cao Thi Uyen', '1989-09-26', 'FEMALE', '0978901235', NOW(), NOW()),
(19, 'Ha Van Viet', '1994-11-11', 'MALE', '0989012346', NOW(), NOW()),
(20, 'Dinh Thi Xuan', '1986-04-05', 'FEMALE', '0990123457', NOW(), NOW()),
(21, 'Tong Van Yen', '1992-06-19', 'MALE', '0901234569', NOW(), NOW()),
(22, 'Tang Thi Anh', '1988-08-24', 'FEMALE', '0912345670', NOW(), NOW()),
(23, 'Lu Van Binh', '1990-10-07', 'MALE', '0923456781', NOW(), NOW()),
(24, 'Khong Thi Chi', '1993-12-31', 'FEMALE', '0934567892', NOW(), NOW()),
(25, 'Mac Van Dat', '1987-02-16', 'MALE', '0945678903', NOW(), NOW()),
(26, 'Kieu Thi Hoa', '1991-05-29', 'FEMALE', '0956789014', NOW(), NOW()),
(27, 'Quan Van Hung', '1989-07-13', 'MALE', '0967890125', NOW(), NOW()),
(28, 'Bach Thi Linh', '1994-09-02', 'FEMALE', '0978901236', NOW(), NOW()),
(29, 'Lam Van Nam', '1986-11-18', 'MALE', '0989012347', NOW(), NOW()),
(30, 'Phi Thi Oanh', '1992-01-27', 'FEMALE', '0990123458', NOW(), NOW());

-- ========================================
-- 4. DOCTORS (20 records)
-- ========================================
INSERT INTO doctors (user_id, full_name, degree, average_rating, created_at, updated_at) VALUES
(31, 'Dr. John Smith', 'MD, FACC', 4.75, NOW(), NOW()),
(32, 'Dr. Nguyen Van Hai', 'MD, PhD', 4.90, NOW(), NOW()),
(33, 'Dr. Priya Patel', 'MD, FAAP', 4.85, NOW(), NOW()),
(34, 'Dr. Sarah Lee', 'MD, PhD, FAAN', 4.60, NOW(), NOW()),
(35, 'Dr. Michael Wilson', 'MD, FAAOS', 4.70, NOW(), NOW()),
(36, 'Dr. Emily Chen', 'MD, FACG', 4.80, NOW(), NOW()),
(37, 'Dr. David Garcia', 'MD, FACE', 4.65, NOW(), NOW()),
(38, 'Dr. Lisa Anderson', 'MD, FCCP', 4.88, NOW(), NOW()),
(39, 'Dr. James Martinez', 'MD, FASN', 4.72, NOW(), NOW()),
(40, 'Dr. Maria Rodriguez', 'MD, FACR', 4.78, NOW(), NOW()),
(41, 'Dr. Robert Johnson', 'MD, FACS', 4.83, NOW(), NOW()),
(42, 'Dr. Jennifer Brown', 'MD, AAO', 4.91, NOW(), NOW()),
(43, 'Dr. William Davis', 'MD, FAAOA', 4.67, NOW(), NOW()),
(44, 'Dr. Susan Miller', 'MD, FPMRS', 4.76, NOW(), NOW()),
(45, 'Dr. Thomas Moore', 'MD, FACS', 4.82, NOW(), NOW()),
(46, 'Dr. Patricia Taylor', 'MD, PhD', 4.89, NOW(), NOW()),
(47, 'Dr. Charles Jackson', 'MD, FACC', 4.74, NOW(), NOW()),
(48, 'Dr. Linda White', 'MD, FAAD', 4.86, NOW(), NOW()),
(49, 'Dr. Daniel Harris', 'MD, FAAP', 4.79, NOW(), NOW()),
(50, 'Dr. Nancy Martin', 'MD, FAAN', 4.84, NOW(), NOW());

-- ========================================
-- 5. DOCTOR_SPECIALTIES (25 records - some doctors have multiple specialties)
-- ========================================
INSERT INTO doctor_specialties (doctor_id, specialty_id) VALUES
(1, 1),   -- Dr. Smith - Cardiology
(2, 2),   -- Dr. Nguyen - Dermatology
(3, 3),   -- Dr. Patel - Pediatrics
(4, 4),   -- Dr. Lee - Neurology
(5, 5),   -- Dr. Wilson - Orthopedics
(6, 8),   -- Dr. Chen - Gastroenterology
(7, 9),   -- Dr. Garcia - Endocrinology
(8, 10),  -- Dr. Anderson - Pulmonology
(9, 11),  -- Dr. Martinez - Nephrology
(10, 12), -- Dr. Rodriguez - Rheumatology
(11, 6),  -- Dr. Johnson - Oncology
(12, 13), -- Dr. Brown - Ophthalmology
(13, 14), -- Dr. Davis - ENT
(14, 15), -- Dr. Miller - Urology
(15, 5),  -- Dr. Moore - Orthopedics
(16, 7),  -- Dr. Taylor - Psychiatry
(17, 1),  -- Dr. Jackson - Cardiology
(18, 2),  -- Dr. White - Dermatology
(19, 3),  -- Dr. Harris - Pediatrics
(20, 4),  -- Dr. Martin - Neurology
-- Additional specialties for some doctors
(1, 10),  -- Dr. Smith also does Pulmonology
(6, 11),  -- Dr. Chen also does Nephrology
(11, 1),  -- Dr. Johnson also does Cardiology
(16, 4),  -- Dr. Taylor also does Neurology
(17, 10); -- Dr. Jackson also does Pulmonology

-- ========================================
-- 6. AVAILABILITY_BLOCKS (60 records - 3 blocks per doctor over 2 weeks)
-- ========================================
INSERT INTO availability_blocks (doctor_id, work_date, start_time, end_time, created_at, updated_at) VALUES
-- Week 1
(1, CURRENT_DATE + INTERVAL '1 day', '08:00:00', '12:00:00', NOW(), NOW()),
(1, CURRENT_DATE + INTERVAL '3 days', '13:00:00', '17:00:00', NOW(), NOW()),
(1, CURRENT_DATE + INTERVAL '5 days', '08:00:00', '12:00:00', NOW(), NOW()),
(2, CURRENT_DATE + INTERVAL '1 day', '13:00:00', '17:00:00', NOW(), NOW()),
(2, CURRENT_DATE + INTERVAL '2 days', '08:00:00', '12:00:00', NOW(), NOW()),
(2, CURRENT_DATE + INTERVAL '4 days', '13:00:00', '17:00:00', NOW(), NOW()),
(3, CURRENT_DATE + INTERVAL '2 days', '09:00:00', '13:00:00', NOW(), NOW()),
(3, CURRENT_DATE + INTERVAL '4 days', '14:00:00', '18:00:00', NOW(), NOW()),
(3, CURRENT_DATE + INTERVAL '6 days', '09:00:00', '13:00:00', NOW(), NOW()),
(4, CURRENT_DATE + INTERVAL '3 days', '14:00:00', '18:00:00', NOW(), NOW()),
(4, CURRENT_DATE + INTERVAL '5 days', '09:00:00', '13:00:00', NOW(), NOW()),
(4, CURRENT_DATE + INTERVAL '7 days', '14:00:00', '18:00:00', NOW(), NOW()),
(5, CURRENT_DATE + INTERVAL '4 days', '08:00:00', '11:00:00', NOW(), NOW()),
(5, CURRENT_DATE + INTERVAL '6 days', '13:00:00', '16:00:00', NOW(), NOW()),
(5, CURRENT_DATE + INTERVAL '8 days', '08:00:00', '11:00:00', NOW(), NOW()),
(6, CURRENT_DATE + INTERVAL '1 day', '09:00:00', '12:00:00', NOW(), NOW()),
(6, CURRENT_DATE + INTERVAL '3 days', '14:00:00', '17:00:00', NOW(), NOW()),
(6, CURRENT_DATE + INTERVAL '5 days', '09:00:00', '12:00:00', NOW(), NOW()),
(7, CURRENT_DATE + INTERVAL '2 days', '10:00:00', '14:00:00', NOW(), NOW()),
(7, CURRENT_DATE + INTERVAL '4 days', '15:00:00', '19:00:00', NOW(), NOW()),
(7, CURRENT_DATE + INTERVAL '6 days', '10:00:00', '14:00:00', NOW(), NOW()),
(8, CURRENT_DATE + INTERVAL '1 day', '08:30:00', '12:30:00', NOW(), NOW()),
(8, CURRENT_DATE + INTERVAL '3 days', '13:30:00', '17:30:00', NOW(), NOW()),
(8, CURRENT_DATE + INTERVAL '5 days', '08:30:00', '12:30:00', NOW(), NOW()),
(9, CURRENT_DATE + INTERVAL '2 days', '09:30:00', '13:30:00', NOW(), NOW()),
(9, CURRENT_DATE + INTERVAL '4 days', '14:30:00', '18:30:00', NOW(), NOW()),
(9, CURRENT_DATE + INTERVAL '6 days', '09:30:00', '13:30:00', NOW(), NOW()),
(10, CURRENT_DATE + INTERVAL '3 days', '08:00:00', '12:00:00', NOW(), NOW()),
(10, CURRENT_DATE + INTERVAL '5 days', '13:00:00', '17:00:00', NOW(), NOW()),
(10, CURRENT_DATE + INTERVAL '7 days', '08:00:00', '12:00:00', NOW(), NOW()),
(11, CURRENT_DATE + INTERVAL '1 day', '10:00:00', '14:00:00', NOW(), NOW()),
(11, CURRENT_DATE + INTERVAL '3 days', '15:00:00', '19:00:00', NOW(), NOW()),
(11, CURRENT_DATE + INTERVAL '5 days', '10:00:00', '14:00:00', NOW(), NOW()),
(12, CURRENT_DATE + INTERVAL '2 days', '08:00:00', '11:00:00', NOW(), NOW()),
(12, CURRENT_DATE + INTERVAL '4 days', '13:00:00', '16:00:00', NOW(), NOW()),
(12, CURRENT_DATE + INTERVAL '6 days', '08:00:00', '11:00:00', NOW(), NOW()),
(13, CURRENT_DATE + INTERVAL '1 day', '09:00:00', '13:00:00', NOW(), NOW()),
(13, CURRENT_DATE + INTERVAL '3 days', '14:00:00', '18:00:00', NOW(), NOW()),
(13, CURRENT_DATE + INTERVAL '5 days', '09:00:00', '13:00:00', NOW(), NOW()),
(14, CURRENT_DATE + INTERVAL '2 days', '10:00:00', '14:00:00', NOW(), NOW()),
(14, CURRENT_DATE + INTERVAL '4 days', '15:00:00', '19:00:00', NOW(), NOW()),
(14, CURRENT_DATE + INTERVAL '6 days', '10:00:00', '14:00:00', NOW(), NOW()),
(15, CURRENT_DATE + INTERVAL '3 days', '08:00:00', '12:00:00', NOW(), NOW()),
(15, CURRENT_DATE + INTERVAL '5 days', '13:00:00', '17:00:00', NOW(), NOW()),
(15, CURRENT_DATE + INTERVAL '7 days', '08:00:00', '12:00:00', NOW(), NOW()),
(16, CURRENT_DATE + INTERVAL '1 day', '09:30:00', '13:30:00', NOW(), NOW()),
(16, CURRENT_DATE + INTERVAL '3 days', '14:30:00', '18:30:00', NOW(), NOW()),
(16, CURRENT_DATE + INTERVAL '5 days', '09:30:00', '13:30:00', NOW(), NOW()),
(17, CURRENT_DATE + INTERVAL '2 days', '08:00:00', '12:00:00', NOW(), NOW()),
(17, CURRENT_DATE + INTERVAL '4 days', '13:00:00', '17:00:00', NOW(), NOW()),
(17, CURRENT_DATE + INTERVAL '6 days', '08:00:00', '12:00:00', NOW(), NOW()),
(18, CURRENT_DATE + INTERVAL '1 day', '10:00:00', '14:00:00', NOW(), NOW()),
(18, CURRENT_DATE + INTERVAL '3 days', '15:00:00', '19:00:00', NOW(), NOW()),
(18, CURRENT_DATE + INTERVAL '5 days', '10:00:00', '14:00:00', NOW(), NOW()),
(19, CURRENT_DATE + INTERVAL '2 days', '09:00:00', '13:00:00', NOW(), NOW()),
(19, CURRENT_DATE + INTERVAL '4 days', '14:00:00', '18:00:00', NOW(), NOW()),
(19, CURRENT_DATE + INTERVAL '6 days', '09:00:00', '13:00:00', NOW(), NOW()),
(20, CURRENT_DATE + INTERVAL '3 days', '08:30:00', '12:30:00', NOW(), NOW()),
(20, CURRENT_DATE + INTERVAL '5 days', '13:30:00', '17:30:00', NOW(), NOW()),
(20, CURRENT_DATE + INTERVAL '7 days', '08:30:00', '12:30:00', NOW(), NOW());

-- ========================================
-- 7. TIME_SLOTS (480 records - 8 slots per block, 60 blocks)
-- ========================================
-- Generate time slots for all availability blocks (30-minute slots)
-- Block 1-8 (Dr. Smith, Dr. Nguyen, Dr. Patel, Dr. Lee, Dr. Wilson, Dr. Chen, Dr. Garcia, Dr. Anderson)
INSERT INTO time_slots (availability_block_id, doctor_id, start_time, end_time, status, created_at)
SELECT
    b.id,
    b.doctor_id,
    (b.work_date + b.start_time) + (n || ' minutes')::interval,
    (b.work_date + b.start_time) + ((n + 30) || ' minutes')::interval,
    CASE
        WHEN RANDOM() < 0.3 THEN 'BOOKED'
        ELSE 'AVAILABLE'
    END,
    NOW()
FROM availability_blocks b
CROSS JOIN generate_series(0, 210, 30) AS n
WHERE EXTRACT(EPOCH FROM (b.end_time - b.start_time)) / 60 > n;

-- ========================================
-- 8. APPOINTMENTS (80 records)
-- ========================================
-- Get booked time slots and create appointments
WITH booked_slots AS (
    SELECT id, doctor_id, ROW_NUMBER() OVER (ORDER BY id) as rn
    FROM time_slots
    WHERE status = 'BOOKED'
    LIMIT 80
)
INSERT INTO appointments (patient_id, doctor_id, time_slot_id, symptoms, suspected_disease, status, reschedule_count, reminder_sent, created_at)
SELECT
    ((bs.rn - 1) % 30) + 1, -- Cycle through 30 patients
    bs.doctor_id,
    bs.id,
    CASE ((bs.rn - 1) % 20)
        WHEN 0 THEN 'Chest pain and shortness of breath'
        WHEN 1 THEN 'Skin rash and itching on arms'
        WHEN 2 THEN 'Child has fever and cough for 3 days'
        WHEN 3 THEN 'Severe headaches and dizziness'
        WHEN 4 THEN 'Knee pain after sports injury'
        WHEN 5 THEN 'Persistent abdominal pain and bloating'
        WHEN 6 THEN 'Chronic fatigue and weight gain'
        WHEN 7 THEN 'Difficulty breathing and wheezing'
        WHEN 8 THEN 'Swelling in legs and feet'
        WHEN 9 THEN 'Joint pain and stiffness in hands'
        WHEN 10 THEN 'Persistent cough with blood in sputum'
        WHEN 11 THEN 'Blurred vision and eye discomfort'
        WHEN 12 THEN 'Ear pain and hearing loss'
        WHEN 13 THEN 'Frequent and painful urination'
        WHEN 14 THEN 'Lower back pain radiating to leg'
        WHEN 15 THEN 'Anxiety attacks and sleep disturbances'
        WHEN 16 THEN 'Irregular heartbeat and palpitations'
        WHEN 17 THEN 'Severe acne and skin discoloration'
        WHEN 18 THEN 'Developmental delays in child'
        WHEN 19 THEN 'Memory loss and confusion'
    END,
    CASE ((bs.rn - 1) % 20)
        WHEN 0 THEN 'Possible angina'
        WHEN 1 THEN 'Eczema or allergic reaction'
        WHEN 2 THEN 'Upper respiratory infection'
        WHEN 3 THEN 'Migraine'
        WHEN 4 THEN 'Ligament strain'
        WHEN 5 THEN 'Gastritis or IBS'
        WHEN 6 THEN 'Hypothyroidism'
        WHEN 7 THEN 'Asthma'
        WHEN 8 THEN 'Kidney disease'
        WHEN 9 THEN 'Rheumatoid arthritis'
        WHEN 10 THEN 'Lung cancer concern'
        WHEN 11 THEN 'Cataracts or glaucoma'
        WHEN 12 THEN 'Otitis media'
        WHEN 13 THEN 'UTI or kidney stones'
        WHEN 14 THEN 'Herniated disc'
        WHEN 15 THEN 'Generalized anxiety disorder'
        WHEN 16 THEN 'Atrial fibrillation'
        WHEN 17 THEN 'Severe acne vulgaris'
        WHEN 18 THEN 'Autism spectrum concern'
        WHEN 19 THEN 'Early dementia'
    END,
    CASE
        WHEN RANDOM() < 0.5 THEN 'COMPLETED'
        ELSE 'PENDING'
    END,
    0,
    RANDOM() < 0.5,
    NOW() - (RANDOM() * INTERVAL '30 days')
FROM booked_slots bs;

-- ========================================
-- 9. RATINGS (50 records - only for completed appointments)
-- ========================================
INSERT INTO ratings (appointment_id, patient_id, doctor_id, stars, feedback_text, created_at)
SELECT
    a.id,
    a.patient_id,
    a.doctor_id,
    (3 + FLOOR(RANDOM() * 3))::INTEGER, -- Random rating 3-5
    CASE (FLOOR(RANDOM() * 20))::INTEGER
        WHEN 0 THEN 'Excellent doctor! Very professional and caring.'
        WHEN 1 THEN 'Good consultation, but waiting time was a bit long.'
        WHEN 2 THEN 'Very knowledgeable and took time to explain everything.'
        WHEN 3 THEN 'Listened to all my concerns and provided detailed answers.'
        WHEN 4 THEN 'Best specialist I have ever consulted. Highly recommend!'
        WHEN 5 THEN 'Professional service and accurate diagnosis.'
        WHEN 6 THEN 'The doctor was patient and understanding.'
        WHEN 7 THEN 'Great experience overall. Will come back again.'
        WHEN 8 THEN 'Thorough examination and clear treatment plan.'
        WHEN 9 THEN 'Very satisfied with the care I received.'
        WHEN 10 THEN 'Competent doctor with excellent bedside manner.'
        WHEN 11 THEN 'The consultation was informative and helpful.'
        WHEN 12 THEN 'Doctor showed genuine concern for my health.'
        WHEN 13 THEN 'Efficient and professional service.'
        WHEN 14 THEN 'Would definitely recommend to friends and family.'
        WHEN 15 THEN 'The doctor answered all my questions patiently.'
        WHEN 16 THEN 'Very impressed with the level of care.'
        WHEN 17 THEN 'Felt comfortable throughout the consultation.'
        WHEN 18 THEN 'Knowledgeable and experienced professional.'
        WHEN 19 THEN 'Outstanding medical care and attention to detail.'
    END,
    NOW() - (RANDOM() * INTERVAL '25 days')
FROM appointments a
WHERE a.status = 'COMPLETED'
LIMIT 50;

-- ========================================
-- Update doctor average ratings based on actual ratings
-- ========================================
UPDATE doctors d
SET average_rating = (
    SELECT COALESCE(AVG(r.stars), 4.5)
    FROM ratings r
    WHERE r.doctor_id = d.id
);

-- ========================================
-- END OF SAMPLE DATA
-- ========================================

-- ========================================
-- 10. TEST DATA: COMPLETED APPOINTMENT WITHOUT RATING
-- ========================================
-- For testing rating feature
INSERT INTO appointments (patient_id, doctor_id, time_slot_id, symptoms, suspected_disease, status, reschedule_count, reminder_sent, created_at)
VALUES (
    1, -- Nguyen Van An
    2, -- Dr. Nguyen Van Hai
    (SELECT MIN(id) FROM time_slots WHERE status = 'AVAILABLE' AND doctor_id = 2),
    'Bị mẩn đỏ và ngứa ở cánh tay, đã 1 tuần chưa hết',
    'Có thể bị viêm da dị ứng',
    'COMPLETED',
    0,
    true,
    NOW() - INTERVAL '1 day'
);

-- Update time slot status
UPDATE time_slots
SET status = 'BOOKED'
WHERE id = (
    SELECT time_slot_id
    FROM appointments
    ORDER BY id DESC
    LIMIT 1
);

COMMIT;