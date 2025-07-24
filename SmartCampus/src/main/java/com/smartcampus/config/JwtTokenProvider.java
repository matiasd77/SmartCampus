package com.smartcampus.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    private final JwtConfig jwtConfig;
    private SecretKey cachedSigningKey;

    /**
     * Get or create a secure signing key for JWT tokens.
     * Supports both Base64-encoded keys and auto-generation for development.
     */
    private SecretKey getSigningKey() {
        if (cachedSigningKey != null) {
            return cachedSigningKey;
        }

        String secret = jwtConfig.getSecret();
        
        try {
            // Check if the secret is Base64 encoded (production mode)
            if (isBase64Encoded(secret)) {
                log.info("Using Base64-encoded JWT secret from configuration");
                byte[] keyBytes = Base64.getDecoder().decode(secret);
                cachedSigningKey = new SecretKeySpec(keyBytes, "HmacSHA512");
            } else {
                // Development mode: generate a secure key if the default is used
                if (secret.equals("your-secret-key-here-make-it-long-and-secure-for-production") ||
                    secret.equals("your-secret-key-here-make-it-long-and-secure-in-production")) {
                    log.warn("Using default JWT secret. Generating secure key for development. " +
                            "For production, set a Base64-encoded secret in application.properties");
                    cachedSigningKey = generateSecureKey();
                } else {
                    // Use the provided secret but ensure it's long enough
                    if (secret.length() < 64) {
                        log.warn("JWT secret is too short. Generating secure key instead.");
                        cachedSigningKey = generateSecureKey();
                    } else {
                        log.info("Using provided JWT secret (ensure it's secure for production)");
                        cachedSigningKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                    }
                }
            }
            
            // Validate key strength
            if (cachedSigningKey.getEncoded().length < 64) {
                log.error("Generated key is too weak for HS512. Minimum 64 bytes required.");
                throw new IllegalStateException("JWT signing key is too weak for HS512 algorithm");
            }
            
            log.info("JWT signing key initialized successfully. Key length: {} bytes", 
                    cachedSigningKey.getEncoded().length);
            
        } catch (Exception e) {
            log.error("Failed to initialize JWT signing key", e);
            throw new IllegalStateException("Failed to initialize JWT signing key", e);
        }
        
        return cachedSigningKey;
    }

    /**
     * Generate a secure random key for HS512 algorithm.
     * This is used for development/testing purposes.
     */
    private SecretKey generateSecureKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] keyBytes = new byte[64]; // 512 bits for HS512
        secureRandom.nextBytes(keyBytes);
        
        log.info("Generated secure JWT key for development. " +
                "For production, set jwt.secret to a Base64-encoded key in application.properties");
        
        return new SecretKeySpec(keyBytes, "HmacSHA512");
    }

    /**
     * Check if a string is Base64 encoded.
     */
    private boolean isBase64Encoded(String str) {
        try {
            Base64.getDecoder().decode(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public String generateToken(Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtConfig.getExpiration());

            // Extract authorities and convert to role names
            List<String> authorities = userDetails.getAuthorities().stream()
                    .map(authority -> authority.getAuthority())
                    .collect(Collectors.toList());

            // Create role names without ROLE_ prefix for Spring Security hasAnyRole()
            List<String> roles = authorities.stream()
                    .map(authority -> authority.startsWith("ROLE_") ? authority.substring(5) : authority)
                    .collect(Collectors.toList());

            log.info("Generating JWT token for user: {} with authorities: {} and roles: {}", 
                    userDetails.getUsername(), authorities, roles);

            return Jwts.builder()
                    .setSubject(userDetails.getUsername())
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .setIssuer(jwtConfig.getIssuer())
                    .claim("authorities", authorities)  // Full authorities with ROLE_ prefix
                    .claim("roles", roles)             // Roles without ROLE_ prefix for hasAnyRole()
                    .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                    .compact();
        } catch (Exception e) {
            log.error("Failed to generate JWT token for authentication", e);
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    public String generateToken(String email) {
        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtConfig.getExpiration());

            return Jwts.builder()
                    .setSubject(email)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .setIssuer(jwtConfig.getIssuer())
                    .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                    .compact();
        } catch (Exception e) {
            log.error("Failed to generate JWT token for email: {}", email, e);
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    public String getEmailFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getSubject();
        } catch (Exception e) {
            log.error("Failed to extract email from JWT token", e);
            throw new RuntimeException("Failed to extract email from token", e);
        }
    }

    public List<String> getAuthoritiesFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            @SuppressWarnings("unchecked")
            List<String> authorities = claims.get("authorities", List.class);
            
            if (authorities == null) {
                log.warn("No authorities found in JWT token for user: {}", claims.getSubject());
                return List.of();
            }
            
            log.info("Extracted authorities from JWT token: {} for user: {}", authorities, claims.getSubject());
            return authorities;
        } catch (Exception e) {
            log.error("Failed to extract authorities from JWT token", e);
            return List.of();
        }
    }

    public List<String> getRolesFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            @SuppressWarnings("unchecked")
            List<String> roles = claims.get("roles", List.class);
            
            if (roles == null) {
                log.warn("No roles found in JWT token for user: {}", claims.getSubject());
                return List.of();
            }
            
            log.info("Extracted roles from JWT token: {} for user: {}", roles, claims.getSubject());
            return roles;
        } catch (Exception e) {
            log.error("Failed to extract roles from JWT token", e);
            return List.of();
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
        } catch (Exception ex) {
            log.error("Unexpected error validating JWT token: {}", ex.getMessage());
        }
        return false;
    }
} 