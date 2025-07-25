package com.smartcampus.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    @Value("${jwt.secret:your-secret-key-here-make-it-long-and-secure-for-production}")
    private String jwtSecret;

    @Value("${jwt.refresh-secret:your-refresh-secret-key-here-make-it-long-and-secure-for-production}")
    private String refreshSecret;

    @Value("${jwt.access-token-expiration:900000}") // 15 minutes default
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration:604800000}") // 7 days default
    private long refreshTokenExpiration;

    @Value("${jwt.cookie-domain:localhost}")
    private String cookieDomain;

    @Value("${jwt.cookie-secure:false}")
    private boolean cookieSecure;

    private SecretKey cachedAccessSigningKey;
    private SecretKey cachedRefreshSigningKey;

    /**
     * Generate access token for user
     */
    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(userDetails, getAccessSigningKey(), accessTokenExpiration, "access");
    }

    /**
     * Generate refresh token for user
     */
    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(userDetails, getRefreshSigningKey(), refreshTokenExpiration, "refresh");
    }

    /**
     * Generate token with specified parameters
     */
    private String generateToken(UserDetails userDetails, SecretKey signingKey, long expiration, String tokenType) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        List<String> authorities = userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList());
        List<String> roles = authorities.stream()
                .map(authority -> authority.startsWith("ROLE_") ? authority.substring(5) : authority)
                .collect(Collectors.toList());

        log.info("Generating {} token for user: {} with authorities: {} and roles: {}",
                tokenType, userDetails.getUsername(), authorities, roles);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .setIssuer("smartcampus")
                .claim("authorities", authorities)
                .claim("roles", roles)
                .claim("tokenType", tokenType)
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Validate access token
     */
    public boolean validateAccessToken(String token) {
        return validateToken(token, getAccessSigningKey(), "access");
    }

    /**
     * Validate refresh token
     */
    public boolean validateRefreshToken(String token) {
        return validateToken(token, getRefreshSigningKey(), "refresh");
    }

    /**
     * Validate token with specified signing key and type
     */
    private boolean validateToken(String token, SecretKey signingKey, String expectedType) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String tokenType = claims.get("tokenType", String.class);
            if (!expectedType.equals(tokenType)) {
                log.warn("Token type mismatch. Expected: {}, Got: {}", expectedType, tokenType);
                return false;
            }

            return !claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extract email from access token
     */
    public String getEmailFromAccessToken(String token) {
        return getEmailFromToken(token, getAccessSigningKey());
    }

    /**
     * Extract email from refresh token
     */
    public String getEmailFromRefreshToken(String token) {
        return getEmailFromToken(token, getRefreshSigningKey());
    }

    /**
     * Extract email from token
     */
    private String getEmailFromToken(String token, SecretKey signingKey) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Error extracting email from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extract authorities from access token
     */
    public List<String> getAuthoritiesFromAccessToken(String token) {
        return getAuthoritiesFromToken(token, getAccessSigningKey());
    }

    /**
     * Extract authorities from token
     */
    private List<String> getAuthoritiesFromToken(String token, SecretKey signingKey) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.get("authorities", List.class);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Error extracting authorities from token: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Add refresh token to HttpOnly cookie
     */
    public void addRefreshTokenToCookie(String refreshToken, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .domain(cookieDomain)
                .path("/")
                .maxAge(refreshTokenExpiration / 1000) // Convert to seconds
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Strict")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
        log.debug("Refresh token added to HttpOnly cookie");
    }

    /**
     * Extract refresh token from HttpOnly cookie
     */
    public String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    log.debug("Refresh token extracted from cookie");
                    return cookie.getValue();
                }
            }
        }
        log.debug("No refresh token found in cookies");
        return null;
    }

    /**
     * Clear refresh token cookie
     */
    public void clearRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .domain(cookieDomain)
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Strict")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
        log.debug("Refresh token cookie cleared");
    }

    /**
     * Get access token signing key
     */
    private SecretKey getAccessSigningKey() {
        if (cachedAccessSigningKey == null) {
            cachedAccessSigningKey = createSigningKey(jwtSecret, "access");
        }
        return cachedAccessSigningKey;
    }

    /**
     * Get refresh token signing key
     */
    private SecretKey getRefreshSigningKey() {
        if (cachedRefreshSigningKey == null) {
            cachedRefreshSigningKey = createSigningKey(refreshSecret, "refresh");
        }
        return cachedRefreshSigningKey;
    }

    /**
     * Create signing key from secret
     */
    private SecretKey createSigningKey(String secret, String keyType) {
        try {
            if (isBase64Encoded(secret)) {
                log.info("Using Base64-encoded {} JWT secret", keyType);
                byte[] keyBytes = Base64.getDecoder().decode(secret);
                return new SecretKeySpec(keyBytes, "HmacSHA512");
            } else {
                if (secret.contains("your-secret-key-here") || secret.length() < 64) {
                    log.warn("Using default or weak {} JWT secret. Generating secure key for development.", keyType);
                    return generateSecureKey();
                } else {
                    log.info("Using provided {} JWT secret", keyType);
                    return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                }
            }
        } catch (Exception e) {
            log.error("Failed to initialize {} JWT signing key", keyType, e);
            throw new IllegalStateException("Failed to initialize " + keyType + " JWT signing key", e);
        }
    }

    /**
     * Generate a secure random key
     */
    private SecretKey generateSecureKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] keyBytes = new byte[64]; // 512 bits for HS512
        secureRandom.nextBytes(keyBytes);
        return new SecretKeySpec(keyBytes, "HmacSHA512");
    }

    /**
     * Check if a string is Base64 encoded
     */
    private boolean isBase64Encoded(String str) {
        try {
            Base64.getDecoder().decode(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
} 