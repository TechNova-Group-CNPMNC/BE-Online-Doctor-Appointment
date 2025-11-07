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
('Tim mạch', NOW(), NOW()),
('Da liễu', NOW(), NOW()),
('Nhi khoa', NOW(), NOW()),
('Thần kinh', NOW(), NOW()),
('Chấn thương chỉnh hình', NOW(), NOW()),
('Ung bướu', NOW(), NOW()),
('Tâm thần', NOW(), NOW()),
('Tiêu hóa', NOW(), NOW()),
('Nội tiết', NOW(), NOW()),
('Hô hấp', NOW(), NOW()),
('Thận - Tiết niệu', NOW(), NOW()),
('Thấp khớp', NOW(), NOW()),
('Mắt', NOW(), NOW()),
('Tai Mũi Họng', NOW(), NOW()),
('Tiết niệu', NOW(), NOW());

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
        WHEN 0 THEN 'Đau ngực và khó thở'
        WHEN 1 THEN 'Phát ban và ngứa ở cánh tay'
        WHEN 2 THEN 'Trẻ bị sốt và ho trong 3 ngày'
        WHEN 3 THEN 'Đau đầu dữ dội và chóng mặt'
        WHEN 4 THEN 'Đau đầu gối sau chấn thương thể thao'
        WHEN 5 THEN 'Đau bụng và đầy hơi kéo dài'
        WHEN 6 THEN 'Mệt mỏi mãn tính và tăng cân'
        WHEN 7 THEN 'Khó thở và thở khò khè'
        WHEN 8 THEN 'Sưng chân và bàn chân'
        WHEN 9 THEN 'Đau khớp và cứng khớp ở bàn tay'
        WHEN 10 THEN 'Ho dai dẳng có máu trong đờm'
        WHEN 11 THEN 'Mờ mắt và khó chịu ở mắt'
        WHEN 12 THEN 'Đau tai và giảm thính lực'
        WHEN 13 THEN 'Tiểu buồi và đau khi đi tiểu'
        WHEN 14 THEN 'Đau lưng dưới lan xuống chân'
        WHEN 15 THEN 'Lo âu và rối loạn giấc ngủ'
        WHEN 16 THEN 'Nhịp tim không đều và hồi hộp'
        WHEN 17 THEN 'Mụn trứng cá nghiêm trọng và da đổi màu'
        WHEN 18 THEN 'Chậm phát triển ở trẻ'
        WHEN 19 THEN 'Mất trí nhớ và lú lẫn'
    END,
    CASE ((bs.rn - 1) % 20)
        WHEN 0 THEN 'Nghi ngờ đau thắt ngực'
        WHEN 1 THEN 'Chàm hoặc dị ứng'
        WHEN 2 THEN 'Nhiễm trùng đường hô hấp trên'
        WHEN 3 THEN 'Đau nửa đầu'
        WHEN 4 THEN 'Căng dây chằng'
        WHEN 5 THEN 'Viêm dạ dày hoặc hội chứng ruột kích thích'
        WHEN 6 THEN 'Suy giáp'
        WHEN 7 THEN 'Hen suyễn'
        WHEN 8 THEN 'Bệnh thận'
        WHEN 9 THEN 'Viêm khớp dạng thấp'
        WHEN 10 THEN 'Nghi ngờ ung thư phổi'
        WHEN 11 THEN 'Đục thủy tinh thể hoặc tăng nhãn áp'
        WHEN 12 THEN 'Viêm tai giữa'
        WHEN 13 THEN 'Nhiễm trùng đường tiết niệu hoặc sỏi thận'
        WHEN 14 THEN 'Thoát vị đĩa đệm'
        WHEN 15 THEN 'Rối loạn lo âu lan tỏa'
        WHEN 16 THEN 'Rung nhĩ'
        WHEN 17 THEN 'Mụn trứng cá thông thường nghiêm trọng'
        WHEN 18 THEN 'Nghi ngờ rối loạn phổ tự kỷ'
        WHEN 19 THEN 'Sa sút trí tuệ giai đoạn đầu'
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
        WHEN 0 THEN 'Bác sĩ xuất sắc! Rất chuyên nghiệp và tận tâm.'
        WHEN 1 THEN 'Tư vấn tốt, nhưng thời gian chờ hơi lâu.'
        WHEN 2 THEN 'Rất am hiểu và dành thời gian giải thích mọi thứ.'
        WHEN 3 THEN 'Lắng nghe tất cả các mối quan tâm của tôi và đưa ra câu trả lời chi tiết.'
        WHEN 4 THEN 'Chuyên gia tốt nhất mà tôi từng tư vấn. Rất khuyến khích!'
        WHEN 5 THEN 'Dịch vụ chuyên nghiệp và chẩn đoán chính xác.'
        WHEN 6 THEN 'Bác sĩ kiên nhẫn và thông cảm.'
        WHEN 7 THEN 'Trải nghiệm tuyệt vời nói chung. Sẽ quay lại lần nữa.'
        WHEN 8 THEN 'Khám kỹ lưỡng và kế hoạch điều trị rõ ràng.'
        WHEN 9 THEN 'Rất hài lòng với sự chăm sóc tôi nhận được.'
        WHEN 10 THEN 'Bác sĩ có năng lực với phong thái ân cần tuyệt vời.'
        WHEN 11 THEN 'Buổi tư vấn rất bổ ích và hữu ích.'
        WHEN 12 THEN 'Bác sĩ thể hiện sự quan tâm chân thành đến sức khỏe của tôi.'
        WHEN 13 THEN 'Dịch vụ hiệu quả và chuyên nghiệp.'
        WHEN 14 THEN 'Chắc chắn sẽ giới thiệu cho bạn bè và gia đình.'
        WHEN 15 THEN 'Bác sĩ trả lời tất cả các câu hỏi của tôi một cách kiên nhẫn.'
        WHEN 16 THEN 'Rất ấn tượng với mức độ chăm sóc.'
        WHEN 17 THEN 'Cảm thấy thoải mái trong suốt buổi tư vấn.'
        WHEN 18 THEN 'Chuyên gia am hiểu và giàu kinh nghiệm.'
        WHEN 19 THEN 'Chăm sóc y tế xuất sắc và chú ý đến từng chi tiết.'
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
-- TEST DATA: COMPLETED APPOINTMENT WITHOUT RATING
-- ========================================
-- For testing rating feature
-- Appointment 1: Patient 1 with Dr. Nguyen Van Hai (Dermatology)
INSERT INTO appointments (patient_id, doctor_id, time_slot_id, symptoms, suspected_disease, status, reschedule_count, reminder_sent, created_at)
SELECT
    1,
    2,
    id,
    'Bị mẩn đỏ và ngứa ở cánh tay, đã 1 tuần chưa hết',
    'Có thể bị viêm da dị ứng',
    'COMPLETED',
    0,
    true,
    NOW() - INTERVAL '2 days'
FROM time_slots
WHERE status = 'AVAILABLE' AND doctor_id = 2
ORDER BY start_time
LIMIT 1;

UPDATE time_slots SET status = 'BOOKED'
WHERE id = (SELECT time_slot_id FROM appointments ORDER BY id DESC LIMIT 1);

-- Appointment 2: Patient 1 with Dr. John Smith (Cardiology)
INSERT INTO appointments (patient_id, doctor_id, time_slot_id, symptoms, suspected_disease, status, reschedule_count, reminder_sent, created_at)
SELECT
    1,
    1,
    id,
    'Đau ngực khi gắng sức, thở nhanh, tim đập mạnh',
    'Nghi ngờ bệnh tim mạch, cần kiểm tra ECG',
    'COMPLETED',
    0,
    true,
    NOW() - INTERVAL '5 days'
FROM time_slots
WHERE status = 'AVAILABLE' AND doctor_id = 1
ORDER BY start_time
LIMIT 1;

UPDATE time_slots SET status = 'BOOKED'
WHERE id = (SELECT time_slot_id FROM appointments ORDER BY id DESC LIMIT 1);

-- Appointment 3: Patient 1 with Dr. Priya Patel (Pediatrics)
INSERT INTO appointments (patient_id, doctor_id, time_slot_id, symptoms, suspected_disease, status, reschedule_count, reminder_sent, created_at)
SELECT
    1,
    3,
    id,
    'Con bị sốt cao 39 độ, ho khan liên tục',
    'Viêm đường hô hấp trên',
    'COMPLETED',
    0,
    true,
    NOW() - INTERVAL '3 days'
FROM time_slots
WHERE status = 'AVAILABLE' AND doctor_id = 3
ORDER BY start_time
LIMIT 1;

UPDATE time_slots SET status = 'BOOKED'
WHERE id = (SELECT time_slot_id FROM appointments ORDER BY id DESC LIMIT 1);

-- Appointment 4: Patient 2 with Dr. Sarah Lee (Neurology)
INSERT INTO appointments (patient_id, doctor_id, time_slot_id, symptoms, suspected_disease, status, reschedule_count, reminder_sent, created_at)
SELECT
    2,
    4,
    id,
    'Đau đầu dữ dội một bên, buồn nôn, sợ ánh sáng',
    'Migraine',
    'COMPLETED',
    0,
    true,
    NOW() - INTERVAL '1 day'
FROM time_slots
WHERE status = 'AVAILABLE' AND doctor_id = 4
ORDER BY start_time
LIMIT 1;

UPDATE time_slots SET status = 'BOOKED'
WHERE id = (SELECT time_slot_id FROM appointments ORDER BY id DESC LIMIT 1);

-- Appointment 5: Patient 3 with Dr. Michael Wilson (Orthopedics)
INSERT INTO appointments (patient_id, doctor_id, time_slot_id, symptoms, suspected_disease, status, reschedule_count, reminder_sent, created_at)
SELECT
    3,
    5,
    id,
    'Đau đầu gối sau khi chơi bóng đá, sưng và khó cử động',
    'Chấn thương dây chằng',
    'COMPLETED',
    0,
    true,
    NOW() - INTERVAL '4 days'
FROM time_slots
WHERE status = 'AVAILABLE' AND doctor_id = 5
ORDER BY start_time
LIMIT 1;

UPDATE time_slots SET status = 'BOOKED'
WHERE id = (SELECT time_slot_id FROM appointments ORDER BY id DESC LIMIT 1);

-- Appointment 6: Patient 4 with Dr. Emily Chen (Gastroenterology)
INSERT INTO appointments (patient_id, doctor_id, time_slot_id, symptoms, suspected_disease, status, reschedule_count, reminder_sent, created_at)
SELECT
    4,
    6,
    id,
    'Đau bụng âm ỉ, đầy hơi, khó tiêu sau bữa ăn',
    'Viêm dạ dày hoặc hội chứng ruột kích thích',
    'COMPLETED',
    0,
    true,
    NOW() - INTERVAL '6 days'
FROM time_slots
WHERE status = 'AVAILABLE' AND doctor_id = 6
ORDER BY start_time
LIMIT 1;

UPDATE time_slots SET status = 'BOOKED'
WHERE id = (SELECT time_slot_id FROM appointments ORDER BY id DESC LIMIT 1);

-- Appointment 7: Patient 5 with Dr. David Garcia (Endocrinology)
INSERT INTO appointments (patient_id, doctor_id, time_slot_id, symptoms, suspected_disease, status, reschedule_count, reminder_sent, created_at)
SELECT
    5,
    7,
    id,
    'Mệt mỏi kéo dài, tăng cân, rụng tóc nhiều',
    'Suy giảm chức năng tuyến giáp',
    'COMPLETED',
    0,
    true,
    NOW() - INTERVAL '7 days'
FROM time_slots
WHERE status = 'AVAILABLE' AND doctor_id = 7
ORDER BY start_time
LIMIT 1;

UPDATE time_slots SET status = 'BOOKED'
WHERE id = (SELECT time_slot_id FROM appointments ORDER BY id DESC LIMIT 1);

-- Appointment 8: Patient 6 with Dr. Lisa Anderson (Pulmonology)
INSERT INTO appointments (patient_id, doctor_id, time_slot_id, symptoms, suspected_disease, status, reschedule_count, reminder_sent, created_at)
SELECT
    6,
    8,
    id,
    'Khó thở, thở khò khè, ho nhiều vào ban đêm',
    'Hen phế quản',
    'COMPLETED',
    0,
    true,
    NOW() - INTERVAL '8 days'
FROM time_slots
WHERE status = 'AVAILABLE' AND doctor_id = 8
ORDER BY start_time
LIMIT 1;

UPDATE time_slots SET status = 'BOOKED'
WHERE id = (SELECT time_slot_id FROM appointments ORDER BY id DESC LIMIT 1);

-- Appointment 9: Patient 7 with Dr. James Martinez (Nephrology)
INSERT INTO appointments (patient_id, doctor_id, time_slot_id, symptoms, suspected_disease, status, reschedule_count, reminder_sent, created_at)
SELECT
    7,
    9,
    id,
    'Sưng chân, sưng mắt buổi sáng, tiểu ít',
    'Suy thận cấp',
    'COMPLETED',
    0,
    true,
    NOW() - INTERVAL '9 days'
FROM time_slots
WHERE status = 'AVAILABLE' AND doctor_id = 9
ORDER BY start_time
LIMIT 1;

UPDATE time_slots SET status = 'BOOKED'
WHERE id = (SELECT time_slot_id FROM appointments ORDER BY id DESC LIMIT 1);

-- Appointment 10: Patient 8 with Dr. Maria Rodriguez (Rheumatology)
INSERT INTO appointments (patient_id, doctor_id, time_slot_id, symptoms, suspected_disease, status, reschedule_count, reminder_sent, created_at)
SELECT
    8,
    10,
    id,
    'Đau và cứng khớp ngón tay buổi sáng, sưng đỏ',
    'Viêm khớp dạng thấp',
    'COMPLETED',
    0,
    true,
    NOW() - INTERVAL '10 days'
FROM time_slots
WHERE status = 'AVAILABLE' AND doctor_id = 10
ORDER BY start_time
LIMIT 1;

UPDATE time_slots SET status = 'BOOKED'
WHERE id = (SELECT time_slot_id FROM appointments ORDER BY id DESC LIMIT 1);

-- Appointment 11: Patient 9 with Dr. Robert Johnson (Oncology)
INSERT INTO appointments (patient_id, doctor_id, time_slot_id, symptoms, suspected_disease, status, reschedule_count, reminder_sent, created_at)
SELECT
    9,
    11,
    id,
    'Ho ra máu, gầy sút cân nhanh, mệt mỏi kéo dài',
    'Cần kiểm tra ung thư phổi',
    'COMPLETED',
    0,
    true,
    NOW() - INTERVAL '11 days'
FROM time_slots
WHERE status = 'AVAILABLE' AND doctor_id = 11
ORDER BY start_time
LIMIT 1;

UPDATE time_slots SET status = 'BOOKED'
WHERE id = (SELECT time_slot_id FROM appointments ORDER BY id DESC LIMIT 1);

-- Appointment 12: Patient 10 with Dr. Jennifer Brown (Ophthalmology)
INSERT INTO appointments (patient_id, doctor_id, time_slot_id, symptoms, suspected_disease, status, reschedule_count, reminder_sent, created_at)
SELECT
    10,
    12,
    id,
    'Nhìn mờ, khó nhìn xa, đau mắt khi đọc sách lâu',
    'Cận thị hoặc viễn thị',
    'COMPLETED',
    0,
    true,
    NOW() - INTERVAL '12 days'
FROM time_slots
WHERE status = 'AVAILABLE' AND doctor_id = 12
ORDER BY start_time
LIMIT 1;

UPDATE time_slots SET status = 'BOOKED'
WHERE id = (SELECT time_slot_id FROM appointments ORDER BY id DESC LIMIT 1);

-- Appointment 13: Patient 11 with Dr. William Davis (ENT)
INSERT INTO appointments (patient_id, doctor_id, time_slot_id, symptoms, suspected_disease, status, reschedule_count, reminder_sent, created_at)
SELECT
    11,
    13,
    id,
    'Đau tai, chảy mủ, giảm thính lực',
    'Viêm tai giữa',
    'COMPLETED',
    0,
    true,
    NOW() - INTERVAL '13 days'
FROM time_slots
WHERE status = 'AVAILABLE' AND doctor_id = 13
ORDER BY start_time
LIMIT 1;

UPDATE time_slots SET status = 'BOOKED'
WHERE id = (SELECT time_slot_id FROM appointments ORDER BY id DESC LIMIT 1);

-- Appointment 14: Patient 12 with Dr. Susan Miller (Urology)
INSERT INTO appointments (patient_id, doctor_id, time_slot_id, symptoms, suspected_disease, status, reschedule_count, reminder_sent, created_at)
SELECT
    12,
    14,
    id,
    'Tiểu buồi, tiểu rát, đau bụng dưới',
    'Viêm đường tiết niệu',
    'COMPLETED',
    0,
    true,
    NOW() - INTERVAL '14 days'
FROM time_slots
WHERE status = 'AVAILABLE' AND doctor_id = 14
ORDER BY start_time
LIMIT 1;

UPDATE time_slots SET status = 'BOOKED'
WHERE id = (SELECT time_slot_id FROM appointments ORDER BY id DESC LIMIT 1);

-- Appointment 15: Patient 13 with Dr. Thomas Moore (Orthopedics)
INSERT INTO appointments (patient_id, doctor_id, time_slot_id, symptoms, suspected_disease, status, reschedule_count, reminder_sent, created_at)
SELECT
    13,
    15,
    id,
    'Đau lưng lan xuống chân, tê bì, khó đi lại',
    'Thoát vị đĩa đệm',
    'COMPLETED',
    0,
    true,
    NOW() - INTERVAL '15 days'
FROM time_slots
WHERE status = 'AVAILABLE' AND doctor_id = 15
ORDER BY start_time
LIMIT 1;

UPDATE time_slots SET status = 'BOOKED'
WHERE id = (SELECT time_slot_id FROM appointments ORDER BY id DESC LIMIT 1);

COMMIT;

COMMIT;