# 📋 API Documentation - Online Doctor Appointment System

## 🔐 I. Authentication API

### 1. Register Patient Account
- **Endpoint:** `POST /api/auth/register`
- **Mô tả:** Đăng ký tài khoản mới cho bệnh nhân
- **Authentication:** ❌ Public (không cần token)
- **Request Body (JSON):**
  ```json
  {
    "email": "patient@example.com",
    "password": "password123",
    "fullName": "Nguyễn Văn A",
    "dateOfBirth": "1990-01-15",
    "gender": "MALE",
    "phoneNumber": "0912345678"
  }
  ```
- **Validation:**
  - `email`: Bắt buộc, phải unique trong hệ thống
  - `password`: Bắt buộc, sẽ được mã hóa bằng BCrypt
  - `fullName`: Bắt buộc
  - `dateOfBirth`: Bắt buộc, format yyyy-MM-dd
  - `gender`: Bắt buộc, enum [MALE, FEMALE, OTHER]
  - `phoneNumber`: Bắt buộc
- **Logic:**
  - Kiểm tra email đã tồn tại chưa
  - Mã hóa password bằng BCryptPasswordEncoder
  - Tạo User với role = PATIENT
  - Tạo Patient record liên kết với User
  - Generate JWT token (expires in 24 hours)
- **Response (200 OK):**
  ```json
  {
    "email": "patient@example.com",
    "role": "PATIENT",
    "message": "Registration successful",
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZSI6IlBBVElFTlQi..."
  }
  ```
- **Error Response (400 Bad Request):**
  ```json
  {
    "email": null,
    "role": null,
    "message": "User with this email already exists",
    "token": null
  }
  ```

### 2. Login
- **Endpoint:** `POST /api/auth/login`
- **Mô tả:** Đăng nhập và nhận JWT token
- **Authentication:** ❌ Public
- **Request Body (JSON):**
  ```json
  {
    "email": "patient@example.com",
    "password": "password123"
  }
  ```
- **Logic:**
  - Tìm user theo email
  - Verify password bằng BCryptPasswordEncoder
  - Generate JWT token nếu credentials hợp lệ
- **Response (200 OK):**
  ```json
  {
    "email": "patient@example.com",
    "role": "PATIENT",
    "message": "Login successful",
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
  ```
- **Error Response (401 Unauthorized):**
  ```json
  {
    "email": null,
    "role": null,
    "message": "Invalid credentials",
    "token": null
  }
  ```

---

## 🏥 II. Specialty API

### 1. Get All Specialties
- **Endpoint:** `GET /api/specialties`
- **Mô tả:** Lấy danh sách tất cả các chuyên khoa y tế
- **Authentication:** ❌ Public (không cần token)
- **Response (200 OK):**
  ```json
  [
    {
      "id": 1,
      "name": "Cardiology",
      "description": "Heart and cardiovascular diseases"
    },
    {
      "id": 2,
      "name": "Dermatology",
      "description": "Skin conditions and treatments"
    }
  ]
  ```

---

## 👨‍⚕️ III. Find Doctor API

### 1. Get All Doctors
- **Endpoint:** `GET /api/doctors`
- **Mô tả:** Lấy danh sách tất cả các bác sĩ hiện tại
- **Authentication:** ✅ Required (Bearer token)
- **Authorization:** Any authenticated user
- **Response (200 OK):**
  ```json
  [
    {
      "id": 1,
      "fullName": "Dr. John Smith",
      "degree": "MD",
      "bio": "Experienced cardiologist with 15 years...",
      "averageRating": 4.8,
      "specialties": ["Cardiology"]
    }
  ]
  ```

### 2. Search Doctors
- **Endpoint:** `GET /api/doctors/search?specialtyId={specialtyId}&doctorName={name}&date={date}`
- **Mô tả:** Tìm kiếm bác sĩ theo chuyên khoa, tên và ngày có lịch làm việc
- **Authentication:** ✅ Required
- **Authorization:** Any authenticated user
- **Query Parameters:**
  - `specialtyId` (optional): ID chuyên khoa
  - `doctorName` (optional): Tên bác sĩ (tìm kiếm gần đúng, không phân biệt hoa thường)
  - `date` (optional): Ngày cần tìm (format: yyyy-MM-dd, ví dụ: 2025-11-04)
    - Nếu không truyền date: Tìm trong 7 ngày tiếp theo tính từ hôm nay
- **Logic:**
  - Nếu `specialtyId = null && doctorName = null && date = null`: 
    → Hiện toàn bộ bác sĩ có availability trong 7 ngày tiếp theo
  - Nếu `specialtyId != null && doctorName = null`: 
    → Hiện bác sĩ thuộc chuyên khoa đã chọn, có availability trong date/7 ngày
  - Nếu `specialtyId != null && doctorName != null`: 
    → Hiện bác sĩ có tên khớp và thuộc chuyên khoa, có availability trong date/7 ngày
  - Chỉ trả về time slots có status = AVAILABLE
- **Response (200 OK):**
  ```json
  [
    {
      "doctorId": 1,
      "fullName": "Dr. John Smith",
      "specialty": "Cardiology",
      "experience": 15,
      "rating": 4.8,
      "availableSlots": [
        {
          "slotId": 101,
          "startTime": "2025-11-05T09:00:00",
          "endTime": "2025-11-05T09:30:00",
          "status": "AVAILABLE"
        },
        {
          "slotId": 102,
          "startTime": "2025-11-05T09:30:00",
          "endTime": "2025-11-05T10:00:00",
          "status": "AVAILABLE"
        }
      ]
    }
  ]
  ```

### 3. Get Doctor Detail (for making appointment)
- **Endpoint:** `GET /api/doctors/{doctorId}/detail?startDate={startDate}&endDate={endDate}`
- **Mô tả:** Lấy thông tin chi tiết bác sĩ và các time slots để đặt lịch
- **Authentication:** ✅ Required
- **Authorization:** Any authenticated user
- **Path Parameters:**
  - `doctorId` (required): ID của bác sĩ
- **Query Parameters:**
  - `startDate` (optional): Ngày bắt đầu (default: hôm nay)
  - `endDate` (optional): Ngày kết thúc (default: hôm nay + 7 ngày)
- **Logic:**
  - Lấy thông tin bác sĩ (fullName, specialties, experience, rating, bio)
  - Lấy tất cả time slots trong khoảng startDate → endDate
  - Chỉ trả về slots có status = AVAILABLE
  - Nhóm slots theo ngày (timeSlotsByDate)
- **Response (200 OK):**
  ```json
  {
    "doctorId": 1,
    "fullName": "Dr. John Smith",
    "specialties": ["Cardiology"],
    "experience": 15,
    "averageRating": 4.8,
    "bio": "Experienced cardiologist specializing in...",
    "timeSlots": [
      {
        "slotId": 101,
        "date": "2025-11-05",
        "startTime": "09:00",
        "endTime": "09:30",
        "status": "AVAILABLE"
      },
      {
        "slotId": 102,
        "date": "2025-11-05",
        "startTime": "09:30",
        "endTime": "10:00",
        "status": "AVAILABLE"
      }
    ]
  }
  ```
- **Error Response (404 Not Found):**
  ```json
  {
    "timestamp": "2025-11-04T10:00:00.000+00:00",
    "status": 404,
    "error": "Not Found",
    "message": "Doctor not found with ID: 999"
  }
  ```

---

## 📅 IV. Availability Block API (Doctor Only)

### 1. Create Availability Block
- **Endpoint:** `POST /api/doctors/{doctorId}/availability`
- **Mô tả:** Bác sĩ tạo khung giờ làm việc mới (tự động sinh time slots 30 phút)
- **Authentication:** ✅ Required
- **Authorization:** 🔒 DOCTOR role only
- **Path Parameters:**
  - `doctorId` (required): ID của bác sĩ
- **Request Body (JSON):**
  ```json
  {
    "workDate": "2025-11-10",
    "startTime": "08:00",
    "endTime": "12:00"
  }
  ```
- **Validation:**
  - `workDate`: Bắt buộc, format yyyy-MM-dd
  - `startTime`: Bắt buộc, format HH:mm
  - `endTime`: Bắt buộc, format HH:mm, phải sau startTime
- **Logic:**
  - Kiểm tra doctor tồn tại
  - Tạo AvailabilityBlock record
  - **Tự động generate time slots 30 phút:**
    - Ví dụ: 08:00 - 12:00 → 8 slots (08:00-08:30, 08:30-09:00, ..., 11:30-12:00)
  - Mỗi slot có status mặc định = AVAILABLE
  - Lưu tất cả slots vào database
- **Response (200 OK):**
  ```json
  {
    "id": 1,
    "doctorId": 1,
    "workDate": "2025-11-10",
    "startTime": "08:00",
    "endTime": "12:00",
    "generatedSlots": 8,
    "createdAt": "2025-11-04T10:00:00"
  }
  ```
- **Error Response (403 Forbidden):**
  ```json
  {
    "timestamp": "2025-11-04T10:00:00.000+00:00",
    "status": 403,
    "error": "Forbidden",
    "message": "Access Denied"
  }
  ```

### 2. Get Availability Blocks by Doctor
- **Endpoint:** `GET /api/doctors/{doctorId}/availability`
- **Mô tả:** Lấy tất cả các khung giờ làm việc của bác sĩ
- **Authentication:** ✅ Required
- **Authorization:** Any authenticated user
- **Path Parameters:**
  - `doctorId` (required): ID của bác sĩ
- **Response (200 OK):**
  ```json
  [
    {
      "id": 1,
      "doctorId": 1,
      "workDate": "2025-11-10",
      "startTime": "08:00",
      "endTime": "12:00",
      "generatedSlots": 8,
      "createdAt": "2025-11-04T10:00:00"
    },
    {
      "id": 2,
      "doctorId": 1,
      "workDate": "2025-11-12",
      "startTime": "14:00",
      "endTime": "17:00",
      "generatedSlots": 6,
      "createdAt": "2025-11-04T11:00:00"
    }
  ]
  ```

### 3. Get Availability Blocks by Date
- **Endpoint:** `GET /api/doctors/{doctorId}/availability?date={date}`
- **Mô tả:** Lấy các khung giờ làm việc của bác sĩ trong một ngày cụ thể
- **Authentication:** ✅ Required
- **Authorization:** Any authenticated user
- **Path Parameters:**
  - `doctorId` (required): ID của bác sĩ
- **Query Parameters:**
  - `date` (required): Ngày cần lọc (format: yyyy-MM-dd)
- **Response (200 OK):**
  ```json
  [
    {
      "id": 1,
      "doctorId": 1,
      "workDate": "2025-11-10",
      "startTime": "08:00",
      "endTime": "12:00",
      "generatedSlots": 8,
      "createdAt": "2025-11-04T10:00:00"
    }
  ]
  ```

### 4. Delete Availability Block (Partial or Full)
- **Endpoint:** `DELETE /api/doctors/{doctorId}/availability/{blockId}`
- **Mô tả:** Xóa khung giờ làm việc (toàn bộ hoặc chỉ một phần)
- **Authentication:** ✅ Required
- **Authorization:** 🔒 DOCTOR role only
- **Path Parameters:**
  - `doctorId` (required): ID của bác sĩ
  - `blockId` (required): ID của availability block cần xóa
- **Request Body (JSON) - Optional:**
  ```json
  {
    "startTime": "13:00",
    "endTime": "15:00"
  }
  ```
  - **Nếu KHÔNG có body:** Xóa toàn bộ block
  - **Nếu CÓ body:** Xóa chỉ một phần khung giờ từ `startTime` đến `endTime`

- **Validation:**
  - `startTime` và `endTime` phải nằm trong khoảng thời gian của block gốc
  - `startTime` phải nhỏ hơn `endTime`
  - Không được xóa các time slots đã BOOKED

- **Logic:**
  
  **Trường hợp 1: Xóa toàn bộ block (không có body)**
  - Kiểm tra có time slot nào đã BOOKED không
  - Nếu có → Throw error "Cannot delete availability block. X time slot(s) already booked."
  - Nếu không → Xóa toàn bộ block và tất cả time slots
  
  **Trường hợp 2: Xóa một phần (có body với startTime/endTime)**
  
  **Ví dụ:** Block gốc: 09:00 - 15:00, muốn xóa: 13:00 - 15:00
  
  - **TH2.1: Xóa phần đầu** (startTime == block.startTime)
    - Block: 09:00-15:00, Xóa: 09:00-12:00
    - Kết quả: Block còn lại 12:00-15:00
    - Cập nhật block.startTime = 12:00
  
  - **TH2.2: Xóa phần cuối** (endTime == block.endTime)
    - Block: 09:00-15:00, Xóa: 13:00-15:00
    - Kết quả: Block còn lại 09:00-13:00
    - Cập nhật block.endTime = 13:00
  
  - **TH2.3: Xóa phần giữa** (xóa khoảng giữa block)
    - Block: 09:00-15:00, Xóa: 11:00-13:00
    - Kết quả: Tạo 2 blocks mới
      - Block 1: 09:00-11:00
      - Block 2: 13:00-15:00
    - Xóa block gốc, tạo 2 blocks mới với time slots tương ứng

- **Response (200 OK) - Xóa toàn bộ:**
  ```json
  "Availability block deleted completely."
  ```

- **Response (200 OK) - Xóa phần đầu/cuối:**
  ```json
  "Deleted last part (13:00 - 15:00). Block updated to 09:00 - 13:00"
  ```

- **Response (200 OK) - Xóa phần giữa:**
  ```json
  "Deleted middle part (11:00 - 13:00). Created 2 new blocks: 09:00-11:00 and 13:00-15:00"
  ```

- **Error Response (400 Bad Request) - Có slot đã booked:**
  ```json
  "Cannot delete time slots. 2 slot(s) in this range already booked."
  ```

- **Error Response (400 Bad Request) - Invalid time range:**
  ```json
  "Delete time range must be within block time range (09:00 - 15:00)"
  ```

- **Error Response (404 Not Found):**
  ```json
  {
    "timestamp": "2025-11-04T10:00:00.000+00:00",
    "status": 404,
    "error": "Not Found",
    "message": "Availability block not found with ID: 999"
  }
  ```

- **Use Cases:**
  
  **Use Case 1: Xóa toàn bộ lịch làm việc**
  ```bash
  DELETE /api/doctors/1/availability/10
  # Không có body
  ```
  
  **Use Case 2: Bác sĩ bận từ 13h-15h, chỉ xóa phần này**
  ```bash
  DELETE /api/doctors/1/availability/10
  Body: { "startTime": "13:00", "endTime": "15:00" }
  ```
  
  **Use Case 3: Bác sĩ bận từ 10h-12h (phần giữa)**
  ```bash
  DELETE /api/doctors/1/availability/10
  Body: { "startTime": "10:00", "endTime": "12:00" }
  # Kết quả: 09:00-10:00 và 12:00-15:00 vẫn available
  ```

---

## 📋 V. Appointment API

### 1. Create Appointment (Book Appointment)
- **Endpoint:** `POST /api/appointments`
- **Mô tả:** Bệnh nhân đặt lịch hẹn với bác sĩ
- **Authentication:** ✅ Required
- **Authorization:** 🔒 PATIENT role only
- **Request Body (JSON):**
  ```json
  {
    "patientId": 1,
    "doctorId": 2,
    "timeSlotId": 101,
    "reason": "Routine checkup and consultation"
  }
  ```
- **Validation:**
  - `patientId`: Bắt buộc
  - `doctorId`: Bắt buộc
  - `timeSlotId`: Bắt buộc
  - `reason`: Bắt buộc
- **Logic:**
  - **Kiểm tra patient tồn tại:** Throw exception nếu không tìm thấy
  - **Kiểm tra doctor tồn tại:** Throw exception nếu không tìm thấy
  - **Kiểm tra timeSlot tồn tại:** Throw exception nếu không tìm thấy
  - **Kiểm tra timeSlot.status = AVAILABLE:** 
    - Nếu BOOKED → Throw "TimeSlot is not available"
  - **Kiểm tra timeSlot thuộc về doctor đúng:**
    - Nếu sai doctor → Throw "TimeSlot does not belong to this doctor"
  - Tạo Appointment với status = SCHEDULED
  - **Cập nhật timeSlot.status = BOOKED**
  - Lưu appointment vào database
- **Response (201 Created):**
  ```json
  {
    "appointmentId": 1,
    "patientName": "Nguyễn Văn A",
    "doctorName": "Dr. John Smith",
    "appointmentTime": "2025-11-05T09:00:00",
    "status": "SCHEDULED",
    "reason": "Routine checkup and consultation"
  }
  ```
- **Error Response (400 Bad Request):**
  ```json
  "TimeSlot with ID 101 not found or not available"
  ```
- **Error Response (403 Forbidden):**
  ```json
  {
    "timestamp": "2025-11-04T10:00:00.000+00:00",
    "status": 403,
    "error": "Forbidden",
    "message": "Access Denied"
  }
  ```

---

## 🔐 VI. Security & Authorization Summary

### Public Endpoints (No Authentication)
```
POST   /api/auth/register          → Register patient
POST   /api/auth/login             → Login
GET    /api/specialties            → Get all specialties
```

### Authenticated Endpoints (Any Role)
```
GET    /api/doctors                → Get all doctors
GET    /api/doctors/search         → Search doctors
GET    /api/doctors/{id}/detail    → Get doctor details
GET    /api/doctors/{id}/availability       → Get availability blocks
GET    /api/doctors/{id}/availability?date  → Get blocks by date
```

### PATIENT Role Only
```
POST   /api/appointments           → Create appointment
```

### DOCTOR Role Only
```
POST   /api/doctors/{id}/availability           → Create availability block
DELETE /api/doctors/{id}/availability/{blockId} → Delete block
```

---

## 📊 VII. Data Flow Diagram

### Appointment Booking Flow
```
1. Patient Login → JWT Token
2. GET /api/specialties → Select specialty
3. GET /api/doctors/search?specialtyId=1&date=2025-11-05 → View available doctors + slots
4. GET /api/doctors/1/detail → View doctor details + all slots for 7 days
5. POST /api/appointments → Book appointment
   → Backend: Check validations → Create appointment → Update slot status to BOOKED
6. Response: Appointment confirmed
```

### Doctor Schedule Management Flow
```
1. Doctor Login → JWT Token (DOCTOR role)
2. POST /api/doctors/1/availability → Create work schedule (09:00-15:00)
   → Backend: Create block → Auto-generate 30-min time slots
3. GET /api/doctors/1/availability → View all blocks
4. DELETE /api/doctors/1/availability/1 → Delete block
   
   Option A - Delete entire block:
   → No request body
   → All time slots deleted (if not BOOKED)
   
   Option B - Delete partial (e.g., 13:00-15:00):
   → Body: { "startTime": "13:00", "endTime": "15:00" }
   → Only delete slots in that range
   → Block updated to 09:00-13:00
   
   Option C - Delete middle part (e.g., 11:00-13:00):
   → Body: { "startTime": "11:00", "endTime": "13:00" }
   → Create 2 new blocks: 09:00-11:00 and 13:00-15:00
   → Delete original block
```

---

## ⚠️ VIII. Error Codes Reference

| Code | Description | Example |
|------|-------------|---------|
| 200 | OK | Successful GET requests |
| 201 | Created | Appointment created |
| 204 | No Content | Successful DELETE |
| 400 | Bad Request | Invalid request body, TimeSlot not available |
| 401 | Unauthorized | Invalid login credentials |
| 403 | Forbidden | No token, invalid token, wrong role |
| 404 | Not Found | Doctor not found, Patient not found |

---

## 🧪 IX. Testing Notes

### JWT Token Management
- Token expires in 24 hours (configured in `application.yml`)
- Include in header: `Authorization: Bearer {token}`
- Token is auto-generated after login/register
- Contains: userId, role, expiration time

### Time Slot Generation Logic
- Availability Block: 08:00 - 12:00 → Generates 8 slots
  - Slot 1: 08:00 - 08:30
  - Slot 2: 08:30 - 09:00
  - Slot 3: 09:00 - 09:30
  - ...
  - Slot 8: 11:30 - 12:00

### Partial Delete Logic
**Example: Block 09:00-15:00, Delete 13:00-15:00**

1. **Delete full block (no body):**
   - Check if any slot is BOOKED
   - Delete all slots + block

2. **Delete first part (09:00-12:00):**
   - Delete slots from 09:00 to 12:00
   - Update block: startTime = 12:00
   - Remaining block: 12:00-15:00

3. **Delete last part (13:00-15:00):**
   - Delete slots from 13:00 to 15:00
   - Update block: endTime = 13:00
   - Remaining block: 09:00-13:00

4. **Delete middle part (11:00-13:00):**
   - Delete slots from 11:00 to 13:00
   - Delete original block
   - Create Block 1: 09:00-11:00 (auto-generate slots)
   - Create Block 2: 13:00-15:00 (auto-generate slots)
   - Result: 2 separate blocks

### Appointment Validation Order
1. Check patient exists
2. Check doctor exists
3. Check timeSlot exists
4. Check timeSlot.status = AVAILABLE
5. Check timeSlot belongs to correct doctor
6. Create appointment + Update slot to BOOKED

---

**Last Updated:** November 4, 2025  
**API Version:** 1.0  
**Base URL:** `http://localhost:8000`