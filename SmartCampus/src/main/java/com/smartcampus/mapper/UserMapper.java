package com.smartcampus.mapper;

import com.smartcampus.dto.RegisterRequest;
import com.smartcampus.entity.User;

public interface UserMapper {
    User toEntity(RegisterRequest request);
    RegisterRequest toDto(User user);
} 