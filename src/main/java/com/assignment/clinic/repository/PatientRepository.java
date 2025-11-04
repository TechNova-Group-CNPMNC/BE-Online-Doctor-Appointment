package com.assignment.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.assignment.clinic.entity.Patient;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // Tìm kiếm Patient bằng user_id (FK)
    Optional<Patient> findByUserId(Long userId);

    // Tìm kiếm Patient bằng số điện thoại
    Optional<Patient> findByPhoneNumber(String phoneNumber);
    
    /**
     * Tìm patient theo ID và load cả User để verify ownership
     */
    @Query("SELECT p FROM Patient p JOIN FETCH p.user WHERE p.id = :patientId")
    Optional<Patient> findByIdWithUser(@Param("patientId") Long patientId);
}
