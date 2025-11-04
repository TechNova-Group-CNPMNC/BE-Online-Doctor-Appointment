package com.assignment.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.assignment.clinic.entity.TimeSlot;
import com.assignment.clinic.entity.TimeSlot.Status;
import com.assignment.clinic.entity.AvailabilityBlock;
import java.time.LocalDate;
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
    
    // Tìm time slots của bác sĩ theo AvailabilityBlock ID
    List<TimeSlot> findByAvailabilityBlockId(Long availabilityBlockId);
    
    // Tìm time slots theo AvailabilityBlock
    List<TimeSlot> findByAvailabilityBlock(AvailabilityBlock availabilityBlock);
    
    // Tìm time slots theo AvailabilityBlock và Status
    List<TimeSlot> findByAvailabilityBlockAndStatus(AvailabilityBlock availabilityBlock, Status status);
    
    // Tìm time slots của bác sĩ theo ngày (từ startTime)
    @Query("SELECT ts FROM TimeSlot ts WHERE ts.doctor.id = :doctorId " +
           "AND CAST(ts.startTime AS LocalDate) = :date " +
           "ORDER BY ts.startTime")
    List<TimeSlot> findByDoctorIdAndDate(@Param("doctorId") Long doctorId, 
                                         @Param("date") LocalDate date);
    
    // Tìm time slots của bác sĩ trong khoảng ngày
    @Query("SELECT ts FROM TimeSlot ts WHERE ts.doctor.id = :doctorId " +
           "AND CAST(ts.startTime AS LocalDate) BETWEEN :startDate AND :endDate " +
           "ORDER BY ts.startTime")
    List<TimeSlot> findByDoctorIdAndDateRange(@Param("doctorId") Long doctorId,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);
}
