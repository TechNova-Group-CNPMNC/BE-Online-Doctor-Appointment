package com.assignment.clinic.service;

import com.assignment.clinic.dto.ChatbotRequest;
import com.assignment.clinic.dto.ChatbotResponse;
import com.assignment.clinic.entity.Doctor;
import com.assignment.clinic.entity.Specialty;
import com.assignment.clinic.repository.DoctorRepository;
import com.assignment.clinic.repository.SpecialtyRepository;
import com.assignment.clinic.repository.TimeSlotRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AIChatbotService {

    private final SpecialtyRepository specialtyRepository;
    private final DoctorRepository doctorRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models}")
    private String geminiApiUrl;

    @Value("${gemini.model:gemini-1.5-flash}")
    private String geminiModel;

    @Value("${gemini.temperature:0.7}")
    private double geminiTemperature;

    @Value("${gemini.max-tokens:500}")
    private int geminiMaxTokens;

    public AIChatbotService(SpecialtyRepository specialtyRepository,
                           DoctorRepository doctorRepository,
                           TimeSlotRepository timeSlotRepository) {
        this.specialtyRepository = specialtyRepository;
        this.doctorRepository = doctorRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public ChatbotResponse analyzeSymptomsAndSuggest(ChatbotRequest request) {
        try {
            // Step 1: Lấy tất cả specialties có trong hệ thống
            List<Specialty> availableSpecialties = specialtyRepository.findAll();
            
            // Step 2: Gọi AI API để phân tích triệu chứng
            String aiAnalysis = callAIAPI(request, availableSpecialties);
            
            // Step 3: Parse AI response và extract specialty suggestions
            List<ChatbotResponse.SpecialtySuggestion> specialtySuggestions = 
                parseSpecialtySuggestions(aiAnalysis, availableSpecialties);
            
            // Step 4: Tìm bác sĩ phù hợp dựa trên specialty suggestions
            List<ChatbotResponse.DoctorSuggestion> doctorSuggestions = 
                findSuggestedDoctors(specialtySuggestions);
            
            // Step 5: Kiểm tra emergency warning
            boolean isEmergency = checkEmergency(aiAnalysis);
            String emergencyMsg = isEmergency ? 
                "⚠️ CẢNH BÁO: Triệu chứng có thể nghiêm trọng. Vui lòng đến cơ sở y tế ngay lập tức hoặc gọi cấp cứu 115!" : 
                null;
            
            return ChatbotResponse.builder()
                .analysis(aiAnalysis)
                .suggestedSpecialties(specialtySuggestions)
                .suggestedDoctors(doctorSuggestions)
                .emergencyWarning(isEmergency)
                .emergencyMessage(emergencyMsg)
                .build();
                
        } catch (Exception e) {
            // Fallback: Nếu AI API fail, dùng rule-based matching
            return fallbackRuleBasedSuggestion(request);
        }
    }

    private String callAIAPI(ChatbotRequest request, List<Specialty> specialties) throws Exception {
        if (geminiApiKey == null || geminiApiKey.isBlank()) {
            throw new IllegalStateException("Gemini API key not configured");
        }

        // Prepare prompt for Gemini
        String specialtiesList = specialties.stream()
            .map(s -> String.format("- %s (ID: %d)", s.getName(), s.getId()))
            .collect(Collectors.joining("\n"));

        String prompt = String.format(
            "Bạn là một trợ lý y tế AI chuyên nghiệp. " +
            "Nhiệm vụ: Phân tích triệu chứng của bệnh nhân và gợi ý chuyên khoa phù hợp.\n\n" +
            "Các chuyên khoa có sẵn:\n%s\n\n" +
            "Triệu chứng của bệnh nhân:\n%s%s%s%s\n\n" +
            "Hãy trả lời CHÍNH XÁC theo format JSON sau (không thêm markdown ```json):\n" +
            "{\n" +
            "  \"analysis\": \"Phân tích chi tiết triệu chứng bằng tiếng Việt\",\n" +
            "  \"specialties\": [\n" +
            "    {\"name\": \"Tên chuyên khoa từ danh sách trên\", \"reason\": \"Lý do gợi ý\", \"confidence\": 85}\n" +
            "  ],\n" +
            "  \"emergency\": false\n" +
            "}\n\n" +
            "Lưu ý:\n" +
            "- Chỉ gợi ý specialties có trong danh sách trên\n" +
            "- Set emergency=true nếu triệu chứng cần cấp cứu ngay\n" +
            "- Độ confidence từ 0-100\n" +
            "- Trả về JSON thuần, không có markdown wrapper",
            specialtiesList,
            request.getSymptoms(),
            request.getAge() != null ? "\nTuổi: " + request.getAge() : "",
            request.getGender() != null ? "\nGiới tính: " + request.getGender() : "",
            request.getMedicalHistory() != null ? "\nTiền sử bệnh: " + request.getMedicalHistory() : ""
        );

        // Build Gemini API request
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", Arrays.asList(
            Map.of("parts", Arrays.asList(
                Map.of("text", prompt)
            ))
        ));
        
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", geminiTemperature);
        generationConfig.put("maxOutputTokens", geminiMaxTokens);
        requestBody.put("generationConfig", generationConfig);

        // Set headers - Gemini uses API key as query parameter
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // Build URL with API key
        String fullUrl = String.format("%s/%s:generateContent?key=%s", 
            geminiApiUrl, geminiModel, geminiApiKey);

        // Call Gemini API
        ResponseEntity<String> response = restTemplate.postForEntity(fullUrl, entity, String.class);
        
        // Parse Gemini response
        JsonNode root = objectMapper.readTree(response.getBody());
        String generatedText = root.path("candidates").get(0)
            .path("content").path("parts").get(0)
            .path("text").asText();
        
        // Remove markdown code blocks if present
        generatedText = generatedText.replaceAll("```json\\s*", "")
                                     .replaceAll("```\\s*", "")
                                     .trim();
        
        return generatedText;
    }

    private List<ChatbotResponse.SpecialtySuggestion> parseSpecialtySuggestions(
            String aiResponse, List<Specialty> availableSpecialties) {
        try {
            // Parse JSON response from AI
            JsonNode jsonResponse = objectMapper.readTree(aiResponse);
            JsonNode specialtiesNode = jsonResponse.path("specialties");
            
            List<ChatbotResponse.SpecialtySuggestion> suggestions = new ArrayList<>();
            
            for (JsonNode node : specialtiesNode) {
                String specialtyName = node.path("name").asText();
                String reason = node.path("reason").asText();
                int confidence = node.path("confidence").asInt(70);
                
                // Tìm specialty ID từ database
                availableSpecialties.stream()
                    .filter(s -> s.getName().equalsIgnoreCase(specialtyName))
                    .findFirst()
                    .ifPresent(specialty -> {
                        suggestions.add(ChatbotResponse.SpecialtySuggestion.builder()
                            .specialtyId(specialty.getId())
                            .specialtyName(specialty.getName())
                            .reason(reason)
                            .confidenceScore(confidence)
                            .build());
                    });
            }
            
            return suggestions;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private List<ChatbotResponse.DoctorSuggestion> findSuggestedDoctors(
            List<ChatbotResponse.SpecialtySuggestion> specialtySuggestions) {
        
        List<ChatbotResponse.DoctorSuggestion> doctorSuggestions = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(7);
        
        for (ChatbotResponse.SpecialtySuggestion specialty : specialtySuggestions) {
            // Tìm top 3 bác sĩ theo rating của mỗi specialty
            List<Doctor> doctors = doctorRepository.findBySpecialties_Id(specialty.getSpecialtyId())
                .stream()
                .sorted((d1, d2) -> d2.getAverageRating().compareTo(d1.getAverageRating()))
                .limit(3)
                .toList();
            
            for (Doctor doctor : doctors) {
                // Kiểm tra có time slot available không
                boolean hasSlots = timeSlotRepository
                    .findByDoctorIdAndDateRange(doctor.getId(), today, endDate)
                    .stream()
                    .anyMatch(slot -> slot.getStatus() == com.assignment.clinic.entity.TimeSlot.Status.AVAILABLE);
                
                doctorSuggestions.add(ChatbotResponse.DoctorSuggestion.builder()
                    .doctorId(doctor.getId())
                    .doctorName(doctor.getFullName())
                    .specialty(specialty.getSpecialtyName())
                    .rating(doctor.getAverageRating().doubleValue())
                    .hasAvailableSlots(hasSlots)
                    .build());
            }
        }
        
        return doctorSuggestions;
    }

    private boolean checkEmergency(String aiResponse) {
        try {
            JsonNode jsonResponse = objectMapper.readTree(aiResponse);
            return jsonResponse.path("emergency").asBoolean(false);
        } catch (Exception e) {
            // Check keywords trong analysis text
            String lowerResponse = aiResponse.toLowerCase();
            return lowerResponse.contains("cấp cứu") || 
                   lowerResponse.contains("nguy hiểm") ||
                   lowerResponse.contains("khẩn cấp") ||
                   lowerResponse.contains("emergency");
        }
    }

    private ChatbotResponse fallbackRuleBasedSuggestion(ChatbotRequest request) {
        String symptoms = request.getSymptoms().toLowerCase();
        List<ChatbotResponse.SpecialtySuggestion> suggestions = new ArrayList<>();
        
        // Simple rule-based matching (khi AI API không available)
        if (symptoms.contains("đau đầu") || symptoms.contains("chóng mặt") || symptoms.contains("migraine")) {
            addSuggestion(suggestions, "Neurology", "Triệu chứng liên quan đến thần kinh", 75);
        }
        
        if (symptoms.contains("tim") || symptoms.contains("đau ngực") || symptoms.contains("huyết áp")) {
            addSuggestion(suggestions, "Cardiology", "Triệu chứng liên quan đến tim mạch", 80);
        }
        
        if (symptoms.contains("da") || symptoms.contains("ngứa") || symptoms.contains("mụn")) {
            addSuggestion(suggestions, "Dermatology", "Triệu chứng về da", 85);
        }
        
        if (symptoms.contains("bụng") || symptoms.contains("tiêu hóa") || symptoms.contains("đau dạ dày")) {
            addSuggestion(suggestions, "Gastroenterology", "Triệu chứng tiêu hóa", 80);
        }
        
        // Default fallback
        if (suggestions.isEmpty()) {
            addSuggestion(suggestions, "General Practice", "Khám tổng quát để chẩn đoán chính xác", 60);
        }
        
        List<ChatbotResponse.DoctorSuggestion> doctors = findSuggestedDoctors(suggestions);
        
        return ChatbotResponse.builder()
            .analysis("Dựa trên triệu chứng bạn mô tả, đây là gợi ý chuyên khoa phù hợp. " +
                     "Lưu ý: Đây chỉ là gợi ý, vui lòng tham khảo ý kiến bác sĩ để chẩn đoán chính xác.")
            .suggestedSpecialties(suggestions)
            .suggestedDoctors(doctors)
            .emergencyWarning(false)
            .build();
    }

    private void addSuggestion(List<ChatbotResponse.SpecialtySuggestion> suggestions, 
                              String specialtyName, String reason, int confidence) {
        specialtyRepository.findAll().stream()
            .filter(s -> s.getName().equalsIgnoreCase(specialtyName))
            .findFirst()
            .ifPresent(specialty -> {
                suggestions.add(ChatbotResponse.SpecialtySuggestion.builder()
                    .specialtyId(specialty.getId())
                    .specialtyName(specialty.getName())
                    .reason(reason)
                    .confidenceScore(confidence)
                    .build());
            });
    }
}
