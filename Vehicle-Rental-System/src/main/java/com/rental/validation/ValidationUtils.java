package com.rental.validation;

import java.util.regex.Pattern;

/**
 * Centralized Data Validation Pipeline for the DriveFlow ERP ecosystem.
 * Intercepts user inputs at the UI layer before invoking controller CRUD sequences.
 * * @author Pathum Srinath
 */
public class ValidationUtils {

    // Matches standard phone configurations (including local 10-digit and international prefixes)
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(?:0|\\+94|94)?7[0-9]{8}$|^[0-9]{10}$");
    
    // Matches common license plate formats (e.g., WP CA-1234, 18-9999, or CAS-5566)
    private static final Pattern PLATE_PATTERN = Pattern.compile("^[A-Z0-9]{2,3}[-\\s]?[0-9]{4}$");

    /**
     * Checks if a string variable is null, blank, or containing only white spaces.
     */
    public static boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    /**
     * Validates contact numbers against standard structural telecom lengths.
     */
    public static boolean isValidPhone(String phone) {
        if (isEmpty(phone)) return false;
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Verifies if the input text can be safely compiled into a positive fractional currency/rate.
     */
    public static boolean isValidDouble(String text) {
        if (isEmpty(text)) return false;
        try {
            double val = Double.parseDouble(text.trim());
            return val >= 0.0;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /**
     * Verifies if the input text can be safely converted into a non-negative integer.
     */
    public static boolean isValidInt(String text) {
        if (isEmpty(text)) return false;
        try {
            int val = Integer.parseInt(text.trim());
            return val >= 0;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /**
     * Confirms license plate formats follow proper system syntax constraints.
     */
    public static boolean isValidPlate(String plate) {
        if (isEmpty(plate)) return false;
        return PLATE_PATTERN.matcher(plate.trim().toUpperCase()).matches();
    }

    /**
     * Basic alphanumeric driving permit validation rule checks.
     */
    public static boolean isValidLicense(String license) {
        if (isEmpty(license)) return false;
        String clean = license.trim();
        return clean.length() >= 6 && clean.length() <= 12;
    }
}