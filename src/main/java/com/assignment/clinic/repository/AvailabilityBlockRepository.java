package com.assignment.clinic.repository;

import com.assignment.clinic.entity.AvailabilityBlock;
import com.assignment.clinic.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AvailabilityBlockRepository extends JpaRepository<AvailabilityBlock, Long> {
    
    List<AvailabilityBlock> findByDoctorAndWorkDate(Doctor doctor, LocalDate workDate);
    
    List<AvailabilityBlock> findByDoctorIdAndWorkDate(Long doctorId, LocalDate workDate);
    
    List<AvailabilityBlock> findByDoctorId(Long doctorId);
    
    // Tìm các ngày có lịch của bác sĩ trong khoảng thời gian
    List<AvailabilityBlock> findByDoctorIdAndWorkDateBetween(Long doctorId, LocalDate startDate, LocalDate endDate);
}
