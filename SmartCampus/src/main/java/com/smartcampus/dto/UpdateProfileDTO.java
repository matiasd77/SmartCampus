package com.smartcampus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for updating user profile information. All fields are optional and support partial updates.")
public class UpdateProfileDTO {

    @Schema(
        description = "User's full name",
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
        description = "User's email address",
        example = "john.smith@example.com",
        maxLength = 100,
        format = "email"
    )
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Schema(
        description = "User's phone number",
        example = "+1234567890",
        maxLength = 20,
        pattern = "^[+]?[0-9\\s\\-()]+$"
    )
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    @Pattern(regexp = "^[+]?[0-9\\s\\-()]+$", message = "Phone number can only contain digits, spaces, hyphens, parentheses, and plus sign")
    private String phoneNumber;

    @Schema(
        description = "User's street address",
        example = "123 Main Street",
        maxLength = 200
    )
    @Size(max = 200, message = "Address must not exceed 200 characters")
    private String address;

    @Schema(
        description = "User's city",
        example = "New York",
        maxLength = 100,
        pattern = "^[a-zA-Z\\s]+$"
    )
    @Size(max = 100, message = "City must not exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "City can only contain letters and spaces")
    private String city;

    @Schema(
        description = "User's state or province",
        example = "NY",
        maxLength = 100,
        pattern = "^[a-zA-Z\\s]+$"
    )
    @Size(max = 100, message = "State must not exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "State can only contain letters and spaces")
    private String state;

    @Schema(
        description = "User's zip code or postal code",
        example = "10001",
        maxLength = 20,
        pattern = "^[0-9\\-]+$"
    )
    @Size(max = 20, message = "Zip code must not exceed 20 characters")
    @Pattern(regexp = "^[0-9\\-]+$", message = "Zip code can only contain digits and hyphens")
    private String zipCode;

    @Schema(
        description = "User's country",
        example = "USA",
        maxLength = 100,
        pattern = "^[a-zA-Z\\s]+$"
    )
    @Size(max = 100, message = "Country must not exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Country can only contain letters and spaces")
    private String country;

    @Schema(
        description = "User's biographical information",
        example = "Software engineering student passionate about technology and innovation",
        maxLength = 500
    )
    @Size(max = 500, message = "Bio must not exceed 500 characters")
    private String bio;

    @Schema(
        description = "URL to user's profile picture",
        example = "https://example.com/profile-picture.jpg",
        maxLength = 200,
        format = "uri"
    )
    @Size(max = 200, message = "Profile picture URL must not exceed 200 characters")
    @Pattern(regexp = "^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})([/\\w .-]*)*/?$", 
             message = "Profile picture URL must be a valid URL")
    private String profilePictureUrl;

    @Schema(
        description = "User's department or faculty",
        example = "Computer Science",
        maxLength = 100,
        pattern = "^[a-zA-Z\\s]+$"
    )
    @Size(max = 100, message = "Department must not exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Department can only contain letters and spaces")
    private String department;

    @Schema(
        description = "User's job position or title",
        example = "Software Engineer",
        maxLength = 100,
        pattern = "^[a-zA-Z\\s]+$"
    )
    @Size(max = 100, message = "Position must not exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Position can only contain letters and spaces")
    private String position;

    @Schema(
        description = "Student's unique identifier",
        example = "STU2024001",
        maxLength = 100,
        pattern = "^[A-Z0-9]+$"
    )
    @Size(max = 100, message = "Student ID must not exceed 100 characters")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Student ID can only contain uppercase letters and digits")
    private String studentId;

    @Schema(
        description = "Employee's unique identifier",
        example = "EMP2024001",
        maxLength = 100,
        pattern = "^[A-Z0-9]+$"
    )
    @Size(max = 100, message = "Employee ID must not exceed 100 characters")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Employee ID can only contain uppercase letters and digits")
    private String employeeId;

    @Schema(
        description = "Student's major field of study",
        example = "Computer Science",
        maxLength = 100,
        pattern = "^[a-zA-Z\\s]+$"
    )
    @Size(max = 100, message = "Major must not exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Major can only contain letters and spaces")
    private String major;

    @Schema(
        description = "Student's minor field of study",
        example = "Mathematics",
        maxLength = 100,
        pattern = "^[a-zA-Z\\s]+$"
    )
    @Size(max = 100, message = "Minor must not exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Minor can only contain letters and spaces")
    private String minor;

    @Schema(
        description = "Student's academic year (1-10)",
        example = "3",
        minimum = "1",
        maximum = "10"
    )
    @Min(value = 1, message = "Year must be at least 1")
    @Max(value = 10, message = "Year must not exceed 10")
    private Integer year;

    @Schema(
        description = "Current academic semester",
        example = "FALL",
        allowableValues = {"FALL", "SPRING", "SUMMER", "WINTER"},
        maxLength = 20
    )
    @Size(max = 20, message = "Semester must not exceed 20 characters")
    @Pattern(regexp = "^(FALL|SPRING|SUMMER|WINTER)$", message = "Semester must be FALL, SPRING, SUMMER, or WINTER")
    private String semester;

    @Schema(
        description = "Academic year in YYYY-YYYY format",
        example = "2024-2025",
        maxLength = 20,
        pattern = "^[0-9]{4}-[0-9]{4}$"
    )
    @Size(max = 20, message = "Academic year must not exceed 20 characters")
    @Pattern(regexp = "^[0-9]{4}-[0-9]{4}$", message = "Academic year must be in format YYYY-YYYY")
    private String academicYear;

    @Schema(
        description = "Student's academic advisor",
        example = "Dr. Jane Smith",
        maxLength = 100,
        pattern = "^[a-zA-Z\\s]+$"
    )
    @Size(max = 100, message = "Advisor must not exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Advisor can only contain letters and spaces")
    private String advisor;

    @Schema(
        description = "Professor's research area",
        example = "Artificial Intelligence",
        maxLength = 100,
        pattern = "^[a-zA-Z\\s]+$"
    )
    @Size(max = 100, message = "Research area must not exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Research area can only contain letters and spaces")
    private String researchArea;

    @Schema(
        description = "Professor's research interests",
        example = "Machine Learning, Deep Learning, Computer Vision",
        maxLength = 500
    )
    @Size(max = 500, message = "Research interests must not exceed 500 characters")
    private String researchInterests;

    @Schema(
        description = "User's personal website URL",
        example = "https://johnsmith.dev",
        maxLength = 200,
        format = "uri"
    )
    @Size(max = 200, message = "Website URL must not exceed 200 characters")
    @Pattern(regexp = "^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})([/\\w .-]*)*/?$", 
             message = "Website URL must be a valid URL")
    private String websiteUrl;

    @Schema(
        description = "User's LinkedIn profile URL",
        example = "https://linkedin.com/in/johnsmith",
        maxLength = 200,
        format = "uri"
    )
    @Size(max = 200, message = "LinkedIn URL must not exceed 200 characters")
    @Pattern(regexp = "^(https?://)?(www\\.)?linkedin\\.com/in/[a-zA-Z0-9-]+/?$", 
             message = "LinkedIn URL must be a valid LinkedIn profile URL")
    private String linkedinUrl;

    @Schema(
        description = "User's GitHub profile URL",
        example = "https://github.com/johnsmith",
        maxLength = 200,
        format = "uri"
    )
    @Size(max = 200, message = "GitHub URL must not exceed 200 characters")
    @Pattern(regexp = "^(https?://)?(www\\.)?github\\.com/[a-zA-Z0-9-]+/?$", 
             message = "GitHub URL must be a valid GitHub profile URL")
    private String githubUrl;

    @Schema(
        description = "User's Twitter profile URL",
        example = "https://twitter.com/johnsmith",
        maxLength = 200,
        format = "uri"
    )
    @Size(max = 200, message = "Twitter URL must not exceed 200 characters")
    @Pattern(regexp = "^(https?://)?(www\\.)?twitter\\.com/[a-zA-Z0-9_]+/?$", 
             message = "Twitter URL must be a valid Twitter profile URL")
    private String twitterUrl;

    @Schema(
        description = "User's Facebook profile URL",
        example = "https://facebook.com/johnsmith",
        maxLength = 200,
        format = "uri"
    )
    @Size(max = 200, message = "Facebook URL must not exceed 200 characters")
    @Pattern(regexp = "^(https?://)?(www\\.)?facebook\\.com/[a-zA-Z0-9.]+/?$", 
             message = "Facebook URL must be a valid Facebook profile URL")
    private String facebookUrl;

    @Schema(
        description = "User's Instagram profile URL",
        example = "https://instagram.com/johnsmith",
        maxLength = 200,
        format = "uri"
    )
    @Size(max = 200, message = "Instagram URL must not exceed 200 characters")
    @Pattern(regexp = "^(https?://)?(www\\.)?instagram\\.com/[a-zA-Z0-9_.]+/?$", 
             message = "Instagram URL must be a valid Instagram profile URL")
    private String instagramUrl;

    @Schema(
        description = "Emergency contact person's name",
        example = "Jane Smith",
        maxLength = 100,
        pattern = "^[a-zA-Z\\s]+$"
    )
    @Size(max = 100, message = "Emergency contact name must not exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Emergency contact name can only contain letters and spaces")
    private String emergencyContactName;

    @Schema(
        description = "Emergency contact person's phone number",
        example = "+1234567890",
        maxLength = 20,
        pattern = "^[+]?[0-9\\s\\-()]+$"
    )
    @Size(max = 20, message = "Emergency contact phone must not exceed 20 characters")
    @Pattern(regexp = "^[+]?[0-9\\s\\-()]+$", message = "Emergency contact phone can only contain digits, spaces, hyphens, parentheses, and plus sign")
    private String emergencyContactPhone;

    @Schema(
        description = "Relationship to emergency contact person",
        example = "Mother",
        maxLength = 200,
        pattern = "^[a-zA-Z\\s]+$"
    )
    @Size(max = 200, message = "Emergency contact relationship must not exceed 200 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Emergency contact relationship can only contain letters and spaces")
    private String emergencyContactRelationship;

    @Schema(
        description = "Emergency contact person's address",
        example = "456 Oak Street",
        maxLength = 200
    )
    @Size(max = 200, message = "Emergency contact address must not exceed 200 characters")
    private String emergencyContactAddress;

    @Schema(
        description = "Emergency contact person's city",
        example = "Los Angeles",
        maxLength = 100,
        pattern = "^[a-zA-Z\\s]+$"
    )
    @Size(max = 100, message = "Emergency contact city must not exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Emergency contact city can only contain letters and spaces")
    private String emergencyContactCity;

    @Schema(
        description = "Emergency contact person's state",
        example = "CA",
        maxLength = 100,
        pattern = "^[a-zA-Z\\s]+$"
    )
    @Size(max = 100, message = "Emergency contact state must not exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Emergency contact state can only contain letters and spaces")
    private String emergencyContactState;

    @Schema(
        description = "Emergency contact person's zip code",
        example = "90210",
        maxLength = 20,
        pattern = "^[0-9\\-]+$"
    )
    @Size(max = 20, message = "Emergency contact zip code must not exceed 20 characters")
    @Pattern(regexp = "^[0-9\\-]+$", message = "Emergency contact zip code can only contain digits and hyphens")
    private String emergencyContactZipCode;

    @Schema(
        description = "Emergency contact person's country",
        example = "USA",
        maxLength = 100,
        pattern = "^[a-zA-Z\\s]+$"
    )
    @Size(max = 100, message = "Emergency contact country must not exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Emergency contact country can only contain letters and spaces")
    private String emergencyContactCountry;

    @Schema(
        description = "User's skills and competencies",
        example = "Java, Spring Boot, React, Docker, AWS",
        maxLength = 500
    )
    @Size(max = 500, message = "Skills must not exceed 500 characters")
    private String skills;

    @Schema(
        description = "Languages the user speaks",
        example = "English (Native), Albanian (Fluent), Spanish (Intermediate)",
        maxLength = 500
    )
    @Size(max = 500, message = "Languages must not exceed 500 characters")
    private String languages;

    @Schema(
        description = "Professional certifications",
        example = "AWS Certified Developer, Oracle Certified Professional",
        maxLength = 500
    )
    @Size(max = 500, message = "Certifications must not exceed 500 characters")
    private String certifications;

    @Schema(
        description = "Awards and recognitions",
        example = "Dean's List 2023, Best Student Award 2022",
        maxLength = 500
    )
    @Size(max = 500, message = "Awards must not exceed 500 characters")
    private String awards;

    @Schema(
        description = "Academic publications",
        example = "Machine Learning Applications in Education (2023)",
        maxLength = 500
    )
    @Size(max = 500, message = "Publications must not exceed 500 characters")
    private String publications;

    @Schema(
        description = "Projects and work experience",
        example = "SmartCampus Management System, E-commerce Platform",
        maxLength = 500
    )
    @Size(max = 500, message = "Projects must not exceed 500 characters")
    private String projects;

    @Schema(
        description = "Work experience and internships",
        example = "Software Engineer Intern at TechCorp (2023)",
        maxLength = 500
    )
    @Size(max = 500, message = "Experience must not exceed 500 characters")
    private String experience;

    @Schema(
        description = "Educational background",
        example = "Bachelor's in Computer Science, University of Technology",
        maxLength = 500
    )
    @Size(max = 500, message = "Education must not exceed 500 characters")
    private String education;

    @Schema(
        description = "Personal interests and hobbies",
        example = "Reading, Hiking, Photography, Coding",
        maxLength = 500
    )
    @Size(max = 500, message = "Interests must not exceed 500 characters")
    private String interests;

    @Schema(
        description = "Hobbies and recreational activities",
        example = "Playing guitar, Swimming, Chess",
        maxLength = 500
    )
    @Size(max = 500, message = "Hobbies must not exceed 500 characters")
    private String hobbies;

    @Schema(
        description = "Career and personal goals",
        example = "Become a senior software engineer, Contribute to open source",
        maxLength = 500
    )
    @Size(max = 500, message = "Goals must not exceed 500 characters")
    private String goals;

    @Schema(
        description = "Additional notes and comments",
        example = "Available for remote work, Open to relocation",
        maxLength = 500
    )
    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;

    // Helper method to check if any field is provided
    public boolean hasAnyField() {
        return name != null || email != null || phoneNumber != null || address != null ||
               city != null || state != null || zipCode != null || country != null ||
               bio != null || profilePictureUrl != null || department != null ||
               position != null || studentId != null || employeeId != null ||
               major != null || minor != null || year != null || semester != null ||
               academicYear != null || advisor != null || researchArea != null ||
               researchInterests != null || websiteUrl != null || linkedinUrl != null ||
               githubUrl != null || twitterUrl != null || facebookUrl != null ||
               instagramUrl != null || emergencyContactName != null ||
               emergencyContactPhone != null || emergencyContactRelationship != null ||
               emergencyContactAddress != null || emergencyContactCity != null ||
               emergencyContactState != null || emergencyContactZipCode != null ||
               emergencyContactCountry != null || skills != null || languages != null ||
               certifications != null || awards != null || publications != null ||
               projects != null || experience != null || education != null ||
               interests != null || hobbies != null || goals != null || notes != null;
    }

    // Helper method to get full address
    public String getFullAddress() {
        StringBuilder addressBuilder = new StringBuilder();
        if (address != null && !address.trim().isEmpty()) {
            addressBuilder.append(address);
        }
        if (city != null && !city.trim().isEmpty()) {
            if (addressBuilder.length() > 0) addressBuilder.append(", ");
            addressBuilder.append(city);
        }
        if (state != null && !state.trim().isEmpty()) {
            if (addressBuilder.length() > 0) addressBuilder.append(", ");
            addressBuilder.append(state);
        }
        if (zipCode != null && !zipCode.trim().isEmpty()) {
            if (addressBuilder.length() > 0) addressBuilder.append(" ");
            addressBuilder.append(zipCode);
        }
        if (country != null && !country.trim().isEmpty()) {
            if (addressBuilder.length() > 0) addressBuilder.append(", ");
            addressBuilder.append(country);
        }
        return addressBuilder.toString();
    }

    // Helper method to get full emergency contact address
    public String getFullEmergencyContactAddress() {
        StringBuilder addressBuilder = new StringBuilder();
        if (emergencyContactAddress != null && !emergencyContactAddress.trim().isEmpty()) {
            addressBuilder.append(emergencyContactAddress);
        }
        if (emergencyContactCity != null && !emergencyContactCity.trim().isEmpty()) {
            if (addressBuilder.length() > 0) addressBuilder.append(", ");
            addressBuilder.append(emergencyContactCity);
        }
        if (emergencyContactState != null && !emergencyContactState.trim().isEmpty()) {
            if (addressBuilder.length() > 0) addressBuilder.append(", ");
            addressBuilder.append(emergencyContactState);
        }
        if (emergencyContactZipCode != null && !emergencyContactZipCode.trim().isEmpty()) {
            if (addressBuilder.length() > 0) addressBuilder.append(" ");
            addressBuilder.append(emergencyContactZipCode);
        }
        if (emergencyContactCountry != null && !emergencyContactCountry.trim().isEmpty()) {
            if (addressBuilder.length() > 0) addressBuilder.append(", ");
            addressBuilder.append(emergencyContactCountry);
        }
        return addressBuilder.toString();
    }

    // Helper method to get display name
    public String getDisplayName() {
        if (name != null && !name.trim().isEmpty()) {
            return name.trim();
        }
        return null;
    }

    // Helper method to get formatted phone number
    public String getFormattedPhoneNumber() {
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            return phoneNumber.trim().replaceAll("\\s+", " ");
        }
        return null;
    }

    // Helper method to get formatted emergency contact phone
    public String getFormattedEmergencyContactPhone() {
        if (emergencyContactPhone != null && !emergencyContactPhone.trim().isEmpty()) {
            return emergencyContactPhone.trim().replaceAll("\\s+", " ");
        }
        return null;
    }

    // Helper method to validate academic year format
    public boolean isValidAcademicYear() {
        if (academicYear == null || academicYear.trim().isEmpty()) {
            return true; // Optional field
        }
        return academicYear.matches("^[0-9]{4}-[0-9]{4}$");
    }

    // Helper method to validate year range
    public boolean isValidYear() {
        if (year == null) {
            return true; // Optional field
        }
        return year >= 1 && year <= 10;
    }

    // Helper method to validate semester
    public boolean isValidSemester() {
        if (semester == null || semester.trim().isEmpty()) {
            return true; // Optional field
        }
        return semester.matches("^(FALL|SPRING|SUMMER|WINTER)$");
    }
} 