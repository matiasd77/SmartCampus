package com.smartcampus.service;

import com.smartcampus.dto.ChangePasswordDTO;
import com.smartcampus.dto.RegisterRequest;
import com.smartcampus.dto.UpdateProfileDTO;
import com.smartcampus.dto.UserDTO;
import com.smartcampus.entity.User;

import java.util.List;

public interface UserService {
    User registerUser(RegisterRequest request);
    User findByEmail(String email);
    boolean existsByEmail(String email);
    
    // Profile management methods
    UserDTO getUserByEmail(String email);
    UserDTO getUserById(Long id);
    UserDTO updateUserProfile(Long userId, UpdateProfileDTO updateProfileDTO);
    boolean isEmailAvailable(String email, Long excludeUserId);
    
    // Password management methods
    void changeUserPassword(Long userId, String newPassword);
    boolean validatePassword(String email, String currentPassword);
    
    // Admin methods
    List<UserDTO> getAllUsers();
    
    // Additional utility methods
    UserDTO convertToDTO(User user);
    User convertToEntity(UserDTO userDTO);
} 