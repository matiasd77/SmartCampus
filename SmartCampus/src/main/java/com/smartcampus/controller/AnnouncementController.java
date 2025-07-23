package com.smartcampus.controller;

import com.smartcampus.dto.ApiResponse;
import com.smartcampus.dto.AnnouncementDTO;
import com.smartcampus.entity.AnnouncementPriority;
import com.smartcampus.entity.AnnouncementStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
@Tag(name = "Announcements", description = "Announcement management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class AnnouncementController {

    private final com.smartcampus.service.AnnouncementService announcementService;

    @PostMapping
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Create Announcement",
        description = "Create a new announcement (Professor or Admin only)",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Announcement details",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AnnouncementDTO.class),
                examples = @ExampleObject(
                    name = "Announcement Creation",
                    value = "{\"title\": \"Important Course Update\", \"content\": \"Please note that the exam date has been changed.\", \"courseId\": 1, \"postedById\": 1, \"priority\": \"HIGH\", \"isPublic\": true}"
                )
            )
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Announcement created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AnnouncementDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Validation error",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Validation Error",
                    value = "{\"success\": false, \"message\": \"Title is required\"}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Professor or Admin access required"
        )
    })
    public ResponseEntity<ApiResponse<AnnouncementDTO>> createAnnouncement(@Valid @RequestBody AnnouncementDTO announcementDTO) {
        AnnouncementDTO createdAnnouncement = announcementService.createAnnouncement(announcementDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Announcement created successfully", createdAnnouncement));
    }

    @GetMapping
    @Operation(
        summary = "Get All Announcements",
        description = "Retrieve all announcements (accessible to all authenticated users)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Announcements retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AnnouncementDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AnnouncementDTO>>> getAllAnnouncements() {
        List<AnnouncementDTO> announcements = announcementService.getAllAnnouncements();
        return ResponseEntity.ok(ApiResponse.success("Announcements retrieved successfully", announcements));
    }

    @GetMapping("/paginated")
    @Operation(
        summary = "Get All Announcements Paginated",
        description = "Retrieve all announcements with pagination (accessible to all authenticated users)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Announcements retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AnnouncementDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<Page<AnnouncementDTO>>> getAllAnnouncementsPaginated(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<AnnouncementDTO> announcements = announcementService.getAllAnnouncementsPaginated(pageable);
        return ResponseEntity.ok(ApiResponse.success("Announcements retrieved successfully", announcements));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get Announcement by ID",
        description = "Retrieve a specific announcement by its ID"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Announcement retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AnnouncementDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Announcement not found"
        )
    })
    public ResponseEntity<ApiResponse<AnnouncementDTO>> getAnnouncementById(@PathVariable Long id) {
        AnnouncementDTO announcement = announcementService.getAnnouncementById(id);
        // Increment view count when announcement is viewed
        announcementService.incrementViewCount(id);
        return ResponseEntity.ok(ApiResponse.success("Announcement retrieved successfully", announcement));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Update Announcement",
        description = "Update an existing announcement (Professor or Admin only)",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated announcement details",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AnnouncementDTO.class)
            )
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Announcement updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AnnouncementDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Announcement not found"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Professor or Admin access required"
        )
    })
    public ResponseEntity<ApiResponse<AnnouncementDTO>> updateAnnouncement(
            @PathVariable Long id,
            @Valid @RequestBody AnnouncementDTO announcementDTO) {
        AnnouncementDTO updatedAnnouncement = announcementService.updateAnnouncement(id, announcementDTO);
        return ResponseEntity.ok(ApiResponse.success("Announcement updated successfully", updatedAnnouncement));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Delete Announcement",
        description = "Delete an announcement (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Announcement deleted successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Delete Response",
                    value = "{\"success\": true, \"message\": \"Announcement deleted successfully\"}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Announcement not found"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Professor or Admin access required"
        )
    })
    public ResponseEntity<ApiResponse<String>> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.ok(ApiResponse.success("Announcement deleted successfully", null));
    }

    @GetMapping("/course/{courseId}")
    @Operation(
        summary = "Get Announcements by Course",
        description = "Retrieve all announcements for a specific course"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Course announcements retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AnnouncementDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AnnouncementDTO>>> getAnnouncementsByCourse(@PathVariable Long courseId) {
        List<AnnouncementDTO> announcements = announcementService.getAnnouncementsByCourseId(courseId);
        return ResponseEntity.ok(ApiResponse.success("Course announcements retrieved successfully", announcements));
    }

    @GetMapping("/course/{courseId}/paginated")
    @Operation(
        summary = "Get Announcements by Course Paginated",
        description = "Retrieve all announcements for a specific course with pagination"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Course announcements retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AnnouncementDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<Page<AnnouncementDTO>>> getAnnouncementsByCoursePaginated(
            @PathVariable Long courseId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<AnnouncementDTO> announcements = announcementService.getAnnouncementsByCourseIdPaginated(courseId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Course announcements retrieved successfully", announcements));
    }

    @GetMapping("/active")
    @Operation(
        summary = "Get Active Announcements",
        description = "Retrieve all active announcements"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Active announcements retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AnnouncementDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AnnouncementDTO>>> getActiveAnnouncements() {
        List<AnnouncementDTO> announcements = announcementService.getActiveAnnouncements();
        return ResponseEntity.ok(ApiResponse.success("Active announcements retrieved successfully", announcements));
    }

    @GetMapping("/active/course/{courseId}")
    @Operation(
        summary = "Get Active Announcements by Course",
        description = "Retrieve all active announcements for a specific course"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Active course announcements retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AnnouncementDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AnnouncementDTO>>> getActiveAnnouncementsByCourse(@PathVariable Long courseId) {
        List<AnnouncementDTO> announcements = announcementService.getActiveAnnouncementsByCourseId(courseId);
        return ResponseEntity.ok(ApiResponse.success("Active course announcements retrieved successfully", announcements));
    }

    @GetMapping("/public")
    @Operation(
        summary = "Get Public Announcements",
        description = "Retrieve all active public announcements"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Public announcements retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AnnouncementDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AnnouncementDTO>>> getPublicAnnouncements() {
        List<AnnouncementDTO> announcements = announcementService.getActivePublicAnnouncements();
        return ResponseEntity.ok(ApiResponse.success("Public announcements retrieved successfully", announcements));
    }

    @GetMapping("/public/course/{courseId}")
    @Operation(
        summary = "Get Public Announcements by Course",
        description = "Retrieve all active public announcements for a specific course"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Public course announcements retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AnnouncementDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AnnouncementDTO>>> getPublicAnnouncementsByCourse(@PathVariable Long courseId) {
        List<AnnouncementDTO> announcements = announcementService.getActivePublicAnnouncementsByCourseId(courseId);
        return ResponseEntity.ok(ApiResponse.success("Public course announcements retrieved successfully", announcements));
    }

    @GetMapping("/urgent")
    @Operation(
        summary = "Get Urgent Announcements",
        description = "Retrieve all urgent announcements"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Urgent announcements retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AnnouncementDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AnnouncementDTO>>> getUrgentAnnouncements() {
        List<AnnouncementDTO> announcements = announcementService.getAnnouncementsByIsUrgent(true);
        return ResponseEntity.ok(ApiResponse.success("Urgent announcements retrieved successfully", announcements));
    }

    @GetMapping("/urgent/course/{courseId}")
    @Operation(
        summary = "Get Urgent Announcements by Course",
        description = "Retrieve all urgent announcements for a specific course"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Urgent course announcements retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AnnouncementDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AnnouncementDTO>>> getUrgentAnnouncementsByCourse(@PathVariable Long courseId) {
        List<AnnouncementDTO> announcements = announcementService.getAnnouncementsByCourseIdAndIsUrgent(courseId, true);
        return ResponseEntity.ok(ApiResponse.success("Urgent course announcements retrieved successfully", announcements));
    }

    @GetMapping("/pinned")
    @Operation(
        summary = "Get Pinned Announcements",
        description = "Retrieve all pinned announcements"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Pinned announcements retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AnnouncementDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AnnouncementDTO>>> getPinnedAnnouncements() {
        List<AnnouncementDTO> announcements = announcementService.getAnnouncementsByIsPinned(true);
        return ResponseEntity.ok(ApiResponse.success("Pinned announcements retrieved successfully", announcements));
    }

    @GetMapping("/pinned/course/{courseId}")
    @Operation(
        summary = "Get Pinned Announcements by Course",
        description = "Retrieve all pinned announcements for a specific course"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Pinned course announcements retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AnnouncementDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AnnouncementDTO>>> getPinnedAnnouncementsByCourse(@PathVariable Long courseId) {
        List<AnnouncementDTO> announcements = announcementService.getAnnouncementsByCourseIdAndIsPinned(courseId, true);
        return ResponseEntity.ok(ApiResponse.success("Pinned course announcements retrieved successfully", announcements));
    }

    @GetMapping("/status/{status}")
    @Operation(
        summary = "Get Announcements by Status",
        description = "Retrieve all announcements with a specific status"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Announcements by status retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AnnouncementDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AnnouncementDTO>>> getAnnouncementsByStatus(@PathVariable AnnouncementStatus status) {
        List<AnnouncementDTO> announcements = announcementService.getAnnouncementsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Announcements by status retrieved successfully", announcements));
    }

    @GetMapping("/priority/{priority}")
    @Operation(
        summary = "Get Announcements by Priority",
        description = "Retrieve all announcements with a specific priority"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Announcements by priority retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AnnouncementDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AnnouncementDTO>>> getAnnouncementsByPriority(@PathVariable AnnouncementPriority priority) {
        List<AnnouncementDTO> announcements = announcementService.getAnnouncementsByPriority(priority);
        return ResponseEntity.ok(ApiResponse.success("Announcements by priority retrieved successfully", announcements));
    }

    @GetMapping("/search")
    @Operation(
        summary = "Search Announcements",
        description = "Search announcements by keyword"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Search results retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AnnouncementDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AnnouncementDTO>>> searchAnnouncements(@RequestParam String keyword) {
        List<AnnouncementDTO> announcements = announcementService.getAnnouncementsByKeyword(keyword);
        return ResponseEntity.ok(ApiResponse.success("Search results retrieved successfully", announcements));
    }

    @GetMapping("/search/course/{courseId}")
    @Operation(
        summary = "Search Announcements by Course",
        description = "Search announcements by keyword within a specific course"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Course search results retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AnnouncementDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AnnouncementDTO>>> searchAnnouncementsByCourse(
            @PathVariable Long courseId,
            @RequestParam String keyword) {
        List<AnnouncementDTO> announcements = announcementService.getAnnouncementsByCourseIdAndKeyword(courseId, keyword);
        return ResponseEntity.ok(ApiResponse.success("Course search results retrieved successfully", announcements));
    }

    @GetMapping("/recent")
    @Operation(
        summary = "Get Recent Announcements",
        description = "Retrieve recent announcements (last 30 days)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Recent announcements retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AnnouncementDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AnnouncementDTO>>> getRecentAnnouncements() {
        LocalDateTime since = LocalDateTime.now().minusDays(30);
        List<AnnouncementDTO> announcements = announcementService.getRecentAnnouncements(since);
        return ResponseEntity.ok(ApiResponse.success("Recent announcements retrieved successfully", announcements));
    }

    @GetMapping("/recent/course/{courseId}")
    @Operation(
        summary = "Get Recent Announcements by Course",
        description = "Retrieve recent announcements for a specific course (last 30 days)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Recent course announcements retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AnnouncementDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AnnouncementDTO>>> getRecentAnnouncementsByCourse(@PathVariable Long courseId) {
        LocalDateTime since = LocalDateTime.now().minusDays(30);
        List<AnnouncementDTO> announcements = announcementService.getRecentAnnouncementsByCourseId(courseId, since);
        return ResponseEntity.ok(ApiResponse.success("Recent course announcements retrieved successfully", announcements));
    }

    @GetMapping("/most-viewed")
    @Operation(
        summary = "Get Most Viewed Announcements",
        description = "Retrieve most viewed announcements"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Most viewed announcements retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AnnouncementDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AnnouncementDTO>>> getMostViewedAnnouncements() {
        List<AnnouncementDTO> announcements = announcementService.getMostViewedAnnouncements();
        return ResponseEntity.ok(ApiResponse.success("Most viewed announcements retrieved successfully", announcements));
    }

    @GetMapping("/most-viewed/course/{courseId}")
    @Operation(
        summary = "Get Most Viewed Announcements by Course",
        description = "Retrieve most viewed announcements for a specific course"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Most viewed course announcements retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AnnouncementDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AnnouncementDTO>>> getMostViewedAnnouncementsByCourse(@PathVariable Long courseId) {
        List<AnnouncementDTO> announcements = announcementService.getMostViewedAnnouncementsByCourseId(courseId);
        return ResponseEntity.ok(ApiResponse.success("Most viewed course announcements retrieved successfully", announcements));
    }

    @GetMapping("/with-attachments")
    @Operation(
        summary = "Get Announcements with Attachments",
        description = "Retrieve all announcements that have attachments"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Announcements with attachments retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AnnouncementDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AnnouncementDTO>>> getAnnouncementsWithAttachments() {
        List<AnnouncementDTO> announcements = announcementService.getAnnouncementsWithAttachments();
        return ResponseEntity.ok(ApiResponse.success("Announcements with attachments retrieved successfully", announcements));
    }

    @GetMapping("/with-attachments/course/{courseId}")
    @Operation(
        summary = "Get Announcements with Attachments by Course",
        description = "Retrieve all announcements with attachments for a specific course"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Course announcements with attachments retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AnnouncementDTO.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<AnnouncementDTO>>> getAnnouncementsWithAttachmentsByCourse(@PathVariable Long courseId) {
        List<AnnouncementDTO> announcements = announcementService.getAnnouncementsWithAttachmentsByCourseId(courseId);
        return ResponseEntity.ok(ApiResponse.success("Course announcements with attachments retrieved successfully", announcements));
    }

    // Management endpoints for professors and admins
    @PostMapping("/{id}/pin")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Pin Announcement",
        description = "Pin an announcement (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Announcement pinned successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Announcement not found"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Professor or Admin access required"
        )
    })
    public ResponseEntity<ApiResponse<String>> pinAnnouncement(@PathVariable Long id) {
        announcementService.pinAnnouncement(id);
        return ResponseEntity.ok(ApiResponse.success("Announcement pinned successfully", null));
    }

    @PostMapping("/{id}/unpin")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Unpin Announcement",
        description = "Unpin an announcement (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Announcement unpinned successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Announcement not found"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Professor or Admin access required"
        )
    })
    public ResponseEntity<ApiResponse<String>> unpinAnnouncement(@PathVariable Long id) {
        announcementService.unpinAnnouncement(id);
        return ResponseEntity.ok(ApiResponse.success("Announcement unpinned successfully", null));
    }

    @PostMapping("/{id}/mark-urgent")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Mark Announcement as Urgent",
        description = "Mark an announcement as urgent (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Announcement marked as urgent successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Announcement not found"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Professor or Admin access required"
        )
    })
    public ResponseEntity<ApiResponse<String>> markAsUrgent(@PathVariable Long id) {
        announcementService.markAsUrgent(id);
        return ResponseEntity.ok(ApiResponse.success("Announcement marked as urgent successfully", null));
    }

    @PostMapping("/{id}/unmark-urgent")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Unmark Announcement as Urgent",
        description = "Unmark an announcement as urgent (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Announcement unmarked as urgent successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Announcement not found"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Professor or Admin access required"
        )
    })
    public ResponseEntity<ApiResponse<String>> unmarkAsUrgent(@PathVariable Long id) {
        announcementService.unmarkAsUrgent(id);
        return ResponseEntity.ok(ApiResponse.success("Announcement unmarked as urgent successfully", null));
    }

    @PostMapping("/{id}/archive")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Archive Announcement",
        description = "Archive an announcement (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Announcement archived successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Announcement not found"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Professor or Admin access required"
        )
    })
    public ResponseEntity<ApiResponse<String>> archiveAnnouncement(
            @PathVariable Long id,
            @RequestParam String archivedBy,
            @RequestParam(required = false) String archiveReason) {
        announcementService.archiveAnnouncement(id, archivedBy, archiveReason);
        return ResponseEntity.ok(ApiResponse.success("Announcement archived successfully", null));
    }

    @PostMapping("/{id}/unarchive")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    @Operation(
        summary = "Unarchive Announcement",
        description = "Unarchive an announcement (Professor or Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Announcement unarchived successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Announcement not found"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Professor or Admin access required"
        )
    })
    public ResponseEntity<ApiResponse<String>> unarchiveAnnouncement(@PathVariable Long id) {
        announcementService.unarchiveAnnouncement(id);
        return ResponseEntity.ok(ApiResponse.success("Announcement unarchived successfully", null));
    }

    // Statistics endpoints
    @GetMapping("/stats/count/active")
    @Operation(
        summary = "Get Active Announcement Count",
        description = "Get the count of active announcements"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Count retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Count Response",
                    value = "{\"success\": true, \"message\": \"Count retrieved successfully\", \"data\": 25}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Long>> getActiveAnnouncementCount() {
        Long count = announcementService.getActiveAnnouncementCount();
        return ResponseEntity.ok(ApiResponse.success("Active announcement count retrieved successfully", count));
    }

    @GetMapping("/stats/count/urgent")
    @Operation(
        summary = "Get Urgent Announcement Count",
        description = "Get the count of urgent announcements"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Count retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Count Response",
                    value = "{\"success\": true, \"message\": \"Count retrieved successfully\", \"data\": 5}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Long>> getUrgentAnnouncementCount() {
        Long count = announcementService.getUrgentAnnouncementCount();
        return ResponseEntity.ok(ApiResponse.success("Urgent announcement count retrieved successfully", count));
    }

    @GetMapping("/stats/count/pinned")
    @Operation(
        summary = "Get Pinned Announcement Count",
        description = "Get the count of pinned announcements"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Count retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Count Response",
                    value = "{\"success\": true, \"message\": \"Count retrieved successfully\", \"data\": 3}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Long>> getPinnedAnnouncementCount() {
        Long count = announcementService.getPinnedAnnouncementCount();
        return ResponseEntity.ok(ApiResponse.success("Pinned announcement count retrieved successfully", count));
    }

    @GetMapping("/stats/count/course/{courseId}")
    @Operation(
        summary = "Get Course Announcement Count",
        description = "Get the count of announcements for a specific course"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Count retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Count Response",
                    value = "{\"success\": true, \"message\": \"Count retrieved successfully\", \"data\": 10}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Long>> getCourseAnnouncementCount(@PathVariable Long courseId) {
        Long count = announcementService.getAnnouncementCountByCourseId(courseId);
        return ResponseEntity.ok(ApiResponse.success("Course announcement count retrieved successfully", count));
    }
} 