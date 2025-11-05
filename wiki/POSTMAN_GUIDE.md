# 📮 Postman Collection Guide

## 🚀 Quick Start

### 1️⃣ Import Collection vào Postman

1. Mở **Postman Desktop App** hoặc **Postman Web**
2. Click **Import** (góc trên bên trái)
3. Chọn file: `Online-Doctor-Appointment-API.postman_collection.json`
4. Click **Import**

### 2️⃣ Setup Environment (Tùy chọn)

**Cách 1: Dùng Collection Variables (Đã tích hợp sẵn)**
- Collection đã có biến `base_url`, `token`, `patient_token`, `doctor_token`
- Không cần setup gì thêm!

**Cách 2: Tạo Environment riêng (Khuyến nghị cho nhiều môi trường)**

1. Click **Environments** (sidebar bên trái)
2. Click **+** để tạo environment mới
3. Đặt tên: `Local Development`
4. Thêm variables:
   ```
   base_url = http://localhost:8000
   token = (để trống, sẽ tự động fill sau khi login)
   patient_token = (để trống)
   doctor_token = (để trống)
   ```
5. Click **Save**
6. Chọn environment "Local Development" ở góc trên phải

---

## 🔐 Authentication Flow

### **Step 1: Register hoặc Login**

**Option A: Register mới**
1. Mở folder **🔐 Authentication**
2. Chọn request **Register Patient**
3. Click **Send**
4. ✅ Token tự động lưu vào biến `{{token}}` và `{{patient_token}}`

**Option B: Login với account có sẵn**
1. Mở folder **🔐 Authentication**
2. Chọn request **Login**
3. Sửa email/password trong Body
4. Click **Send**
5. ✅ Token tự động lưu vào biến `{{token}}`

### **Step 2: Test Authenticated Endpoints**

Tất cả requests trong các folder sau đã tự động sử dụng `{{token}}`:
- 👨‍⚕️ Doctors
- 📅 Availability Blocks
- 📋 Appointments

Bạn chỉ cần click **Send** mà không cần copy-paste token!

---

## 📂 Collection Structure

### **🔐 Authentication (3 requests)**
```
├── Register Patient          → POST /api/auth/register
├── Login                     → POST /api/auth/login
└── Login - Invalid           → Test 401 error
```

### **🏥 Specialties (1 request)**
```
└── Get All Specialties       → GET /api/specialties (Public)
```

### **👨‍⚕️ Doctors (3 requests)**
```
├── Get All Doctors           → GET /api/doctors
├── Search by Specialty       → GET /api/doctors/search?specialtyId=1&date=...
└── Get Doctor Detail         → GET /api/doctors/{id}/detail?startDate=...
```

### **📅 Availability Blocks (4 requests)**
```
├── Create Availability       → POST /api/doctors/{id}/availability (DOCTOR only)
├── Get All Blocks            → GET /api/doctors/{id}/availability
├── Get Blocks by Date        → GET /api/doctors/{id}/availability?date=...
└── Delete Block              → DELETE /api/doctors/{id}/availability/{blockId}
```

### **📋 Appointments (2 requests)**
```
├── Create Appointment        → POST /api/appointments (PATIENT only)
└── Invalid TimeSlot Test     → Test 400 error
```

### **🧪 Test - Security (3 requests)**
```
├── Search WITHOUT Token      → Test 403 Forbidden
├── Create Appointment NO Token → Test 403 Forbidden
└── Patient tries DOCTOR endpoint → Test 403 Access Denied
```

---

## 🎯 Testing Scenarios

### **Scenario 1: Patient Books Appointment**

1. **Register/Login as Patient**
   ```
   POST /api/auth/register
   Body: { email, password, fullName, ... }
   → Save patient_token
   ```

2. **View Specialties**
   ```
   GET /api/specialties
   → Get specialty IDs
   ```

3. **Search Doctors**
   ```
   GET /api/doctors/search?specialtyId=1&date=2025-11-05
   → Get available doctors and time slots
   ```

4. **Get Doctor Details**
   ```
   GET /api/doctors/1/detail?startDate=2025-11-05&endDate=2025-11-12
   → See all available time slots for 7 days
   ```

5. **Create Appointment**
   ```
   POST /api/appointments
   Body: { patientId: 1, doctorId: 1, timeSlotId: 101, reason: "..." }
   → Book appointment
   ```

### **Scenario 2: Doctor Creates Schedule**

1. **Login as Doctor** (Cần có doctor account trước)
   ```
   POST /api/auth/login
   Body: { email: "doctor@test.com", password: "..." }
   → Save doctor_token
   ```

2. **Create Availability Block**
   ```
   POST /api/doctors/1/availability
   Headers: Authorization: Bearer {{doctor_token}}
   Body: { workDate: "2025-11-10", startTime: "08:00", endTime: "12:00" }
   → Auto-generates 8 time slots (30 min each)
   ```

3. **View Created Blocks**
   ```
   GET /api/doctors/1/availability?date=2025-11-10
   → See all blocks for specific date
   ```

4. **Delete Block (if needed)**
   ```
   DELETE /api/doctors/1/availability/1
   → Remove availability block
   ```

### **Scenario 3: Security Testing**

1. **Test Public Endpoints**
   ```
   GET /api/specialties (No token needed)
   ✅ Should return 200 OK
   ```

2. **Test Protected Endpoints WITHOUT Token**
   ```
   GET /api/doctors/search (No Authorization header)
   ❌ Should return 403 Forbidden
   ```

3. **Test Role-Based Access**
   ```
   POST /api/doctors/1/availability
   Headers: Authorization: Bearer {{patient_token}}
   ❌ Should return 403 Forbidden (PATIENT can't access DOCTOR endpoint)
   ```

---

## 🧪 Test Scripts (Built-in Assertions)

Mỗi request đã có **Test Scripts** tự động kiểm tra:

### **Register/Login Requests**
```javascript
✅ Status code is 200/201
✅ Response has token
✅ Token is saved to environment
✅ Role is correct (PATIENT/DOCTOR)
```

### **GET Requests**
```javascript
✅ Status code is 200
✅ Response is array/object
✅ Required fields exist
```

### **Error Requests**
```javascript
✅ Status code is 400/401/403
✅ Error message returned
```

**Xem Test Results:**
- Click **Send** request
- Xem tab **Test Results** bên dưới
- Tất cả tests PASS = ✅ API hoạt động đúng

---

## 🌍 Multiple Environments

### **Local Development**
```json
{
  "base_url": "http://localhost:8000",
  "token": "",
  "patient_token": "",
  "doctor_token": ""
}
```

### **Staging Server**
```json
{
  "base_url": "https://staging-api.yourdomain.com",
  "token": "",
  "patient_token": "",
  "doctor_token": ""
}
```

### **Production**
```json
{
  "base_url": "https://api.yourdomain.com",
  "token": "",
  "patient_token": "",
  "doctor_token": ""
}
```

**Switch giữa environments:**
- Click dropdown góc trên phải
- Chọn environment muốn dùng
- Tất cả requests sẽ tự động dùng `base_url` của environment đó

---

## 📝 Request Body Templates

### **Register Patient**
```json
{
  "email": "patient@test.com",
  "password": "password123",
  "fullName": "Nguyen Van A",
  "dateOfBirth": "1990-01-15",
  "gender": "MALE",
  "phoneNumber": "0912345678"
}
```

### **Login**
```json
{
  "email": "patient@test.com",
  "password": "password123"
}
```

### **Create Availability Block**
```json
{
  "workDate": "2025-11-10",
  "startTime": "08:00",
  "endTime": "12:00"
}
```

### **Create Appointment**
```json
{
  "patientId": 1,
  "doctorId": 1,
  "timeSlotId": 101,
  "reason": "Routine checkup and consultation"
}
```

---

## ⚙️ Variables Reference

| Variable | Usage | Auto-filled? |
|----------|-------|--------------|
| `{{base_url}}` | API base URL | Manual |
| `{{token}}` | Current user token | ✅ Auto (login/register) |
| `{{patient_token}}` | Patient role token | ✅ Auto (patient login) |
| `{{doctor_token}}` | Doctor role token | ✅ Auto (doctor login) |

**Truy cập variables:**
- `{{base_url}}/api/doctors` → Tự động thay bằng `http://localhost:8000/api/doctors`
- `Authorization: Bearer {{token}}` → Tự động thay bằng JWT token

---

## 🐛 Troubleshooting

### **❌ 403 Forbidden - All Requests**
**Nguyên nhân:** Token expired hoặc không có token

**Giải pháp:**
1. Chạy lại **Login** request
2. Check biến `{{token}}` có giá trị không (Console → Variables)
3. Đảm bảo request có `Authorization: Bearer {{token}}` trong Headers

### **❌ 401 Unauthorized - Login Failed**
**Nguyên nhân:** Email/password sai

**Giải pháp:**
1. Check email/password trong Body
2. Đảm bảo đã register account trước
3. Password phải khớp với lúc register

### **❌ 403 Access Denied - Role Issue**
**Nguyên nhân:** Dùng sai token cho endpoint

**Giải pháp:**
- DOCTOR endpoints: Dùng `{{doctor_token}}`
- PATIENT endpoints: Dùng `{{patient_token}}`
- Check `Authorization` header trong request

### **❌ Connection Refused**
**Nguyên nhân:** Backend chưa chạy hoặc sai port

**Giải pháp:**
1. Start backend: `./mvnw spring-boot:run`
2. Check port: `http://localhost:8000` (không phải 8080)
3. Đảm bảo `base_url` đúng trong environment

### **❌ 400 Bad Request - Invalid Data**
**Nguyên nhân:** Request body sai format hoặc thiếu field

**Giải pháp:**
1. Check Body JSON có đúng format không
2. Check required fields: email, password, fullName, ...
3. Check date format: `YYYY-MM-DD` (e.g., `2025-11-05`)
4. Check time format: `HH:mm` (e.g., `08:00`)

---

## 🚀 Advanced Tips

### **1. Run Collection (Test toàn bộ API)**
1. Click **...** bên cạnh tên collection
2. Chọn **Run collection**
3. Chọn requests muốn test
4. Click **Run**
5. Xem tổng hợp Test Results (pass/fail)

### **2. Export Test Results**
- Sau khi Run collection
- Click **Export Results**
- Lưu file JSON/HTML để báo cáo

### **3. Share Collection**
- Click **...** → **Share**
- Generate link để chia sẻ với team
- Hoặc export file JSON và commit vào Git

### **4. Pre-request Scripts**
Tự động set date/time trước khi gửi request:
```javascript
// Pre-request Script tab
const tomorrow = new Date();
tomorrow.setDate(tomorrow.getDate() + 1);
pm.environment.set("tomorrow", tomorrow.toISOString().split('T')[0]);
```

Dùng trong URL:
```
GET /api/doctors/search?date={{tomorrow}}
```

### **5. Chain Requests**
Dùng kết quả từ request này cho request khác:
```javascript
// Test script của "Search Doctors"
const doctors = pm.response.json();
if (doctors.length > 0) {
    pm.environment.set("first_doctor_id", doctors[0].doctorId);
    pm.environment.set("first_slot_id", doctors[0].availableSlots[0].slotId);
}
```

Dùng trong request tiếp theo:
```json
{
  "doctorId": {{first_doctor_id}},
  "timeSlotId": {{first_slot_id}},
  ...
}
```

---

## 📚 Documentation trong Postman

Mỗi request đã có **Description** chi tiết:
- Authentication requirements
- Request parameters
- Request body schema
- Response examples
- Error cases

**Xem Documentation:**
1. Click vào request
2. Xem tab **Documentation** bên phải
3. Hoặc click **View Documentation** từ menu

---

## ✅ Checklist

- [ ] Import collection vào Postman
- [ ] Setup environment (hoặc dùng collection variables)
- [ ] Start backend server (`./mvnw spring-boot:run`)
- [ ] Test **Register** request → Lưu token
- [ ] Test **Get All Specialties** (public endpoint)
- [ ] Test **Search Doctors** (authenticated endpoint)
- [ ] Test **Create Appointment** (patient endpoint)
- [ ] Test **Security** requests (403 errors)
- [ ] Run entire collection để verify tất cả APIs

---

## 📞 Support

Nếu gặp vấn đề:
1. Check **Console** trong Postman (View → Show Postman Console)
2. Check backend logs (`./mvnw spring-boot:run` output)
3. Verify database connection (PostgreSQL running?)
4. Check `JWT_AUTH_TESTS.md` để hiểu authentication flow

---

## 🎉 Ready to Test!

**Next Steps:**
1. Import collection ✅
2. Click **Send** trên request **Register Patient** 🚀
3. Watch token auto-save 🔐
4. Test all other endpoints 🎯

Happy Testing! 🧪✨
