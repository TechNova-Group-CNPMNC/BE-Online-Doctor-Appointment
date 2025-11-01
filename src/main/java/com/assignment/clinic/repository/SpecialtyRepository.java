package com.assignment.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.assignment.clinic.entity.Specialty;
import java.util.Optional;

@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {

    // Tìm kiếm Specialty bằng tên
    Optional<Specialty> findByName(String name);
}
