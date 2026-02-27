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

        String result = input.replaceFirst("^\\d+_", "");

        result = result.replaceFirst("\\s+(TI|SI|PTI)\\b.*", "");

        result = result.replaceAll("[^A-Za-z\\s]", "");

        result = result.trim().replaceAll("\\s+", " ");

        String[] parts = result.toLowerCase().split(" ");
        StringBuilder builder = new StringBuilder();

        for (String part : parts) {
            if (!part.isEmpty()) {
                builder.append(Character.toUpperCase(part.charAt(0)))
                        .append(part.substring(1))
                        .append(" ");
            }
        }

        return builder.toString().trim();
    }
}
