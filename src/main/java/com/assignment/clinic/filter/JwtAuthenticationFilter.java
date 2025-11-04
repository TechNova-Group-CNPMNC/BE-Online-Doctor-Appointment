package com.assignment.clinic.filter;

import com.assignment.clinic.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        // Lấy token từ header Authorization
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String userId = null;

        // Kiểm tra header có format: "Bearer <token>"
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                userId = jwtUtil.extractId(token);
            } catch (Exception e) {
                // Token không hợp lệ
                logger.error("Error extracting user ID from token: " + e.getMessage());
            }
        }

        // Nếu có userId và chưa authenticate
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // Validate token
                if (jwtUtil.validateToken(token, Long.parseLong(userId))) {
                    // Lấy role từ token
                    String role = jwtUtil.extractClaim(token, claims -> claims.get("role", String.class));
                    
                    // Tạo authentication object
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                    );
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Set vào SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                logger.error("Error validating token: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
