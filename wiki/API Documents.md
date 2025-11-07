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
- **Mô tả:** Lấy thông tin chi tiết bác sĩ, các time slots và đánh giá từ bệnh nhân
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
  - Lấy tối đa 10 ratings gần nhất của bác sĩ
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
    ],
    "ratings": [
      {
        "ratingId": 15,
        "patientName": "Nguyễn Văn A",
        "stars": 5,
        "feedbackText": "Bác sĩ rất tận tâm và chuyên nghiệp",
        "createdAt": "2025-11-06T10:00:00Z"
      },
      {
        "ratingId": 14,
        "patientName": "Trần Thị B",
        "stars": 4,
        "feedbackText": "Khám bệnh kỹ lưỡng",
        "createdAt": "2025-11-05T14:30:00Z"
      }
    ],
    "totalRatings": 25
  }
  ```
- **Response Fields:**
  - `ratings`: Danh sách tối đa 10 ratings gần nhất
  - `totalRatings`: Tổng số ratings của bác sĩ
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
    "symptoms": "Đau đầu, chóng mặt",
    "suspectedDisease": "Migraine"
  }
  ```
- **Validation:**
  - `patientId`: Bắt buộc
  - `doctorId`: Bắt buộc
  - `timeSlotId`: Bắt buộc
  - `symptoms`: Khuyến nghị (có thể rút gọn)
- **Logic:**
  - **Kiểm tra patient tồn tại:** Throw exception nếu không tìm thấy
  - **Kiểm tra doctor tồn tại:** Throw exception nếu không tìm thấy
  - **Kiểm tra timeSlot tồn tại:** Throw exception nếu không tìm thấy
  - **Kiểm tra timeSlot.status = AVAILABLE:** 
    - Nếu BOOKED → Throw "Time slot is not available"
  - **Kiểm tra timeSlot thuộc về doctor đúng:**
    - Nếu sai doctor → Throw "Time slot does not belong to the specified doctor"
  - Tạo Appointment với status = PENDING
  - **Cập nhật timeSlot.status = BOOKED**
  - Lưu appointment vào database
- **Response (201 Created):**
  ```json
  {
    "id": 1,
    "patientId": 1,
    "patientName": "Nguyễn Văn A",
    "doctorId": 2,
    "doctorName": "Dr. John Smith",
    "timeSlotId": 101,
    "startTime": "2025-11-05T09:00:00",
    "endTime": "2025-11-05T09:30:00",
    "symptoms": "Đau đầu, chóng mặt",
    "suspectedDisease": "Migraine",
    "status": "PENDING"
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

### 2. Delete Appointment (Cancel Appointment)
- **Endpoint:** `DELETE /api/appointments/{appointmentId}`
- **Mô tả:** Bệnh nhân hủy cuộc hẹn đã đặt
- **Authentication:** ✅ Required
- **Authorization:** 🔒 PATIENT role only (chỉ được hủy appointment của chính mình)
- **Path Parameters:**
  - `appointmentId` (required): ID của appointment cần hủy
- **Business Rules:**
  - ⏰ **Chỉ được hủy trước 48h** - Nếu còn < 48h sẽ bị reject
  - ❌ Không thể hủy appointment đã CANCELED hoặc COMPLETED
  - ✅ Khi hủy thành công, time slot sẽ được giải phóng (BOOKED → AVAILABLE)
- **Logic:**
  1. Tìm appointment theo ID
  2. Kiểm tra authorization (chỉ patient tạo appointment mới được hủy)
  3. Kiểm tra status (không được CANCELED/COMPLETED)
  4. **Kiểm tra thời gian:** Tính số giờ từ hiện tại đến appointment time
     - Nếu < 48h → Throw error "Cannot cancel appointment. Must cancel at least 48 hours in advance"
  5. Cập nhật appointment.status = CANCELED
  6. Giải phóng time slot: timeSlot.status = AVAILABLE
- **Response (200 OK):**
  ```json
  "Appointment canceled successfully. Time slot is now available for other patients."
  ```
- **Error Response (400 Bad Request) - Quá gần thời gian hẹn:**
  ```json
  "Cannot cancel appointment. Must cancel at least 48 hours in advance. Only 36 hours remaining."
  ```
- **Error Response (400 Bad Request) - Đã canceled:**
  ```json
  "Appointment is already canceled"
  ```
- **Error Response (403 Forbidden) - Không phải appointment của mình:**
  ```json
  "You can only cancel your own appointments"
  ```

### 3. Update Appointment (Change Appointment Information)
- **Endpoint:** `PUT /api/appointments/{appointmentId}`
- **Mô tả:** Cập nhật thông tin cuộc hẹn (triệu chứng hoặc đổi lịch)
- **Authentication:** ✅ Required
- **Authorization:** 🔒 PATIENT role only (chỉ được update appointment của chính mình)
- **Path Parameters:**
  - `appointmentId` (required): ID của appointment cần update
- **Request Body (JSON):**
  ```json
  {
    "symptoms": "Đau đầu dữ dội, buồn nôn, chóng mặt",
    "suspectedDisease": "Migraine cấp tính",
    "newTimeSlotId": 456
  }
  ```
  - **Tất cả fields đều optional** - Chỉ gửi field nào cần update
- **Business Rules:**
  
  **A. Update Symptoms/Suspected Disease:**
  - ✅ **Không giới hạn thời gian** - Có thể update bất kỳ lúc nào
  - ✅ **Không giới hạn số lần** - Có thể update nhiều lần
  
  **B. Update Time Slot (Reschedule):**
  - ⏰ **Chỉ được đổi lịch trước 48h** - Nếu còn < 48h sẽ bị reject
  - 🔢 **Giới hạn tối đa 2 lần đổi lịch** - Lần thứ 3 sẽ bị reject
  - ✅ Time slot mới phải thuộc về cùng bác sĩ
  - ✅ Time slot mới phải có status = AVAILABLE
  - ✅ Khi đổi lịch: Old slot → AVAILABLE, New slot → BOOKED
  
- **Validation:**
  - ❌ Không thể update appointment đã CANCELED hoặc COMPLETED
  - `newTimeSlotId`: Phải tồn tại và có status = AVAILABLE
  - `newTimeSlotId`: Phải thuộc về cùng doctor với appointment gốc
- **Logic:**
  1. Tìm appointment theo ID
  2. Kiểm tra authorization (chỉ patient tạo appointment mới được update)
  3. Kiểm tra status (không được CANCELED/COMPLETED)
  4. **Update symptoms và suspected disease (nếu có trong request):**
     - Không kiểm tra thời gian
     - Không kiểm tra số lần
  5. **Update time slot (nếu có newTimeSlotId trong request):**
     - 5.1: Kiểm tra thời gian - Phải còn ≥ 48h
     - 5.2: Kiểm tra reschedule count - Phải < 2
     - 5.3: Kiểm tra new time slot tồn tại và AVAILABLE
     - 5.4: Kiểm tra new time slot thuộc về cùng doctor
     - 5.5: Giải phóng old time slot (BOOKED → AVAILABLE)
     - 5.6: Book new time slot (AVAILABLE → BOOKED)
     - 5.7: Cập nhật appointment.timeSlot = newTimeSlot
     - 5.8: Tăng appointment.rescheduleCount += 1
  6. Lưu appointment đã update
- **Response (200 OK) - Update symptoms only:**
  ```json
  {
    "id": 1,
    "patientId": 1,
    "patientName": "Nguyễn Văn A",
    "doctorId": 2,
    "doctorName": "Dr. John Smith",
    "timeSlotId": 101,
    "startTime": "2025-11-10T09:00:00",
    "endTime": "2025-11-10T09:30:00",
    "symptoms": "Đau đầu dữ dội, buồn nôn, chóng mặt",
    "suspectedDisease": "Migraine cấp tính",
    "status": "PENDING"
  }
  ```
- **Response (200 OK) - Reschedule (change timeSlot):**
  ```json
  {
    "id": 1,
    "patientId": 1,
    "patientName": "Nguyễn Văn A",
    "doctorId": 2,
    "doctorName": "Dr. John Smith",
    "timeSlotId": 456,
    "startTime": "2025-11-12T14:00:00",
    "endTime": "2025-11-12T14:30:00",
    "symptoms": "Đau đầu, chóng mặt",
    "status": "PENDING"
  }
  ```
- **Error Response (400 Bad Request) - Reschedule quá gần:**
  ```json
  "Cannot reschedule appointment. Must reschedule at least 48 hours in advance. Only 30 hours remaining."
  ```
- **Error Response (400 Bad Request) - Vượt quá số lần reschedule:**
  ```json
  "Cannot reschedule appointment. Maximum 2 reschedules allowed. Current reschedule count: 2"
  ```
- **Error Response (400 Bad Request) - Time slot không available:**
  ```json
  "New time slot is not available"
  ```
- **Error Response (400 Bad Request) - Time slot sai doctor:**
  ```json
  "New time slot does not belong to the same doctor"
  ```
- **Error Response (403 Forbidden):**
  ```json
  "You can only update your own appointments"
  ```

### 4. Get List of Appointments
- **Endpoint:** `GET /api/appointments?patientId={patientId}&status={status}`
- **Mô tả:** Lấy danh sách các cuộc hẹn của patient
- **Authentication:** ✅ Required
- **Authorization:** 🔒 PATIENT role only (chỉ được xem appointments của chính mình)
- **Query Parameters:**
  - `patientId` (required): ID của patient
  - `status` (optional): Filter theo trạng thái (PENDING, COMPLETED, CANCELED)
- **Logic:**
  1. Kiểm tra authorization (patient chỉ được xem appointments của mình)
  2. Nếu có `status` parameter:
     - Parse status string → enum
     - Query appointments với filter: `findByPatientIdAndStatusOrderByTimeSlotStartTimeDesc`
  3. Nếu không có `status` parameter:
     - Query tất cả appointments: `findByPatientIdOrderByTimeSlotStartTimeDesc`
  4. Convert to response DTOs và return
- **Response (200 OK) - Không có filter:**
  ```json
  [
    {
      "id": 3,
      "patientId": 1,
      "patientName": "Nguyễn Văn A",
      "doctorId": 2,
      "doctorName": "Dr. John Smith",
      "timeSlotId": 201,
      "startTime": "2025-11-15T10:00:00",
      "endTime": "2025-11-15T10:30:00",
      "symptoms": "Khám định kỳ",
      "suspectedDisease": null,
      "status": "PENDING"
    },
    {
      "id": 2,
      "patientId": 1,
      "patientName": "Nguyễn Văn A",
      "doctorId": 1,
      "doctorName": "Dr. Jane Doe",
      "timeSlotId": 150,
      "startTime": "2025-11-08T14:00:00",
      "endTime": "2025-11-08T14:30:00",
      "symptoms": "Đau bụng",
      "suspectedDisease": "Viêm dạ dày",
      "status": "COMPLETED"
    },
    {
      "id": 1,
      "patientId": 1,
      "patientName": "Nguyễn Văn A",
      "doctorId": 2,
      "doctorName": "Dr. John Smith",
      "timeSlotId": 101,
      "startTime": "2025-11-05T09:00:00",
      "endTime": "2025-11-05T09:30:00",
      "symptoms": "Đau đầu",
      "suspectedDisease": "Migraine",
      "status": "CANCELED"
    }
  ]
  ```
- **Response (200 OK) - Có filter status=PENDING:**
  ```json
  [
    {
      "id": 3,
      "patientId": 1,
      "patientName": "Nguyễn Văn A",
      "doctorId": 2,
      "doctorName": "Dr. John Smith",
      "timeSlotId": 201,
      "startTime": "2025-11-15T10:00:00",
      "endTime": "2025-11-15T10:30:00",
      "symptoms": "Khám định kỳ",
      "suspectedDisease": null,
      "status": "PENDING"
    }
  ]
  ```
- **Error Response (400 Bad Request) - Invalid status:**
  ```json
  "Invalid status filter. Valid values: PENDING, COMPLETED, CANCELED"
  ```
- **Error Response (403 Forbidden) - Không phải appointments của mình:**
  ```json
  "You can only view your own appointments"
  ```
- **Use Cases:**
  
  **Use Case 1: Xem tất cả appointments**
  ```bash
  GET /api/appointments?patientId=1
  ```
  
  **Use Case 2: Xem chỉ appointments đang chờ (PENDING)**
  ```bash
  GET /api/appointments?patientId=1&status=PENDING
  ```
  
  **Use Case 3: Xem appointments đã hoàn thành**
  ```bash
  GET /api/appointments?patientId=1&status=COMPLETED
  ```
  
  **Use Case 4: Xem appointments đã hủy**
  ```bash
  GET /api/appointments?patientId=1&status=CANCELED
  ```

---

## � VI. Patient Profile API

### 1. Get Patient Profile
- **Endpoint:** `GET /api/patients/{patientId}/profile`
- **Mô tả:** Xem thông tin cá nhân của bệnh nhân
- **Authentication:** ✅ Required
- **Authorization:** 🔒 PATIENT role only (chỉ được xem thông tin của chính mình)
- **Path Parameters:**
  - `patientId` (required): ID của bệnh nhân
- **Logic:**
  - Kiểm tra patient tồn tại
  - Kiểm tra authorization (chỉ được xem profile của chính mình)
  - Trả về thông tin cá nhân bao gồm: email, fullName, dateOfBirth, gender, phoneNumber, address, medicalHistory
- **Response (200 OK):**
  ```json
  {
    "patientId": 1,
    "email": "patient@example.com",
    "fullName": "Nguyễn Văn A",
    "dateOfBirth": "1990-01-15",
    "gender": "MALE",
    "phoneNumber": "0912345678",
    "address": "123 Nguyễn Huệ, Q1, TP.HCM",
    "medicalHistory": "Tiền sử dị ứng thuốc kháng sinh, cao huyết áp"
  }
  ```
- **Error Response (404 Not Found):**
  ```json
  {
    "timestamp": "2025-11-07T10:00:00.000+00:00",
    "status": 404,
    "error": "Not Found",
    "message": "Patient not found with ID: 999"
  }
  ```
- **Error Response (403 Forbidden):**
  ```json
  {
    "timestamp": "2025-11-07T10:00:00.000+00:00",
    "status": 403,
    "error": "Forbidden",
    "message": "Access Denied"
  }
  ```

### 2. Update Patient Profile (Partial Update)
- **Endpoint:** `PUT /api/patients/{patientId}/profile`
- **Mô tả:** Cập nhật thông tin cá nhân của bệnh nhân (partial update - chỉ gửi fields cần update)
- **Authentication:** ✅ Required
- **Authorization:** 🔒 PATIENT role only (chỉ được cập nhật thông tin của chính mình)
- **Path Parameters:**
  - `patientId` (required): ID của bệnh nhân
- **Request Body (JSON):**
  ```json
  {
    "fullName": "Nguyễn Văn A",
    "dateOfBirth": "1990-01-15",
    "gender": "MALE",
    "phoneNumber": "0912345678",
    "address": "123 Nguyễn Huệ, Q1, TP.HCM",
    "medicalHistory": "Tiền sử dị ứng thuốc kháng sinh, cao huyết áp"
  }
  ```
- **⭕ TẤT CẢ FIELDS ĐỀU OPTIONAL:**
  - Bạn có thể gửi tất cả fields hoặc chỉ một vài fields cần update
  - Chỉ những fields được gửi lên mới được update
  - Những fields không gửi sẽ giữ nguyên giá trị cũ
- **Validation:**
  - `fullName`: Nếu gửi, không được rỗng (blank)
  - `dateOfBirth`: Nếu gửi, phải là ngày trong quá khứ
  - `gender`: Nếu gửi, phải là enum [MALE, FEMALE, OTHER]
  - `phoneNumber`: Nếu gửi, không được rỗng (blank)
  - `address`: Không bắt buộc (có thể null hoặc rỗng)
  - `medicalHistory`: Không bắt buộc (có thể null hoặc rỗng)
- **Logic:**
  - Kiểm tra patient tồn tại
  - Kiểm tra authorization (chỉ được cập nhật profile của chính mình)
  - Validate các fields được gửi lên
  - **CHỈ UPDATE CÁC FIELDS KHÔNG NULL** (partial update)
  - Tự động cập nhật updatedAt timestamp
  - **Lưu ý:** Email KHÔNG được phép thay đổi
- **Response (200 OK):**
  ```json
  {
    "patientId": 1,
    "email": "patient@example.com",
    "fullName": "Nguyễn Văn A",
    "dateOfBirth": "1990-01-15",
    "gender": "MALE",
    "phoneNumber": "0912345678",
    "address": "456 Lê Lợi, Q1, TP.HCM",
    "medicalHistory": "Tiền sử dị ứng thuốc kháng sinh, cao huyết áp, đái tháo đường type 2"
  }
  ```
- **Use Cases:**
  
  **Use Case 1: Update chỉ address**
  ```json
  {
    "address": "456 Lê Lợi, Q1, TP.HCM"
  }
  ```
  → Chỉ address được update, các fields khác giữ nguyên
  
  **Use Case 2: Update chỉ medical history**
  ```json
  {
    "medicalHistory": "Thêm tiền sử đái tháo đường type 2"
  }
  ```
  → Chỉ medicalHistory được update
  
  **Use Case 3: Update nhiều fields cùng lúc**
  ```json
  {
    "fullName": "Nguyễn Văn B",
    "phoneNumber": "0987654321",
    "address": "789 Trần Hưng Đạo, Q5"
  }
  ```
  → Cả 3 fields được update, các fields khác giữ nguyên
- **Error Response (400 Bad Request):**
  ```json
  {
    "timestamp": "2025-11-07T10:00:00.000+00:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Date of birth must be in the past"
  }
  ```
- **Error Response (404 Not Found):**
  ```json
  {
    "timestamp": "2025-11-07T10:00:00.000+00:00",
    "status": 404,
    "error": "Not Found",
    "message": "Patient not found with ID: 999"
  }
  ```

---

## 🌟 VII. Rating & Comment API

### 1. Create Rating and Comment
- **Endpoint:** `POST /api/appointments/{appointmentId}/rating`
- **Mô tả:** Bệnh nhân để lại đánh giá và nhận xét về cuộc hẹn đã hoàn thành
- **Authentication:** ✅ Required
- **Authorization:** 🔒 PATIENT role only
- **Path Parameters:**
  - `appointmentId` (required): ID của appointment cần đánh giá
- **Request Body (JSON):**
  ```json
  {
    "stars": 5,
    "feedbackText": "Bác sĩ rất tận tâm và chuyên nghiệp. Khám bệnh kỹ lưỡng."
  }
  ```
- **Validation:**
  - `stars`: Bắt buộc, số nguyên từ 1-5
  - `feedbackText`: Optional (có thể null hoặc rỗng)
- **Business Rules:**
  - ✅ Chỉ appointment có status = COMPLETED mới được đánh giá
  - ✅ Mỗi appointment chỉ được đánh giá 1 lần duy nhất
  - ✅ Sau khi tạo rating, hệ thống tự động tính lại average_rating của bác sĩ
- **Logic:**
  1. Kiểm tra appointment tồn tại
  2. Kiểm tra appointment đã COMPLETED chưa
  3. Kiểm tra appointment đã được rating chưa (1 appointment chỉ được rating 1 lần)
  4. Tạo rating mới với stars và feedbackText
  5. Tính toán lại average_rating của bác sĩ:
     - Lấy tất cả ratings của bác sĩ
     - Tính trung bình cộng số stars
     - Làm tròn đến 2 chữ số thập phân
     - Cập nhật vào Doctor.averageRating
- **Response (201 Created):**
  ```json
  {
    "ratingId": 1,
    "appointmentId": 10,
    "patientId": 1,
    "patientName": "Nguyễn Văn A",
    "doctorId": 2,
    "doctorName": "Dr. John Smith",
    "stars": 5,
    "feedbackText": "Bác sĩ rất tận tâm và chuyên nghiệp. Khám bệnh kỹ lưỡng.",
    "createdAt": "2025-11-07T10:00:00Z"
  }
  ```
- **Error Response (400 Bad Request) - Appointment chưa completed:**
  ```json
  "Can only rate completed appointments. Current status: PENDING"
  ```
- **Error Response (400 Bad Request) - Đã rating rồi:**
  ```json
  "This appointment has already been rated"
  ```
- **Error Response (400 Bad Request) - Stars không hợp lệ:**
  ```json
  {
    "timestamp": "2025-11-07T10:00:00.000+00:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Stars must be between 1 and 5"
  }
  ```
- **Error Response (404 Not Found):**
  ```json
  "Appointment not found with ID: 999"
  ```
- **Use Cases:**
  
  **Use Case 1: Đánh giá với feedback đầy đủ**
  ```bash
  POST /api/appointments/10/rating
  Body: {
    "stars": 5,
    "feedbackText": "Bác sĩ rất tận tâm, khám bệnh kỹ lưỡng"
  }
  ```
  
  **Use Case 2: Đánh giá chỉ có stars (không có feedback)**
  ```bash
  POST /api/appointments/10/rating
  Body: {
    "stars": 4
  }
  ```
  
  **Use Case 3: Đánh giá với feedback ngắn**
  ```bash
  POST /api/appointments/10/rating
  Body: {
    "stars": 3,
    "feedbackText": "Tạm ổn"
  }
  ```

### 2. Get Appointments with Rating Information
- **Mô tả:** Khi gọi API `GET /api/appointments`, các appointment đã COMPLETED sẽ có thêm thông tin rating và feedback
- **Endpoint:** `GET /api/appointments?patientId={patientId}&status={status}`
- **Response Fields mới:**
  - `rating`: Số sao (1-5) - chỉ có khi appointment đã được rating
  - `feedback`: Nhận xét - chỉ có khi appointment đã được rating
- **Response Example:**
  ```json
  [
    {
      "id": 10,
      "patientId": 1,
      "patientName": "Nguyễn Văn A",
      "doctorId": 2,
      "doctorName": "Dr. John Smith",
      "timeSlotId": 201,
      "startTime": "2025-11-05T10:00:00",
      "endTime": "2025-11-05T10:30:00",
      "symptoms": "Đau đầu",
      "suspectedDisease": "Migraine",
      "status": "COMPLETED",
      "rating": 5,
      "feedback": "Bác sĩ rất tận tâm và chuyên nghiệp"
    },
    {
      "id": 11,
      "patientId": 1,
      "patientName": "Nguyễn Văn A",
      "doctorId": 3,
      "doctorName": "Dr. Jane Doe",
      "timeSlotId": 202,
      "startTime": "2025-11-10T14:00:00",
      "endTime": "2025-11-10T14:30:00",
      "symptoms": "Khám định kỳ",
      "suspectedDisease": null,
      "status": "PENDING",
      "rating": null,
      "feedback": null
    }
  ]
  ```

---


Rating & Comment API
1. Create rating and comment.
- Bệnh nhân để lại nhận xét và đánh giá về cuộc hẹn
- Yêu cầu Patient authenticate
- params sẽ là appointment id
- request body: số điểm rating(star) và nhận xét(feedback_text)
- Sau khi để lại số điểm -> tiến hành tính toán lại average_rating của bác sĩ.


## �🔐 VII. Security & Authorization Summary

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
POST   /api/appointments                     → Create appointment
DELETE /api/appointments/{appointmentId}     → Cancel appointment (must be ≥48h before)
PUT    /api/appointments/{appointmentId}     → Update appointment (symptoms anytime, reschedule ≥48h, max 2 times)
GET    /api/appointments?patientId={id}&status={status} → Get appointments list (optional status filter)
GET    /api/patients/{patientId}/profile     → Get patient profile
PUT    /api/patients/{patientId}/profile     → Update patient profile
POST   /api/appointments/{appointmentId}/rating → Create rating and comment (only for COMPLETED appointments)
```

### DOCTOR Role Only
```
POST   /api/doctors/{id}/availability           → Create availability block
DELETE /api/doctors/{id}/availability/{blockId} → Delete block
```

---

## 📊 IX. Data Flow Diagram

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

### Appointment Management Flow
```
1. Patient Login → JWT Token (PATIENT role)

2. GET /api/appointments?patientId=1 → View all appointments
   → Or with filter: GET /api/appointments?patientId=1&status=PENDING

3. PUT /api/appointments/5 → Update appointment
   
   Option A - Update symptoms only:
   → Body: { "symptoms": "New symptoms", "suspectedDisease": "New diagnosis" }
   → No time validation
   → Update anytime, unlimited times
   
   Option B - Reschedule appointment:
   → Body: { "newTimeSlotId": 456 }
   → Check: Must be ≥48h before appointment time
   → Check: rescheduleCount < 2
   → Old slot → AVAILABLE, New slot → BOOKED
   → rescheduleCount += 1
   
   Option C - Update both:
   → Body: { "symptoms": "...", "newTimeSlotId": 456 }
   → Apply both validations

4. DELETE /api/appointments/5 → Cancel appointment
   → Check: Must be ≥48h before appointment time
   → Appointment.status → CANCELED
   → TimeSlot.status → AVAILABLE
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

### Patient Profile Management Flow
```
1. Patient Login → JWT Token (PATIENT role)

2. GET /api/patients/1/profile → View personal information
   → Returns: email, fullName, dateOfBirth, gender, phoneNumber, address, medicalHistory

3. PUT /api/patients/1/profile → Update personal information
   → Body: {
       "fullName": "Nguyễn Văn A",
       "dateOfBirth": "1990-01-15",
       "gender": "MALE",
       "phoneNumber": "0912345678",
       "address": "New address",
       "medicalHistory": "Updated medical history"
     }
   → Validation: fullName, dateOfBirth, gender, phoneNumber required
   → Email CANNOT be changed
   → Returns: Updated profile
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

### Appointment Cancellation Rules (48h Rule)
- **Scenario 1:** Appointment at 2025-11-10 10:00
  - Now: 2025-11-08 09:00 (49h before) → ✅ Can cancel
  - Now: 2025-11-08 11:00 (47h before) → ❌ Cannot cancel
  
- **Validation:** `HOURS.between(now, appointmentTime) >= 48`
- **On Cancel:**
  - Appointment.status → CANCELED
  - TimeSlot.status → AVAILABLE (giải phóng slot cho người khác)

### Appointment Update Rules
**A. Update Symptoms/Suspected Disease:**
- ✅ No time limit - Anytime
- ✅ No count limit - Unlimited times
- Use case: Patient nhớ thêm triệu chứng, bổ sung thông tin

**B. Reschedule (Change Time Slot):**
- ⏰ **48h Rule:** Must be ≥48h before appointment time
- 🔢 **Max 2 Reschedules:** rescheduleCount < 2
- ✅ New slot must belong to same doctor
- ✅ New slot must be AVAILABLE
- **On Reschedule:**
  - Old TimeSlot.status → AVAILABLE
  - New TimeSlot.status → BOOKED
  - Appointment.timeSlot → newTimeSlot
  - Appointment.rescheduleCount += 1

**Example:**
```json
// Reschedule 1st time (OK)
PUT /api/appointments/1
Body: { "newTimeSlotId": 200 }
→ rescheduleCount = 1

// Reschedule 2nd time (OK - Last chance)
PUT /api/appointments/1
Body: { "newTimeSlotId": 300 }
→ rescheduleCount = 2

// Reschedule 3rd time (REJECTED)
PUT /api/appointments/1
Body: { "newTimeSlotId": 400 }
→ Error: "Maximum 2 reschedules allowed"
```

### Get Appointments - Status Filter
```bash
# Get all appointments
GET /api/appointments?patientId=1
→ Returns: PENDING, COMPLETED, CANCELED (all statuses)

# Get only pending appointments
GET /api/appointments?patientId=1&status=PENDING
→ Returns: Only appointments with status = PENDING

# Get completed appointments
GET /api/appointments?patientId=1&status=COMPLETED
→ Returns: Only appointments with status = COMPLETED

# Get canceled appointments
GET /api/appointments?patientId=1&status=CANCELED
→ Returns: Only appointments with status = CANCELED
```

**Status Enum Values:**
- `PENDING` - Appointment scheduled, waiting for appointment time
- `COMPLETED` - Appointment finished
- `CANCELED` - Appointment canceled by patient

**Sort Order:**
- Ordered by `timeSlot.startTime` DESC (newest first)
- Recent appointments appear first in list

---

**Last Updated:** November 4, 2025  
**API Version:** 1.0  
**Base URL:** `http://localhost:8000`
Đồng thời ở API Get List of Appointments, đối với các appointment đã completed thì hãy lấy thêm rating và feedback. 