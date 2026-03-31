package com.rynrama.simakerjabackend.util;

public class FormatString {

    //    teknik_informatika > Teknik Informatika
    public static String formatProgramStudy(String input) {
        if (input == null || input.isBlank()) return input;

        String[] words = input.replace("_", " ").toLowerCase().split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }

        return result.toString().trim();
    }

    public static String formatFullname(String input) {
        if (input == null || input.isBlank()) {
            return input;
        }

        // 1. Remove numbers and characters (like '_') before the first letter
        // Regex explanation: ^ (start of string) [^a-zA-Z]+ (one or more characters that are NOT letters)
        String cleaned = input.replaceFirst("^[^a-zA-Z]+", "");

        // 2. Remove the specific suffixes at the end (e.g., TIC23, SI..., PTI...)
        // Regex explanation: \s+ (spaces) followed by TI, SI, or PTI, then any characters .* to the end $
        cleaned = cleaned.replaceFirst("\\s+(TI|SI|PTI).*$", "");

        // 3. Capitalize each word and return
        return toTitleCase(cleaned.trim());
    }

    private static String toTitleCase(String text) {
        String[] words = text.split("\\s+");
        StringBuilder titleCase = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                titleCase.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }

        return titleCase.toString().trim();
    }
}
