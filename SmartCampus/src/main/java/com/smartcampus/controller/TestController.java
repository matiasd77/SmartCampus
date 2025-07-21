package com.smartcampus.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/test")
@Tag(name = "Test", description = "Test API for OpenAPI verification")
public class TestController {

    @GetMapping("/health")
    @Operation(summary = "Simple health check", description = "Returns a simple health status")
    public String health() {
        return "OK";
    }
} 