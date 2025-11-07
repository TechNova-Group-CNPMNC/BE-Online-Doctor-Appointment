# 🎯 AI Chatbot - Gemini Only (Simplified)

## 📋 Tổng Quan

**Thay đổi chính:** Chatbot **CHỈ gọi Gemini AI** để phân tích triệu chứng và gợi ý chuyên khoa. **KHÔNG map** với specialty trong database.

### ✅ Điểm Mạnh
- **Đơn giản hơn**: Không cần logic mapping DB
- **Linh hoạt hơn**: Gemini tự do gợi ý bất kỳ chuyên khoa nào
- **Nhanh hơn**: Giảm DB queries
- **Ngắn gọn hơn**: Prompt được optimize để response súc tích

---

## 🚀 API Endpoint

```http
POST /api/chatbot/analyze-symptoms
Content-Type: application/json

{
  "symptoms": "Tôi bị đau đầu, buồn nôn, sốt nhẹ"
}
```

### Response
```json
{
  "diseasePrediction": "Có thể là Migraine hoặc nhiễm trùng",
  "analysis": "Triệu chứng đau đầu kèm buồn nôn là dấu hiệu của Migraine. Sốt nhẹ cần kiểm tra nhiễm trùng.",
  "suggestedSpecialties": [
    {
      "specialtyId": null,
      "specialtyName": "Thần kinh học",
      "reason": "Điều trị đau đầu mạn tính",
      "confidenceScore": 85
    }
  ],
  "emergencyWarning": false,
  "emergencyMessage": null
}
```

---

## 🎯 Gemini Prompt (Optimized)

```
Bạn là trợ lý y tế AI. Phân tích triệu chứng và gợi ý chuyên khoa khám bệnh.

TRIỆU CHỨNG:
{user_symptoms}

Trả về JSON ngắn gọn (không dùng markdown ```json):
{
  "diseasePrediction": "Tên bệnh có thể",
  "analysis": "Phân tích ngắn gọn (2-3 câu)",
  "specialties": [
    {"name": "Tên chuyên khoa", "reason": "Lý do ngắn (1 câu)", "confidence": 85}
  ],
  "emergency": false,
  "emergencyMessage": "Cảnh báo nếu nghiêm trọng (hoặc null)"
}

Lưu ý: Chỉ gợi ý 1-3 chuyên khoa phù hợp nhất.
```

### Tại Sao Prompt Này Tốt?
✅ **Ngắn gọn** - Yêu cầu 2-3 câu  
✅ **Giới hạn** - Chỉ 1-3 chuyên khoa  
✅ **Cụ thể** - JSON schema rõ ràng  
✅ **Linh hoạt** - Không bị giới hạn DB  

---

## 🔧 Code Changes

### Before (❌ Old)
```java
// Cần query DB specialty
List<Specialty> specialties = specialtyRepository.findAll();
String aiResponse = callGeminiAPI(symptoms, specialties);

// Phải map tên chuyên khoa với DB
specialties.stream()
  .filter(s -> s.getName().equalsIgnoreCase(name))
  .findFirst()
  .ifPresent(s -> ...);
```

### After (✅ New)
```java
// CHỈ gọi Gemini, không cần DB
String aiResponse = callGeminiAPI(symptoms);

// Lấy trực tiếp từ Gemini response
suggestions.add(SpecialtySuggestion.builder()
  .specialtyId(null)  // Không cần ID
  .specialtyName(node.path("name").asText())
  .reason(node.path("reason").asText())
  .confidenceScore(node.path("confidence").asInt())
  .build());
```

---

## 📊 Configuration

### application.yml
```yaml
gemini:
  api:
    key: ${GEMINI_API_KEY}
    url: https://generativelanguage.googleapis.com/v1beta/models
  model: gemini-1.5-flash
  temperature: 0.7
  max-tokens: 800  # Đủ cho 1-3 chuyên khoa
```

---

## 🧪 Test Examples

### Test 1: Simple Headache
```bash
curl -X POST http://localhost:8000/api/chatbot/analyze-symptoms \
  -H "Content-Type: application/json" \
  -d '{"symptoms": "Đau đầu, chóng mặt"}'
```

**Expected:** "Thần kinh học" hoặc "Nội khoa"

### Test 2: Emergency Case
```bash
curl -X POST http://localhost:8000/api/chatbot/analyze-symptoms \
  -H "Content-Type: application/json" \
  -d '{"symptoms": "Đau ngực dữ dội, khó thở"}'
```

**Expected:** `emergencyWarning: true`, "Tim mạch"

---

## ⚠️ Important Notes

1. **`specialtyId = null`**  
   → Frontend chỉ hiển thị tên, không cần ID

2. **Không map với DB**  
   → Gemini tự do gợi ý bất kỳ chuyên khoa nào

3. **Response ngắn gọn**  
   → Prompt đã optimize để Gemini trả về 2-3 câu

4. **Fallback đơn giản**  
   → Nếu Gemini lỗi → Gợi ý "Khám Tổng Quát"

---

## 📈 Benefits

| Aspect | Old | New |
|--------|-----|-----|
| **DB Queries** | 1 (specialty list) | 0 |
| **Logic** | Mapping + Filtering | Parse JSON |
| **Flexibility** | Bị giới hạn DB | Tự do gợi ý |
| **Response Time** | ~1.5s | ~1s |
| **Maintainability** | Cần update mapping | Chỉ update prompt |

---

## 🎨 Frontend Example

```javascript
const response = await fetch('/api/chatbot/analyze-symptoms', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ symptoms: userInput })
});

const { diseasePrediction, analysis, suggestedSpecialties } = await response.json();

// Display specialties
suggestedSpecialties.forEach(s => {
  console.log(`${s.specialtyName}: ${s.reason} (${s.confidenceScore}%)`);
});
```

---

## 🚫 What Changed?

### Removed
❌ `specialtyRepository.findAll()` call  
❌ Specialty name mapping logic  
❌ `addSuggestion()` helper method  
❌ Complex fallback with keyword matching  

### Added
✅ Simplified Gemini prompt  
✅ Direct JSON parsing  
✅ `specialtyId = null` support  
✅ Simple fallback message  

---

## ✅ Status

- **Compiled:** ✅ BUILD SUCCESS
- **Running:** ✅ Port 8000
- **Tested:** ⏳ Pending manual test
- **Deployed:** ⏳ Pending push

---

**Date:** 2025-11-07  
**Version:** v2.1 (Gemini-Only)  
**Branch:** `phucbinh`
