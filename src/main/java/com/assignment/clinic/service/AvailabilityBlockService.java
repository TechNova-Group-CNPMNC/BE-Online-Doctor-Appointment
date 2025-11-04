package com.assignment.clinic.service;

import com.assignment.clinic.dto.AvailabilityBlockDTO;
import com.assignment.clinic.dto.AvailabilityBlockRequest;
import com.assignment.clinic.entity.AvailabilityBlock;
import com.assignment.clinic.entity.Doctor;
import com.assignment.clinic.entity.TimeSlot;
import com.assignment.clinic.repository.AvailabilityBlockRepository;
import com.assignment.clinic.repository.DoctorRepository;
import com.assignment.clinic.repository.TimeSlotRepository;
import com.assignment.clinic.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AvailabilityBlockService {

    @Autowired
    private AvailabilityBlockRepository availabilityBlockRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    /**
     * ✅ Bác sĩ tạo khung giờ làm việc - WITH AUTHORIZATION CHECK
     * Hệ thống tự động chia thành các slot 30 phút
     */
    @Transactional
    public AvailabilityBlockDTO 
    createAvailabilityBlock(Long doctorId, AvailabilityBlockRequest request) {
        // 🔒 STEP 1: Verify ownership
        Doctor doctor = doctorRepository.findByIdWithUser(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + doctorId));
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!currentUserId.equals(doctor.getUser().getId())) {
            throw new AccessDeniedException("You can only create availability blocks for your own account");
        }

        // STEP 2: Validate business rules
        if (request.getWorkDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot create availability for past dates");
        }

        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        // STEP 3: Tạo availability block
        AvailabilityBlock block = new AvailabilityBlock();
        block.setDoctor(doctor);
        block.setWorkDate(request.getWorkDate());
        block.setStartTime(request.getStartTime());
        block.setEndTime(request.getEndTime());
        
        AvailabilityBlock savedBlock = availabilityBlockRepository.save(block);

        // STEP 4: Tự động tạo các time slots (30 phút mỗi slot)
        generateTimeSlots(savedBlock);

        return convertToDTO(savedBlock);
    }

    /**
     * Tự động chia khung giờ thành các slot 30 phút
     * Ví dụ: 08:00-12:00 -> 08:00-08:30, 08:30-09:00, ..., 11:30-12:00
     */
    private void generateTimeSlots(AvailabilityBlock block) {
        List<TimeSlot> slots = new ArrayList<>();
        
        LocalDateTime currentStart = LocalDateTime.of(block.getWorkDate(), block.getStartTime());
        LocalDateTime blockEnd = LocalDateTime.of(block.getWorkDate(), block.getEndTime());
        
        // Chia thành các slot 30 phút
        while (currentStart.isBefore(blockEnd)) {
            LocalDateTime slotEnd = currentStart.plusMinutes(30);
        
            // Đảm bảo slot cuối không vượt quá blockEnd
            if (slotEnd.isAfter(blockEnd)) {
                break;
            }
            TimeSlot slot = new TimeSlot();
            slot.setAvailabilityBlock(block);
            slot.setDoctor(block.getDoctor());
            slot.setStartTime(currentStart);
            slot.setEndTime(slotEnd);
            slot.setStatus(TimeSlot.Status.AVAILABLE);
            
            slots.add(slot);
            currentStart = slotEnd;
        }
        
        timeSlotRepository.saveAll(slots);
    }

    /**
     * ✅ Lấy tất cả khung giờ làm việc của bác sĩ theo ngày - WITH AUTHORIZATION CHECK
     */
    public List<AvailabilityBlockDTO> getAvailabilityBlocksByDoctorAndDate(Long doctorId, LocalDate date) {
        // 🔒 Verify ownership
        Doctor doctor = doctorRepository.findByIdWithUser(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + doctorId));
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!currentUserId.equals(doctor.getUser().getId())) {
            throw new AccessDeniedException("You can only view your own availability blocks");
        }

        List<AvailabilityBlock> blocks = availabilityBlockRepository.findByDoctorIdAndWorkDate(doctorId, date);
        return blocks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * ✅ Lấy tất cả khung giờ làm việc của bác sĩ - WITH AUTHORIZATION CHECK
     */
    public List<AvailabilityBlockDTO> getAvailabilityBlocksByDoctor(Long doctorId) {
        // 🔒 Verify ownership
        Doctor doctor = doctorRepository.findByIdWithUser(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + doctorId));
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!currentUserId.equals(doctor.getUser().getId())) {
            throw new AccessDeniedException("You can only view your own availability blocks");
        }

        List<AvailabilityBlock> blocks = availabilityBlockRepository.findByDoctorId(doctorId);
        return blocks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * ✅ Xóa khung giờ làm việc (toàn bộ hoặc một phần) - WITH AUTHORIZATION CHECK
     * @param blockId ID của availability block
     * @param request Nếu null: xóa toàn bộ block. Nếu có startTime/endTime: xóa một phần
     * @return Thông báo kết quả
     */
    @Transactional
    public String deleteAvailabilityBlock(Long blockId, AvailabilityBlockRequest request) {
        // 🔒 STEP 1: Verify block ownership
        AvailabilityBlock block = availabilityBlockRepository.findById(blockId)
                .orElseThrow(() -> new IllegalArgumentException("Availability block not found with ID: " + blockId));

        // Verify the logged-in user owns this doctor account
        Doctor doctor = doctorRepository.findByIdWithUser(block.getDoctor().getId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!currentUserId.equals(doctor.getUser().getId())) {
            throw new AccessDeniedException("You can only delete your own availability blocks");
        }
        
        // STEP 2: Trường hợp 1: Xóa toàn bộ block (không có request body)
        if (request == null || (request.getStartTime() == null && request.getEndTime() == null)) {
            // Kiểm tra có slot nào đã BOOKED không
            List<TimeSlot> bookedSlots = timeSlotRepository
                    .findByAvailabilityBlockAndStatus(block, TimeSlot.Status.BOOKED);
            
            if (!bookedSlots.isEmpty()) {
                throw new IllegalStateException("Cannot delete availability block. " + 
                        bookedSlots.size() + " time slot(s) already booked.");
            }
            
            // Xóa tất cả time slots trước (bao gồm cả AVAILABLE slots)
            List<TimeSlot> allSlots = timeSlotRepository.findByAvailabilityBlock(block);
            timeSlotRepository.deleteAll(allSlots);
            
            // Sau đó mới xóa availability block
            availabilityBlockRepository.delete(block);
            return "Availability block deleted completely.";
        }
        
        // Trường hợp 2: Xóa một phần khung giờ
        LocalTime deleteStart = request.getStartTime();
        LocalTime deleteEnd = request.getEndTime();
        
        // Validate: deleteStart và deleteEnd phải nằm trong block
        if (deleteStart.isBefore(block.getStartTime()) || deleteEnd.isAfter(block.getEndTime())) {
            throw new RuntimeException("Delete time range must be within block time range (" + 
                    block.getStartTime() + " - " + block.getEndTime() + ")");
        }
        
        if (!deleteStart.isBefore(deleteEnd)) {
            throw new RuntimeException("Start time must be before end time");
        }
        
        // Lấy tất cả time slots trong khung giờ cần xóa
        LocalDateTime deleteStartDateTime = LocalDateTime.of(block.getWorkDate(), deleteStart);
        LocalDateTime deleteEndDateTime = LocalDateTime.of(block.getWorkDate(), deleteEnd);
        
        List<TimeSlot> slotsToDelete = timeSlotRepository.findByAvailabilityBlock(block).stream()
                .filter(slot -> !slot.getStartTime().isBefore(deleteStartDateTime) && 
                                !slot.getEndTime().isAfter(deleteEndDateTime))
                .collect(Collectors.toList());
        
        // Kiểm tra có slot nào đã BOOKED không
        List<TimeSlot> bookedSlots = slotsToDelete.stream()
                .filter(slot -> slot.getStatus() == TimeSlot.Status.BOOKED)
                .collect(Collectors.toList());
        
        if (!bookedSlots.isEmpty()) {
            throw new RuntimeException("Cannot delete time slots. " + 
                    bookedSlots.size() + " slot(s) in this range already booked.");
        }
        
        // Xóa các time slots trong khung giờ
        timeSlotRepository.deleteAll(slotsToDelete);
        
        // Cập nhật lại availability block
        // TH2.1: Xóa phần đầu (deleteStart == block.startTime)
        if (deleteStart.equals(block.getStartTime()) && deleteEnd.isBefore(block.getEndTime())) {
            block.setStartTime(deleteEnd);
            availabilityBlockRepository.save(block);
            return "Deleted first part (" + deleteStart + " - " + deleteEnd + "). " +
                   "Block updated to " + deleteEnd + " - " + block.getEndTime();
        }
        
        // TH2.2: Xóa phần cuối (deleteEnd == block.endTime)
        if (deleteEnd.equals(block.getEndTime()) && deleteStart.isAfter(block.getStartTime())) {
            block.setEndTime(deleteStart);
            availabilityBlockRepository.save(block);
            return "Deleted last part (" + deleteStart + " - " + deleteEnd + "). " +
                   "Block updated to " + block.getStartTime() + " - " + deleteStart;
        }
        
        // TH2.3: Xóa phần giữa -> Tạo 2 blocks mới
        if (deleteStart.isAfter(block.getStartTime()) && deleteEnd.isBefore(block.getEndTime())) {
            // Block 1: startTime -> deleteStart
            AvailabilityBlock block1 = new AvailabilityBlock();
            block1.setDoctor(block.getDoctor());
            block1.setWorkDate(block.getWorkDate());
            block1.setStartTime(block.getStartTime());
            block1.setEndTime(deleteStart);
            availabilityBlockRepository.save(block1);
            generateTimeSlots(block1);
            
            // Block 2: deleteEnd -> endTime
            AvailabilityBlock block2 = new AvailabilityBlock();
            block2.setDoctor(block.getDoctor());
            block2.setWorkDate(block.getWorkDate());
            block2.setStartTime(deleteEnd);
            block2.setEndTime(block.getEndTime());
            availabilityBlockRepository.save(block2);
            generateTimeSlots(block2);
            
            // Xóa block cũ
            availabilityBlockRepository.delete(block);
            
            return "Deleted middle part (" + deleteStart + " - " + deleteEnd + "). " +
                   "Created 2 new blocks: " + block1.getStartTime() + "-" + block1.getEndTime() + 
                   " and " + block2.getStartTime() + "-" + block2.getEndTime();
        }
        
        return "Time slots deleted successfully.";
    }

    private AvailabilityBlockDTO convertToDTO(AvailabilityBlock block) {
        AvailabilityBlockDTO dto = new AvailabilityBlockDTO();
        dto.setId(block.getId());
        dto.setDoctorId(block.getDoctor().getId());
        dto.setDoctorName(block.getDoctor().getFullName());
        dto.setWorkDate(block.getWorkDate());
        dto.setStartTime(block.getStartTime());
        dto.setEndTime(block.getEndTime());
        return dto;
    }
}
