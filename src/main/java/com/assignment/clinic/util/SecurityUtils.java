package com.assignment.clinic.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    /**
     * Lấy userId của user đang đăng nhập từ JWT token
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("User not authenticated");
        }
        
        try {
            // getPrincipal() trả về userId đã set trong JwtAuthenticationFilter
            return Long.parseLong(authentication.getPrincipal().toString());
        } catch (NumberFormatException e) {
            throw new SecurityException("Invalid user ID in token");
        }
    }

    /**
     * Lấy role của user đang đăng nhập
     */
    public static String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("User not authenticated");
        }
        
        return authentication.getAuthorities().stream()
                .findFirst()
                .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                .orElseThrow(() -> new SecurityException("No role found for user"));
    }

    /**
     * Kiểm tra user hiện tại có phải là chủ sở hữu của doctor account không
     */
    public static boolean isOwnerOfDoctor(Long doctorUserId) {
        Long currentUserId = getCurrentUserId();
        return currentUserId.equals(doctorUserId);
    }

    /**
     * Kiểm tra user hiện tại có phải là chủ sở hữu của patient account không
     */
    public static boolean isOwnerOfPatient(Long patientUserId) {
        Long currentUserId = getCurrentUserId();
        return currentUserId.equals(patientUserId);
    }
}
