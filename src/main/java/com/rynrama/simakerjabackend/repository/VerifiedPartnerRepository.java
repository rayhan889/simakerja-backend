package com.rynrama.simakerjabackend.repository;

import com.rynrama.simakerjabackend.dto.PartnerProfileDTO;
import com.rynrama.simakerjabackend.model.VerifiedPartnerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface VerifiedPartnerRepository extends JpaRepository<VerifiedPartnerModel, UUID> {

    @Query("""
    select exists (
        select 1 from VerifiedPartnerModel v
            where v.partnerName = :partnerName
                and ( :partnerNumber is null or :partnerNumber = '' or v.partnerNumber = :partnerNumber )
        )
    """)
    Boolean isPartnerNameAndPartnerNumberExists(String partnerName, String partnerNumber);

    @Query("""
        SELECT new com.rynrama.simakerjabackend.dto.PartnerProfileDTO(
            v.partnerName,
            v.partnerAddress,
            v.partnerNumber,
            v.partnerRepresentativeName,
            v.partnerRepresentativePosition,
            v.activityType,
            v.partnerLogoKey,
            v.facultyRepresentativeName
        )
        FROM VerifiedPartnerModel v
        WHERE v.verifiedUntil >= CURRENT_TIMESTAMP
            AND (:search IS NULL OR :search = '' OR LOWER(v.partnerName) LIKE LOWER(CONCAT('%', :search, '%')))
    """)
    List<PartnerProfileDTO> findAllValidVerifiedPartners(@Param("search") String search);

    @Query(value = """
        SELECT v.partner_name, v.partner_name_normalized, v.partner_name_acronym,
               similarity(v.partner_name_normalized, :normalizedName) AS sim_score
        FROM verified_partners v
        WHERE v.partner_name_normalized = :normalizedName
           OR (:isAcronymInput = true AND v.partner_name_acronym = :normalizedName)
           OR (v.partner_name_normalized = :acronym)
           OR similarity(v.partner_name_normalized, :normalizedName) > :threshold
        ORDER BY sim_score DESC
        LIMIT 5
        """, nativeQuery = true)
    List<Object[]> findPotentialDuplicatesByName(
            @Param("normalizedName") String normalizedName,
            @Param("acronym") String acronym,
            @Param("isAcronymInput") boolean isAcronymInput,
            @Param("threshold") double threshold
    );
    @Query(value = """
        SELECT v.partner_name
        FROM verified_partners v
        WHERE v.partner_number = :partnerNumber
        LIMIT 1
        """, nativeQuery = true)
    List<Object[]> findPotentialDuplicatesByNumber(@Param("partnerNumber") String partnerNumber);
}
