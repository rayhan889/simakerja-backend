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
}
