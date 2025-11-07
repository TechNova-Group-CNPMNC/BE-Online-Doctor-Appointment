# 🤖 AI Chatbot Quick Start

## 🚀 3 Bước Nhanh

### 1️⃣ Lấy API Key (2 phút)
```
https://makersuite.google.com/app/apikey
→ Create API Key → Copy
```

### 2️⃣ Cấu Hình `.env`
```bash
GEMINI_API_KEY=AIzaSyXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
```

### 3️⃣ Test API
```bash
# Start app
./mvnw spring-boot:run

# Test endpoint
curl -X POST http://localhost:8000/api/chatbot/suggest-specialty \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"symptoms": "Đau đầu, chóng mặt"}'
```

---

## 📖 Chi Tiết

Xem hướng dẫn đầy đủ: [GEMINI_AI_SETUP_GUIDE.md](GEMINI_AI_SETUP_GUIDE.md)

---

## ⚡ API Endpoints

### POST `/api/chatbot/suggest-specialty`
Gợi ý chuyên khoa dựa trên triệu chứng

**Request:**
```json
{
  "symptoms": "Đau đầu, chóng mặt, buồn nôn",
  "age": "35",
  "gender": "MALE",
  "medicalHistory": "Không có"
}
```

**Response:**
```json
{
  "analysis": "Phân tích của AI...",
  "suggestedSpecialties": [
    {
      "specialtyId": 1,
      "specialtyName": "Neurology",
      "reason": "...",
      "confidenceScore": 85
    }
  ],
  "suggestedDoctors": [...],
  "emergencyWarning": false
}
```

### GET `/api/chatbot/health`
Health check

---

## 🆘 Troubleshooting

| Lỗi | Giải pháp |
|-----|-----------|
| "API key not configured" | Thêm `GEMINI_API_KEY` vào `.env` |
| 401 Unauthorized | API key không đúng |
| 429 Too Many Requests | Đợi 1 phút (free tier: 60 req/min) |
| Empty suggestions | Fallback tự động dùng rule-based |

---

## 💰 Chi Phí

- **FREE:** 60 requests/phút, 1,500/ngày
- **Paid:** $0.00025/request (~5k VNĐ/1000 requests)

---

## 🔗 Links

- [Gemini API Docs](https://ai.google.dev/docs)
- [Get API Key](https://makersuite.google.com/app/apikey)
- [Pricing](https://ai.google.dev/pricing)
