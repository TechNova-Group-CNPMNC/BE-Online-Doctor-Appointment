package com.assignment.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.assignment.clinic.entity.Patient;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // Tìm kiếm Patient bằng user_id (FK)
    Optional<Patient> findByUserId(Long userId);

    // Tìm kiếm Patient bằng số điện thoại
    Optional<Patient> findByPhoneNumber(String phoneNumber);
}
