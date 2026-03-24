package com.rynrama.simakerjabackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DuplicateCheckResult {
    private boolean blocked;           // Hard duplicate found (e.g., exact acronym match)
    private boolean warned;            // Soft/fuzzy match found (e.g., high trigram score)
    private String matchedPartnerName; // The existing partner that matched
    private String matchType;          // "EXACT_NUMBER", "NORMALIZED_NAME", "ACRONYM", "TRIGRAM"
    private double similarityScore;    // For trigram matches
}
