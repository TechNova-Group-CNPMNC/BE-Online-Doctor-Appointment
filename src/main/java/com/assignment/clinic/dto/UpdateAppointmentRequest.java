package com.assignment.clinic.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAppointmentRequest {
    private String symptoms;          // Có thể update bất kỳ lúc nào
    private String suspectedDisease;  // Có thể update bất kỳ lúc nào
    private Long newTimeSlotId;       // Chỉ update được trước 48h, tối đa 2 lần
}
