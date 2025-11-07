// COPY TOÀN BỘ CODE NÀY VÀO: src/main/java/com/assignment/clinic/service/AIChatbotService.java

package com.assignment.clinic.service;

import com.assignment.clinic.dto.*;
import com.assignment.clinic.entity.*;
import com.assignment.clinic.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AIChatbotService {

    private final SpecialtyRepository specialtyRepository;
    private final DoctorRepository doctorRepository;
    private final AvailabilityBlockRepository availabilityBlockRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models}")
    private String geminiApiUrl;

    @Value("${gemini.model:gemini-1.5-flash}")
    private String geminiModel;

    @Value("${gemini.temperature:0.7}")
    private double geminiTemperature;

    @Value("${gemini.max-tokens:800}")
    private int geminiMaxTokens;

    public AIChatbotService(SpecialtyRepository specialtyRepository,
                           DoctorRepository doctorRepository,
                           AvailabilityBlockRepository availabilityBlockRepository) {
        this.specialtyRepository = specialtyRepository;
        this.doctorRepository = doctorRepository;
        this.availabilityBlockRepository = availabilityBlockRepository;
    }

    // METHOD 1: Phân tích triệu chứng
    public ChatbotResponse analyzeSymptoms(ChatbotRequest request) {
        try {
            String aiResponse = callGeminiAPI(request.getSymptoms());
            return parseAIResponse(aiResponse);
        } catch (Exception e) {
            return fallbackAnalysis(request.getSymptoms());
        }
    }

    // METHOD 2: Gợi ý bác sĩ
    public DoctorSuggestionResponse suggestDoctors(DoctorSuggestionRequest request) {
        Specialty specialty = specialtyRepository.findById(request.getSpecialtyId())
            .orElseThrow(() -> new IllegalArgumentException("Specialty not found"));
        
        LocalDate start = request.getAppointmentDate() != null ? request.getAppointmentDate() : LocalDate.now();
        LocalDate end = start.plusDays(request.getSearchDays() != null ? request.getSearchDays() : 7);
        
        List<Doctor> doctors = doctorRepository.findBySpecialties_Id(specialty.getId());
        List<DoctorSuggestionResponse.DoctorWithAvailability> result = doctors.stream()
            .map(d -> buildDoctorWithAvailability(d, start, end))
            .filter(d -> !d.getAvailableSlots().isEmpty())
            .sorted((d1, d2) -> d2.getRating().compareTo(d1.getRating()))
            .collect(Collectors.toList());
        
        return DoctorSuggestionResponse.builder()
            .specialtyId(specialty.getId())
            .specialtyName(specialty.getName())
            .doctors(result)
            .message(result.isEmpty() ? "Không có lịch trống" : "Tìm thấy " + result.size() + " bác sĩ")
            .build();
    }

    // HELPER: Call Gemini API
    private String callGeminiAPI(String symptoms) throws Exception {
        if (geminiApiKey == null || geminiApiKey.isBlank()) {
            throw new IllegalStateException("Gemini API key not configured");
        }

        String prompt = """
            Bạn là trợ lý y tế AI. Phân tích triệu chứng và gợi ý chuyên khoa khám bệnh.
            
            TRIỆU CHỨNG:
            %s
            
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
            """.formatted(symptoms);

        Map<String, Object> body = new HashMap<>();
        body.put("contents", Arrays.asList(Map.of("parts", Arrays.asList(Map.of("text", prompt)))));
        body.put("generationConfig", Map.of(
            "temperature", geminiTemperature, 
            "maxOutputTokens", geminiMaxTokens,
            "topP", 0.95
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        String url = geminiApiUrl + "/" + geminiModel + ":generateContent?key=" + geminiApiKey;
        ResponseEntity<String> res = restTemplate.postForEntity(url, new HttpEntity<>(body, headers), String.class);
        
        JsonNode root = objectMapper.readTree(res.getBody());
        String text = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
        return text.replaceAll("```json", "").replaceAll("```", "").trim();
    }

    // HELPER: Parse AI response
    private ChatbotResponse parseAIResponse(String aiResponse) {
        try {
            JsonNode json = objectMapper.readTree(aiResponse);
            List<ChatbotResponse.SpecialtySuggestion> suggestions = new ArrayList<>();
            
            // Lấy trực tiếp từ Gemini, không cần map với DB
            for (JsonNode node : json.path("specialties")) {
                suggestions.add(ChatbotResponse.SpecialtySuggestion.builder()
                    .specialtyId(null) // Không cần ID vì chỉ hiển thị gợi ý
                    .specialtyName(node.path("name").asText())
                    .reason(node.path("reason").asText())
                    .confidenceScore(node.path("confidence").asInt(70))
                    .build());
            }
            
            boolean emergency = json.path("emergency").asBoolean(false);
            String emergencyMsg = json.path("emergencyMessage").asText(null);
            
            return ChatbotResponse.builder()
                .diseasePrediction(json.path("diseasePrediction").asText("Không xác định"))
                .analysis(json.path("analysis").asText())
                .suggestedSpecialties(suggestions)
                .emergencyWarning(emergency)
                .emergencyMessage(emergency && emergencyMsg != null ? emergencyMsg : (emergency ? "⚠️ Cần cấp cứu! Gọi 115" : null))
                .build();
        } catch (Exception e) {
            throw new RuntimeException("Parse error: " + e.getMessage());
        }
    }

    // HELPER: Fallback (khi Gemini không hoạt động)
    private ChatbotResponse fallbackAnalysis(String symptoms) {
        List<ChatbotResponse.SpecialtySuggestion> suggestions = new ArrayList<>();
        
        // Gợi ý fallback đơn giản
        suggestions.add(ChatbotResponse.SpecialtySuggestion.builder()
            .specialtyId(null)
            .specialtyName("Khoa Khám Bệnh Tổng Quát")
            .reason("Vui lòng đến khám để được chẩn đoán chính xác")
            .confidenceScore(60)
            .build());
        
        return ChatbotResponse.builder()
            .diseasePrediction("Chưa thể xác định - AI tạm thời không khả dụng")
            .analysis("Hệ thống AI đang gặp sự cố. Vui lòng đến khám trực tiếp để được bác sĩ chẩn đoán.")
            .suggestedSpecialties(suggestions)
            .emergencyWarning(false)
            .build();
    }

    private DoctorSuggestionResponse.DoctorWithAvailability buildDoctorWithAvailability(
            Doctor doctor, LocalDate start, LocalDate end) {
        List<AvailabilityBlock> blocks = availabilityBlockRepository.findByDoctorIdAndWorkDateBetween(doctor.getId(), start, end);
        
        List<DoctorSuggestionResponse.DoctorWithAvailability.AvailableSlot> slots = blocks.stream()
            .map(b -> {
                // Count available slots from timeSlots list
                long availableCount = b.getTimeSlots().stream()
                    .filter(ts -> ts.getStatus() == TimeSlot.Status.AVAILABLE)
                    .count();
                
                return DoctorSuggestionResponse.DoctorWithAvailability.AvailableSlot.builder()
                    .availabilityBlockId(b.getId())
                    .date(b.getWorkDate())
                    .startTime(b.getStartTime())
                    .endTime(b.getEndTime())
                    .availableSlots((int) availableCount)
                    .build();
            })
            .collect(Collectors.toList());
        
        return DoctorSuggestionResponse.DoctorWithAvailability.builder()
            .doctorId(doctor.getId())
            .doctorName(doctor.getFullName())
            .bio(doctor.getBio())
            .rating(doctor.getAverageRating().doubleValue())
            .availableSlots(slots)
            .build();
    }
}
