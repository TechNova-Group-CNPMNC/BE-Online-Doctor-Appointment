# 📊 API Collection Visualization

## 🗺️ API Endpoint Map

```
Online Doctor Appointment API
├── 🔐 Authentication (Public)
│   ├── POST   /api/auth/register          [Register new patient]
│   ├── POST   /api/auth/login             [Login and get JWT token]
│   └── POST   /api/auth/login             [Test invalid credentials]
│
├── 🏥 Specialties (Public)
│   └── GET    /api/specialties            [Get all medical specialties]
│
├── 👨‍⚕️ Doctors (Authenticated - Any Role)
│   ├── GET    /api/doctors                [Get all doctors]
│   ├── GET    /api/doctors/search         [Search with filters + availability]
│   └── GET    /api/doctors/{id}/detail    [Get doctor details + time slots]
│
├── 📅 Availability Blocks (Doctor Only)
│   ├── POST   /api/doctors/{id}/availability           [Create work schedule]
│   ├── GET    /api/doctors/{id}/availability           [Get all blocks]
│   ├── GET    /api/doctors/{id}/availability?date=...  [Filter by date]
│   └── DELETE /api/doctors/{id}/availability/{blockId} [Delete block]
│
├── 📋 Appointments (Patient Only)
│   ├── POST   /api/appointments           [Book appointment]
│   └── POST   /api/appointments           [Test invalid booking]
│
└── 🧪 Security Tests
    ├── GET    /api/doctors/search         [Test without token → 403]
    ├── POST   /api/appointments           [Test without token → 403]
    └── POST   /api/doctors/{id}/availability [Test wrong role → 403]
```

---

## 🔄 Request Flow Diagram

### **Patient Booking Flow**

```
┌─────────────────────────────────────────────────────────────────┐
│                     PATIENT BOOKING JOURNEY                      │
└─────────────────────────────────────────────────────────────────┘

Step 1: Authentication
┌──────────────────────┐
│  POST /auth/register │  → Create account + Get JWT token
│  or                  │
│  POST /auth/login    │  → Login + Get JWT token
└──────────┬───────────┘
           │
           ▼ Token saved to {{token}}
           
Step 2: Browse Specialties
┌──────────────────────┐
│  GET /specialties    │  → [ {id:1, name:"Cardiology"}, ... ]
└──────────┬───────────┘
           │
           ▼ Select specialty
           
Step 3: Search Doctors
┌──────────────────────────────────────┐
│  GET /doctors/search                 │  
│  ?specialtyId=1&date=2025-11-05     │  → Doctors with available slots
└──────────┬───────────────────────────┘
           │
           ▼ Select doctor & time slot
           
Step 4: View Doctor Details
┌──────────────────────────────────────┐
│  GET /doctors/1/detail               │
│  ?startDate=2025-11-05              │  → Doctor info + 7 days slots
│  &endDate=2025-11-12                │
└──────────┬───────────────────────────┘
           │
           ▼ Confirm slot selection
           
Step 5: Book Appointment
┌──────────────────────────────────────┐
│  POST /appointments                  │
│  {                                   │
│    patientId: 1,                    │  → Appointment created
│    doctorId: 1,                     │     Slot marked as BOOKED
│    timeSlotId: 101,                 │
│    reason: "Checkup"                │
│  }                                   │
└──────────────────────────────────────┘

✅ Success: Appointment confirmed!
```

---

### **Doctor Schedule Management Flow**

```
┌─────────────────────────────────────────────────────────────────┐
│                  DOCTOR SCHEDULE MANAGEMENT                      │
└─────────────────────────────────────────────────────────────────┘

Step 1: Login as Doctor
┌──────────────────────┐
│  POST /auth/login    │  → JWT token with DOCTOR role
└──────────┬───────────┘
           │
           ▼ Token saved to {{doctor_token}}
           
Step 2: Create Availability Block
┌──────────────────────────────────────┐
│  POST /doctors/1/availability        │
│  {                                   │
│    workDate: "2025-11-10",          │  → Block created
│    startTime: "08:00",              │     8 time slots generated
│    endTime: "12:00"                 │     (08:00, 08:30, 09:00, ...)
│  }                                   │
└──────────┬───────────────────────────┘
           │
           ▼ Slots available for booking
           
Step 3: View Schedule
┌──────────────────────────────────────┐
│  GET /doctors/1/availability         │
│  ?date=2025-11-10                   │  → All blocks for that date
└──────────┬───────────────────────────┘
           │
           ▼ Review schedule
           
Step 4: Delete Block (if needed)
┌──────────────────────────────────────┐
│  DELETE /doctors/1/availability/1    │  → Block removed
└──────────────────────────────────────┘     Slots deleted

✅ Schedule updated!
```

---

## 🔐 Authentication Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                    JWT AUTHENTICATION FLOW                        │
└──────────────────────────────────────────────────────────────────┘

Client                          Backend                      Database
  │                                │                             │
  │  1. POST /auth/register        │                             │
  ├───────────────────────────────►│  2. Hash password (BCrypt)  │
  │                                ├────────────────────────────►│
  │                                │  3. Save user + patient     │
  │                                │◄────────────────────────────┤
  │                                │                             │
  │  4. Generate JWT token         │                             │
  │     {                          │                             │
  │       sub: "userId",          │                             │
  │       role: "PATIENT",        │                             │
  │       exp: "24h"              │                             │
  │     }                          │                             │
  │◄───────────────────────────────┤                             │
  │                                │                             │
  │  5. Save token to {{token}}    │                             │
  │                                │                             │
  │  6. GET /doctors/search        │                             │
  │     Header: Bearer {{token}}   │                             │
  ├───────────────────────────────►│                             │
  │                                │  7. Validate JWT            │
  │                                │     - Check signature       │
  │                                │     - Check expiration      │
  │                                │     - Extract role          │
  │                                │                             │
  │                                │  8. Check authorization     │
  │                                │     - Role = PATIENT ✅      │
  │                                │                             │
  │                                │  9. Query doctors           │
  │                                ├────────────────────────────►│
  │                                │◄────────────────────────────┤
  │  10. Return data               │                             │
  │◄───────────────────────────────┤                             │
  │                                │                             │

If token invalid/expired:
  │                                │                             │
  │  ❌ 403 Forbidden               │                             │
  │◄───────────────────────────────┤                             │
  │                                │                             │

If wrong role:
  │  POST /doctors/*/availability  │                             │
  │  (with PATIENT token)         │                             │
  ├───────────────────────────────►│                             │
  │                                │  Check: role = PATIENT      │
  │                                │  Required: DOCTOR           │
  │  ❌ 403 Access Denied           │                             │
  │◄───────────────────────────────┤                             │
```

---

## 📦 Collection Organization

```
┌─────────────────────────────────────────────────────────────────┐
│                    POSTMAN COLLECTION STRUCTURE                  │
└─────────────────────────────────────────────────────────────────┘

Collection: "Online Doctor Appointment API"
│
├── Variables (Built-in)
│   ├── base_url: "http://localhost:8000"
│   ├── token: "" (auto-filled by test scripts)
│   ├── patient_token: "" (auto-filled)
│   └── doctor_token: "" (auto-filled)
│
├── Folder: 🔐 Authentication
│   ├── Register Patient
│   │   ├── Method: POST
│   │   ├── URL: {{base_url}}/api/auth/register
│   │   ├── Body: { email, password, fullName, ... }
│   │   ├── Tests: ✅ Save token to {{token}}
│   │   └── Response: { token, role: "PATIENT" }
│   │
│   ├── Login
│   │   ├── Method: POST
│   │   ├── URL: {{base_url}}/api/auth/login
│   │   ├── Body: { email, password }
│   │   ├── Tests: ✅ Save token, Check role
│   │   └── Response: { token, role, message }
│   │
│   └── Login - Invalid Credentials
│       ├── Method: POST
│       ├── Tests: ✅ Check 401 status
│       └── Response: 401 Unauthorized
│
├── Folder: 🏥 Specialties
│   └── Get All Specialties
│       ├── Method: GET
│       ├── Auth: ❌ No token required
│       ├── Tests: ✅ Check array response
│       └── Response: [ {id, name, description}, ... ]
│
├── Folder: 👨‍⚕️ Doctors
│   ├── Get All Doctors
│   │   ├── Auth: ✅ Bearer {{token}}
│   │   └── Response: [ Doctor objects ]
│   │
│   ├── Search Doctors by Specialty
│   │   ├── URL: /api/doctors/search?specialtyId=1&date=...
│   │   ├── Auth: ✅ Bearer {{token}}
│   │   ├── Tests: ✅ Check availableSlots exist
│   │   └── Response: [ DoctorSearchDTO ]
│   │
│   └── Get Doctor Detail
│       ├── URL: /api/doctors/{id}/detail?startDate&endDate
│       ├── Auth: ✅ Bearer {{token}}
│       └── Response: { doctorId, fullName, timeSlots: [...] }
│
├── Folder: 📅 Availability Blocks
│   ├── Create Availability Block
│   │   ├── Method: POST
│   │   ├── Auth: ✅ Bearer {{doctor_token}} (DOCTOR only)
│   │   ├── Body: { workDate, startTime, endTime }
│   │   ├── Tests: ✅ Check generatedSlots > 0
│   │   └── Response: { id, generatedSlots: 8 }
│   │
│   ├── Get Availability Blocks
│   │   ├── Method: GET
│   │   ├── URL: /api/doctors/{id}/availability
│   │   └── Response: [ AvailabilityBlockDTO ]
│   │
│   ├── Get by Date
│   │   ├── URL: ?date=2025-11-10
│   │   └── Response: Filtered blocks
│   │
│   └── Delete Block
│       ├── Method: DELETE
│       ├── Auth: ✅ DOCTOR only
│       └── Response: 204 No Content
│
├── Folder: 📋 Appointments
│   ├── Create Appointment
│   │   ├── Method: POST
│   │   ├── Auth: ✅ Bearer {{patient_token}} (PATIENT only)
│   │   ├── Body: { patientId, doctorId, timeSlotId, reason }
│   │   ├── Tests: ✅ Status 201, Check appointmentId
│   │   └── Response: { appointmentId, patientName, ... }
│   │
│   └── Invalid TimeSlot Test
│       ├── Body: { timeSlotId: 999999 }
│       ├── Tests: ✅ Check 400 error
│       └── Response: "TimeSlot not available"
│
└── Folder: 🧪 Test - Security
    ├── Search WITHOUT Token
    │   ├── Auth: ❌ No Authorization header
    │   ├── Tests: ✅ Check 403
    │   └── Response: 403 Forbidden
    │
    ├── Create Appointment WITHOUT Token
    │   ├── Tests: ✅ Check 403
    │   └── Response: 403 Forbidden
    │
    └── Patient tries DOCTOR endpoint
        ├── Auth: ✅ Bearer {{patient_token}}
        ├── URL: POST /doctors/*/availability
        ├── Tests: ✅ Check 403
        └── Response: 403 Access Denied
```

---

## 🎯 Test Script Architecture

```javascript
┌─────────────────────────────────────────────────────────────────┐
│                    TEST SCRIPT WORKFLOW                          │
└─────────────────────────────────────────────────────────────────┘

Request Execution
      ↓
┌─────────────────┐
│  Pre-request    │  (Optional: Set dynamic variables)
│  Script         │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Send HTTP      │  → Backend API
│  Request        │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Receive        │  ← Response + Headers
│  Response       │
└────────┬────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────────┐
│  Test Script (Post-response)                                │
│                                                             │
│  // 1. Status Code Assertion                               │
│  pm.test("Status code is 200", function () {              │
│      pm.response.to.have.status(200);                     │
│  });                                                       │
│                                                             │
│  // 2. Response Structure Validation                       │
│  pm.test("Response has token", function () {              │
│      var jsonData = pm.response.json();                   │
│      pm.expect(jsonData.token).to.exist;                  │
│  });                                                       │
│                                                             │
│  // 3. Save to Environment Variables                       │
│  if (pm.response.code === 200) {                          │
│      var jsonData = pm.response.json();                   │
│      pm.environment.set("token", jsonData.token);         │
│  }                                                         │
│                                                             │
│  // 4. Chain to Next Request                              │
│  pm.environment.set("doctor_id", jsonData.doctorId);      │
└─────────────────────────────────────────────────────────────┘
         │
         ▼
┌─────────────────┐
│  Test Results   │  → PASS ✅ or FAIL ❌
└─────────────────┘
```

---

## 📊 HTTP Status Code Reference

```
┌─────────────────────────────────────────────────────────────────┐
│                    STATUS CODES IN THIS API                      │
└─────────────────────────────────────────────────────────────────┘

✅ Success Codes
├── 200 OK
│   └── GET requests (login, search, get data)
│
├── 201 Created
│   └── POST /appointments (appointment created)
│
└── 204 No Content
    └── DELETE /availability/{id} (deleted successfully)

❌ Error Codes
├── 400 Bad Request
│   ├── Invalid request body format
│   ├── Missing required fields
│   ├── TimeSlot not available
│   └── Validation errors
│
├── 401 Unauthorized
│   └── Invalid credentials (wrong password)
│
├── 403 Forbidden
│   ├── No JWT token provided
│   ├── Invalid/expired token
│   ├── Wrong role (PATIENT accessing DOCTOR endpoint)
│   └── Access Denied
│
└── 404 Not Found
    └── Resource not found (doctor, patient, slot)
```

---

## 🔄 Data Flow Visualization

```
┌─────────────────────────────────────────────────────────────────┐
│              TIME SLOT GENERATION & BOOKING FLOW                 │
└─────────────────────────────────────────────────────────────────┘

1. Doctor Creates Availability Block
   ┌────────────────────────────────────┐
   │ POST /doctors/1/availability       │
   │ {                                  │
   │   workDate: "2025-11-10",         │
   │   startTime: "08:00",             │
   │   endTime: "12:00"                │
   │ }                                  │
   └───────────────┬────────────────────┘
                   │
                   ▼
   ┌────────────────────────────────────┐
   │ Backend Auto-generates TimeSlots   │
   │ (30-minute intervals)              │
   └───────────────┬────────────────────┘
                   │
                   ▼
   Database: availability_blocks
   ┌────┬──────────┬────────┬────────┬──────────┐
   │ id │ doctor_id│work_date│start   │end       │
   ├────┼──────────┼────────┼────────┼──────────┤
   │ 1  │ 1        │11-10   │08:00   │12:00     │
   └────┴──────────┴────────┴────────┴──────────┘
   
   Database: time_slots (8 slots created)
   ┌────┬──────────┬─────────────┬────────┬────────┬───────────┐
   │ id │ doctor_id│avail_block  │start   │end     │status     │
   ├────┼──────────┼─────────────┼────────┼────────┼───────────┤
   │101 │ 1        │ 1           │08:00   │08:30   │AVAILABLE  │
   │102 │ 1        │ 1           │08:30   │09:00   │AVAILABLE  │
   │103 │ 1        │ 1           │09:00   │09:30   │AVAILABLE  │
   │104 │ 1        │ 1           │09:30   │10:00   │AVAILABLE  │
   │105 │ 1        │ 1           │10:00   │10:30   │AVAILABLE  │
   │106 │ 1        │ 1           │10:30   │11:00   │AVAILABLE  │
   │107 │ 1        │ 1           │11:00   │11:30   │AVAILABLE  │
   │108 │ 1        │ 1           │11:30   │12:00   │AVAILABLE  │
   └────┴──────────┴─────────────┴────────┴────────┴───────────┘

2. Patient Searches Doctors
   ┌────────────────────────────────────┐
   │ GET /doctors/search?date=11-10     │
   └───────────────┬────────────────────┘
                   │
                   ▼
   Response: Doctors with AVAILABLE slots
   [
     {
       doctorId: 1,
       availableSlots: [
         { slotId: 101, startTime: "08:00", status: "AVAILABLE" },
         { slotId: 102, startTime: "08:30", status: "AVAILABLE" },
         ...
       ]
     }
   ]

3. Patient Books Appointment
   ┌────────────────────────────────────┐
   │ POST /appointments                 │
   │ {                                  │
   │   timeSlotId: 101                 │
   │ }                                  │
   └───────────────┬────────────────────┘
                   │
                   ▼
   Backend: Update TimeSlot status
   ┌────┬──────────┬─────────────┬────────┬────────┬───────────┐
   │101 │ 1        │ 1           │08:00   │08:30   │BOOKED ✅   │
   └────┴──────────┴─────────────┴────────┴────────┴───────────┘
   
   Backend: Create Appointment record
   ┌────┬──────────┬─────────┬──────────┬──────────┐
   │ id │patient_id│doctor_id│timeslot  │status    │
   ├────┼──────────┼─────────┼──────────┼──────────┤
   │ 1  │ 1        │ 1       │ 101      │SCHEDULED │
   └────┴──────────┴─────────┴──────────┴──────────┘

4. Next Search Excludes Booked Slot
   ┌────────────────────────────────────┐
   │ GET /doctors/search?date=11-10     │
   └───────────────┬────────────────────┘
                   │
                   ▼
   Response: Only AVAILABLE slots
   [
     {
       doctorId: 1,
       availableSlots: [
         { slotId: 102, startTime: "08:30", status: "AVAILABLE" },
         { slotId: 103, startTime: "09:00", status: "AVAILABLE" },
         ...
         // Slot 101 NOT included (BOOKED)
       ]
     }
   ]
```

---

## 🎨 Response Format Examples

### **Success Response: Create Appointment**
```json
{
  "appointmentId": 1,
  "patientName": "Nguyen Van A",
  "doctorName": "Dr. John Smith",
  "appointmentTime": "2025-11-10T08:00:00",
  "status": "SCHEDULED",
  "reason": "Routine checkup"
}
```

### **Success Response: Search Doctors**
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
        "startTime": "2025-11-10T08:00:00",
        "endTime": "2025-11-10T08:30:00",
        "status": "AVAILABLE"
      }
    ]
  }
]
```

### **Error Response: 403 Forbidden**
```json
{
  "timestamp": "2025-11-04T10:00:00.000+00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/api/doctors/1/availability"
}
```

### **Error Response: 400 Bad Request**
```json
"TimeSlot with ID 999999 not found or not available"
```

---

## 🔧 Environment Variables in Action

```javascript
// Before Request
URL: {{base_url}}/api/doctors/search
     ↓
Resolved: http://localhost:8000/api/doctors/search

Headers: Authorization: Bearer {{token}}
         ↓
Resolved: Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWI...

// After Response (Test Script)
var jsonData = pm.response.json();
pm.environment.set("token", jsonData.token);
pm.environment.set("doctor_id", jsonData.doctorId);

// Next Request can use
URL: {{base_url}}/api/doctors/{{doctor_id}}/detail
     ↓
Resolved: http://localhost:8000/api/doctors/1/detail
```

---

## 🎯 Summary

This collection provides:
- ✅ **20+ API requests** covering all endpoints
- ✅ **Automatic JWT token management** (no manual copy-paste!)
- ✅ **Built-in test assertions** (verify responses automatically)
- ✅ **Security testing** (403 Forbidden scenarios)
- ✅ **Role-based access control** testing
- ✅ **Complete documentation** in each request
- ✅ **Multiple environments** support
- ✅ **Chained requests** (use data from previous responses)

**Ready to import and test!** 🚀
