package com.smartcampus.service;

import com.smartcampus.dto.PasswordChangeRequest;
import com.smartcampus.dto.UserProfileDTO;
import com.smartcampus.dto.UserProfileUpdateRequest;

public interface UserProfileService {
    UserProfileDTO getCurrentUserProfile(String email);
    UserProfileDTO updateUserProfile(String email, UserProfileUpdateRequest request);
    void changePassword(String email, PasswordChangeRequest request);
    void updateLastLogin(String email);
    boolean isEmailAvailable(String email, Long excludeUserId);
    UserProfileDTO getUserProfileById(Long userId);
} 