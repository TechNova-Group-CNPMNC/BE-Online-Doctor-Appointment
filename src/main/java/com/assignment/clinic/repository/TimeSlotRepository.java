package com.assignment.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.assignment.clinic.entity.TimeSlot;
import com.assignment.clinic.entity.TimeSlot.Status;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    // Tìm kiếm các TimeSlot của một Doctor cụ thể
    List<TimeSlot> findByDoctorId(Long doctorId);

    // Tìm kiếm các TimeSlot CÓ SẴN (AVAILABLE) của một Doctor trong một khoảng thời gian
    List<TimeSlot> findByDoctorIdAndStatusAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(
            Long doctorId, Status status, LocalDateTime start, LocalDateTime end);

    // Tìm kiếm các TimeSlot theo trạng thái
    List<TimeSlot> findByStatus(Status status);
}
