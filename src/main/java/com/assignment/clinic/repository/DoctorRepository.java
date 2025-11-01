package com.assignment.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.assignment.clinic.entity.Doctor;
import java.util.List;
import java.math.BigDecimal;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // Tìm kiếm tất cả Doctors có rating lớn hơn hoặc bằng một giá trị
    List<Doctor> findByAverageRatingGreaterThanEqual(BigDecimal rating);

    List<Doctor> findByFullNameContainingIgnoreCase(String fullName);

    List<Doctor> findBySpecialties_NameContainingIgnoreCase(String specialtyName);
}
