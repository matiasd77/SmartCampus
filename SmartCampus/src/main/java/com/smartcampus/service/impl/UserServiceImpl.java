package com.smartcampus.service.impl;

import com.smartcampus.dto.ChangePasswordDTO;
import com.smartcampus.dto.RegisterRequest;
import com.smartcampus.dto.UpdateProfileDTO;
import com.smartcampus.dto.UserDTO;
import com.smartcampus.entity.User;
import com.smartcampus.exception.PasswordChangeException;
import com.smartcampus.exception.ResourceNotFoundException;
import com.smartcampus.repository.UserRepository;
import com.smartcampus.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User with this email already exists");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        return userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        User user = findByEmail(email);
        return convertToDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return convertToDTO(user);
    }

    @Override
    public UserDTO updateUserProfile(Long userId, UpdateProfileDTO updateProfileDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Update basic information
        if (updateProfileDTO.getName() != null) {
            user.setName(updateProfileDTO.getName());
        }
        if (updateProfileDTO.getEmail() != null) {
            user.setEmail(updateProfileDTO.getEmail());
        }

        // Update contact information
        if (updateProfileDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(updateProfileDTO.getPhoneNumber());
        }
        if (updateProfileDTO.getAddress() != null) {
            user.setAddress(updateProfileDTO.getAddress());
        }
        if (updateProfileDTO.getCity() != null) {
            user.setCity(updateProfileDTO.getCity());
        }
        if (updateProfileDTO.getState() != null) {
            user.setState(updateProfileDTO.getState());
        }
        if (updateProfileDTO.getZipCode() != null) {
            user.setZipCode(updateProfileDTO.getZipCode());
        }
        if (updateProfileDTO.getCountry() != null) {
            user.setCountry(updateProfileDTO.getCountry());
        }

        // Update profile information
        if (updateProfileDTO.getBio() != null) {
            user.setBio(updateProfileDTO.getBio());
        }
        if (updateProfileDTO.getProfilePictureUrl() != null) {
            user.setProfilePictureUrl(updateProfileDTO.getProfilePictureUrl());
        }

        // Update academic/professional information
        if (updateProfileDTO.getDepartment() != null) {
            user.setDepartment(updateProfileDTO.getDepartment());
        }
        if (updateProfileDTO.getPosition() != null) {
            user.setPosition(updateProfileDTO.getPosition());
        }
        if (updateProfileDTO.getStudentId() != null) {
            user.setStudentId(updateProfileDTO.getStudentId());
        }
        if (updateProfileDTO.getEmployeeId() != null) {
            user.setEmployeeId(updateProfileDTO.getEmployeeId());
        }
        if (updateProfileDTO.getMajor() != null) {
            user.setMajor(updateProfileDTO.getMajor());
        }
        if (updateProfileDTO.getMinor() != null) {
            user.setMinor(updateProfileDTO.getMinor());
        }
        if (updateProfileDTO.getYear() != null) {
            user.setYear(updateProfileDTO.getYear());
        }
        if (updateProfileDTO.getSemester() != null) {
            user.setSemester(updateProfileDTO.getSemester());
        }
        if (updateProfileDTO.getAcademicYear() != null) {
            user.setAcademicYear(updateProfileDTO.getAcademicYear());
        }
        if (updateProfileDTO.getAdvisor() != null) {
            user.setAdvisor(updateProfileDTO.getAdvisor());
        }
        if (updateProfileDTO.getResearchArea() != null) {
            user.setResearchArea(updateProfileDTO.getResearchArea());
        }
        if (updateProfileDTO.getResearchInterests() != null) {
            user.setResearchInterests(updateProfileDTO.getResearchInterests());
        }

        // Update social media links
        if (updateProfileDTO.getWebsiteUrl() != null) {
            user.setWebsiteUrl(updateProfileDTO.getWebsiteUrl());
        }
        if (updateProfileDTO.getLinkedinUrl() != null) {
            user.setLinkedinUrl(updateProfileDTO.getLinkedinUrl());
        }
        if (updateProfileDTO.getGithubUrl() != null) {
            user.setGithubUrl(updateProfileDTO.getGithubUrl());
        }
        if (updateProfileDTO.getTwitterUrl() != null) {
            user.setTwitterUrl(updateProfileDTO.getTwitterUrl());
        }
        if (updateProfileDTO.getFacebookUrl() != null) {
            user.setFacebookUrl(updateProfileDTO.getFacebookUrl());
        }
        if (updateProfileDTO.getInstagramUrl() != null) {
            user.setInstagramUrl(updateProfileDTO.getInstagramUrl());
        }

        // Update emergency contact information
        if (updateProfileDTO.getEmergencyContactName() != null) {
            user.setEmergencyContactName(updateProfileDTO.getEmergencyContactName());
        }
        if (updateProfileDTO.getEmergencyContactPhone() != null) {
            user.setEmergencyContactPhone(updateProfileDTO.getEmergencyContactPhone());
        }
        if (updateProfileDTO.getEmergencyContactRelationship() != null) {
            user.setEmergencyContactRelationship(updateProfileDTO.getEmergencyContactRelationship());
        }
        if (updateProfileDTO.getEmergencyContactAddress() != null) {
            user.setEmergencyContactAddress(updateProfileDTO.getEmergencyContactAddress());
        }
        if (updateProfileDTO.getEmergencyContactCity() != null) {
            user.setEmergencyContactCity(updateProfileDTO.getEmergencyContactCity());
        }
        if (updateProfileDTO.getEmergencyContactState() != null) {
            user.setEmergencyContactState(updateProfileDTO.getEmergencyContactState());
        }
        if (updateProfileDTO.getEmergencyContactZipCode() != null) {
            user.setEmergencyContactZipCode(updateProfileDTO.getEmergencyContactZipCode());
        }
        if (updateProfileDTO.getEmergencyContactCountry() != null) {
            user.setEmergencyContactCountry(updateProfileDTO.getEmergencyContactCountry());
        }

        // Update additional information
        if (updateProfileDTO.getSkills() != null) {
            user.setSkills(updateProfileDTO.getSkills());
        }
        if (updateProfileDTO.getLanguages() != null) {
            user.setLanguages(updateProfileDTO.getLanguages());
        }
        if (updateProfileDTO.getCertifications() != null) {
            user.setCertifications(updateProfileDTO.getCertifications());
        }
        if (updateProfileDTO.getAwards() != null) {
            user.setAwards(updateProfileDTO.getAwards());
        }
        if (updateProfileDTO.getPublications() != null) {
            user.setPublications(updateProfileDTO.getPublications());
        }
        if (updateProfileDTO.getProjects() != null) {
            user.setProjects(updateProfileDTO.getProjects());
        }
        if (updateProfileDTO.getExperience() != null) {
            user.setExperience(updateProfileDTO.getExperience());
        }
        if (updateProfileDTO.getEducation() != null) {
            user.setEducation(updateProfileDTO.getEducation());
        }
        if (updateProfileDTO.getInterests() != null) {
            user.setInterests(updateProfileDTO.getInterests());
        }
        if (updateProfileDTO.getHobbies() != null) {
            user.setHobbies(updateProfileDTO.getHobbies());
        }
        if (updateProfileDTO.getGoals() != null) {
            user.setGoals(updateProfileDTO.getGoals());
        }
        if (updateProfileDTO.getNotes() != null) {
            user.setNotes(updateProfileDTO.getNotes());
        }

        // Update timestamp
        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email, Long excludeUserId) {
        if (excludeUserId == null) {
            return !userRepository.existsByEmail(email);
        }
        
        return userRepository.findByEmail(email)
                .map(user -> user.getId().equals(excludeUserId))
                .orElse(true);
    }

    @Override
    public void changeUserPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Validate new password
        ChangePasswordDTO tempDTO = ChangePasswordDTO.builder()
                .newPassword(newPassword)
                .build();

        if (!tempDTO.isPasswordStrong()) {
            throw new PasswordChangeException("New password does not meet strength requirements");
        }

        if (tempDTO.containsCommonPatterns()) {
            throw new PasswordChangeException("New password contains common patterns that are not allowed");
        }

        // Encode and set new password
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validatePassword(String email, String currentPassword) {
        User user = findByEmail(email);
        return passwordEncoder.matches(currentPassword, user.getPassword());
    }

    @Override
    public UserDTO convertToDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword()) // Include for internal use
                .role(user.getRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .city(user.getCity())
                .state(user.getState())
                .zipCode(user.getZipCode())
                .country(user.getCountry())
                .bio(user.getBio())
                .profilePictureUrl(user.getProfilePictureUrl())
                .department(user.getDepartment())
                .position(user.getPosition())
                .studentId(user.getStudentId())
                .employeeId(user.getEmployeeId())
                .major(user.getMajor())
                .minor(user.getMinor())
                .year(user.getYear())
                .semester(user.getSemester())
                .academicYear(user.getAcademicYear())
                .advisor(user.getAdvisor())
                .researchArea(user.getResearchArea())
                .researchInterests(user.getResearchInterests())
                .websiteUrl(user.getWebsiteUrl())
                .linkedinUrl(user.getLinkedinUrl())
                .githubUrl(user.getGithubUrl())
                .twitterUrl(user.getTwitterUrl())
                .facebookUrl(user.getFacebookUrl())
                .instagramUrl(user.getInstagramUrl())
                .emergencyContactName(user.getEmergencyContactName())
                .emergencyContactPhone(user.getEmergencyContactPhone())
                .emergencyContactRelationship(user.getEmergencyContactRelationship())
                .emergencyContactAddress(user.getEmergencyContactAddress())
                .emergencyContactCity(user.getEmergencyContactCity())
                .emergencyContactState(user.getEmergencyContactState())
                .emergencyContactZipCode(user.getEmergencyContactZipCode())
                .emergencyContactCountry(user.getEmergencyContactCountry())
                .skills(user.getSkills())
                .languages(user.getLanguages())
                .certifications(user.getCertifications())
                .awards(user.getAwards())
                .publications(user.getPublications())
                .projects(user.getProjects())
                .experience(user.getExperience())
                .education(user.getEducation())
                .interests(user.getInterests())
                .hobbies(user.getHobbies())
                .goals(user.getGoals())
                .notes(user.getNotes())
                .build();
    }

    @Override
    public User convertToEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        return User.builder()
                .id(userDTO.getId())
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .role(userDTO.getRole())
                .isActive(userDTO.getIsActive())
                .createdAt(userDTO.getCreatedAt())
                .updatedAt(userDTO.getUpdatedAt())
                .lastLoginAt(userDTO.getLastLoginAt())
                .phoneNumber(userDTO.getPhoneNumber())
                .address(userDTO.getAddress())
                .city(userDTO.getCity())
                .state(userDTO.getState())
                .zipCode(userDTO.getZipCode())
                .country(userDTO.getCountry())
                .bio(userDTO.getBio())
                .profilePictureUrl(userDTO.getProfilePictureUrl())
                .department(userDTO.getDepartment())
                .position(userDTO.getPosition())
                .studentId(userDTO.getStudentId())
                .employeeId(userDTO.getEmployeeId())
                .major(userDTO.getMajor())
                .minor(userDTO.getMinor())
                .year(userDTO.getYear())
                .semester(userDTO.getSemester())
                .academicYear(userDTO.getAcademicYear())
                .advisor(userDTO.getAdvisor())
                .researchArea(userDTO.getResearchArea())
                .researchInterests(userDTO.getResearchInterests())
                .websiteUrl(userDTO.getWebsiteUrl())
                .linkedinUrl(userDTO.getLinkedinUrl())
                .githubUrl(userDTO.getGithubUrl())
                .twitterUrl(userDTO.getTwitterUrl())
                .facebookUrl(userDTO.getFacebookUrl())
                .instagramUrl(userDTO.getInstagramUrl())
                .emergencyContactName(userDTO.getEmergencyContactName())
                .emergencyContactPhone(userDTO.getEmergencyContactPhone())
                .emergencyContactRelationship(userDTO.getEmergencyContactRelationship())
                .emergencyContactAddress(userDTO.getEmergencyContactAddress())
                .emergencyContactCity(userDTO.getEmergencyContactCity())
                .emergencyContactState(userDTO.getEmergencyContactState())
                .emergencyContactZipCode(userDTO.getEmergencyContactZipCode())
                .emergencyContactCountry(userDTO.getEmergencyContactCountry())
                .skills(userDTO.getSkills())
                .languages(userDTO.getLanguages())
                .certifications(userDTO.getCertifications())
                .awards(userDTO.getAwards())
                .publications(userDTO.getPublications())
                .projects(userDTO.getProjects())
                .experience(userDTO.getExperience())
                .education(userDTO.getEducation())
                .interests(userDTO.getInterests())
                .hobbies(userDTO.getHobbies())
                .goals(userDTO.getGoals())
                .notes(userDTO.getNotes())
                .build();
    }
} 