# 🎯 AI Chatbot Integration Summary

## ✅ Đã Hoàn Thành

### 1️⃣ Backend Code
✅ **DTOs (Data Transfer Objects)**
- `ChatbotRequest.java` - Nhận triệu chứng từ user
- `ChatbotResponse.java` - Trả kết quả phân tích AI

✅ **Service Layer**
- `AIChatbotService.java` - Tích hợp Gemini API
- Fallback rule-based matching khi AI không khả dụng
- Parse AI response và mapping với database

✅ **Controller**
- `ChatbotController.java` - REST endpoint
- `POST /api/chatbot/suggest-specialty` - API chính
- `GET /api/chatbot/health` - Health check

✅ **Configuration**
- `application.yml` - Gemini API config
- `WebConfig.java` - RestTemplate bean
- `.env.example` - Template for API key

---

## 📁 Files Changed/Created

### Created Files (6 files):
1. `src/main/java/com/assignment/clinic/dto/ChatbotRequest.java`
2. `src/main/java/com/assignment/clinic/dto/ChatbotResponse.java`
3. `src/main/java/com/assignment/clinic/service/AIChatbotService.java`
4. `src/main/java/com/assignment/clinic/controller/ChatbotController.java`
5. `wiki/GEMINI_AI_SETUP_GUIDE.md` - Hướng dẫn chi tiết
6. `wiki/CHATBOT_QUICK_START.md` - Quick reference
7. `wiki/chatbot-test.http` - Test requests

### Modified Files (4 files):
1. `src/main/resources/application.yml` - Added Gemini config
2. `src/main/java/com/assignment/clinic/config/WebConfig.java` - Added RestTemplate
3. `.env.example` - Added Gemini API key template
4. `wiki/DOCUMENTATION_INDEX.md` - Added chatbot docs section

---

## 🔧 Technical Architecture

### Request Flow:
```
Patient
  ↓ POST /api/chatbot/suggest-specialty
ChatbotController
  ↓ Validate request
AIChatbotService
  ↓ 1. Fetch specialties from DB
  ↓ 2. Build AI prompt
  ↓ 3. Call Gemini API
  ↓ 4. Parse JSON response
  ↓ 5. Extract specialty IDs
  ↓ 6. Find matching doctors
  ↓ 7. Check availability
  ↓ 8. Build response
  ↓
ChatbotResponse
  ↓ JSON response
Patient
```

### Fallback Mechanism:
```
Try: Gemini AI API
  ↓ Success → Return AI analysis
  ↓ Failed/Timeout → Fallback
Rule-Based Matching
  ↓ Keyword detection (đau đầu → Neurology)
  ↓ Return basic suggestions
```

---

## 🚀 How to Use

### Step 1: Get Gemini API Key (FREE)
```
https://makersuite.google.com/app/apikey
```

### Step 2: Add to `.env`
```bash
GEMINI_API_KEY=AIzaSyXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
```

### Step 3: Start Application
```bash
./mvnw spring-boot:run
```

### Step 4: Test API
```bash
POST http://localhost:8000/api/chatbot/suggest-specialty
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>

{
  "symptoms": "Đau đầu, chóng mặt, buồn nôn",
  "age": "35",
  "gender": "MALE"
}
```

---

## 📊 API Specification

### Endpoint: `POST /api/chatbot/suggest-specialty`

**Authentication:** Required (JWT Bearer token)

**Request Body:**
```typescript
{
  symptoms: string,           // Required: Triệu chứng bệnh nhân
  age?: string,              // Optional: Tuổi
  gender?: "MALE" | "FEMALE", // Optional: Giới tính
  medicalHistory?: string    // Optional: Tiền sử bệnh
}
```

**Response:**
```typescript
{
  analysis: string,          // Phân tích của AI
  suggestedSpecialties: [    // Chuyên khoa gợi ý
    {
      specialtyId: number,
      specialtyName: string,
      reason: string,
      confidenceScore: number  // 0-100
    }
  ],
  suggestedDoctors: [        // Bác sĩ đề xuất
    {
      doctorId: number,
      doctorName: string,
      specialty: string,
      rating: number,
      hasAvailableSlots: boolean
    }
  ],
  emergencyWarning: boolean,  // Cảnh báo cấp cứu
  emergencyMessage?: string   // Nội dung cảnh báo
}
```

---

## 💡 Key Features

### 1. AI-Powered Analysis
- Sử dụng Google Gemini 1.5 Flash
- Phân tích triệu chứng bằng tiếng Việt
- Gợi ý chuyên khoa dựa trên context

### 2. Smart Specialty Matching
- Validate specialty có trong database
- Confidence score cho mỗi gợi ý
- Support multiple specialties

### 3. Doctor Recommendations
- Tự động tìm bác sĩ phù hợp
- Sắp xếp theo rating
- Check availability trong 7 ngày tới

### 4. Emergency Detection
- Phát hiện triệu chứng nguy hiểm
- Cảnh báo cần cấp cứu ngay
- Keywords: đau tim, khó thở, chảy máu...

### 5. Fallback System
- Rule-based matching khi AI fail
- Keyword detection cho từng specialty
- Đảm bảo luôn có response

---

## 🔒 Security

### Current:
- ✅ JWT authentication required
- ✅ API key stored in environment variables
- ✅ No API key in source code

### Recommended Improvements:
- [ ] Add rate limiting (prevent API abuse)
- [ ] Restrict to PATIENT role only
- [ ] Log AI requests for monitoring
- [ ] Add CAPTCHA for public endpoints

---

## 💰 Cost & Limits

### Free Tier (Current):
- **Quota:** 60 requests/minute
- **Daily:** 1,500 requests/day
- **Cost:** FREE
- **Model:** gemini-1.5-flash

### Paid Tier (If needed):
- **Quota:** 1,000 requests/minute
- **Cost:** $0.00025/request (~5k VNĐ/1000 req)
- **Model:** gemini-1.5-flash or gemini-pro

---

## 🧪 Testing

### Test Files:
1. `wiki/chatbot-test.http` - 8 test cases
   - Đau đầu (Neurology)
   - Đau bụng (Gastroenterology)
   - Vấn đề da (Dermatology)
   - Đau ngực (Emergency)
   - Trẻ em
   - Minimal request
   - etc.

### Manual Testing:
```bash
# 1. Health check
curl http://localhost:8000/api/chatbot/health

# 2. Get JWT token
curl -X POST http://localhost:8000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"patient1","password":"password"}'

# 3. Test chatbot
curl -X POST http://localhost:8000/api/chatbot/suggest-specialty \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{"symptoms":"Đau đầu"}'
```

---

## 📚 Documentation

### Quick Start:
- **CHATBOT_QUICK_START.md** - 3-step setup guide

### Complete Guide:
- **GEMINI_AI_SETUP_GUIDE.md** - Full documentation
  - API key setup
  - Configuration
  - Troubleshooting
  - Best practices
  - Cost analysis

### Test Files:
- **chatbot-test.http** - Ready-to-use test requests

---

## 🐛 Troubleshooting

### Common Issues:

1. **"Gemini API key not configured"**
   - Add `GEMINI_API_KEY` to `.env`
   - Restart application

2. **401 Unauthorized**
   - Check API key validity
   - Regenerate at https://makersuite.google.com/app/apikey

3. **429 Too Many Requests**
   - Free tier: 60 req/min limit
   - Wait 1 minute or upgrade to paid

4. **Empty specialty suggestions**
   - Fallback automatically activated
   - Check if specialties exist in database
   - Verify Gemini response format

---

## 🔄 Future Enhancements

### Planned Features:
- [ ] Cache AI responses (reduce API calls)
- [ ] Multi-language support (English, Vietnamese)
- [ ] Conversation history
- [ ] Follow-up questions
- [ ] Integration with appointment booking
- [ ] Analytics dashboard (most common symptoms)
- [ ] Fine-tuned model for medical domain

### Optional Integrations:
- [ ] Twilio SMS notifications
- [ ] Email alerts for emergency cases
- [ ] Webhook for external systems
- [ ] WebSocket for real-time chat

---

## ✅ Checklist for Production

### Before Deploying:
- [ ] Replace `.env.example` API key with real key
- [ ] Add API key to production environment variables
- [ ] Setup rate limiting middleware
- [ ] Add monitoring/logging (Sentry, CloudWatch)
- [ ] Test with various symptom combinations
- [ ] Verify emergency detection accuracy
- [ ] Setup API key rotation strategy
- [ ] Add usage analytics
- [ ] Configure CORS for production domain
- [ ] Load test with expected traffic

---

## 📞 Support

### Documentation:
- Gemini API Docs: https://ai.google.dev/docs
- Get API Key: https://makersuite.google.com/app/apikey
- Pricing: https://ai.google.dev/pricing

### Internal Docs:
- Quick Start: `wiki/CHATBOT_QUICK_START.md`
- Full Guide: `wiki/GEMINI_AI_SETUP_GUIDE.md`
- Test Requests: `wiki/chatbot-test.http`

---

**🎉 Integration Complete!**

All code is ready to use. Just add your Gemini API key and start testing!
