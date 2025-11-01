package com.assignment.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.assignment.clinic.entity.Rating;
import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    // Tìm kiếm rating dựa trên appointment_id (unique)
    Optional<Rating> findByAppointmentId(Long appointmentId);

    // Tìm kiếm tất cả ratings của một Doctor
    List<Rating> findByDoctorIdOrderByCreatedAtDesc(Long doctorId);

    // Tìm kiếm tất cả ratings của một Patient
    List<Rating> findByPatientId(Long patientId);
}