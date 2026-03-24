package com.rynrama.simakerjabackend.util;

import com.rynrama.simakerjabackend.repository.MoAIADocumentRepository;

import java.util.regex.Pattern;

public class PartnerNameNormalizer {

    private final MoAIADocumentRepository moAIADocumentRepository;

//    PT, PT., CV, CV., Tbk, Tbk., Persero, (Persero)
    private static final Pattern PREFIX_SUFFIX = Pattern.compile(
            "(?i)^(PT\\.?\\s*|CV\\.?\\s*)|\\s*(Tbk\\.?|\\(Persero\\)|Persero)\\s*$"
    );

    public PartnerNameNormalizer(
            MoAIADocumentRepository moAIADocumentRepository
    ) {
        this.moAIADocumentRepository = moAIADocumentRepository;
    }

    public static String normalize(String name) {
        if (name == null || name.isBlank()) {
            return "";
        }

        String cleaned = PREFIX_SUFFIX.matcher(name).replaceAll("");

        return cleaned.toLowerCase().trim().replaceAll("\\s+", " ");
    }

    public static String acronym(String normalizedName) {
        if (normalizedName == null || normalizedName.isBlank()) {
            return "";
        }

        String[] words = normalizedName.split("\\s+");
        if (words.length <= 1) {
            return normalizedName;
        }

        StringBuilder acronym = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                acronym.append(word.charAt(0));
            }
        }
        return acronym.toString();
    }

    public static boolean isAcronym(String input) {
        if (input == null || input.isBlank()) {
            return false;
        }
        String trimmed = input.trim();

        return !trimmed.contains(" ") && trimmed.length() >= 2 && trimmed.length() <= 6;
    }
}
