package com.assignment.clinic.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.assignment.clinic.entity.Doctor;
import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDTO {
    private Long id;
    private String fullName;
    private String degree;
    private String bio;
    private BigDecimal averageRating;
    private String email; // Thêm email từ User Entity

    // Bao gồm danh sách Specialty dưới dạng DTO
    private Set<SpecialtyDTO> specialties;

    // Constructor để chuyển đổi từ Entity sang DTO
    public DoctorDTO(Doctor doctor) {
        this.id = doctor.getId();
        this.fullName = doctor.getFullName();
        this.degree = doctor.getDegree();
        this.bio = doctor.getBio();
        this.averageRating = doctor.getAverageRating();

        // Lấy email từ User Entity liên kết
        if (doctor.getUser() != null) {
            this.email = doctor.getUser().getEmail();
        }

        // Chuyển đổi Set<Specialty> sang Set<SpecialtyDTO>
        if (doctor.getSpecialties() != null) {
            this.specialties = doctor.getSpecialties().stream()
                    .map(SpecialtyDTO::new)
                    .collect(Collectors.toSet());
        }
    }
}