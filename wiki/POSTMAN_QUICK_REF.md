# 🚀 Postman Quick Reference Card

## ⚡ 30-Second Setup

```bash
1. Import → Online-Doctor-Appointment-API.postman_collection.json
2. Run "Register Patient" → Token auto-saved ✅
3. Test other endpoints → All use {{token}} automatically
```

---

## 📌 Essential Endpoints

| Endpoint | Method | Auth | Role | Description |
|----------|--------|------|------|-------------|
| `/api/auth/register` | POST | ❌ | - | Register patient |
| `/api/auth/login` | POST | ❌ | - | Login → Get token |
| `/api/specialties` | GET | ❌ | - | Get specialties |
| `/api/doctors/search` | GET | ✅ | Any | Search doctors |
| `/api/doctors/{id}/detail` | GET | ✅ | Any | Doctor details |
| `/api/doctors/{id}/availability` | POST | ✅ | DOCTOR | Create schedule |
| `/api/appointments` | POST | ✅ | PATIENT | Book appointment |

---

## 🔑 Variables

```javascript
{{base_url}}       // http://localhost:8000
{{token}}          // Auto-saved from login
{{patient_token}}  // Auto-saved (PATIENT role)
{{doctor_token}}   // Auto-saved (DOCTOR role)
```

---

## 📝 Sample Bodies

### Register
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

### Create Availability (Doctor)
```json
{
  "workDate": "2025-11-10",
  "startTime": "08:00",
  "endTime": "12:00"
}
```

### Create Appointment (Patient)
```json
{
  "patientId": 1,
  "doctorId": 1,
  "timeSlotId": 101,
  "reason": "Checkup"
}
```

---

## 🧪 Test Workflow

```
1. Register → Token saved
   ↓
2. Get Specialties → Select specialty ID
   ↓
3. Search Doctors → Get available slots
   ↓
4. Create Appointment → Booking confirmed
```

---

## ⚠️ Common Errors

| Code | Cause | Fix |
|------|-------|-----|
| 403 | No token | Run Login first |
| 401 | Wrong password | Check credentials |
| 400 | Bad request | Check JSON format |
| Connection refused | Server not running | `./mvnw spring-boot:run` |

---

## ✅ Quick Checklist

- [ ] Import collection
- [ ] Start server (`./mvnw spring-boot:run`)
- [ ] Run "Register Patient"
- [ ] Check token saved (Console → Variables)
- [ ] Test other endpoints
- [ ] View Test Results (all green ✅)

---

## 🎯 One-Liner

**Import → Register → Test → Done!** 🚀

---

## 📚 Full Docs

- `POSTMAN_GUIDE.md` → Complete guide
- `API_TESTING_README.md` → Overview
- `POSTMAN_VISUALIZATION.md` → Diagrams
- `JWT_AUTH_TESTS.md` → Security tests
