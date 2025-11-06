package com.assignment.clinic.controller;

import com.assignment.clinic.dto.ChatbotRequest;
import com.assignment.clinic.dto.ChatbotResponse;
import com.assignment.clinic.service.AIChatbotService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final AIChatbotService chatbotService;

    public ChatbotController(AIChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    /**
     * API: AI Chatbot - Gợi ý chuyên khoa dựa trên triệu chứng
     * POST /api/chatbot/suggest-specialty
     * 
     * Request Body:
     * {
     *   "symptoms": "Tôi bị đau đầu dữ dội, chóng mặt và buồn nôn từ 2 ngày nay",
     *   "age": "35",
     *   "gender": "MALE",
     *   "medicalHistory": "Không có tiền sử bệnh"
     * }
     * 
     * Response:
     * {
     *   "analysis": "Phân tích của AI...",
     *   "suggestedSpecialties": [
     *     {
     *       "specialtyId": 1,
     *       "specialtyName": "Neurology",
     *       "reason": "Triệu chứng đau đầu dữ dội kèm chóng mặt...",
     *       "confidenceScore": 85
     *     }
     *   ],
     *   "suggestedDoctors": [
     *     {
     *       "doctorId": 5,
     *       "doctorName": "Dr. Smith",
     *       "specialty": "Neurology",
     *       "rating": 4.8,
     *       "hasAvailableSlots": true
     *     }
     *   ],
     *   "emergencyWarning": false,
     *   "emergencyMessage": null
     * }
     */
    @PostMapping("/suggest-specialty")
    public ResponseEntity<?> suggestSpecialty(@Valid @RequestBody ChatbotRequest request) {
        try {
            ChatbotResponse response = chatbotService.analyzeSymptomsAndSuggest(request);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            // AI API not configured - return fallback response
            ChatbotResponse fallback = chatbotService.analyzeSymptomsAndSuggest(request);
            return ResponseEntity.ok(fallback);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Failed to analyze symptoms",
                    "message", e.getMessage()
                ));
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("AI Chatbot service is running");
    }
}
