package com.rynrama.simakerjabackend.service;

import com.rynrama.simakerjabackend.dto.DuplicateCheckResult;
import com.rynrama.simakerjabackend.repository.MoAIADocumentRepository;
import com.rynrama.simakerjabackend.repository.VerifiedPartnerRepository;
import com.rynrama.simakerjabackend.util.PartnerNameNormalizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartnerDuplicateChecker {

    private final MoAIADocumentRepository moAIADocumentRepository;
    private final VerifiedPartnerRepository verifiedPartnerRepository;

    private static final double TRIGRAM_SIMILARITY_THRESHOLD = 0.6;

    public DuplicateCheckResult checkPotentialDuplication(String partnerName, String partnerNumber) {
        log.info("Checking duplication for partner name={}, and partner number={}", partnerName, partnerNumber);

        String normalizedInput = PartnerNameNormalizer.normalize(partnerName);
        String inputAcronym = PartnerNameNormalizer.acronym(normalizedInput);
        boolean isInputAcronym = PartnerNameNormalizer.isAcronym(normalizedInput);

//        #1 - Exact number check
        if (!partnerNumber.isBlank()) {
            var exactNumberMatches = verifiedPartnerRepository.findPotentialDuplicatesByNumber(partnerNumber);
            if (!exactNumberMatches.isEmpty()) {
                String existingPartnerName = (String) exactNumberMatches.get(0)[0];
                return DuplicateCheckResult.builder()
                        .blocked(true)
                        .matchedPartnerName(existingPartnerName)
                        .matchType("EXACT_NUMBER")
                        .build();
            }
        }

//        #2 - Db check for normalized names, acronyms & trigram sim score
        List<Object[]> matches = verifiedPartnerRepository.findPotentialDuplicatesByName(
                normalizedInput,
                inputAcronym,
                isInputAcronym,
                TRIGRAM_SIMILARITY_THRESHOLD
        );

        if (matches != null && !matches.isEmpty()) {
            for (Object[] match : matches) {
                String existingPartnerName = (String) match[0];
                String existingNormalized = (String) match[1];
                String existingAcronym = (String) match[2];
                Float simScore = (Float) match[3];

                if (normalizedInput.equals(existingNormalized)) {
                    return DuplicateCheckResult.builder()
                            .blocked(true)
                            .matchedPartnerName(existingPartnerName)
                            .matchType("NORMALIZED_NAME")
                            .build();
                } else if (isInputAcronym && normalizedInput.equals(existingAcronym)) {
                    return DuplicateCheckResult.builder()
                            .blocked(true)
                            .matchedPartnerName(existingPartnerName)
                            .matchType("ACRONYM_CROSS_MATCH")
                            .build();
                } else if (!inputAcronym.isBlank() && inputAcronym.equals(existingNormalized)) {
                    return DuplicateCheckResult.builder()
                            .blocked(true)
                            .matchedPartnerName(existingPartnerName)
                            .matchType("ACRONYM_CROSS_MATCH")
                            .build();
                } else if (simScore != null && simScore >= TRIGRAM_SIMILARITY_THRESHOLD) {
                    return DuplicateCheckResult.builder()
                            .blocked(false)
                            .warned(true)
                            .matchedPartnerName(existingPartnerName)
                            .matchType("TRIGRAM")
                            .similarityScore(simScore)
                            .build();
                }
            }
        }

        return DuplicateCheckResult.builder()
                .blocked(false)
                .warned(false)
                .build();
    }
}
