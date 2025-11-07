package com.assignment.clinic.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotRequest {
    
    /**
     * Mô tả triệu chứng/tình trạng bệnh (REQUIRED)
     * Ví dụ: "Tôi bị đau đầu dữ dội, chóng mặt và buồn nôn từ 2 ngày nay"
     */
    @NotBlank(message = "Vui lòng mô tả triệu chứng của bạn")
    private String symptoms;
}
