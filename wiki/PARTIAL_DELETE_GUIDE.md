# 🗑️ Hướng Dẫn Xóa Khung Giờ Làm Việc (Partial Delete)

## 📋 Tổng Quan

API DELETE Availability Block hỗ trợ **xóa toàn bộ** hoặc **xóa một phần** khung giờ làm việc của bác sĩ.

**Endpoint:** `DELETE /api/doctors/{doctorId}/availability/{blockId}`

---

## 🎯 Use Cases

### 1️⃣ Use Case 1: Xóa Toàn Bộ Khung Giờ

**Tình huống:** Bác sĩ hủy toàn bộ lịch làm việc trong ngày

**Request:**
```http
DELETE /api/doctors/1/availability/10
Authorization: Bearer {doctor_token}
# Không có body
```

**Điều kiện:**
- ✅ Tất cả time slots phải có status = AVAILABLE
- ❌ Nếu có bất kỳ slot nào đã BOOKED → Error

**Response thành công:**
```json
"Availability block deleted completely."
```

**Response lỗi:**
```json
"Cannot delete availability block. 3 time slot(s) already booked."
```

---

### 2️⃣ Use Case 2: Xóa Phần Cuối (Last Part)

**Tình huống:** Bác sĩ làm việc 09:00-15:00, nhưng bận từ 13:00-15:00

**Request:**
```http
DELETE /api/doctors/1/availability/10
Authorization: Bearer {doctor_token}
Content-Type: application/json

{
  "startTime": "13:00",
  "endTime": "15:00"
}
```

**Kết quả:**
- Block gốc: **09:00 - 15:00**
- Xóa: **13:00 - 15:00**
- Block còn lại: **09:00 - 13:00** ✅

**Response:**
```json
"Deleted last part (13:00 - 15:00). Block updated to 09:00 - 13:00"
```

**Time slots sau khi xóa:**
```
✅ 09:00 - 09:30 (AVAILABLE)
✅ 09:30 - 10:00 (AVAILABLE)
✅ 10:00 - 10:30 (AVAILABLE)
...
✅ 12:30 - 13:00 (AVAILABLE)
❌ 13:00 - 13:30 (DELETED)
❌ 13:30 - 14:00 (DELETED)
❌ 14:00 - 14:30 (DELETED)
❌ 14:30 - 15:00 (DELETED)
```

---

### 3️⃣ Use Case 3: Xóa Phần Đầu (First Part)

**Tình huống:** Bác sĩ làm việc 09:00-15:00, nhưng bận từ 09:00-12:00

**Request:**
```http
DELETE /api/doctors/1/availability/10
Authorization: Bearer {doctor_token}
Content-Type: application/json

{
  "startTime": "09:00",
  "endTime": "12:00"
}
```

**Kết quả:**
- Block gốc: **09:00 - 15:00**
- Xóa: **09:00 - 12:00**
- Block còn lại: **12:00 - 15:00** ✅

**Response:**
```json
"Deleted first part (09:00 - 12:00). Block updated to 12:00 - 15:00"
```

---

### 4️⃣ Use Case 4: Xóa Phần Giữa (Middle Part) ⭐

**Tình huống:** Bác sĩ làm việc 09:00-15:00, nhưng bận từ 11:00-13:00

**Request:**
```http
DELETE /api/doctors/1/availability/10
Authorization: Bearer {doctor_token}
Content-Type: application/json

{
  "startTime": "11:00",
  "endTime": "13:00"
}
```

**Kết quả:**
- Block gốc: **09:00 - 15:00** (XÓA)
- Tạo Block 1: **09:00 - 11:00** ✅
- Tạo Block 2: **13:00 - 15:00** ✅

**Response:**
```json
"Deleted middle part (11:00 - 13:00). Created 2 new blocks: 09:00-11:00 and 13:00-15:00"
```

**Database sau khi xóa:**

**Trước khi xóa:**
| ID | Doctor ID | Work Date | Start Time | End Time |
|----|-----------|-----------|------------|----------|
| 10 | 1         | 2025-11-10 | 09:00     | 15:00    |

**Sau khi xóa:**
| ID | Doctor ID | Work Date | Start Time | End Time |
|----|-----------|-----------|------------|----------|
| 11 | 1         | 2025-11-10 | 09:00     | 11:00    |
| 12 | 1         | 2025-11-10 | 13:00     | 15:00    |

---

## ⚠️ Validation Rules

### 1. Time Range Validation
```
startTime và endTime phải nằm trong block gốc
```

**Ví dụ:**
- Block gốc: 09:00 - 15:00
- ✅ Valid: 10:00 - 12:00
- ✅ Valid: 09:00 - 11:00
- ✅ Valid: 13:00 - 15:00
- ❌ Invalid: 08:00 - 10:00 (08:00 < 09:00)
- ❌ Invalid: 14:00 - 16:00 (16:00 > 15:00)

**Error:**
```json
"Delete time range must be within block time range (09:00 - 15:00)"
```

### 2. Start < End Validation
```
startTime phải nhỏ hơn endTime
```

**Ví dụ:**
- ❌ Invalid: { "startTime": "13:00", "endTime": "11:00" }

**Error:**
```json
"Start time must be before end time"
```

### 3. Booked Slots Validation
```
Không được xóa time slots đã BOOKED
```

**Ví dụ:**
- Block: 09:00 - 15:00
- Slot 13:00 - 13:30: **BOOKED** ❌
- Xóa 13:00 - 15:00: **ERROR**

**Error:**
```json
"Cannot delete time slots. 1 slot(s) in this range already booked."
```

---

## 🧪 Testing Scenarios

### Scenario 1: Doctor cancels afternoon shift
```bash
# Setup: Create block 09:00-17:00
POST /api/doctors/1/availability
{
  "workDate": "2025-11-10",
  "startTime": "09:00",
  "endTime": "17:00"
}

# Action: Delete afternoon (13:00-17:00)
DELETE /api/doctors/1/availability/{blockId}
{
  "startTime": "13:00",
  "endTime": "17:00"
}

# Expected: Block now 09:00-13:00
GET /api/doctors/1/availability
# Response: [{ "startTime": "09:00", "endTime": "13:00" }]
```

### Scenario 2: Doctor has lunch break
```bash
# Setup: Create block 09:00-15:00
POST /api/doctors/1/availability
{
  "workDate": "2025-11-10",
  "startTime": "09:00",
  "endTime": "15:00"
}

# Action: Delete lunch break (12:00-13:00)
DELETE /api/doctors/1/availability/{blockId}
{
  "startTime": "12:00",
  "endTime": "13:00"
}

# Expected: 2 blocks created
GET /api/doctors/1/availability
# Response: 
# [
#   { "startTime": "09:00", "endTime": "12:00" },
#   { "startTime": "13:00", "endTime": "15:00" }
# ]
```

### Scenario 3: Cannot delete booked slots
```bash
# Setup: Create block + patient books 13:30 slot
POST /api/doctors/1/availability
POST /api/appointments { "timeSlotId": 123 } # Slot 13:30-14:00

# Action: Try to delete 13:00-15:00
DELETE /api/doctors/1/availability/{blockId}
{
  "startTime": "13:00",
  "endTime": "15:00"
}

# Expected: Error 400
# Response: "Cannot delete time slots. 1 slot(s) in this range already booked."
```

---

## 📊 Flow Diagram

```
┌─────────────────────────────────────────────────────────────┐
│          DELETE /api/doctors/1/availability/10              │
│         { "startTime": "13:00", "endTime": "15:00" }        │
└─────────────────────────────────────────────────────────────┘
                            ↓
        ┌───────────────────────────────────────┐
        │   1. Validate block exists            │
        │   2. Validate time range              │
        │   3. Check for booked slots           │
        └───────────────────────────────────────┘
                            ↓
        ┌───────────────────────────────────────┐
        │   Determine delete type:              │
        │   - Full delete (no body)?            │
        │   - Delete first part?                │
        │   - Delete last part?                 │
        │   - Delete middle part?               │
        └───────────────────────────────────────┘
                            ↓
    ┌───────────┬───────────┴───────────┬───────────┐
    ↓           ↓                       ↓           ↓
┌─────────┐ ┌────────┐            ┌─────────┐ ┌─────────┐
│  Full   │ │ First  │            │  Last   │ │ Middle  │
│ Delete  │ │ Part   │            │  Part   │ │  Part   │
└─────────┘ └────────┘            └─────────┘ └─────────┘
    ↓           ↓                       ↓           ↓
Delete      Update                  Update      Delete old
block       startTime               endTime     Create 2
+ slots     Keep block              Keep block  new blocks
```

---

## 💡 Best Practices

### ✅ DO:
- Kiểm tra slots đã booked trước khi xóa
- Validate time range nằm trong block gốc
- Use case "xóa phần giữa" cho lunch break
- Test với Postman trước khi integrate frontend

### ❌ DON'T:
- Không xóa block có slots đã BOOKED
- Không xóa với time range ngoài block gốc
- Không xóa với startTime >= endTime

---

## 🔗 Related APIs

- `POST /api/doctors/{id}/availability` - Tạo khung giờ mới
- `GET /api/doctors/{id}/availability` - Xem danh sách blocks
- `GET /api/doctors/{id}/availability?date=2025-11-10` - Lọc theo ngày

---

**Last Updated:** November 4, 2025  
**Version:** 1.0  
**Author:** TechNova Group
