package com.library.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utility class for password hashing and verification.
 */
public class PasswordUtil {

    /**
     * Hashes a password using SHA-256 algorithm and returns a Base64 encoded string.
     * 
     * @param password The plain text password to hash
     * @return The hashed password as a Base64 encoded string, or null if an error occurs
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Verifies if a plain text password matches a hashed password.
     * 
     * @param plainPassword The plain text password to check
     * @param hashedPassword The hashed password to compare against
     * @return true if the passwords match, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        String hashedInput = hashPassword(plainPassword);
        return hashedInput != null && hashedInput.equals(hashedPassword);
    }
}
