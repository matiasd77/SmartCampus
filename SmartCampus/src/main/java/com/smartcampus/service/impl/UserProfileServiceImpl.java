package com.smartcampus.service.impl;

import com.smartcampus.dto.PasswordChangeRequest;
import com.smartcampus.dto.UserProfileDTO;
import com.smartcampus.dto.UserProfileUpdateRequest;
import com.smartcampus.entity.User;
import com.smartcampus.exception.PasswordChangeException;
import com.smartcampus.exception.ProfileUpdateException;
import com.smartcampus.exception.ResourceNotFoundException;
import com.smartcampus.mapper.UserProfileMapper;
import com.smartcampus.repository.UserRepository;
import com.smartcampus.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserProfileMapper userProfileMapper;

    @Override
    @Transactional(readOnly = true)
    public UserProfileDTO getCurrentUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return userProfileMapper.toDto(user);
    }

    @Override
    public UserProfileDTO updateUserProfile(String email, UserProfileUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        // Check if the new email is already taken by another user
        if (!email.equals(request.getEmail()) && isEmailTaken(request.getEmail(), user.getId())) {
            throw new ProfileUpdateException("Email is already taken by another user");
        }

        // Update user profile
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        // Note: Additional profile fields would be added here if User entity is extended
        // For now, we're updating only the basic fields available in the User entity

        User updatedUser = userRepository.save(user);
        return userProfileMapper.toDto(updatedUser);
    }

    @Override
    public void changePassword(String email, PasswordChangeRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new PasswordChangeException("Current password is incorrect");
        }

        // Verify new password matches confirm password
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new PasswordChangeException("New password and confirm password do not match");
        }

        // Check if new password is different from current password
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new PasswordChangeException("New password must be different from current password");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public void updateLastLogin(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        
        // Note: This would require adding lastLoginAt field to User entity
        // For now, we'll just update the updatedAt timestamp
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email, Long excludeUserId) {
        return userRepository.findByEmailAndIdNot(email, excludeUserId).isEmpty();
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileDTO getUserProfileById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return userProfileMapper.toDto(user);
    }

    private boolean isEmailTaken(String email, Long excludeUserId) {
        return !userRepository.findByEmailAndIdNot(email, excludeUserId).isEmpty();
    }
} 