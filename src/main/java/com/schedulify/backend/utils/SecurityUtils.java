package com.schedulify.backend.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Random;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityUtils {

    private static final Random RANDOM = new Random();
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    /**
     * Get the current user email from the Spring Security context.
     *
     * @return the email of the current user or null if not authenticated
     */
    public static String getCurrentUserEmail() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getName)
                .orElse(null);
    }

    /**
     * Generate a random password for OAuth users.
     *
     * @return a random password
     */
    public static String generateRandomPassword() {
        StringBuilder sb = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    /**
     * Check if the current user is authenticated.
     *
     * @return true if the user is authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .isPresent();
    }

    /**
     * Check if the current user has a specific authority.
     *
     * @param authority the authority to check
     * @return true if the current user has the authority, false otherwise
     */
    public static boolean hasAuthority(String authority) {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .filter(auth -> auth.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals(authority)))
                .isPresent();
    }

    /**
     * Encode a password using BCrypt.
     *
     * @param password the raw password
     * @return the encoded password
     */
    public static String encodePassword(String password) {
        return PASSWORD_ENCODER.encode(password);
    }

    /**
     * Check if a raw password matches an encoded password.
     *
     * @param rawPassword the raw password
     * @param encodedPassword the encoded password
     * @return true if the passwords match, false otherwise
     */
    public static boolean matchPassword(String rawPassword, String encodedPassword) {
        return PASSWORD_ENCODER.matches(rawPassword, encodedPassword);
    }
}