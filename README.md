# BE-Online-Doctor-Appointment
enum UserRole {
  PATIENT
  DOCTOR
  ADMIN
}

enum Gender {
  MALE
  FEMALE
  OTHER
}

enum TimeSlotStatus {
  AVAILABLE // Khung giờ còn trống
  BOOKED    // Khung giờ đã được đặt
}

enum AppointmentStatus {
  PENDING   // Đang chờ
  COMPLETED // Đã hoàn thành
  CANCELED  // Đã hủy
}

// ============================================
// USERS & PROFILES (Quản lý Người dùng & Hồ sơ)
// ============================================

model Users {
  id           Int      @id @default(autoincrement())
  email        String   @unique
  passwordHash String   @map("password_hash")
  role         UserRole @default(PATIENT)
  createdAt    DateTime @default(now()) @map("created_at")
  updatedAt    DateTime @updatedAt @map("updated_at")

  patientProfile Patients?
  doctorProfile  Doctors?
}

model Patients {
  id          Int      @id @default(autoincrement())
  userId      Int      @unique @map("user_id")
  fullName    String   @map("full_name")
  dateOfBirth DateTime?  @map("date_of_birth") @db.Date
  gender      Gender?
  phoneNumber String?  @unique @map("phone_number")
  createdAt   DateTime @default(now()) @map("created_at")
  updatedAt   DateTime @updatedAt @map("updated_at")

  user         Users          @relation(fields: [userId], references: [id], onDelete: Cascade)
  appointments Appointments[]
  ratings      Ratings[]
}

model Doctors {
  id            Int     @id @default(autoincrement())
  userId        Int     @unique @map("user_id")
  fullName      String  @map("full_name")
  degree        String? // Bằng cấp (ví dụ: "ThS.BS")
  bio           String? @db.Text
  averageRating Float   @default(0) @map("average_rating") // Từ US 5, US 8
  createdAt     DateTime @default(now()) @map("created_at")
  updatedAt     DateTime @updatedAt @map("updated_at")

  user         Users                @relation(fields: [userId], references: [id], onDelete: Cascade)
  specialties  DoctorSpecialties[]
  timeSlots    TimeSlots[]
  appointments Appointments[]
  ratings      Ratings[]
}

// ============================================
// APP CORE (Các chức năng chính của Ứng dụng)
// ============================================

model Specialties {
  id          Int     @id @default(autoincrement())
  name        String  @unique // Tên chuyên khoa
  createdAt   DateTime @default(now()) @map("created_at")
  updatedAt   DateTime @updatedAt @map("updated_at")

  doctors DoctorSpecialties[]
}

model DoctorSpecialties {
  id          Int @id @default(autoincrement())
  doctorId    Int @map("doctor_id")
  specialtyId Int @map("specialty_id")

  doctor    Doctors     @relation(fields: [doctorId], references: [id], onDelete: Cascade)
  specialty Specialties @relation(fields: [specialtyId], references: [id], onDelete: Cascade)

  @@unique([doctorId, specialtyId])
}

// Bảng lưu khung giờ làm việc lớn mà bác sĩ đặt
// Ví dụ: Ngày 2/11/2025 từ 08:00 đến 12:00
model AvailabilityBlocks {
  id        Int      @id @default(autoincrement())
  doctorId  Int      @map("doctor_id")
  workDate  DateTime @map("work_date") @db.Date
  startTime DateTime @map("start_time") @db.Time
  endTime   DateTime @map("end_time") @db.Time
  createdAt DateTime @default(now()) @map("created_at")
  updatedAt DateTime @updatedAt @map("updated_at")

  doctor    Doctors      @relation(fields: [doctorId], references: [id], onDelete: Cascade)
  timeSlots TimeSlots[]
}

// Bảng lưu các slot chi tiết (30 phút mỗi slot)
// Tự động tạo từ AvailabilityBlocks
// Ví dụ: Block 08:00-12:00 -> 8 slots: 08:00-08:30, 08:30-09:00, ...
model TimeSlots {
  id                   Int            @id @default(autoincrement())
  availabilityBlockId  Int            @map("availability_block_id")
  doctorId             Int            @map("doctor_id")
  startTime            DateTime       @map("start_time")
  endTime              DateTime       @map("end_time")
  status               TimeSlotStatus @default(AVAILABLE)
  createdAt            DateTime       @default(now()) @map("created_at")

  availabilityBlock AvailabilityBlocks @relation(fields: [availabilityBlockId], references: [id], onDelete: Cascade)
  doctor            Doctors            @relation(fields: [doctorId], references: [id], onDelete: Cascade)
  appointment       Appointments?
}

model Appointments {
  id               Int               @id @default(autoincrement())
  patientId        Int               @map("patient_id")
  doctorId         Int               @map("doctor_id")
  timeSlotId       Int               @unique @map("time_slot_id")
  symptoms         String            @db.Text // Triệu chứng
  suspectedDisease String?           @map("suspected_disease") // Bệnh nghi ngờ
  status           AppointmentStatus @default(PENDING)
  rescheduleCount  Int               @default(0) @map("reschedule_count") // Từ US 10
  reminderSent     Boolean           @default(false) @map("reminder_sent") // Từ US 9
  createdAt        DateTime          @default(now()) @map("created_at")

  patient  Patients  @relation(fields: [patientId], references: [id], onDelete: NoAction)
  doctor   Doctors   @relation(fields: [doctorId], references: [id], onDelete: NoAction)
  timeSlot TimeSlots @relation(fields: [timeSlotId], references: [id], onDelete: Cascade)
  rating   Ratings?
}

// ============================================
// FEEDBACK (Đánh giá & Phản hồi)
// ============================================

model Ratings {
  id            Int     @id @default(autoincrement())
  appointmentId Int     @unique @map("appointment_id") // 1 đánh giá cho 1 lịch hẹn (US 8)
  patientId     Int     @map("patient_id")
  doctorId      Int     @map("doctor_id")
  stars         Int     // Điểm sao (1-5)
  feedbackText  String? @db.Text @map("feedback_text") // Nội dung feedback (optional - US 8)
  createdAt     DateTime @default(now()) @map("created_at")

  appointment Appointments @relation(fields: [appointmentId], references: [id], onDelete: Cascade)
  patient     Patients     @relation(fields: [patientId], references: [id], onDelete: NoAction)
  doctor      Doctors      @relation(fields: [doctorId], references: [id], onDelete: NoAction)
}