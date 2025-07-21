package com.smartcampus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for creating a new student")
public class StudentRequest {

    @Schema(
        description = "Student's full name",
        example = "John Smith",
        minLength = 2,
        maxLength = 100,
        pattern = "^[a-zA-Z\\s]+$"
    )
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name can only contain letters and spaces")
    private String name;

    @Schema(
        description = "Student's email address",
        example = "john.smith@example.com",
        maxLength = 100,
        format = "email"
    )
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Schema(
        description = "Student's academic year (1-10)",
        example = "3",
        minimum = "1",
        maximum = "10"
    )
    @NotNull(message = "Year is required")
    @Min(value = 1, message = "Year must be at least 1")
    @Max(value = 10, message = "Year must not exceed 10")
    private Integer year;

    @Schema(
        description = "Student's major field of study",
        example = "Computer Science",
        maxLength = 100,
        pattern = "^[a-zA-Z\\s]+$"
    )
    @NotBlank(message = "Major is required")
    @Size(max = 100, message = "Major must not exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Major can only contain letters and spaces")
    private String major;
} 