package com.assignment.clinic.controller;

import com.assignment.clinic.dto.AvailabilityBlockDTO;
import com.assignment.clinic.dto.AvailabilityBlockRequest;
import com.assignment.clinic.service.AvailabilityBlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/doctors/{doctorId}/availability")
public class AvailabilityBlockController {

    @Autowired
    private AvailabilityBlockService availabilityBlockService;

    /**
     * ✅ Bác sĩ tạo khung giờ làm việc mới - PROTECTED
     * POST /api/doctors/123/availability
     * Body: { "workDate": "2025-11-02", "startTime": "08:00", "endTime": "12:00" }
     */
    @PostMapping
    public ResponseEntity<?> createAvailabilityBlock(
            @PathVariable Long doctorId,
            @RequestBody AvailabilityBlockRequest request) {
        try {
            AvailabilityBlockDTO result = availabilityBlockService.createAvailabilityBlock(doctorId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    /**
     * ✅ Lấy khung giờ làm việc của bác sĩ theo ngày - PROTECTED
     * GET /api/doctors/123/availability?date=2025-11-02
     */
    @GetMapping
    public ResponseEntity<?> getAvailabilityBlocks(
            @PathVariable Long doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        try {
            List<AvailabilityBlockDTO> blocks;
            if (date != null) {
                blocks = availabilityBlockService.getAvailabilityBlocksByDoctorAndDate(doctorId, date);
            } else {
                blocks = availabilityBlockService.getAvailabilityBlocksByDoctor(doctorId);
            }
            return ResponseEntity.ok(blocks);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    /**
     * ✅ Xóa khung giờ làm việc (toàn bộ hoặc một phần) - PROTECTED
     * DELETE /api/doctors/123/availability/456
     * Body (optional): { "startTime": "13:00", "endTime": "15:00" }
     * - Nếu không có body: Xóa toàn bộ block
     * - Nếu có body: Xóa chỉ một phần khung giờ (từ startTime đến endTime)
     */
    @DeleteMapping("/{blockId}")
    public ResponseEntity<?> deleteAvailabilityBlock(
            @PathVariable Long doctorId,
            @PathVariable Long blockId,
            @RequestBody(required = false) AvailabilityBlockRequest request) {
        
        try {
            String result = availabilityBlockService.deleteAvailabilityBlock(blockId, request);
            return ResponseEntity.ok(result);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
