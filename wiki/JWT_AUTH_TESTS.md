# JWT Authentication Test Cases

## 📋 Tổng quan
Các test cases này kiểm tra JWT authentication và role-based authorization.

---

## 1️⃣ Register User (Public)

### Endpoint
```
POST http://localhost:8000/api/auth/register
```

### Request Body
```json
{
  "email": "patient@test.com",
  "password": "password123",
  "fullName": "Nguyen Van Test",
  "dateOfBirth": "1990-01-15",
  "gender": "MALE",
  "phoneNumber": "0912345678"
}
```

### Expected Response (200 OK)
```json
{
  "email": "patient@test.com",
  "role": "PATIENT",
  "message": "Registration successful",
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZSI6IlBBVElFTlQiLCJpYXQiOjE3MDAwMDAwMDAsImV4cCI6MTcwMDA4NjQwMH0.xxxx"
}
```

### Test Script (Postman)
```javascript
// Lưu token vào environment
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response has token", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.token).to.exist;
    pm.environment.set("auth_token", jsonData.token);
});
```

---

## 2️⃣ Login (Public)

### Endpoint
```
POST http://localhost:8000/api/auth/login
```

### Request Body
```json
{
  "email": "patient@test.com",
  "password": "password123"
}
```

### Expected Response (200 OK)
```json
{
  "email": "patient@test.com",
  "role": "PATIENT",
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZSI6IlBBVElFTlQiLCJpYXQiOjE3MDAwMDAwMDAsImV4cCI6MTcwMDA4NjQwMH0.xxxx"
}
```

### Test Script (Postman)
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response has token", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.token).to.exist;
    pm.expect(jsonData.role).to.eql("PATIENT");
    pm.environment.set("patient_token", jsonData.token);
});
```

---

## 3️⃣ Login Invalid Credentials (Public)

### Endpoint
```
POST http://localhost:8000/api/auth/login
```

### Request Body
```json
{
  "email": "patient@test.com",
  "password": "wrongpassword"
}
```

### Expected Response (401 Unauthorized)
```json
{
  "email": null,
  "role": null,
  "message": "Invalid credentials",
  "token": null
}
```

---

## 4️⃣ Search Doctors WITHOUT Token (Protected)

### Endpoint
```
GET http://localhost:8000/api/doctors/search?specialtyId=1
```

### Headers
```
(NO Authorization header)
```

### Expected Response (403 Forbidden)
```json
{
  "timestamp": "2024-01-15T10:00:00.000+00:00",
  "status": 403,
  "error": "Forbidden",
  "path": "/api/doctors/search"
}
```

---

## 5️⃣ Search Doctors WITH Valid Token (Protected)

### Endpoint
```
GET http://localhost:8000/api/doctors/search?specialtyId=1
```

### Headers
```
Authorization: Bearer {{patient_token}}
```

### Expected Response (200 OK)
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
        "startTime": "2024-01-20T09:00:00",
        "endTime": "2024-01-20T09:30:00",
        "status": "AVAILABLE"
      }
    ]
  }
]
```

### Test Script (Postman)
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is array", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.be.an('array');
});
```

---

## 6️⃣ Create Appointment AS PATIENT (Requires PATIENT role)

### Endpoint
```
POST http://localhost:8000/api/appointments
```

### Headers
```
Authorization: Bearer {{patient_token}}
```

### Request Body
```json
{
  "patientId": 1,
  "doctorId": 1,
  "timeSlotId": 101,
  "reason": "Routine checkup"
}
```

### Expected Response (200 OK)
```json
{
  "appointmentId": 1,
  "patientName": "Nguyen Van Test",
  "doctorName": "Dr. John Smith",
  "appointmentTime": "2024-01-20T09:00:00",
  "status": "SCHEDULED",
  "reason": "Routine checkup"
}
```

---

## 7️⃣ Create Availability Block WITHOUT DOCTOR Token (Requires DOCTOR role)

### Endpoint
```
POST http://localhost:8000/api/doctors/1/availability
```

### Headers
```
Authorization: Bearer {{patient_token}}
```

### Request Body
```json
{
  "workDate": "2024-01-25",
  "startTime": "09:00",
  "endTime": "12:00"
}
```

### Expected Response (403 Forbidden)
```json
{
  "timestamp": "2024-01-15T10:00:00.000+00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/api/doctors/1/availability"
}
```

---

## 8️⃣ Register Doctor Account

### Endpoint
```
POST http://localhost:8000/api/auth/register
```

### Request Body (Cần modify AuthService để hỗ trợ DOCTOR registration)
```json
{
  "email": "doctor@test.com",
  "password": "doctor123",
  "role": "DOCTOR",
  "fullName": "Dr. Jane Doe",
  "specialty": "Cardiology",
  "experience": 10
}
```

### Note
⚠️ Hiện tại AuthService chỉ hỗ trợ PATIENT registration. Cần bổ sung logic để tạo Doctor.

---

## 9️⃣ Get Specialties (Public)

### Endpoint
```
GET http://localhost:8000/api/specialties
```

### Headers
```
(NO Authorization header needed)
```

### Expected Response (200 OK)
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

## 🔐 Security Rules Summary

| Endpoint | Method | Access |
|----------|--------|--------|
| `/api/auth/**` | ALL | Public (permitAll) |
| `/api/specialties/**` | GET | Public |
| `/api/doctors/*/availability` | POST | DOCTOR only |
| `/api/appointments` | POST | PATIENT only |
| `/api/appointments/**` | GET/PUT | Authenticated users |
| `/api/doctors/**` | GET | Authenticated users |

---

## 🧪 Testing Workflow

1. **Register** a patient account → Save token
2. **Login** with credentials → Verify token returned
3. **Test Public Endpoints** without token (Specialties)
4. **Test Protected Endpoints** without token → Expect 403
5. **Test Protected Endpoints** with valid token → Expect success
6. **Test Role-Based Access** (Patient trying to create availability) → Expect 403
7. **Test Invalid Token** → Expect 403

---

## 📝 Postman Environment Variables

Create these variables in Postman:
```
patient_token: (auto-saved from login)
doctor_token: (auto-saved from doctor login)
auth_token: (temporary storage)
```

---

## ⚠️ Known Issues

1. **Doctor Registration**: AuthService chưa hỗ trợ tạo Doctor account
2. **CORS**: Nếu test từ browser, cần cấu hình CORS
3. **Token Expiration**: Token hết hạn sau 24 giờ (cấu hình trong application.yml)
