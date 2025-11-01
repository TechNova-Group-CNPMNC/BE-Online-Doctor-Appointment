package com.assignment.clinic.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.assignment.clinic.entity.Specialty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecialtyDTO {
    private Long id;
    private String name;

    // Constructor để chuyển đổi từ Entity sang DTO
    public SpecialtyDTO(Specialty specialty) {
        this.id = specialty.getId();
        this.name = specialty.getName();
    }
}