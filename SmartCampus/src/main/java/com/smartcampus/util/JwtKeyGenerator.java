package com.smartcampus.util;

import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for generating secure JWT keys.
 * This class can be used to generate Base64-encoded keys for production use.
 */
@Slf4j
public class JwtKeyGenerator {

    /**
     * Generate a secure Base64-encoded key for HS512 algorithm.
     * 
     * @return Base64-encoded key string
     */
    public static String generateSecureKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] keyBytes = new byte[64]; // 512 bits for HS512
        secureRandom.nextBytes(keyBytes);
        
        String base64Key = Base64.getEncoder().encodeToString(keyBytes);
        
        log.info("Generated secure JWT key for HS512 algorithm");
        log.info("Key length: {} bytes (512 bits)", keyBytes.length);
        log.info("Base64-encoded key: {}", base64Key);
        
        return base64Key;
    }

    /**
     * Generate multiple secure keys for testing/development.
     * 
     * @param count Number of keys to generate
     */
    public static void generateMultipleKeys(int count) {
        log.info("Generating {} secure JWT keys:", count);
        
        for (int i = 1; i <= count; i++) {
            String key = generateSecureKey();
            log.info("Key {}: {}", i, key);
        }
    }

    /**
     * Main method to generate keys when run as a standalone utility.
     * Usage: java JwtKeyGenerator [count]
     */
    public static void main(String[] args) {
        int count = 1;
        
        if (args.length > 0) {
            try {
                count = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                log.error("Invalid count argument. Using default value of 1.");
            }
        }
        
        if (count == 1) {
            String key = generateSecureKey();
            System.out.println("\n=== JWT Key Generation ===");
            System.out.println("Generated secure key for HS512:");
            System.out.println(key);
            System.out.println("\nAdd this to your application.properties:");
            System.out.println("jwt.secret=" + key);
            System.out.println("===========================");
        } else {
            generateMultipleKeys(count);
        }
    }
} 