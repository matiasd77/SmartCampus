package com.smartcampus.controller;

import com.smartcampus.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> getAdminDashboard() {
        return ApiResponse.success("Welcome to Admin Dashboard", "Admin dashboard data");
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> getAllUsers() {
        return ApiResponse.success("All users retrieved", "User list data");
    }
} 