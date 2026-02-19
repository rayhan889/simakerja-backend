package com.rynrama.simakerjabackend.repository;

import com.rynrama.simakerjabackend.dto.DocumentSubmissionDTO;
import com.rynrama.simakerjabackend.dto.MoAIADocumentDTO;
import com.rynrama.simakerjabackend.dto.PartnerProfileDTO;
import com.rynrama.simakerjabackend.model.MoAIAPDFViewModel;
import com.rynrama.simakerjabackend.model.MoaIADocumentModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface MoAIADocumentRepository extends JpaRepository<MoaIADocumentModel, UUID> {

    @Query("""
        select new com.rynrama.simakerjabackend.dto.MoAIADocumentDTO(
            m.partnerName,
            m.partnerNumber,
            m.facultyRepresentativeName,
            m.partnerRepresentativeName,
            m.partnerRepresentativePosition,
            m.activityType,
            m.documentType,
            m.studentSnapshots,
            m.partnerAddress,
            m.partnerLogoKey
        )
        from MoaIADocumentModel m
            where m.submission.id = :submissionId
    """)
    Optional<MoAIADocumentDTO> findAllMoAIABySubmissionId(@Param("submissionId") String submissionId);

    @Query("""
        select new com.rynrama.simakerjabackend.dto.MoAIADocumentDTO(
            m.partnerName,
            m.partnerNumber,
            m.facultyRepresentativeName,
            m.partnerRepresentativeName,
            m.partnerRepresentativePosition,
            m.activityType,
            m.documentType,
            m.studentSnapshots,
            m.partnerAddress,
            m.partnerLogoKey
        )
        from MoaIADocumentModel m
            left join m.submission s
                left join s.user u
                    where (:userId is null or u.id = :userId)
                    and (
                        :search is null or :search = ''
                        or lower(m.partnerName) like lower(concat('%', CAST(:search AS string), '%'))
                        or lower(m.partnerNumber) like lower(concat('%', CAST(:search AS string), '%'))
                        or lower(m.facultyRepresentativeName) like lower(concat('%', CAST(:search AS string), '%'))
                        or lower(m.partnerRepresentativeName) like lower(concat('%', CAST(:search AS string), '%'))
                    )
""")
    Page<MoAIADocumentDTO> findAllMoAIADocumentsByUserEmail(
            Pageable pageable,
            @Param("userId") UUID userId,
            @Param("search") String search
    );

    @Query("""
    select
          m
            from MoaIADocumentModel m
                join fetch m.submission
                    where m.submission.id = :submissionId
""")
    MoaIADocumentModel findMoAIADocumentBySubmissionId(UUID submissionId);

    @Query("""
    select m.partnerLogoKey from MoaIADocumentModel m where m.partnerLogoKey is not null
""")
    List<String> findAllPartnerLogoKeys();

    @Query("""
    select exists (
        select 1 from MoaIADocumentModel m where lower(m.partnerName) like lower(concat('%', :partnerName, '%'))
    )
""")
    Boolean isPartnerNameExists(String partnerName);

    @Query("""
    select distinct new com.rynrama.simakerjabackend.dto.PartnerProfileDTO(
        m.partnerName,
        m.partnerAddress,
        m.partnerNumber,
        m.partnerRepresentativeName,
        m.partnerRepresentativePosition,
        m.activityType,
        m.partnerLogoKey,
        m.facultyRepresentativeName
    ) from MoaIADocumentModel m\s
        left join SubmissionModel s on m.submission.id = s.id
            where (
                :search is null\s
                or :search = ''\s
                or lower(m.partnerName) like lower(concat('%', CAST(:search AS string), '%'))
            )
            and s.status IN ('verified_adhoc', 'verified_staff', 'completed')
""")
    List<PartnerProfileDTO> findAllVerifiedExistingPartners(String search);

    @Query("""
    select m from MoaIADocumentModel m
        join fetch SubmissionModel s
            on m.submission.id = s.id
                where s.id = :submissionId
""")
    Optional<MoaIADocumentModel> findBySubmissionId(String submissionId);
}