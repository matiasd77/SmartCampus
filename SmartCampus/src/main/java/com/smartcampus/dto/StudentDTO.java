package com.smartcampus.dto;

import com.smartcampus.entity.StudentStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {

    private Long id;

    @NotBlank(message = "Student ID is required")
    @Pattern(regexp = "^[A-Z0-9]{8,10}$", message = "Student ID must be 8-10 characters long and contain only uppercase letters and numbers")
    private String studentId;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    private LocalDate dateOfBirth;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number should be valid")
    private String phoneNumber;

    private String address;

    private String major;

    @NotNull(message = "Year of study is required")
    private Integer yearOfStudy;

    private Double gpa;

    @NotNull(message = "Status is required")
    private StudentStatus status;

    // User ID for linking to User entity
    private Long userId;
} 