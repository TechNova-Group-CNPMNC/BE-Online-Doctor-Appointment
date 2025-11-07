package com.assignment.clinic.controller;

import com.assignment.clinic.dto.ChatbotRequest;
import com.assignment.clinic.dto.ChatbotResponse;
import com.assignment.clinic.dto.DoctorSuggestionRequest;
import com.assignment.clinic.dto.DoctorSuggestionResponse;
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
     * ENDPOINT 1: Phân tích triệu chứng → Dự đoán bệnh + Gợi ý chuyên khoa
     * POST /api/chatbot/analyze-symptoms
     * 
     * Request:
     * {
     *   "symptoms": "Tôi bị đau đầu dữ dội, chóng mặt và buồn nôn"
     * }
     * 
     * Response:
     * {
     *   "diseasePrediction": "Có thể là Migraine (đau nửa đầu)",
     *   "analysis": "Dựa trên triệu chứng đau đầu dữ dội kèm chóng mặt và buồn nôn...",
     *   "suggestedSpecialties": [
     *     {
     *       "specialtyId": 1,
     *       "specialtyName": "Neurology",
     *       "reason": "Triệu chứng liên quan đến hệ thần kinh",
     *       "confidenceScore": 85
     *     }
     *   ],
     *   "emergencyWarning": false,
     *   "emergencyMessage": null
     * }
     */
    @PostMapping("/analyze-symptoms")
    public ResponseEntity<?> analyzeSymptoms(@Valid @RequestBody ChatbotRequest request) {
        try {
            ChatbotResponse response = chatbotService.analyzeSymptoms(request);
            return ResponseEntity.ok(response);
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
     * ENDPOINT 2: Lấy danh sách bác sĩ theo chuyên khoa + ngày khám
     * POST /api/chatbot/suggest-doctors
     * 
     * Request:
     * {
     *   "specialtyId": 1,
     *   "appointmentDate": "2025-11-15",  // Optional, default = today
     *   "searchDays": 7                    // Optional, default = 7 days
     * }
     * 
     * Response:
     * {
     *   "specialtyId": 1,
     *   "specialtyName": "Neurology",
     *   "message": "Tìm thấy 3 bác sĩ có lịch trống",
     *   "doctors": [
     *     {
     *       "doctorId": 5,
     *       "doctorName": "Dr. Nguyễn Văn A",
     *       "bio": "Chuyên gia thần kinh...",
     *       "rating": 4.8,
     *       "availableSlots": [
     *         {
     *           "availabilityBlockId": 10,
     *           "date": "2025-11-15",
     *           "startTime": "09:00",
     *           "endTime": "12:00",
     *           "availableSlots": 5
     *         }
     *       ]
     *     }
     *   ]
     * }
     */
    @PostMapping("/suggest-doctors")
    public ResponseEntity<?> suggestDoctors(@Valid @RequestBody DoctorSuggestionRequest request) {
        try {
            DoctorSuggestionResponse response = chatbotService.suggestDoctors(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", "Failed to suggest doctors",
                    "message", e.getMessage()
                ));
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "AI Chatbot Service",
            "message", "Service is running normally"
        ));
    }
}
