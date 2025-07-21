package com.smartcampus.mapper.impl;

import com.smartcampus.dto.RegisterRequest;
import com.smartcampus.entity.User;
import com.smartcampus.mapper.UserMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toEntity(RegisterRequest request) {
        if (request == null) {
            return null;
        }

        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .role(request.getRole())
                .build();
    }

    @Override
    public RegisterRequest toDto(User user) {
        if (user == null) {
            return null;
        }

        return RegisterRequest.builder()
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
} 