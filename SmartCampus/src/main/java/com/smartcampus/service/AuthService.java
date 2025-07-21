package com.smartcampus.service;

import com.smartcampus.dto.JwtResponse;
import com.smartcampus.dto.LoginRequest;
import com.smartcampus.dto.RegisterRequest;
import com.smartcampus.entity.User;

public interface AuthService {
    JwtResponse login(LoginRequest request);
    User register(RegisterRequest request);
} 