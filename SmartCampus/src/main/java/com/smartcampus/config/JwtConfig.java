package com.smartcampus.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    private String secret = "your-secret-key-here-make-it-long-and-secure-in-production";
    private long expiration = 86400000; // 24 hours in milliseconds
    private String issuer = "smartcampus";
} 