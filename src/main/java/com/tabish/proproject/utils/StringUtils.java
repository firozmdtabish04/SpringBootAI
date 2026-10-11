package com.tabish.proproject.utils;

import java.security.SecureRandom;
import java.util.Locale;

public final class StringUtils {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private StringUtils() {
        // Prevent instantiation
    }

    public static boolean isNullOrBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean hasText(String str) {
        return !isNullOrBlank(str);
    }

    public static String toSlug(String input) {
        if (isNullOrBlank(input)) {
            return "";
        }
        return input.toLowerCase(Locale.ENGLISH)
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    public static String generateRandomString(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be greater than zero");
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    public static String maskEmail(String email) {
        if (isNullOrBlank(email) || !email.contains("@")) {
            return email;
        }
        int atIndex = email.indexOf('@');
        String namePart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex);

        if (namePart.length() <= 2) {
            return namePart.charAt(0) + "*" + domainPart;
        }
        return namePart.charAt(0) + "*".repeat(namePart.length() - 2) + namePart.charAt(namePart.length() - 1) + domainPart;
    }
}

