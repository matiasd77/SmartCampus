package com.smartcampus.service.impl;

import com.smartcampus.config.JwtTokenProvider;
import com.smartcampus.dto.JwtResponse;
import com.smartcampus.dto.LoginRequest;
import com.smartcampus.dto.RegisterRequest;
import com.smartcampus.entity.Role;
import com.smartcampus.entity.User;
import com.smartcampus.repository.UserRepository;
import com.smartcampus.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public JwtResponse login(LoginRequest request) {
        log.info("Starting login process for email: {}", request.getEmail());
        
        try {
            // First, check if user exists in database
            User user = userRepository.findByEmail(request.getEmail())
                    .orElse(null);
            
            if (user == null) {
                log.warn("Login failed - User not found in database for email: {}", request.getEmail());
                throw new RuntimeException("Invalid email or password");
            }
            
            log.info("User found in database: {} (ID: {}) with role: {} and isActive: {}", 
                    user.getName(), user.getId(), user.getRole(), user.getIsActive());
            
            // Check if user is active
            if (user.getIsActive() == null || !user.getIsActive()) {
                log.warn("Login failed - User account is inactive for email: {}", request.getEmail());
                throw new RuntimeException("Account is inactive");
            }
            
            // Attempt authentication
            log.debug("Attempting authentication for email: {}", request.getEmail());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            log.info("Authentication successful for email: {}", request.getEmail());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Generate JWT token
            log.debug("Generating JWT token for email: {}", request.getEmail());
            String jwt = jwtTokenProvider.generateToken(authentication);

            log.info("Login successful for user: {} (ID: {}) with role: {}", 
                    user.getName(), user.getId(), user.getRole());
            
            return JwtResponse.builder()
                    .token(jwt)
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .build();
                    
        } catch (Exception e) {
            log.error("Login failed for email: {} - Error: {}", request.getEmail(), e.getMessage(), e);
            throw e; // Re-throw to be handled by controller
        }
    }

    @Override
    public User register(RegisterRequest request) {
        log.info("Starting registration process for email: {}", request.getEmail());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed - email already exists: {}", request.getEmail());
            throw new RuntimeException("User with this email already exists");
        }

        // Ensure only STUDENT role is allowed for public registration
        Role userRole = request.getRole();
        if (userRole == null) {
            userRole = Role.STUDENT; // Default to STUDENT if no role provided
            log.info("No role specified, defaulting to STUDENT for email: {}", request.getEmail());
        } else if (userRole != Role.STUDENT) {
            log.warn("Registration failed - non-STUDENT role attempted: {} for email: {}", userRole, request.getEmail());
            throw new RuntimeException("Only STUDENT role is allowed for public registration. Professors and Admins must be created by administrators.");
        }

        log.debug("Creating new student user with email: {}", request.getEmail());
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(userRole)
                .build();

        User savedUser = userRepository.save(user);
        log.info("Student registration successful for user: {} (ID: {})", savedUser.getName(), savedUser.getId());
        
        return savedUser;
    }
} 