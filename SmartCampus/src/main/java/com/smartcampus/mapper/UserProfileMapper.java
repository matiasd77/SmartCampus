package com.smartcampus.mapper;

import com.smartcampus.dto.UserProfileDTO;
import com.smartcampus.entity.User;

public interface UserProfileMapper {
    UserProfileDTO toDto(User user);
} 