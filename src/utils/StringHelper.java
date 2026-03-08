package utils;

public class StringHelper {

    /**
     * Converts a given string (like a student's name) to Title Case.
     * Example: "john doe" -> "John Doe"
     * @param input The raw string that needs to be capitalized.
     * @return The formatted string in Title Case.
     */
    public static String toTitleCase(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        StringBuilder results = new StringBuilder(); 
        String[] names= input.trim().split("\\s+");
        for(String name : names){
            results.append(Character.toUpperCase(name.charAt(0)));
            results.append(name.substring(1).toLowerCase());
            results.append(" ");
        }
        
        return results.toString().trim(); 
    }

    /**
     * Performs a case-insensitive search to check if a keyword exists within a target string.
     * Useful for your DatabaseOperations search method.
     * @param target The raw string that will be searched.
     * @param keyword The specific string you are looking for inside the target.
     * @return true if the keyword is found, false otherwise.
     */
    public static boolean containsIgnoreCase(String target, String keyword) {
        if (target == null || keyword == null) {
            return false;
        }
        
        String lowerTarget = target.toLowerCase();
        String lowerKeyword = keyword.toLowerCase();
        
        return lowerTarget.contains(lowerKeyword);
    }

    /**
     * Splits a string based on a specific delimiter.
     * Useful if you need to extract parts of an email or separate first/last names.
     * @param input the raw string that needs to be split
     * @param delimiter the delimiter that will slice the input
     * @return an array of strings resulting from the split
     */
    public static String[] splitString(String input, String delimiter) {
        if (input == null || delimiter == null) {
            return new String[0];
        }
        
        return input.trim().split(delimiter);
    }
    /**
     * Extracts a substring from a given string.
     * Useful for generating a short student ID from a name or parsing specific data.
     * @param input the raw string to extract the substring from.
     * @param startIndex the beginning index of the substring (inclusive).
     * @param endIndex the ending index of the substring (exclusive).
     * @return the extracted substring, or an empty string if indices are invalid.
     */
    public static String extractSubstring(String input, int startIndex, int endIndex) {
        if (input == null || startIndex < 0 || endIndex > input.length() || startIndex > endIndex) {
            return "";
        }
        
        return input.substring(startIndex, endIndex);
    }

    /**
     * Concatenates multiple strings into one.
     * Useful for building dynamic GUI messages or combining first and last names.
     * @param delimiter the string used to separate each part (e.g., " " or ",")
     * @param parts the strings that will be concatenated 
     * @return the concatenated result.
     */
    public static String concatenateStrings(String delimiter, String... parts) {
        if (parts == null || parts.length == 0) {
            return "";
        }
        if (delimiter == null) {
            delimiter = ""; // Prevents accidentally printing the word "null"
        }
        
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < parts.length; i++) {
            // Append the actual string
            if (parts[i] != null) {
                result.append(parts[i]);
            }
            
            // Only append the delimiter if it is NOT the very last item
            if (i < parts.length - 1) {
                result.append(delimiter);
            }
        }
        
        return result.toString();
    }
}