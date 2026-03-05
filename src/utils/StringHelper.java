package utils;

public class StringHelper {

    /**
     * Converts a given string (like a student's name) to Title Case.
     * Example: "jone doe" -> "Jone Doe"
     */
    public static String toTitleCase(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        // TODO: Implement logic to split by space, capitalize first letter of each word, and rejoin
        return ""; 
    }

    /**
     * Performs a case-insensitive search to check if a keyword exists within a target string.
     * Useful for your DatabaseOperations search method.
     */
    public static boolean containsIgnoreCase(String target, String keyword) {
        if (target == null || keyword == null) {
            return false;
        }
        // TODO: Implement logic using .toLowerCase() or .toUpperCase() to compare
        return false;
    }

    /**
     * Splits a string based on a specific delimiter.
     * Useful if you need to extract parts of an email or separate first/last names.
     */
    public static String[] splitString(String input, String delimiter) {
        if (input == null || delimiter == null) {
            return new String[0];
        }
        // TODO: Implement logic using input.split(delimiter)
        return new String[0];
    }

    /**
     * Extracts a substring from a given string.
     * Useful for generating a short student ID from a name or parsing specific data.
     */
    public static String extractSubstring(String input, int startIndex, int endIndex) {
        if (input == null || startIndex < 0 || endIndex > input.length()) {
            return "";
        }
        // TODO: Implement logic using input.substring(startIndex, endIndex)
        return "";
    }

    /**
     * Concatenates multiple strings into one.
     * Useful for building dynamic GUI messages or combining first and last names.
     */
    public static String concatenateStrings(String... parts) {
        // TODO: Implement logic using a StringBuilder to append all parts efficiently
        return "";
    }
}