package com.smartcampus.mapper.impl;

import com.smartcampus.dto.UserProfileDTO;
import com.smartcampus.entity.User;
import com.smartcampus.mapper.UserProfileMapper;
import org.springframework.stereotype.Component;

@Component
public class UserProfileMapperImpl implements UserProfileMapper {

    @Override
    public UserProfileDTO toDto(User user) {
        if (user == null) {
            return null;
        }

        return UserProfileDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .isActive(true) // Assuming all users are active by default
                .build();
    }
} 