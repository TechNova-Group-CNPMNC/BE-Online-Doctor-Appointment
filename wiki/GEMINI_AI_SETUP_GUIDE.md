# 🤖 Hướng Dẫn Tích Hợp Google Gemini AI Chatbot

## 📋 Mục Lục
1. [Tổng Quan](#tổng-quan)
2. [Lấy API Key Miễn Phí](#lấy-api-key-miễn-phí)
3. [Cấu Hình Project](#cấu-hình-project)
4. [Test API](#test-api)
5. [Troubleshooting](#troubleshooting)

---

## 🎯 Tổng Quan

### AI Chatbot giúp gì?
- **Bệnh nhân mô tả triệu chứng** → AI phân tích → **Gợi ý chuyên khoa phù hợp**
- Tự động đề xuất bác sĩ có rating cao + có lịch trống
- Phát hiện triệu chứng nguy hiểm cần cấp cứu

### Tại sao chọn Gemini?
✅ **Hoàn toàn MIỄN PHÍ** (60 requests/phút)  
✅ Hỗ trợ tiếng Việt xuất sắc  
✅ Model mới nhất: `gemini-1.5-flash`  
✅ Không cần credit card  

---

## 🔑 Lấy API Key Miễn Phí

### Bước 1: Truy cập Google AI Studio
```
https://makersuite.google.com/app/apikey
```

### Bước 2: Đăng nhập
- Dùng Google Account của bạn
- Chấp nhận Terms of Service

### Bước 3: Tạo API Key
1. Click nút **"Create API Key"**
2. Chọn project có sẵn HOẶC tạo project mới
3. Copy API key (dạng: `AIzaSyXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX`)

### Bước 4: Lưu API Key an toàn
⚠️ **KHÔNG** commit API key lên GitHub!

---

## ⚙️ Cấu Hình Project

### Bước 1: Cập nhật file `.env`

Tạo/sửa file `.env` trong thư mục root project:

```bash
# Google Gemini AI Configuration
GEMINI_API_KEY=AIzaSyXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
GEMINI_MODEL=gemini-1.5-flash
GEMINI_API_URL=https://generativelanguage.googleapis.com/v1beta/models
GEMINI_MAX_TOKENS=500
GEMINI_TEMPERATURE=0.7
```

**Thay `AIzaSyXXX...`** bằng API key thật của bạn!

### Bước 2: Verify Configuration

File `application.yml` đã được cấu hình sẵn:

```yaml
gemini:
  api:
    key: ${GEMINI_API_KEY:your-gemini-api-key-here}
    model: ${GEMINI_MODEL:gemini-1.5-flash}
    url: ${GEMINI_API_URL:https://generativelanguage.googleapis.com/v1beta/models}
    max-tokens: ${GEMINI_MAX_TOKENS:500}
    temperature: ${GEMINI_TEMPERATURE:0.7}
```

### Bước 3: Start Application

```bash
# Windows PowerShell
./mvnw spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

---

## 🧪 Test API

### Test 1: Health Check

```http
GET http://localhost:8000/api/chatbot/health

Response:
"AI Chatbot service is running"
```

### Test 2: Gợi Ý Chuyên Khoa (Triệu chứng đau đầu)

**Request:**
```http
POST http://localhost:8000/api/chatbot/suggest-specialty
Content-Type: application/json
Authorization: Bearer <your-jwt-token>

{
  "symptoms": "Tôi bị đau đầu dữ dội, chóng mặt và buồn nôn từ 2 ngày nay",
  "age": "35",
  "gender": "MALE",
  "medicalHistory": "Không có tiền sử bệnh"
}
```

**Response:**
```json
{
  "analysis": "Dựa trên các triệu chứng đau đầu dữ dội, chóng mặt và buồn nôn kéo dài 2 ngày, có thể đây là các biểu hiện của migraine hoặc vấn đề thần kinh khác. Triệu chứng này cần được bác sĩ chuyên khoa thần kinh khám và chẩn đoán chính xác.",
  "suggestedSpecialties": [
    {
      "specialtyId": 1,
      "specialtyName": "Neurology",
      "reason": "Triệu chứng đau đầu dữ dội kèm chóng mặt và buồn nôn là các dấu hiệu điển hình của vấn đề thần kinh",
      "confidenceScore": 85
    }
  ],
  "suggestedDoctors": [
    {
      "doctorId": 5,
      "doctorName": "Dr. Nguyễn Văn A",
      "specialty": "Neurology",
      "rating": 4.8,
      "hasAvailableSlots": true
    }
  ],
  "emergencyWarning": false,
  "emergencyMessage": null
}
```

### Test 3: Triệu Chứng Cấp Cứu

**Request:**
```json
{
  "symptoms": "Đau ngực dữ dội lan ra tay trái, khó thở, toát mồ hôi lạnh",
  "age": "55",
  "gender": "MALE"
}
```

**Response:**
```json
{
  "analysis": "Triệu chứng đau ngực dữ dội lan ra tay trái kèm khó thở và toát mồ hôi lạnh là dấu hiệu nguy hiểm của cơn đau tim (nhồi máu cơ tim). ĐÂY LÀ TÌNH TRẠNG CẤP CỨU!",
  "suggestedSpecialties": [
    {
      "specialtyId": 2,
      "specialtyName": "Cardiology",
      "reason": "Triệu chứng nghi ngờ cơn đau tim cấp",
      "confidenceScore": 95
    }
  ],
  "emergencyWarning": true,
  "emergencyMessage": "⚠️ CẢNH BÁO: Triệu chứng có thể nghiêm trọng. Vui lòng đến cơ sở y tế ngay lập tức hoặc gọi cấp cứu 115!"
}
```

### Test 4: Dùng Postman

1. Import collection: `wiki/Online-Doctor-Appointment-API.postman_collection.json`
2. Tạo request mới:
   - Method: `POST`
   - URL: `{{base_url}}/chatbot/suggest-specialty`
   - Headers: `Authorization: Bearer {{token}}`
   - Body (JSON):
     ```json
     {
       "symptoms": "Đau bụng, tiêu chảy, buồn nôn"
     }
     ```

---

## 🛠️ Troubleshooting

### Lỗi 1: "Gemini API key not configured"

**Nguyên nhân:** File `.env` chưa có `GEMINI_API_KEY`

**Giải pháp:**
```bash
# Kiểm tra file .env
cat .env | grep GEMINI_API_KEY

# Thêm API key nếu chưa có
echo "GEMINI_API_KEY=AIzaSyXXXXXXXX" >> .env

# Restart application
./mvnw spring-boot:run
```

### Lỗi 2: 401 Unauthorized khi gọi Gemini API

**Nguyên nhân:** API key không hợp lệ hoặc hết hạn

**Giải pháp:**
1. Kiểm tra API key tại: https://makersuite.google.com/app/apikey
2. Tạo API key mới nếu cần
3. Update lại `.env` file
4. Restart application

### Lỗi 3: 429 Too Many Requests

**Nguyên nhân:** Vượt giới hạn 60 requests/phút (free tier)

**Giải pháp:**
- Đợi 1 phút rồi thử lại
- Implement rate limiting ở frontend
- Upgrade lên paid plan nếu cần (https://ai.google.dev/pricing)

### Lỗi 4: Response không có specialty suggestions

**Nguyên nhân:** 
- Gemini trả về specialty không có trong database
- Response format không đúng

**Giải pháp:**
- Service có **fallback mechanism**: Tự động dùng rule-based matching
- Response vẫn trả về gợi ý dựa trên keywords

### Lỗi 5: JSON Parse Error

**Nguyên nhân:** Gemini đôi khi trả về text có markdown wrapper

**Giải pháp:** Code đã xử lý sẵn:
```java
// Remove markdown code blocks if present
generatedText = generatedText.replaceAll("```json\\s*", "")
                             .replaceAll("```\\s*", "")
                             .trim();
```

---

## 📊 Chi Phí & Giới Hạn

### Free Tier (Gemini 1.5 Flash)
- **Rate limit:** 60 requests/phút
- **Price:** $0 (FREE!)
- **Quota:** 1,500 requests/ngày
- **Thích hợp:** Development, small apps

### Paid Tier (nếu cần scale)
- **Rate limit:** 1,000 requests/phút
- **Price:** $0.00025/request (~5.000 VNĐ/1000 requests)
- **Quota:** Unlimited
- **Docs:** https://ai.google.dev/pricing

---

## 🔒 Security Best Practices

### 1. Bảo vệ API Key
```bash
# ❌ KHÔNG làm thế này
git add .env
git commit -m "Add API key"

# ✅ Đúng cách
echo ".env" >> .gitignore
git add .gitignore
```

### 2. Sử dụng Environment Variables
```yaml
# application.yml
gemini:
  api:
    key: ${GEMINI_API_KEY}  # ✅ Read from .env
    # key: AIzaSyXXX       # ❌ NEVER hardcode!
```

### 3. Restrict API Key (Google Cloud Console)
- Chỉ cho phép API: **Generative Language API**
- Restrict by IP (nếu production)
- Set usage quotas

---

## 🚀 Nâng Cao

### Tùy Chỉnh AI Behavior

**Thay đổi độ sáng tạo (temperature):**
```bash
# .env
GEMINI_TEMPERATURE=0.3  # Conservative (0.0 - 1.0)
GEMINI_TEMPERATURE=0.7  # Balanced (default)
GEMINI_TEMPERATURE=1.0  # Creative
```

**Tăng độ dài response:**
```bash
# .env
GEMINI_MAX_TOKENS=1000  # Longer analysis
```

### Monitoring & Logging

Xem Gemini API calls trong logs:
```bash
# application.yml
logging:
  level:
    com.assignment.clinic.service.AIChatbotService: DEBUG
```

---

## 📚 Tài Liệu Tham Khảo

- **Gemini API Docs:** https://ai.google.dev/docs
- **Get API Key:** https://makersuite.google.com/app/apikey
- **Pricing:** https://ai.google.dev/pricing
- **Rate Limits:** https://ai.google.dev/docs/rate_limits
- **Model Info:** https://ai.google.dev/models/gemini

---

## ✅ Checklist Hoàn Thành

- [ ] Lấy Gemini API key từ Google AI Studio
- [ ] Thêm `GEMINI_API_KEY` vào file `.env`
- [ ] Verify `.env` KHÔNG bị commit lên GitHub
- [ ] Start application thành công
- [ ] Test health check endpoint
- [ ] Test suggest-specialty với triệu chứng đơn giản
- [ ] Test emergency case
- [ ] Kiểm tra AI response có specialty suggestions
- [ ] Verify doctors được gợi ý đúng specialty

---

## 💡 Tips & Tricks

### 1. Test Nhanh Không Cần Token
Tạm thời disable security cho chatbot endpoint (chỉ dùng development):
```java
// SecurityConfig.java
.requestMatchers("/api/chatbot/**").permitAll()
```

### 2. Mock AI Response (Development)
Khi không muốn tốn API quota:
```java
// AIChatbotService.java
if (geminiApiKey.equals("mock")) {
    return fallbackRuleBasedSuggestion(request);
}
```

### 3. Cache AI Responses
Giảm API calls bằng cách cache responses cho cùng triệu chứng:
```java
@Cacheable(value = "symptomAnalysis", key = "#request.symptoms")
public ChatbotResponse analyzeSymptomsAndSuggest(ChatbotRequest request) {
    // ...
}
```

---

**🎉 Chúc bạn tích hợp thành công!**

Nếu gặp vấn đề, hãy kiểm tra logs hoặc mở issue trên GitHub.
